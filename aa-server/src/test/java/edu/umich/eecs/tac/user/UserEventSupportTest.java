package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.Mockery;
import org.jmock.Expectations;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.AdLink;

/**
 * @author Patrick Jordan
 */
@RunWith(JMock.class)
public class UserEventSupportTest {
    private Mockery context;

    private UserEventListener listener;
    private Query query;
    private String advertiser;
    private AdLink ad;
    private double cpc;
    private int slot;
    private double salesProfit;

    
    @Before
    public void setup() {
        context = new JUnit4Mockery();
        listener = context.mock(UserEventListener.class);
        query = new Query();
        advertiser = "alice";
        ad = new AdLink(new Ad(), advertiser);
        cpc = 1.0;
        slot = 2;
        salesProfit = 3.0;
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
        UserEventSupport support = new UserEventSupport();

        support.addUserEventListener(listener);
        
        assertTrue(support.containsUserEventListener(listener));

        support.removeUserEventListener(listener);

        assertFalse(support.containsUserEventListener(listener));
    }


    @Test
    public void testFireQueryIssued() {
        UserEventSupport support = new UserEventSupport();

        support.addUserEventListener(listener);
        
        context.checking(new Expectations() {{
            oneOf(listener).queryIssued(null);
        }});

        support.fireQueryIssued(null);
    }


    @Test
    public void testFireAdViewed() {

        UserEventSupport support = new UserEventSupport();

        support.addUserEventListener(listener);

        context.checking(new Expectations() {{
            oneOf(listener).viewed(query,ad,slot,advertiser, false);
        }});

        support.fireAdViewed(query, ad, slot, false);
    }


    @Test
    public void testFfireAdClicked() {

        UserEventSupport support = new UserEventSupport();

        support.addUserEventListener(listener);

        context.checking(new Expectations() {{
            oneOf(listener).clicked(query,ad,slot,cpc,advertiser);
        }});

        support.fireAdClicked(query, ad, slot, cpc);
    }

    @Test
    public void testFireAdConverted() {
        UserEventSupport support = new UserEventSupport();


        support.addUserEventListener(listener);

        context.checking(new Expectations() {{
            oneOf(listener).converted(query,ad,slot,salesProfit,advertiser);
        }});

        support.fireAdConverted(query, ad, slot, salesProfit);
    }
}
