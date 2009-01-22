package edu.umich.eecs.tac.auction;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import edu.umich.eecs.tac.props.*;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrick Jordan, Lee Callender
 */
@RunWith(JMock.class)
public class BidManagerImplTest {
	private Mockery context;

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
		context = new JUnit4Mockery();

		alice = "alice";
		bob = "bob";
		eve = "eve";

		q1 = new Query("1", "");
		q2 = new Query("2", "");
		q3 = new Query("3", "");

		q1Bid = 0.50;
		q2Bid = 0.75;
		q3Bid = 1.00;

		adAlice = new Ad();
		adBob = new Ad(new Product("bob", ""));
		adEve = new Ad(new Product("eve", ""));

		bidTracker = context.mock(BidTracker.class);
		spendTracker = context.mock(SpendTracker.class);

		userClickModel = new UserClickModel(new Query[] { q1, q2, q3 },
				new String[] { alice, bob, eve });

		int[] qIndices = new int[] { userClickModel.queryIndex(q1),
				userClickModel.queryIndex(q2), userClickModel.queryIndex(q3) };
		int[] aIndices = new int[] { userClickModel.advertiserIndex(alice),
				userClickModel.advertiserIndex(bob),
				userClickModel.advertiserIndex(eve) };

		for (int i = 0; i < qIndices.length; i++) {
			userClickModel.setAdvertiserEffect(i, 0, 0.5);
			userClickModel.setAdvertiserEffect(i, 1, 0.5 * 0.5);
			userClickModel.setAdvertiserEffect(i, 2, 0.5 * 0.5 * 0.5);
		}

