package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.Publisher;

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


    public void triggerBehavior(Publisher publisher) {

        synchronized (lock) {
            log.finest("START OF USER TRIGGER");

            Collections.shuffle(users, random);

            for (User user : users) {

                boolean transacted = handleSearch(user, publisher);

                handleTransition(user, transacted);
            }

            log.finest("FINISH OF USER TRIGGER");
        }

        
    }

    private boolean handleSearch(User user, Publisher publisher) {
        boolean transacted = false;

        Query query = generateQuery(user);
        if (query != null) {
            Auction auction = publisher.runAuction(query);

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
        int[] distribution  = new int[QueryState.values().length];

        for(User user : users) {
            distribution[user.getState().ordinal()]++;
        }
        
        return distribution;
    }


    public UserClickModel getUserClickModel() {
        return userClickModel;
    }

    public void setUserClickModel(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;

        if(viewManager!=null) {
            viewManager.setUserClickModel(userClickModel);
        }
    }
}
