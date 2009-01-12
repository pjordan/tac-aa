package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.sim.Auctioneer;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Patrick Jordan, Ben Cassell
 */
public class DefaultUserManager implements UserManager {
    protected Logger log = Logger.getLogger(Publisher.class.getName());

    private final Object lock;

    private List<User> users;

    private Random random;

    private UserQueryManager queryManager;

    private UserTransitionManager transitionManager;

    private UserViewManager viewManager;

    private UserClickModel userClickModel;

    public DefaultUserManager(RetailCatalog retailCatalog, UserTransitionManager transitionManager, UserQueryManager queryManager, UserViewManager viewManager, int populationSize) {
        this(retailCatalog, transitionManager, queryManager, viewManager, populationSize, new Random());
    }


    public DefaultUserManager(RetailCatalog retailCatalog, UserTransitionManager transitionManager, UserQueryManager queryManager, UserViewManager viewManager, int populationSize, Random random) {
        lock = new Object();

        if (retailCatalog == null) {
            throw new NullPointerException("Retail catalog cannot be null");
        }

        if (transitionManager == null) {
            throw new NullPointerException("User transition manager cannot be null");
        }

        if (queryManager == null) {
            throw new NullPointerException("User query manager cannot be null");
        }

        if (viewManager == null) {
            throw new NullPointerException("User view manager cannot be null");
        }

        if (populationSize < 0) {
            throw new IllegalArgumentException("Population size cannot be negative");
        }

        if (random == null) {
            throw new NullPointerException("Random number generator cannot be null");
        }

        this.random = random;
        this.transitionManager = transitionManager;
        this.queryManager = queryManager;
        this.viewManager = viewManager;

        users = buildUsers(retailCatalog, populationSize);
    }

    private List<User> buildUsers(RetailCatalog catalog, int populationSize) {
        List<User> users = new ArrayList<User>();

        for (Product product : catalog) {
            for (int i = 0; i < populationSize; i++) {
                users.add(new User(QueryState.NON_SEARCHING, product));
            }
        }

        return users;
    }


    public void initialize(int virtualDays) {
        for (int d = virtualDays; d >= 1; d--) {
            transitionManager.nextTimeUnit(-d);

            for (User user : users) {
                user.setState(transitionManager.transition(user.getState(), false));
            }
        }
    }

    public void triggerBehavior(Auctioneer auctioneer) {

        synchronized (lock) {
            log.finest("START OF USER TRIGGER");

            Collections.shuffle(users, random);

            for (User user : users) {

                boolean transacted = handleSearch(user, auctioneer);

                handleTransition(user, transacted);
            }

            log.finest("FINISH OF USER TRIGGER");
        }


    }

    private boolean handleSearch(User user, Auctioneer auctioneer) {
        boolean transacted = false;


        Query query = generateQuery(user);
        if (query != null) {
            Auction auction = auctioneer.runAuction(query);

            transacted = handleImpression(query, auction, user);

        }

        return transacted;
    }

    private boolean handleImpression(Query query, Auction auction, User user) {
        return viewManager.processImpression(user, query, auction);
    }


    private void handleTransition(User user, boolean transacted) {
        user.setState(transitionManager.transition(user.getState(), transacted));
    }


    private Query generateQuery(User user) {
        return queryManager.generateQuery(user);
    }

    public boolean addUserEventListener(UserEventListener listener) {
        synchronized (lock) {
            return viewManager.addUserEventListener(listener);
        }
    }

    public boolean containsUserEventListener(UserEventListener listener) {
        synchronized (lock) {
            return viewManager.containsUserEventListener(listener);
        }
    }

    public boolean removeUserEventListener(UserEventListener listener) {
        synchronized (lock) {
            return viewManager.removeUserEventListener(listener);
        }
    }


    public void nextTimeUnit(int timeUnit) {
        viewManager.nextTimeUnit(timeUnit);
        queryManager.nextTimeUnit(timeUnit);
        transitionManager.nextTimeUnit(timeUnit);
    }


    public int[] getStateDistribution() {
        int[] distribution = new int[QueryState.values().length];

        for (User user : users) {
            distribution[user.getState().ordinal()]++;
        }

        return distribution;
    }


    public UserClickModel getUserClickModel() {
        return userClickModel;
    }

    public void setUserClickModel(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;
        viewManager.setUserClickModel(userClickModel);
    }
}
