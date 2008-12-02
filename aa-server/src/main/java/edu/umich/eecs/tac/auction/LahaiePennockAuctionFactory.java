package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.Publisher;
import se.sics.isl.util.ConfigManager;

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

        for(int i = 0; i < advertisers.length; i++) {
            bids[i] = bidManager.getBid(advertisers[i],query);
            qualityScores[i] = bidManager.getQualityScore(advertisers[i],query);
            ads[i] = bidManager.getAdLink(advertisers[i],query);
            scores[i] = Math.pow(qualityScores[i],squashValue)*bids[i];
            indices[i] = i;
            log.finest("Advertiser: "+advertisers[i]+"\tScore: "+scores[i]);
        }

        //This currently runs for an infinite loop if scores are NaN
        bubbleSort(scores,indices);
        
        calculateCPC(indices, scores, bids, cpc);

        Ranking ranking = new Ranking();
        Pricing pricing = new Pricing();

        for(int i = 0; i < indices.length && i < slotLimit; i++) {
            if(ads[indices[i]]!=null) {
                AdLink ad = ads[indices[i]];
                double price = cpc[indices[i]];

                price = Double.isNaN(price) ? 0.0 : price;
                
                pricing.setPrice(ads[indices[i]],price);
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

    private void bubbleSort(double[] scores, int[] indices) {
        boolean flag;

        do {
            flag = false;
            for(int i = 0; i < indices.length-1; i++) {
                if(!(Double.isNaN(scores[indices[i]]) && Double.isNaN(scores[indices[i+1]]))) {
                    if(!(scores[indices[i]] > scores[indices[i+1]])) {
                        int sw = indices[i];
                        indices[i] = indices[i+1];
                        indices[i+1] = sw;
                        flag=true;
                    }
                }
            }
        } while(flag);
    }

    private void calculateCPC(int[] indices, double[] scores, double[] bids, double[] cpc) {

       for(int i = 0; i < indices.length-1; i++) {
           cpc[i] = scores[i+1]/scores[i] * bids[i];
       }
    }
}
