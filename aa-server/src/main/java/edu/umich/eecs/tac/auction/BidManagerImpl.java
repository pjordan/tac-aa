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
    private Map<String, Map<Query, QueryEntry>> advertiserBidInfo;
    private Logger log = Logger.getLogger(BidManagerImpl.class.getName());
    private Set<Query> possibleQueries;
    private List<Message> bidBundleList;
    private UserClickModel userClickModel;

    public BidManagerImpl(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;
        advertiserBidInfo = new HashMap<String, Map<Query, QueryEntry>>();
        bidBundleList = new ArrayList<Message>();
    }

    public void initializeQuerySpace(Set<Query> space) {
        if (possibleQueries == null) {
            possibleQueries = new HashSet<Query>(space);
            //TODO: Initialize default BidBundle?
        } else {
            log.warning("Attempt to re-initialize query space");
        }

    }

    public QueryEntry addQuery(String advertiser, Query query) {
        if (!advertiserBidInfo.containsKey(advertiser)) {
            return null;
        }

        if (advertiserBidInfo.get(advertiser).containsKey(query)) {
            return null;
        }

        //Every QueryEntry should be set to default values
        QueryEntry qe = new QueryEntry(0.0, Double.POSITIVE_INFINITY, 1.0, new AdLink(null, advertiser));
        advertiserBidInfo.get(advertiser).put(query, qe);

        return qe;
    }

    public double getBid(String advertiser, Query query) {
        if (!advertiserBidInfo.containsKey(advertiser))
            return 0.0;  //Double.NaN?
        if (!advertiserBidInfo.get(advertiser).containsKey(query))
            return 0.0;


        return advertiserBidInfo.get(advertiser).get(query).getBid();
    }

    public double getQualityScore(String advertiser, Query query) {
        if (this.userClickModel==null)
            return 1.0;

        int advertiserIndex = userClickModel.advertiserIndex(advertiser);
        int queryIndex = userClickModel.queryIndex(query);

        if(advertiserIndex<0 || queryIndex<0)
            return 1.0;
        else
            return userClickModel.getAdvertiserEffect(queryIndex,advertiserIndex);
    }

    public AdLink getAdLink(String advertiser, Query query) {
        if (!advertiserBidInfo.containsKey(advertiser)) {
            AdLink generic = new AdLink(null, advertiser);
            return generic;
        }

        if (!advertiserBidInfo.get(advertiser).containsKey(query)) {
            AdLink generic = new AdLink(null, advertiser);
            return generic;
        }

        AdLink ad = advertiserBidInfo.get(advertiser).get(query).getAdLink();
        if (ad == null) {
            ad = new AdLink(null, advertiser);
        }
        return ad;
    }

    public void updateBids(String advertiser, BidBundle bundle) {
        //Store all of the BidBundles until nextTimeUnit.
        //We'll call actualUpdateBids method there.
        Message m = new Message(advertiser, advertiser, bundle);
        bidBundleList.add(m);
    }

    public void actualUpdateBids(String advertiser, BidBundle bundle) {
        //TODO: This should be storing the bid updates, but not applying them until nextTimeUnit is called
        if (possibleQueries == null) {
            log.warning("Cannot update bids because query space is un-instantiated");
            return;
        }

        if (!advertiserBidInfo.containsKey(advertiser)) {
            addAdvertiser(advertiser);
        }

        Map<Query, QueryEntry> bids = advertiserBidInfo.get(advertiser);
        for (Iterator<Query> it = bundle.iterator(); it.hasNext();) {
            Query query = it.next();

            //TODO: Make sure query is a valid query
            if (!possibleQueries.contains(query)) {
                log.warning("Unknown query " + query.toString() + " from " + advertiser);
                continue;
            }

            if (!bids.containsKey(query)) {
                addQuery(advertiser, query);
            }

            //Update bid for query only if bid was specified in BidBundle
            double bid = bundle.getBid(query);
            if (bid != Double.NaN && bid >= 0.0) {
                bids.get(query).setBid(bid);
            }

            //Update ad for query only if ad was specified in BidBundle
            Ad ad = bundle.getAd(query);
            if (ad != null) {
                AdLink adLink = new AdLink(ad.getProduct(), advertiser);
                bids.get(query).setAdLink(adLink);
            }

            //Update dailyLimit for query only if query was specified in BidBundle
            double dailyLimit = bundle.getDailyLimit(query);
            if (true) {//TODO: what's the domain of DailyLimit?
                bids.get(query).setDailyLimit(dailyLimit);      
            }
        }
    }

    public Set<String> advertisers() {
        return advertiserBidInfo.keySet();
    }

    public void nextTimeUnit(int timeUnit) {
        //TODO: apply updates here
        for (Iterator<Message> it = bidBundleList.iterator(); it.hasNext();) {
            Message m = it.next();
            actualUpdateBids(m.getSender(), (BidBundle) m.getContent());
        }
        bidBundleList.clear();

    }

    public void addAdvertiser(String advertiser) {
        if (!advertiserBidInfo.containsKey(advertiser)) {
            advertiserBidInfo.put(advertiser, new HashMap<Query, QueryEntry>());
        }
    }

    private static class QueryEntry {
        //TODO: track the attributes for each query
        private double bid;
        private double dailyLimit;
        private double qualityScore;
        private AdLink ad;

        public QueryEntry() {
            bid = 0.0;      //Breaks auction if Double.NaN?
            dailyLimit = Double.POSITIVE_INFINITY;
            qualityScore = 1.0;
        }

        public QueryEntry(double bid, double dailyLimit, double qualityScore, AdLink ad) {
            setBid(bid);
            setDailyLimit(dailyLimit);
            setQualityScore(qualityScore);
            setAdLink(ad);
        }

        public double getDailyLimit() {
			    return dailyLimit;
		    }

		    public void setDailyLimit(double dailyLimit) {
			    this.dailyLimit = dailyLimit;
	    	}
      
        public AdLink getAdLink() {
            return ad;
        }

        public void setAdLink(AdLink ad) {
            this.ad = ad;
        }

        public double getBid() {
            return bid;
        }

        public void setBid(double bid) {
            this.bid = bid;
        }

        public double getQualityScore() {
            return qualityScore;
        }

        public void setQualityScore(double score) {
            this.qualityScore = score;
        }

    }
}
