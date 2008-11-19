package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.Publisher;
import com.sun.tools.javac.util.Pair;

import java.util.*;

/**
 * @author Patrick Jordan
 */
public class UserManager {
    private final Object lock;

    private List<User> users;

    private List<UserEventListener> listeners;

    private Random random;
    
    public UserManager() {
        lock = new Object();
        users = new ArrayList<User>();
        listeners = new ArrayList<UserEventListener>();
        random = new Random();
    }


    public void transition(Publisher publisher) {

        synchronized( lock ) {

            Collections.shuffle(users,random);

            for(User user : users) {

                if(user.state.isSearching()) {

                    handleSearch(user,publisher);

                } else {
                
                    handleTransition(user,false);

                }
            }
        }
    }

    private void handleSearch(User user, Publisher publisher) {

        Query query = generateQuery(user);

        Auction auction = publisher.runAuction(query);

        handleTransition(user, handleImpression(query, auction, user) );
    }

    private boolean handleImpression(Query query, Auction auction, User user) {

        fireQueryIssued(query);

        boolean converted = false;

        boolean clicking = true;

        //TODO: grab this value
        double continuationProbability = 0.0;

        Ranking ranking = auction.getRanking();

        Pricing pricing = auction.getPricing();

        for(int i = 0; i < ranking.size(); i++) {

            Ad ad = ranking.get(i);

            fireAdViewed(query, ad, i+1);

            if ( clicking ) {
                double clickProbability = calculateClickProbability(user,ad);


            }
        }

        return converted;
    }

    private double calculateClickProbability(User user, Ad ad) {
        double probability;

        return 0;
    }


    private void handleTransition(User user, boolean converted) {
        if(converted) {
            user.state = QueryState.TRANSACTED;
        } else {
            //TODO: Handle probabilities
        }
    }


    private Query generateQuery(User user) {
        //TODO: generate query
        return null;
    }
                                                          
    public boolean addUserEventListener(UserEventListener listener) {
        synchronized( lock ) {
            return listeners.add(listener);
        }
    }

    public boolean containsUserEVentListener(UserEventListener listener) {
        synchronized( lock ) {
            return listeners.contains(listener);
        }
    }

    public boolean removeUserEventListener(UserEventListener listener) {
        synchronized( lock ) {
            return listeners.remove(listener);
        }
    }

    private void fireQueryIssued(Query query) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).queryIssued(query);
        }
    }

    private void fireAdViewed(Query query, Ad ad, int slot) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).viewed(query,ad,slot);
        }
    }

    private void fireAdClicked(Query query, Ad ad, int slot, double cpc) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).clicked(query,ad,slot,cpc);
        }
    }

    private void fireAdConverted(Query query, Ad ad, int slot, double salesProfit) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).converted(query,ad,slot,salesProfit);
        }
    }


    private static class User {
        public QueryState state;
        public Product product;
    }
}
