package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * @author Patrick Jordan, Ben Cassell
 */
public class DefaultUserViewManager implements UserViewManager {
    private List<UserEventListener> listeners;

    private Map<String, AdvertiserInfo> advertiserInfo;

    private RetailCatalog catalog;

    private Random random;

    public DefaultUserViewManager(RetailCatalog catalog, Map<String, AdvertiserInfo> advertiserInfo) {
        this(catalog, advertiserInfo, new Random());
    }

    public DefaultUserViewManager(RetailCatalog catalog, Map<String, AdvertiserInfo> advertiserInfo, Random random) {
        this.catalog = catalog;
        this.random = random;
        this.advertiserInfo = advertiserInfo;
        listeners = new ArrayList<UserEventListener>();
    }


    public void nextTimeUnit(int timeUnit) {
        
    }

    public boolean processImpression(User user, Query query, Auction auction) {
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

    private double calculateConversionProbability(Query query, AdLink ad, Map<String, AdvertiserInfo> advertiserInfo) {
		// TODO Auto-generated method stub
		return 0;
	}


	private double calculateClickProbability(User user, AdLink ad) {
		//TODO: THIS
        double probability;

        return 0;
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

    public boolean addUserEventListener(UserEventListener listener) {
        return listeners.add(listener);
    }

    public boolean containsUserEventListener(UserEventListener listener) {
        return listeners.contains(listener);
    }

    public boolean removeUserEventListener(UserEventListener listener) {
        return listeners.remove(listener);
    }

    private void fireQueryIssued(Query query) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).queryIssued(query);
        }
    }

    private void fireAdViewed(Query query, AdLink ad, int slot) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).viewed(query,ad,slot,ad.getAdvertiser());
        }
    }

    private void fireAdClicked(Query query, AdLink ad, int slot, double cpc) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).clicked(query,ad,slot,cpc,ad.getAdvertiser());
        }
    }

    private void fireAdConverted(Query query, AdLink ad, int slot, double salesProfit) {
        for(int i = 0; i < listeners.size(); i++) {
            listeners.get(i).converted(query,ad,slot,salesProfit,ad.getAdvertiser());
        }
    }
}
