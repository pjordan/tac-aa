package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.*;

import java.util.logging.Logger;

/**
 * @author Patrick Jordan, Lee Callender
 */

//TODO: Discuss/Resolve bids that are NaN, discuss tie-breakers
public class LahaiePennockAuctionFactory implements AuctionFactory {

    private BidManager bidManager;

    private double squashValue;

    private int slotLimit;

    private Logger log = Logger.getLogger(LahaiePennockAuctionFactory.class.getName());

    public Auction runAuction(Query query) {
        int nAdvertisers = bidManager.advertisers().size();
        String[] advertisers = bidManager.advertisers().toArray(new String[nAdvertisers]);
        double[] qualityScores = new double[advertisers.length];
        double[] bids = new double[advertisers.length];
        double[] scores = new double[advertisers.length];
        AdLink[] ads = new AdLink[advertisers.length];
        int[] indices = new int[advertisers.length];
        double[] cpc = new double[advertisers.length];

        for (int i = 0; i < advertisers.length; i++) {
            bids[i] = bidManager.getBid(advertisers[i], query);
            qualityScores[i] = bidManager.getQualityScore(advertisers[i], query);
            ads[i] = bidManager.getAdLink(advertisers[i], query);
            scores[i] = Math.pow(qualityScores[i], squashValue) * bids[i];
            indices[i] = i;
            //log.finest("Advertiser: "+advertisers[i]+"\tScore: "+scores[i]);
        }

        //This currently runs for an infinite loop if scores are NaN
        hardSort(scores, indices);

        calculateCPC(indices, scores, bids, cpc);

        Ranking ranking = new Ranking();
        Pricing pricing = new Pricing();

        for (int i = 0; i < indices.length && i < slotLimit; i++) {
            if (ads[indices[i]] != null) {
                AdLink ad = ads[indices[i]];
                double price = cpc[indices[i]];

                price = Double.isNaN(price) ? 0.0 : price;

                pricing.setPrice(ads[indices[i]], price);
                ranking.add(ad);

            }
        }

        ranking.lock();
        pricing.lock();

        Auction auction = new Auction();
        auction.setQuery(query);
        auction.setPricing(pricing);
        auction.setRanking(ranking);

        return auction;
    }

    public BidManager getBidManager() {
        return bidManager;
    }

    public void setBidManager(BidManager bidManager) {
        this.bidManager = bidManager;
    }

    public double getSquashValue() {
        return squashValue;
    }

    public void setSquashValue(double squash) {
        squashValue = squash;
    }

    public int getSlotLimit() {
        return slotLimit;
    }

    public void setSlotLimit(int slotLimit) {
        this.slotLimit = slotLimit;
    }

    private void hardSort(double[] scores, int[] indices) {
        for (int i = 0; i < indices.length - 1; i++) {
            for (int j = i + 1; j < indices.length; j++) {
                if (!(scores[indices[i]] >= scores[indices[j]])) {
                    int sw = indices[i];
                    indices[i] = indices[j];
                    indices[j] = sw;
                }
            }
        }
    }

    private void calculateCPC(int[] indices, double[] scores, double[] bids, double[] cpc) {

        for (int i = 0; i < indices.length - 1; i++) {
            cpc[i] = scores[i + 1] / scores[i] * bids[i];
        }
    }
}
