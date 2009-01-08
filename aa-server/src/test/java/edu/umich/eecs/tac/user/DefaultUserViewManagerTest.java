package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.util.Random;
import java.util.Map;

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


    @Before
    public void setup() {
        random = new Random(100);

        retailCatalog = new RetailCatalog();

        userClickModel = new UserClickModel();
        
        recentConversionsTracker = new SimpleRecentConversionsTracker();

        userViewManager = new DefaultUserViewManager(retailCatalog, recentConversionsTracker, advertiserInfo, random);
        userViewManager.setUserClickModel(userClickModel);        
    }

    @Test
    public void testConstructors() {
        assertNotNull(userViewManager);
        assertNotNull(new DefaultUserViewManager(retailCatalog, recentConversionsTracker, advertiserInfo));
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
