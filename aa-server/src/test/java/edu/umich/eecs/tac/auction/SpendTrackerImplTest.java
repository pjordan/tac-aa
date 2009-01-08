package edu.umich.eecs.tac.auction;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.umich.eecs.tac.props.Query;

/**
 * @author Patrick Jordan
 */
public class SpendTrackerImplTest {

    @Test
    public void testConstructor() {
        SpendTrackerImpl spendTracker = new SpendTrackerImpl();

        assertNotNull(spendTracker);
    }

    @Test
    public void testAddAdvertiser() {
        SpendTrackerImpl spendTracker = new SpendTrackerImpl();

        String advertiser = "Alice";

        assertEquals(spendTracker.size(),0);
        spendTracker.addAdvertiser(advertiser);
        assertEquals(spendTracker.size(),1);
        spendTracker.addAdvertiser(advertiser);
        assertEquals(spendTracker.size(),1);


        for(int i = 0; i < 8; i++) {
            spendTracker.addAdvertiser(""+i);
            assertEquals(spendTracker.size(),i+2);
        }
    }

    @Test
    public void testAddCost() {
        SpendTrackerImpl spendTracker = new SpendTrackerImpl();

        String advertiser = "Alice";
        Query query = new Query();
        double cost = 1.0;

        spendTracker.addCost(advertiser,query,cost);


        assertEquals(spendTracker.getDailyCost(advertiser),cost);
        assertEquals(spendTracker.getDailyCost(advertiser,query),cost);
        assertEquals(spendTracker.size(),1);

        spendTracker.addCost(advertiser,query,cost);

        assertEquals(spendTracker.getDailyCost(advertiser),2*cost);
        assertEquals(spendTracker.getDailyCost(advertiser,query),2*cost);
        assertEquals(spendTracker.size(),1);

        assertEquals(spendTracker.getDailyCost("notAlice"),0.0);
        assertEquals(spendTracker.getDailyCost(advertiser,new Query("a","")),0.0);
        assertEquals(spendTracker.getDailyCost("notAlice",query),0.0);

        spendTracker.addAdvertiser("bob");
        assertEquals(spendTracker.getDailyCost("bob",query),0.0);

        spendTracker.addAdvertiser("bobbob");
        assertEquals(spendTracker.getDailyCost("bobbob"),0.0);

        spendTracker.reset();
        assertEquals(spendTracker.getDailyCost(advertiser),0.0);

        for(int i = 0; i < 8; i++) {
            spendTracker.addCost(advertiser,new Query(""+i,""),cost);
        }
    }
}