		bidManager = new BidManagerImpl(userClickModel, bidTracker,
				spendTracker);
	}

	@Test
	public void testConstructor() {
		assertNotNull(bidManager);
	}

	@Test(expected = NullPointerException.class)
	public void testUserClickModelNull() {
		assertNotNull(new BidManagerImpl(null, bidTracker, spendTracker));
	}

	@Test(expected = NullPointerException.class)
	public void testUserBidTrackerNull() {
		assertNotNull(new BidManagerImpl(userClickModel, null, spendTracker));
	}

	@Test(expected = NullPointerException.class)
	public void testUserSpendTrackerNull() {
		assertNotNull(new BidManagerImpl(userClickModel, bidTracker, null));
	}

	@Test
	public void testGetBid() {
		context.checking(new Expectations() {
			{
				atLeast(1).of(spendTracker).getDailyCost(alice);
				will(returnValue(0.0));
				atLeast(1).of(spendTracker).getDailyCost(alice, q1);
				will(returnValue(0.0));
				atLeast(1).of(spendTracker).getDailyCost(alice, q2);
				will(returnValue(0.0));
				atLeast(1).of(spendTracker).getDailyCost(alice, q3);
				will(returnValue(0.0));

				atLeast(1).of(spendTracker).getDailyCost(bob);
				will(returnValue(0.0));
				atLeast(1).of(spendTracker).getDailyCost(bob, q1);
				will(returnValue(0.0));
				atLeast(1).of(spendTracker).getDailyCost(bob, q2);
				will(returnValue(0.0));
				atLeast(1).of(spendTracker).getDailyCost(bob, q3);
				will(returnValue(0.0));

				atLeast(1).of(bidTracker).getBid(alice, q1);
				will(returnValue(q1Bid));
				atLeast(1).of(bidTracker).getBid(alice, q2);
				will(returnValue(q2Bid));
				atLeast(1).of(bidTracker).getBid(alice, q3);
				will(returnValue(q3Bid));
				atLeast(0).of(bidTracker).getDailySpendLimit(alice, q1);
				will(returnValue(0.75));
				atLeast(0).of(bidTracker).getDailySpendLimit(alice, q2);
				will(returnValue(0.75));
				atLeast(0).of(bidTracker).getDailySpendLimit(alice, q3);
				will(returnValue(0.75));
				atLeast(0).of(bidTracker).getDailySpendLimit(alice);
				will(returnValue(1.00));

				atLeast(1).of(bidTracker).getBid(bob, q1);
				will(returnValue(q1Bid));
				atLeast(1).of(bidTracker).getBid(bob, q2);
				will(returnValue(q2Bid));
				atLeast(1).of(bidTracker).getBid(bob, q3);
				will(returnValue(q3Bid));
				atLeast(0).of(bidTracker).getDailySpendLimit(bob, q1);
				will(returnValue(1.00));
				atLeast(0).of(bidTracker).getDailySpendLimit(bob, q2);
				will(returnValue(1.00));
				atLeast(0).of(bidTracker).getDailySpendLimit(bob, q3);
				will(returnValue(1.00));
				atLeast(0).of(bidTracker).getDailySpendLimit(bob);
				will(returnValue(0.75));

			}
		});

		// Normal bid
		assertEquals(bidManager.getBid(alice, q1), q1Bid, 0.000000001);

		// Query overspent bid
		assertEquals(bidManager.getBid(alice, q2), 0.0, 0.000000001);

		// Global overspent bid
		assertEquals(bidManager.getBid(alice, q3), 0.0, 0.000000001);

		// Normal bid
		assertEquals(bidManager.getBid(bob, q1), q1Bid, 0.000000001);

		// Global overspent bid
		assertEquals(bidManager.getBid(bob, q2), 0.0, 0.000000001);

		// Query overspent bid
		assertEquals(bidManager.getBid(bob, q3), 0.0, 0.000000001);
	}

	@Test
	public void testNextTimeUnit() {
		final BidBundle bidBundle = new BidBundle();

		context.checking(new Expectations() {
			{
				atLeast(1).of(bidTracker).updateBids(alice, bidBundle);
			}
		});

		bidManager.updateBids(alice, bidBundle);
		bidManager.nextTimeUnit(0);
	}

	@Test
	public void testAddAdvertisers() {
		context.checking(new Expectations() {
			{
				oneOf(bidTracker).addAdvertiser(alice);
				oneOf(bidTracker).addAdvertiser(bob);
				oneOf(bidTracker).addAdvertiser(eve);

				oneOf(spendTracker).addAdvertiser(alice);
				oneOf(spendTracker).addAdvertiser(bob);
				oneOf(spendTracker).addAdvertiser(eve);
			}
		});

		bidManager.addAdvertiser(alice);
		bidManager.addAdvertiser(bob);
		bidManager.addAdvertiser(eve);

		Set<String> advertisers = new HashSet<String>();
		advertisers.add(alice);
		advertisers.add(bob);
		advertisers.add(eve);

		assertEquals(advertisers, bidManager.advertisers());

	}

	@Test
	public void testAdLink() {
		final AdLink aliceLink = new AdLink(adAlice.getProduct(), alice);
		final AdLink bobLink = new AdLink(adBob.getProduct(), bob);
		final AdLink eveLink = new AdLink(adEve.getProduct(), eve);

		context.checking(new Expectations() {
			{
				oneOf(bidTracker).getAdLink(alice, q1);
				will(returnValue(aliceLink));
				oneOf(bidTracker).getAdLink(bob, q2);
				will(returnValue(bobLink));
				oneOf(bidTracker).getAdLink(eve, q3);
				will(returnValue(eveLink));
			}
		});

		assertEquals(aliceLink, bidManager.getAdLink(alice, q1));
		assertEquals(bobLink, bidManager.getAdLink(bob, q2));
		assertEquals(eveLink, bidManager.getAdLink(eve, q3));
	}

	@Test
	public void testGetQualityScore() {
		assertEquals(bidManager.getQualityScore(alice, q1), 0.5, 0.000000001);
		assertEquals(bidManager.getQualityScore(bob, q1), 0.5 * 0.5,
				0.000000001);
		assertEquals(bidManager.getQualityScore(eve, q1), 0.5 * 0.5 * 0.5,
				0.000000001);

		assertEquals(bidManager.getQualityScore("nobody", q1), 1.0, 0.000000001);
		assertEquals(bidManager.getQualityScore(alice, new Query("z", "")),
				1.0, 0.000000001);
	}
}
