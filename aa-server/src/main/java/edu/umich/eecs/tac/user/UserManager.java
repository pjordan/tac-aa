package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.Publisher;

import java.util.*;

/**
 * @author Patrick Jordan, Ben Cassell
 */
public class UserManager {
    private final Object lock;

    private List<User> users;

    private List<UserEventListener> listeners;

    private Random random;
    
    private RetailCatalog catalog;
    
    public UserManager(RetailCatalog c) {
        lock = new Object();
        users = new ArrayList<User>();
        listeners = new ArrayList<UserEventListener>();
        random = new Random();
        catalog = c;
    }

	public void transition(Publisher publisher, Map<String, AdvertiserInfo> advertiserInfo) {

        synchronized( lock ) {

            Collections.shuffle(users,random);

            for(User user : users) {

                if(user.state.isSearching()) {

                    handleSearch(user,publisher, advertiserInfo);

                } else {
                
                    handleTransition(user,false);

                }
            }
        }
    }

    private void handleSearch(User user, Publisher publisher, Map<String, AdvertiserInfo> advertiserInfo) {

        Query query = generateQuery(user);

        Auction auction = publisher.runAuction(query);

        handleTransition(user, handleImpression(query, auction, user,advertiserInfo) );
    }

    private boolean handleImpression(Query query, Auction auction, User user, Map<String, AdvertiserInfo> advertiserInfo) {

        fireQueryIssued(query);

        boolean converted = false;

        boolean clicking = true;   

        //TODO: grab this value
        double continuationProbability = 0.0;

        Ranking ranking = auction.getRanking();

        Pricing pricing = auction.getPricing();

        for(int i = 0; i < ranking.size(); i++) {

            AdLink ad = ranking.get(i);

            fireAdViewed(query, ad, i+1);

            if ( clicking ) {
                double clickProbability = calculateClickProbability(user,ad);
                if(random.nextDouble() < clickProbability) {
                	fireAdClicked(query, ad, i+1, pricing.getPrice(ad));
                	double conversionProbability = calculateConversionProbability(query, ad,  advertiserInfo);
                	if(random.nextDouble() < conversionProbability) {
                		fireAdConverted(query, ad, i+1, calculateSalesProfit(ad, advertiserInfo));
                		converted = true;
                		break;
                	}
                }
            }
            if(random.nextDouble()>continuationProbability)
            	break;
        }

        return converted;
    }

    private double calculateSalesProfit(AdLink ad, Map<String, AdvertiserInfo> advertiserInfo) {
		double salesProfit;
		AdvertiserInfo AI = advertiserInfo.get(ad.getAdvertiser());
		if(AI.getManufacturerSpecialty().equals(ad.getProduct().getManufacturer()))
			salesProfit = AI.getManufacturerBonus()*catalog.getSalesProfit(ad.getProduct());
		else
			salesProfit = catalog.getSalesProfit(ad.getProduct());
		return salesProfit;
		
	}



	private double calculateConversionProbability(Query query, AdLink ad, Map<String, AdvertiserInfo> advertiserInfo) {
		// TODO Auto-generated method stub
		return 0;
	}


	private double calculateClickProbability(User user, AdLink ad) {
		//TODO: THIS
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
    	Query query = new Query();
    	if(user.state.equals(QueryState.INFORMATIONAL_SEARCH)) {
    		double temp = random.nextDouble();
    		if(temp <(double)(1/3)) {
        		query.setComponent(null);
        		query.setManufacturer(null);
    		}
    		else if(temp <(double)(1/2)) {
    			query.setComponent(user.product.getComponent());
    			query.setManufacturer(null);
    		}
    		else if(temp < (double)(2/3)) {
    			query.setManufacturer(user.product.getManufacturer());
    			query.setComponent(null);
    		}
    		else {
        		query.setComponent(user.product.getComponent());
        		query.setManufacturer(user.product.getManufacturer());
    		}
    	}
    	else if(user.state.equals(QueryState.FOCUS_LEVEL_ZERO)) {
    		query.setComponent(null);
    		query.setManufacturer(null);
    	}
    	else if(user.state.equals(QueryState.FOCUS_LEVEL_ONE)) {
    		if(random.nextDouble() < .5) {
    			query.setComponent(user.product.getComponent());
    			query.setManufacturer(null);
    		}
    		else {
    			query.setManufacturer(user.product.getManufacturer());
    			query.setComponent(null);
    		}
    	}
    	else if(user.state.equals(QueryState.FOCUS_LEVEL_TWO)) {
    		query.setComponent(user.product.getComponent());
    		query.setManufacturer(user.product.getManufacturer());
    	}
        return query;
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

    private void fireAdViewed(Query query, AdLink ad, int slot) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).viewed(query,ad,slot);
        }
    }

    private void fireAdClicked(Query query, AdLink ad, int slot, double cpc) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).clicked(query,ad,slot,cpc);
        }
    }

    private void fireAdConverted(Query query, AdLink ad, int slot, double salesProfit) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).converted(query,ad,slot,salesProfit);
        }
    }


    private static class User {
        public QueryState state;
        public Product product;
    }
    
    
    
}
