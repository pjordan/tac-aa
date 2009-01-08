package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.props.BidBundle.BidEntry;

import java.util.*;
import java.util.logging.Logger;

import se.sics.tasim.aw.Message;

/**
 * @author Patrick Jordan, Lee Callender
 */
public class BidManagerImpl implements BidManager {

    //TODO: Discuss 'security' issues, test, add quality score update
    //TODO: getBid, getQualityScore, etc. should remain constant throughout the day.
    private Logger log = Logger.getLogger(BidManagerImpl.class.getName());

    private Set<String> advertisers;

    private Set<String> advertisersView;

    private List<Message> bidBundleList;

    private UserClickModel userClickModel;

    private BidTracker bidTracker;

    private SpendTracker spendTracker;


    public BidManagerImpl(UserClickModel userClickModel, BidTracker bidTracker, SpendTracker spendTracker) {
        this.userClickModel = userClickModel;
        this.bidTracker = bidTracker;
        this.spendTracker = spendTracker;


        advertisers = new HashSet<String>();
        advertisersView = Collections.unmodifiableSet(advertisers);
        bidBundleList = new ArrayList<Message>();
    }


  /**
   * NOTE: isOverspent will only function correctly in this instance
   * if auctions are computed for EVERY query and not cached.
   */
    public double getBid(String advertiser, Query query) {
        double bid = bidTracker.getBid(advertiser, query);

        if(isOverspent(bid, advertiser, query))
            return 0.0;
        else
            return bid;
    }


    public double getQualityScore(String advertiser, Query query) {
        int advertiserIndex = userClickModel.advertiserIndex(advertiser);
        int queryIndex = userClickModel.queryIndex(query);

        if(advertiserIndex<0 || queryIndex<0)
            return 1.0;
        else
            return userClickModel.getAdvertiserEffect(queryIndex,advertiserIndex);
    }


    public AdLink getAdLink(String advertiser, Query query) {
        return bidTracker.getAdLink(advertiser,query);
    }


    public void updateBids(String advertiser, BidBundle bundle) {

        //Store all of the BidBundles until nextTimeUnit.
        //We'll call actualUpdateBids method there.
        Message m = new Message(advertiser, advertiser, bundle);

        bidBundleList.add(m);
    }

    public Set<String> advertisers() {
        return advertisersView;
    }

    public void nextTimeUnit(int timeUnit) {

        for (Message m : bidBundleList) {
            bidTracker.updateBids(m.getSender(), (BidBundle) m.getContent());
        }

        bidBundleList.clear();
    }

    public void addAdvertiser(String advertiser) {
        advertisers.add(advertiser);
        bidTracker.addAdvertiser(advertiser);
        spendTracker.addAdvertiser(advertiser);
    }

    private boolean isOverspent(double bid, String advertiser, Query query) {
        return (bid >= bidTracker.getDailySpendLimit(advertiser,query) - spendTracker.getDailyCost(advertiser,query)) ||
               (bid >= bidTracker.getDailySpendLimit(advertiser) - spendTracker.getDailyCost(advertiser));
    }
}
