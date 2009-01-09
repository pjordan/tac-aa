package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.RecentConversionsTracker;

import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import static edu.umich.eecs.tac.user.UserUtils.*;

/**
 * @author Patrick Jordan, Ben Cassell
 */
public class DefaultUserViewManager implements UserViewManager {
    private Logger log = Logger.getLogger(DefaultUserViewManager.class.getName());

    private UserEventSupport eventSupport;

    private Map<String, AdvertiserInfo> advertiserInfo;

    private RetailCatalog catalog;

    private Random random;

    private UserClickModel userClickModel;

    private RecentConversionsTracker recentConversionsTracker;

    public DefaultUserViewManager(RetailCatalog catalog, RecentConversionsTracker recentConversionsTracker, Map<String, AdvertiserInfo> advertiserInfo) {
        this(catalog, recentConversionsTracker, advertiserInfo, new Random());
    }

    public DefaultUserViewManager(RetailCatalog catalog, RecentConversionsTracker recentConversionsTracker, Map<String, AdvertiserInfo> advertiserInfo, Random random) {
        if(catalog==null) {
            throw new NullPointerException("Retail catalog cannot be null");
        }

        if(recentConversionsTracker==null) {
            throw new NullPointerException("Recent conversions tracker cannot be null");
        }

        if(advertiserInfo==null) {
            throw new NullPointerException("Advertiser information cannot be null");
        }

        if(random==null) {
            throw new NullPointerException("Random generator cannot be null");
        }
        
        this.catalog = catalog;
        this.random = random;
        this.recentConversionsTracker = recentConversionsTracker;
        this.advertiserInfo = advertiserInfo;
        eventSupport = new UserEventSupport();
    }


    public void nextTimeUnit(int timeUnit) {

    }

    public boolean processImpression(User user, Query query, Auction auction) {
        fireQueryIssued(query);

        boolean converted = false;
        boolean clicking = true;

        // Grab the continuation probability from the user click model.
        double continuationProbability = 0.0;

        int queryIndex = userClickModel.queryIndex(query);

        if (queryIndex < 0) {
            log.warning(String.format("Query: %s does not have a click model.",query));
        } else {
            continuationProbability = userClickModel.getContinuationProbability(queryIndex);
        }
        
        Ranking ranking = auction.getRanking();
        Pricing pricing = auction.getPricing();

        // Users will view all ads, but may only click on some.
        for (int i = 0; i < ranking.size(); i++) {

            AdLink ad = ranking.get(i);

            fireAdViewed(query, ad, i + 1);

            // If the user is still considering clicks, process the attempt
            if (clicking) {

                AdvertiserInfo info = advertiserInfo.get(ad.getAdvertiser());

                double clickProbability = calculateClickProbability(user, ad, info, findAdvertiserEffect(query, ad, userClickModel));


                if (random.nextDouble() <= clickProbability) {
                    // Users has clicked on the ad

                    fireAdClicked(query, ad, i + 1, pricing.getPrice(ad));

                    double conversionProbability = calculateConversionProbability(user, query, info, recentConversionsTracker.getRecentConversions(ad.getAdvertiser()));

                    if (random.nextDouble() <= conversionProbability) {
                        // User has converted and will no longer click

                        double salesProfit = catalog.getSalesProfit(user.getProduct());

                        fireAdConverted(query, ad, i + 1, modifySalesProfitForManufacturerSpecialty(user, info.getManufacturerSpecialty(), info.getManufacturerBonus(), salesProfit));

                        converted = true;
                        clicking = false;
                    }
                }
            }

            if (random.nextDouble() > continuationProbability) {
                clicking = false;
            }
        }

        return converted;
    }

    public boolean addUserEventListener(UserEventListener listener) {
        return eventSupport.addUserEventListener(listener);
    }

    public boolean containsUserEventListener(UserEventListener listener) {
        return eventSupport.containsUserEventListener(listener);
    }

    public boolean removeUserEventListener(UserEventListener listener) {
        return eventSupport.removeUserEventListener(listener);
    }

    private void fireQueryIssued(Query query) {
        eventSupport.fireQueryIssued(query);
    }

    private void fireAdViewed(Query query, AdLink ad, int slot) {
        eventSupport.fireAdViewed(query, ad, slot);
    }

    private void fireAdClicked(Query query, AdLink ad, int slot, double cpc) {
        eventSupport.fireAdClicked(query, ad, slot, cpc);
    }

    private void fireAdConverted(Query query, AdLink ad, int slot, double salesProfit) {
        eventSupport.fireAdConverted(query, ad, slot, salesProfit);
    }

    public UserClickModel getUserClickModel() {
        return userClickModel;
    }

    public void setUserClickModel(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;
    }
}
