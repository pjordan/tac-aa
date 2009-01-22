package edu.umich.eecs.tac.auction;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.AdLink;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Ad;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrick Jordan
 */
public class BidTrackerImplTest {

	@Test
	public void testConstructor() {
		BidTrackerImpl bidTracker = new BidTrackerImpl();

		assertNotNull(bidTracker);
	}

	@Test
	public void testAddAdvertiser() {
		BidTrackerImpl bidTracker = new BidTrackerImpl();

		String advertiser = "Alice";

		assertEquals(bidTracker.size(), 0, 0);
		bidTracker.addAdvertiser(advertiser);
		assertEquals(bidTracker.size(), 1, 0);
		bidTracker.addAdvertiser(advertiser);
		assertEquals(bidTracker.size(), 1, 0);

		for (int i = 0; i < 8; i++) {
			bidTracker.addAdvertiser("" + i);
			assertEquals(bidTracker.size(), i + 2, 0);
		}
	}

	@Test
	public void testInitializeQuerySpace() {
		BidTrackerImpl bidTracker = new BidTrackerImpl();

		Set<Query> querySpace = new HashSet<Query>();

		bidTracker.initializeQuerySpace(querySpace);

		bidTracker.initializeQuerySpace(querySpace);

	}

	@Test
	public void testGetDailySpendLimits() {
		BidTrackerImpl bidTracker = new BidTrackerImpl();

		String advertiser = "alice";
		Query query = new Query();

		assertEquals(bidTracker.getDailySpendLimit(advertiser),
				Double.POSITIVE_INFINITY, 0.0);
		assertEquals(bidTracker.getDailySpendLimit(advertiser, query),
				Double.POSITIVE_INFINITY, 0.0);

		bidTracker.addAdvertiser(advertiser);

		assertEquals(bidTracker.getDailySpendLimit(advertiser),
				Double.POSITIVE_INFINITY, 0.0);
		assertEquals(bidTracker.getDailySpendLimit(advertiser),
				Double.POSITIVE_INFINITY, 0.0);

		advertiser = "bob";
		bidTracker.addAdvertiser(advertiser);
		assertEquals(bidTracker.getDailySpendLimit(advertiser, query),
				Double.POSITIVE_INFINITY, 0.0);
		assertEquals(bidTracker.getDailySpendLimit(advertiser, query),
				Double.POSITIVE_INFINITY, 0.0);
	}

	@Test
	public void testGetBids() {
		BidTrackerImpl bidTracker = new BidTrackerImpl();

		String advertiser = "alice";
		Query query = new Query();

		assertEquals(bidTracker.getBid(advertiser, query), 0.0, 0.0);

		bidTracker.addAdvertiser(advertiser);

		assertEquals(bidTracker.getBid(advertiser, query), 0.0, 0.0);
		assertEquals(bidTracker.getBid(advertiser, query), 0.0, 0.0);
	}

	@Test
	public void testGetAdLink() {
		BidTrackerImpl bidTracker = new BidTrackerImpl();

		String advertiser = "alice";
		Query query = new Query();

		AdLink adLink = new AdLink((Ad) null, advertiser);

		assertEquals(bidTracker.getAdLink(advertiser, query), adLink);

		bidTracker.addAdvertiser(advertiser);

		assertEquals(bidTracker.getAdLink(advertiser, query), adLink);
		assertEquals(bidTracker.getAdLink(advertiser, query), adLink);
	}

	@Test
	public void testUpdateBids() {
		BidTrackerImpl bidTracker = new BidTrackerImpl();

		Set<Query> querySpace = new HashSet<Query>();
		Query query1 = new Query("1", "");
		Query query2 = new Query("2", "");
		Query query3 = new Query("3", "");

		querySpace.add(query1);
		querySpace.add(query2);
		querySpace.add(query3);

		bidTracker.initializeQuerySpace(querySpace);

		String advertiser1 = "alice";
		BidBundle bundle1 = new BidBundle();

		bidTracker.updateBids(advertiser1, bundle1);
		bidTracker.updateBids(advertiser1, bundle1);

		String advertiser2 = "bob";
		BidBundle bundle2 = new BidBundle();

		bundle2.setBid(query1, 1.0);
		bundle2.setCampaignDailySpendLimit(1.0);

		bundle2.setAd(query2, new Ad());

		bundle2.setDailyLimit(query3, 1.0);

		bidTracker.addAdvertiser(advertiser2);
		bidTracker.updateBids(advertiser2, bundle2);

		assertEquals(bidTracker.getBid(advertiser2, query1), 1.0, 0.0);
		assertEquals(bidTracker.getAdLink(advertiser2, query2), new AdLink(
				(Ad) null, advertiser2));
		assertEquals(bidTracker.getDailySpendLimit(advertiser2, query3), 1.0,
				0.0);

		bidTracker.updateBids(advertiser2, bundle2);
		assertEquals(bidTracker.getBid(advertiser2, query1), 1.0, 0.0);
		assertEquals(bidTracker.getAdLink(advertiser2, query2), new AdLink(
				(Ad) null, advertiser2));
		assertEquals(bidTracker.getDailySpendLimit(advertiser2, query3), 1.0,
				0.0);
	}

}
