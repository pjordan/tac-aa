package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.RecentConversionsTracker;

/**
 * @author Patrick Jordan
 */
public class DefaultUserViewManagerTest {
    private Random random;

    private RetailCatalog retailCatalog;

    private UserClickModel userClickModel;

    private RecentConversionsTracker recentConversionsTracker;

    private DefaultUserViewManager userViewManager;

    private Map<String, AdvertiserInfo> advertiserInfo;

    private Query query;

    private String alice;

    private String bob;

    private String eve;

    private Product product;

    private String manufacturer;

    private String component;

    @Before
    public void setup() {
        random = new Random(103);

        alice = "alice";
        bob = "bob";
        eve = "eve";

        manufacturer = "man";

        component = "com";

        product = new Product(manufacturer,component);

        query = new Query(manufacturer,component);
        
        retailCatalog = new RetailCatalog();

        retailCatalog.addProduct(product);

        retailCatalog.setSalesProfit(product,1.0);




        recentConversionsTracker = new SimpleRecentConversionsTracker();

        AdvertiserInfo aliceInfo = new AdvertiserInfo();

        aliceInfo.setDecayRate(0.0);

        AdvertiserInfo bobInfo = new AdvertiserInfo();

        bobInfo.setDecayRate(0.0);

        AdvertiserInfo eveInfo = new AdvertiserInfo();

        eveInfo.setDecayRate(1.0);
        eveInfo.setFocusEffects(query.getType(),1.0);
        eveInfo.setManufacturerSpecialty(manufacturer);
        
        advertiserInfo = new HashMap<String,AdvertiserInfo>();
        advertiserInfo.put(alice,aliceInfo);
        advertiserInfo.put(bob,bobInfo);
        advertiserInfo.put(eve,eveInfo);




        userClickModel = new UserClickModel(new Query[] {query}, new String[] {alice, bob, eve});
        userClickModel.setContinuationProbability(0,0.75);
        userClickModel.setAdvertiserEffect(0,0,0.0);
        userClickModel.setAdvertiserEffect(0,1,1.0);
        userClickModel.setAdvertiserEffect(0,2,1.0);

        userViewManager = new DefaultUserViewManager(retailCatalog, recentConversionsTracker, advertiserInfo, random);
        userViewManager.setUserClickModel(userClickModel);        
    }

    @Test
    public void testConstructors() {
        assertNotNull(userViewManager);
        assertNotNull(new DefaultUserViewManager(retailCatalog, recentConversionsTracker, advertiserInfo));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorRetailCatalogNull() {
        new DefaultUserViewManager(null, recentConversionsTracker, advertiserInfo);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorTrackerNull() {
        new DefaultUserViewManager(retailCatalog, null, advertiserInfo);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorInfoNull() {
        new DefaultUserViewManager(retailCatalog, recentConversionsTracker, null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorRandomNull() {
        new DefaultUserViewManager(retailCatalog, recentConversionsTracker, advertiserInfo, null);
    }

    @Test
    public void testUserClickModel() {
        assertSame(userViewManager.getUserClickModel(), userClickModel);
    }

    @Test
    public void testUserEventSupport() {
        UserEventListener listener = new SimpleUserEventListener();

        assertTrue(userViewManager.addUserEventListener(listener));
        assertTrue(userViewManager.containsUserEventListener(listener));

        
        
        assertTrue(userViewManager.removeUserEventListener(listener));
        assertFalse(userViewManager.containsUserEventListener(listener));
    }

    @Test
    public void testProcessImpression() {
        User user = new User(QueryState.FOCUS_LEVEL_TWO, product);

        AdLink aliceAd = new AdLink(product, alice);
        AdLink bobAd = new AdLink(product, bob);
        AdLink eveAd = new AdLink(product, eve);

        Pricing pricing = new Pricing();
        pricing.setPrice(aliceAd, 1.0);
        pricing.setPrice(bobAd, 0.5);
        pricing.setPrice(eveAd, 0.25);

        Ranking ranking = new Ranking();
        ranking.add(aliceAd);
        ranking.add(bobAd);
        ranking.add(eveAd);

        Auction auction = new Auction();
        auction.setQuery(query);
        auction.setPricing(pricing);
        auction.setRanking(ranking);

        userViewManager.nextTimeUnit(0);
        
        assertTrue(userViewManager.processImpression(user, query, auction));

        assertFalse(userViewManager.processImpression(user, new Query(), auction));
    }
    
    private class SimpleRecentConversionsTracker implements RecentConversionsTracker {
        public double getRecentConversions(String name) {
            return 0;
        }
    }

    public class SimpleUserEventListener implements UserEventListener {
        public void queryIssued(Query query) {
        }

        public void viewed(Query query, Ad ad, int slot, String advertiser) {
        }

        public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
        }

        public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {            
        }
    }
}
