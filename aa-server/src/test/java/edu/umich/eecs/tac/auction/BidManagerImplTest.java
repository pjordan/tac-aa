package edu.umich.eecs.tac.auction;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import edu.umich.eecs.tac.props.*;

import java.util.Set;
import java.util.HashSet;


/**
 * @author Patrick Jordan, Lee Callender
 */
public class BidManagerImplTest {
    private String alice;
    private String bob;
    private String eve;

    private Query q1;
    private Query q2;
    private Query q3;

    private double q1Bid;
    private double q2Bid;
    private double q3Bid;

    private Ad adAlice;
    private Ad adBob;
    private Ad adEve;

    private BidTracker bidTracker;
    private SpendTracker spendTracker;
    private UserClickModel userClickModel;
    private BidManagerImpl bidManager;

    @Before
    public void setUp() {
        alice = "alice";
        bob = "bob";
        eve = "eve";

        q1 = new Query("1","");
        q2 = new Query("2","");
        q3 = new Query("3","");

        q1Bid = 0.50;
        q2Bid = 0.75;
        q3Bid = 1.00;

        adAlice = new Ad();
        adBob = new Ad(new Product("bob",""));
        adEve = new Ad(new Product("eve",""));

        bidTracker = new SimpleBidTracker();
        spendTracker = new SimpleSpendTracker();


        userClickModel = new UserClickModel(new Query[] {q1,q2,q3}, new String[] {alice, bob, eve});

        int[] qIndices = new int[] {userClickModel.queryIndex(q1), userClickModel.queryIndex(q2), userClickModel.queryIndex(q3) };
        int[] aIndices = new int[] {userClickModel.advertiserIndex(alice), userClickModel.advertiserIndex(bob), userClickModel.advertiserIndex(eve) };

        for(int i = 0; i < qIndices.length; i++) {
            userClickModel.setAdvertiserEffect(i,0,0.5);
            userClickModel.setAdvertiserEffect(i,1,0.5*0.5);
            userClickModel.setAdvertiserEffect(i,2,0.5*0.5*0.5);
        }

        bidManager = new BidManagerImpl(userClickModel, bidTracker, spendTracker);
    }

    @Test
    public void testConstructor() {
        assertNotNull(bidManager);
    }

    @Test
    public void testGetBid() {
        // Normal bid
        assertEquals(bidManager.getBid(alice,q1),q1Bid);

        // Query overspent bid
        assertEquals(bidManager.getBid(alice,q2),0.0);

        // Global overspent bid
        assertEquals(bidManager.getBid(alice,q3),0.0);

        // Normal bid
        assertEquals(bidManager.getBid(bob,q1),q1Bid);

        // Global overspent bid
        assertEquals(bidManager.getBid(bob,q2),0.0);

        // Query overspent bid
        assertEquals(bidManager.getBid(bob,q3),0.0);
    }

    @Test
    public void testNextTimeUnit() {

        bidManager.updateBids(alice, new BidBundle());
        bidManager.nextTimeUnit(0);
    }

    @Test
    public void testAddAdvertisers() {
        bidManager.addAdvertiser(alice);
        bidManager.addAdvertiser(bob);
        bidManager.addAdvertiser(eve);

        Set<String> advertisers = new HashSet<String>();
        advertisers.add(alice);
        advertisers.add(bob);
        advertisers.add(eve);

        assertEquals(advertisers,bidManager.advertisers());

    }

    @Test
    public void testAdLink() {

        assertEquals(new AdLink(adAlice.getProduct(),alice),bidManager.getAdLink(alice,q1));
        assertEquals(new AdLink(adBob.getProduct(),bob),bidManager.getAdLink(bob,q2));
        assertEquals(new AdLink(adEve.getProduct(),eve),bidManager.getAdLink(eve,q3));
    }

    @Test
    public void testGetQualityScore() {
        assertEquals(bidManager.getQualityScore(alice,q1),0.5);
        assertEquals(bidManager.getQualityScore(bob,q1),0.5*0.5);
        assertEquals(bidManager.getQualityScore(eve,q1),0.5*0.5*0.5);

        assertEquals(bidManager.getQualityScore("nobody",q1),1.0);
        assertEquals(bidManager.getQualityScore(alice,new Query("z","")),1.0);
    }

    private class SimpleBidTracker implements BidTracker {

        public void addAdvertiser(String advertiser) {
        }

        public void initializeQuerySpace(Set<Query> space) {
        }

        public double getDailySpendLimit(String advertiser) {
            if(alice.equals(advertiser))
               return 1.00;
            else if(bob.equals(advertiser))
               return 0.75;
            else
               return Double.POSITIVE_INFINITY;
        }

        public double getBid(String advertiser, Query query) {
            if(q1.equals(query))
                return q1Bid;
            else if(q2.equals(query))
                return q2Bid;
            else if(q3.equals(query))
                return q3Bid;
            else
                return 0.0;
        }

        public double getDailySpendLimit(String advertiser, Query query) {
            if(alice.equals(advertiser))
               return 0.75;
            else if(bob.equals(advertiser))
               return 1.00;
            else
               return Double.POSITIVE_INFINITY;

        }

        public AdLink getAdLink(String advertiser, Query query) {
            if(alice.equals(advertiser))
               return new AdLink(adAlice.getProduct(),alice);
            else if(bob.equals(advertiser))
               return new AdLink(adBob.getProduct(),bob);
            else if(eve.equals(advertiser))
               return new AdLink(adEve.getProduct(),eve);
            else
               return null;
        }

        public void updateBids(String advertiser, BidBundle bundle) {
        }

        public int size() {
            return 0;
        }
    }

    private class SimpleSpendTracker implements SpendTracker {

        public void addAdvertiser(String advertiser) {
        }

        public double getDailyCost(String advertiser) {
            return 0.0;
        }

        public double getDailyCost(String advertiser, Query query) {
            return 0.0;
        }

        public void reset() {
        }

        public int size() {
            return 0;
        }

        public void addCost(String advertiser, Query query, double cost) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
