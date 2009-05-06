package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.*;

import java.util.logging.Logger;
import static edu.umich.eecs.tac.auction.AuctionUtils.*;
import edu.umich.eecs.tac.util.config.ConfigProxy;

/**
 * @author Patrick Jordan, Lee Callender
 */

// TODO: Discuss/Resolve bids that are NaN, discuss tie-breakers
public class LahaiePennockAuctionFactory implements AuctionFactory {

	private BidManager bidManager;

	private PublisherInfo publisherInfo;

	private SlotInfo slotInfo;

	private ReserveInfo reserveInfo;

	private Logger log = Logger.getLogger(LahaiePennockAuctionFactory.class
			.getName());

	public Auction runAuction(Query query) {
		String[] advertisers = bidManager.advertisers().toArray(new String[0]);
		double[] qualityScores = new double[advertisers.length];
		double[] bids = new double[advertisers.length];
		double[] scores = new double[advertisers.length];
		double[] weight = new double[advertisers.length];
		boolean[] promoted = new boolean[advertisers.length];
		AdLink[] ads = new AdLink[advertisers.length];
		int[] indices = new int[advertisers.length];
		double[] cpc = new double[advertisers.length];

		for (int i = 0; i < advertisers.length; i++) {
			bids[i] = bidManager.getBid(advertisers[i], query);
			qualityScores[i] = bidManager.getQualityScore(advertisers[i], query);
			ads[i] = bidManager.getAdLink(advertisers[i], query);
			weight[i] = Math.pow(qualityScores[i], publisherInfo.getSquashingParameter());
			scores[i] = weight[i] * bids[i];
			indices[i] = i;
			// log.finest("Advertiser: "+advertisers[i]+"\tScore: "+scores[i]);
		}

		// This currently runs for an infinite loop if scores are NaN
		hardSort(scores, indices);

		generalizedSecondPrice(indices, weight, bids, cpc, promoted, slotInfo
				.getPromotedSlots(), reserveInfo.getPromotedReserve(), slotInfo
				.getRegularSlots(), reserveInfo.getRegularReserve());

		Ranking ranking = new Ranking();
		Pricing pricing = new Pricing();

		for (int i = 0; i < indices.length && i < slotInfo.getRegularSlots(); i++) {
			if (ads[indices[i]] != null && !Double.isNaN(cpc[indices[i]])) {
				AdLink ad = ads[indices[i]];
				double price = cpc[indices[i]];

				pricing.setPrice(ad, price);

				ranking.add(ad, promoted[indices[i]]);
			}
		}

		ranking.lock();
		pricing.lock();

		Auction auction = new Auction();
		auction.setQuery(query);
		auction.setPricing(pricing);
		auction.setRanking(ranking);

		auction.lock();

		return auction;
	}

	public void configure(ConfigProxy configProxy) {

	}

	public BidManager getBidManager() {
		return bidManager;
	}

	public void setBidManager(BidManager bidManager) {
		this.bidManager = bidManager;
	}

	public PublisherInfo getPublisherInfo() {
		return publisherInfo;
	}

	public void setPublisherInfo(PublisherInfo publisherInfo) {
		this.publisherInfo = publisherInfo;
	}

	public SlotInfo getSlotInfo() {
		return slotInfo;
	}

	public void setSlotInfo(SlotInfo slotInfo) {
		this.slotInfo = slotInfo;
	}

	public ReserveInfo getReserveInfo() {
		return reserveInfo;
	}

	public void setReserveInfo(ReserveInfo reserveInfo) {
		this.reserveInfo = reserveInfo;
	}
}
