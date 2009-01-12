package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.AdLink;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;
import com.botbox.util.ArrayUtils;

import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Patrick Jordan
 */
public class BidTrackerImpl implements BidTracker {
    private final static double DEFAULT_SPEND_LIMIT = Double.POSITIVE_INFINITY;
    private final static double DEFAULT_BID = 0.0;
    private final static Ad DEFAULT_AD = new Ad();

    private Logger logger = Logger.getLogger(BidTrackerImpl.class.getName());

    private String[] advertisers;
    private int advertisersCount;
    private QueryBid[] queryBid;
    private Query[] querySpace;

    public BidTrackerImpl() {
        this(0);
    }

    public BidTrackerImpl(int advertisersCount) {
        this.advertisersCount = advertisersCount;
        advertisers = new String[advertisersCount];
        queryBid = new QueryBid[advertisersCount];
    }

    public void initializeQuerySpace(Set<Query> space) {
        if (querySpace == null) {
            querySpace = space.toArray(new Query[0]);
        } else {
            logger.warning("Attempt to re-initialize query space");
        }
    }

    private synchronized int doAddAdvertiser(String advertiser) {
        if (advertisersCount == advertisers.length) {
            int newSize = advertisersCount + 8;
            advertisers = (String[]) ArrayUtils.setSize(advertisers, newSize);
            queryBid = (QueryBid[])ArrayUtils.setSize(queryBid, newSize);
        }

        advertisers[advertisersCount] = advertiser;

        return advertisersCount++;
    }

    public void addAdvertiser(String advertiser) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);
        if (index < 0) {
            doAddAdvertiser(advertiser);
        }
    }

    public double getDailySpendLimit(String advertiser) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);

        if (index < 0) {
            return DEFAULT_SPEND_LIMIT;
        }

        if (queryBid[index] == null) {
            queryBid[index] = new QueryBid(advertiser, 0);
        }

        return queryBid[index].getCampaignSpendLimit();
    }

    public double getDailySpendLimit(String advertiser, Query query) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);

        if (index < 0) {
            return DEFAULT_SPEND_LIMIT;
        }

        if (queryBid[index] == null) {
            queryBid[index] = new QueryBid(advertiser, 0);
        }

        return queryBid[index].getSpendLimits(query);
    }

    public double getBid(String advertiser, Query query) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);

        if (index < 0) {
            return DEFAULT_BID;
        }

        if (queryBid[index] == null) {
            queryBid[index] = new QueryBid(advertiser, 0);
        }

        return queryBid[index].getBid(query);
    }

    public AdLink getAdLink(String advertiser, Query query) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);

        if (index < 0) {
            return new AdLink(DEFAULT_AD.getProduct(), advertiser);
        }

        if (queryBid[index] == null) {
            queryBid[index] = new QueryBid(advertiser, 0);
        }

        return queryBid[index].getAdLink(query);
    }

    public void updateBids(String advertiser, BidBundle bundle) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);

        if (index < 0) {
            index = doAddAdvertiser(advertiser);
        }

        if (queryBid[index] == null) {
            queryBid[index] = new QueryBid(advertiser, 0);
        }

        if(!Double.isNaN(bundle.getCampaignDailySpendLimit())) {
            queryBid[index].setCampaignSpendLimit(bundle.getCampaignDailySpendLimit());
        }

        for(Query query : querySpace) {
            Ad ad = bundle.getAd(query);
            double dailyLimit = bundle.getDailyLimit(query);
            double bid = bundle.getBid(query);

            if(ad != null) {
                queryBid[index].setAd(query, ad);
            }

            if(bid >= 0.0 ) {
                queryBid[index].setBid(query, bid);
            }

            if(dailyLimit >= 0.0 ) {
                queryBid[index].setSpendLimit(query, dailyLimit);
            }
        }
    }

    public int size() {
        return advertisersCount;
    }

    private static class QueryBid {
        private String advertiser;
        private Query[] queries;
        private AdLink[] adLinks;
        private double[] spendLimits;
        private double[] bids;
        private int queryCount;
        private double campaignSpendLimit;

        public QueryBid(String advertiser, int queryCount) {
            this.advertiser = advertiser;
            queries = new Query[queryCount];
            adLinks = new AdLink[queryCount];
            spendLimits = new double[queryCount];
            bids = new double[queryCount];
            this.queryCount = queryCount;
            campaignSpendLimit = DEFAULT_SPEND_LIMIT;
        }

        private synchronized int doAddQuery(Query query) {
            if (queryCount == queries.length) {
                int newSize = queryCount + 8;
                queries = (Query[]) ArrayUtils.setSize(queries, newSize);
                adLinks = (AdLink[]) ArrayUtils.setSize(adLinks, newSize);
                spendLimits = ArrayUtils.setSize(spendLimits, newSize);
                bids = ArrayUtils.setSize(bids, newSize);

                for(int i = queryCount; i < newSize; i++) {
                    bids[i] = DEFAULT_BID;
                    spendLimits[i] = DEFAULT_SPEND_LIMIT;
                    adLinks[i] = new AdLink(DEFAULT_AD.getProduct(), advertiser);
                }
            }
            queries[queryCount] = query;

            return queryCount++;
        }

        protected void setBid(Query query, double bid) {
            int index = ArrayUtils.indexOf(queries, 0, queryCount, query);

            if (index < 0) {
                index = doAddQuery(query);
            }

            this.bids[index] = bid;
        }

        protected double getBid(Query query) {
            int index = ArrayUtils.indexOf(queries, 0, queryCount, query);

            if (index < 0) {
                return DEFAULT_BID;
            }

            return this.bids[index];
        }

        protected void setSpendLimit(Query query, double spendLimit) {
            int index = ArrayUtils.indexOf(queries, 0, queryCount, query);

            if (index < 0) {
                index = doAddQuery(query);
            }

            this.spendLimits[index] = spendLimit;
        }

        protected double getSpendLimits(Query query) {
            int index = ArrayUtils.indexOf(queries, 0, queryCount, query);

            if (index < 0) {
                return Double.POSITIVE_INFINITY;
            }

            return this.spendLimits[index];
        }

        protected void setAd(Query query, Ad ad) {
            int index = ArrayUtils.indexOf(queries, 0, queryCount, query);

            if (index < 0) {
                index = doAddQuery(query);
            }

            this.adLinks[index] = new AdLink(ad.getProduct(), advertiser);
        }

        protected AdLink getAdLink(Query query) {
            int index = ArrayUtils.indexOf(queries, 0, queryCount, query);

            if (index < 0) {
                return new AdLink(DEFAULT_AD.getProduct(), advertiser);
            }

            return this.adLinks[index];
        }


        public double getCampaignSpendLimit() {
            return campaignSpendLimit;
        }

        protected void setCampaignSpendLimit(double campaignSpendLimit) {
            this.campaignSpendLimit = campaignSpendLimit;
        }
    }
}
