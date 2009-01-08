package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;

/**
 * @author Patrick Jordan
 */
public class UserEventSupportTest {
    private UserEventSupport support;
    private UserEventSupport emptySupport;
    private UserEventListener listener;

    @Before
    public void setup() {
        support = new UserEventSupport();
        listener = new SimpleUserEventListener();
        support.addUserEventListener(listener);
        emptySupport = new UserEventSupport();
    }
    @Test
    public void testConstructor() {
        assertNotNull(new UserEventSupport());
    }

    @Test
    public void testAddListener() {
        UserEventSupport support = new UserEventSupport();
        
        assertFalse(support.containsUserEventListener(listener));
        support.addUserEventListener(listener);
        assertTrue(support.containsUserEventListener(listener));
    }

    @Test
    public void testRemoveListener() {
        assertTrue(support.containsUserEventListener(listener));

        support.removeUserEventListener(listener);

        assertFalse(support.containsUserEventListener(listener));
    }

    @Test(expected=RuntimeException.class)
    public void testFireQueryIssued() {
        emptySupport.fireQueryIssued(null);
        support.fireQueryIssued(null);
    }

    @Test(expected=RuntimeException.class)
    public void testFireAdViewed() {
        emptySupport.fireAdViewed(null, null, 0);
        support.fireAdViewed(null, null, 0);
    }

    @Test(expected=RuntimeException.class)
    public void testFfireAdClicked() {
        emptySupport.fireAdClicked(null, null, 0, 0);
        support.fireAdClicked(null, null, 0, 0);
    }

    @Test(expected=RuntimeException.class)
    public void testFireAdConverted() {
        emptySupport.fireAdConverted(null, null, 0, 0);
        support.fireAdConverted(null, null, 0, 0);
    }

    public class SimpleUserEventListener implements UserEventListener {
        public void queryIssued(Query query) {
            throw new RuntimeException("Query issued");
        }

        public void viewed(Query query, Ad ad, int slot, String advertiser) {
            throw new RuntimeException("Ad viewed");
        }

        public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
            throw new RuntimeException("Ad clicked");
        }

        public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
            throw new RuntimeException("Ad converted");
        }
    }
}
