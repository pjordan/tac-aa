package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.SalesAnalyst;

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

    private UserClickModel userClickModel;

    private SalesAnalyst salesAnalyst;

    public DefaultUserViewManager(RetailCatalog catalog, SalesAnalyst salesAnalyst, Map<String, AdvertiserInfo> advertiserInfo) {
        this(catalog, salesAnalyst, advertiserInfo, new Random());
    }

    public DefaultUserViewManager(RetailCatalog catalog, SalesAnalyst salesAnalyst, Map<String, AdvertiserInfo> advertiserInfo, Random random) {
        this.catalog = catalog;
        this.random = random;
        this.salesAnalyst = salesAnalyst;
        this.advertiserInfo = advertiserInfo;
        listeners = new ArrayList<UserEventListener>();
    }


    public void nextTimeUnit(int timeUnit) {
        
    }

    public boolean processImpression(User user, Query query, Auction auction) {
        fireQueryIssued(query);

        boolean converted = false;
        boolean clicking = true;


        // Grab the continuation probability from the user click model.
        double continuationProbability = 1.0;
        if(userClickModel!=null) {
            int queryIndex = userClickModel.queryIndex(query);
            if(queryIndex>=0) {
                continuationProbability = userClickModel.getContinuationProbability(queryIndex);
            }
        }

        Ranking ranking = auction.getRanking();
        Pricing pricing = auction.getPricing();

        // Users will view all ads, but may only click on some.
        for(int i = 0; i < ranking.size(); i++) {

            AdLink ad = ranking.get(i);

            fireAdViewed(query, ad, i+1);

            // If the user is still considering clicks, process the attempt
            if ( clicking ) {

                double clickProbability = calculateClickProbability(user, query, ad);


                if(random.nextDouble() <= clickProbability) {
                    // Users has clicked on the ad

                    fireAdClicked(query, ad, i+1, pricing.getPrice(ad));

                    double conversionProbability = calculateConversionProbability(user, query, ad,  advertiserInfo.get(ad.getAdvertiser()));

                    if(random.nextDouble() <= conversionProbability) {
                        // User has converted and will no longer click

                        fireAdConverted(query, ad, i+1, calculateSalesProfit(user, ad, advertiserInfo.get(ad.getAdvertiser())));

                        converted = true;
                        clicking = false;
                	}
                }
            }

            if(random.nextDouble()>continuationProbability) {
            	clicking = false;
            }
        }

        return converted;
    }

    private double calculateConversionProbability(User user, Query query, AdLink ad, AdvertiserInfo advertiserInfo) {
		double sales = salesAnalyst.getRecentConversions(ad.getAdvertiser());
        double criticalSales = advertiserInfo.getDistributionCapacity();

        double probability = advertiserInfo.getFocusEffects(query.getType())*Math.pow(advertiserInfo.getDecayRate(), Math.max(0.0, sales-criticalSales));

        if(user.getProduct().getComponent().equals(advertiserInfo.getComponentSpecialty())) {
            probability = modifyOdds(probability,1.0+advertiserInfo.getComponentBonus());
        }


        return probability;
    }


	private double calculateClickProbability(User user, Query query, AdLink ad) {
        int advertiserIndex = userClickModel.advertiserIndex(ad.getAdvertiser());
        int queryIndex = userClickModel.queryIndex(query);

        double probability = 0.0;

        if(advertiserIndex >= 0 && queryIndex >=0 ) {
            probability = userClickModel.getAdvertiserEffect(queryIndex, advertiserIndex);

            if(!ad.isGeneric()) {
                if(user.getProduct().equals(ad.getProduct())) {
                    probability = modifyOdds(probability, 1+advertiserInfo.get(ad.getAdvertiser()).getTargetEffect());
                } else {
                    probability = modifyOdds(probability, 1.0/(1+advertiserInfo.get(ad.getAdvertiser()).getTargetEffect()/2.0));
                }
            }
        }

        return probability;
    }

    private double modifyOdds(double probability, double effect) {
        return probability * effect / (effect * probability + (1.0 - probability));
    }

    private double calculateSalesProfit(User user, AdLink ad, AdvertiserInfo advertiserInfo) {
		double salesProfit = catalog.getSalesProfit(user.getProduct());

        if(advertiserInfo.getManufacturerSpecialty().equals(user.getProduct().getManufacturer()))
			salesProfit *= advertiserInfo.getManufacturerBonus();

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


    public UserClickModel getUserClickModel() {
        return userClickModel;
    }

    public void setUserClickModel(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;
    }
}
