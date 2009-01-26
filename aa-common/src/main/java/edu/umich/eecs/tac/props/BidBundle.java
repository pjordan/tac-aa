package edu.umich.eecs.tac.props;

import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

/**
 * BidBundles specify all relevant bidding information for a given Advertister.
 * Each BidBundle contains a set of {@link BidBundle.BidEntry Bid Entries},
 * each for a given query. Each BidEntry contains:
 * <ul>
 *  <li> {@link Ad} - The ad to be used for the given query.</li>
 *  <li> Bid - The given bid to be used for the given query.</li>
 *  <li> Daily Limit - The daily spend limit to be used for the given query</li>
 * </ul>
 *
 * <p>Each BidBundle also can contains a Campaign Daily Spend Limit, which
 * specifies the total amount of money an advertiser is willing to spend
 * over all queries daily.
 * 
 * Advertisers typically send BidBundles on a daily basis to the Publisher.
 *
 * @author Ben Cassell, Patrick Jordan, Lee Callender
 */
public class BidBundle extends AbstractQueryKeyedReportTransportable<BidBundle.BidEntry> {
  /**
   * The persistent value for spend limit.
   * Publisher's reading a persistent value will ignore the value and instead use
   * yesterday's given spend limit in its place.
   */
    private static final double PERSISTENT_SPEND_LIMIT = Double.NaN;

  /**
   * The persistent value for bid.
   * Publisher's reading a persistent value will ignore the value and instead use
   * yesterday's given bid in its place.
   */
    private static final double PERSISTENT_BID = Double.NaN;

  /**
   * The persistent value for ad.
   * Publisher's reading a persistent value will ignore the value and instead use
   * yesterday's given Ad in its place.
   */
    private static final Ad     PERSISTENT_AD = null;

  /**
   * Advertiser's wishing to have no spend limit should use the given NO_SPEND_LIMIT value.
   */
    public  static final double NO_SPEND_LIMIT = Double.POSITIVE_INFINITY;

  /**
   * Advertiser's wishing to have no bid should use the given NO_BID value.
   */
    public  static final double NO_BID = 0.0;

   /**
   * The serial version id.
   */
    private static final long serialVersionUID = 5057969669832603679L;

   /**
   * The campaign daily spend limit
   */
    private double campaignDailySpendLimit;

  /**
   * Creates a new BidBundle. Sets the campaign daily spend
   * limit to the PERSISTENT_SPEND_LIMIT.
   */
    public BidBundle() {
        campaignDailySpendLimit = PERSISTENT_SPEND_LIMIT;
    }


   /**
   * Creates a {@link BidBundle.BidEntry} with the given {@link Query query} as the key.
   * @param key the query key
   * @return a {@link BidBundle.BidEntry} with the given {@link Query query} as the key.
   */
    protected BidEntry createEntry(Query key) {
        BidEntry entry = new BidEntry();
        entry.setQuery(key);
        return entry;
    }

   /**
     * Returns the {@link BidBundle.BidEntry} class.
     *
     * @return the {@link BidBundle.BidEntry} class.
     */
    protected Class entryClass() {
        return BidEntry.class;
    }

  /**
   * Adds a {@link BidBundle.BidEntry} keyed with the specified query and the
   * given bid and Ad. The spend limit is set to PERSISTENT_SPEND_LIMIT.
   * @param query   the query key.
   * @param bid     the bid value.
   * @param ad      the ad to be used.
   */
    public void addQuery(Query query, double bid, Ad ad) {
        int index = addQuery(query);
        BidEntry entry = getEntry(index);
        entry.setQuery(query);
        entry.setBid(bid);
        entry.setAd(ad);
    }

  /**
   * Adds a {@link BidBundle.BidEntry} keyed with the specified query and the
   * given bid and Ad. The spend limit is set to PERSISTENT_SPEND_LIMIT.
   * @param query       the query key.
   * @param bid         the bid value.
   * @param ad          the ad to be used.
   * @param dailyLimit  the daily limit.
   */
    public void addQuery(Query query, double bid, Ad ad, double dailyLimit) {
        int index = addQuery(query);
        BidEntry entry = getEntry(index);
        entry.setQuery(query);
        entry.setBid(bid);
        entry.setAd(ad);
        entry.setDailyLimit(dailyLimit);
    }

  /**
   * Sets the bid for a query.
   * @param query the query.
   * @param bid   the bid.
   */
    public void setBid(Query query, double bid) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setBid(index, bid);
    }

  /**
   * Sets the bid for a query.
   * @param index the index of the query.
   * @param bid   the bid.
   */
    public void setBid(int index, double bid) {
        lockCheck();
        getEntry(index).setBid(bid);
    }

  /**
   * Sets the ad for a query.
   * @param query the query.
   * @param ad    the ad.
   */
    public void setAd(Query query, Ad ad) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setAd(index, ad);
    }

  /**
   * Sets the ad for a query.
   * @param index the index of the query.
   * @param ad    the ad.
   */
    public void setAd(int index, Ad ad) {
        lockCheck();
        getEntry(index).setAd(ad);
    }

  /**
   * Sets the daily spend limit for a query.
   * @param query         the query.
   * @param dailyLimit    the daily spend limit.
   */
    public void setDailyLimit(Query query, double dailyLimit) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setDailyLimit(index, dailyLimit);
    }

  /**
   * Sets the daily spend limit for a query.
   * @param index         the index of the query.
   * @param dailyLimit    the daily spend limit.
   */
    public void setDailyLimit(int index, double dailyLimit) {
        lockCheck();
        getEntry(index).setDailyLimit(dailyLimit);
    }

  /**
   * Sets the bid and ad for a query.
   * @param query the query.
   * @param bid   the bid.
   * @param ad    the ad.
   */
    public void setBidAndAd(Query query, double bid, Ad ad) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setBidAndAd(index, bid, ad);

    }

  /**
   * Sets the bid and ad for a query.
   * @param index the index of the query.
   * @param bid   the bid.
   * @param ad    the ad.
   */
    public void setBidAndAd(int index, double bid, Ad ad) {
        lockCheck();
        BidEntry entry = getEntry(index);
        entry.setBid(bid);
        entry.setAd(ad);
    }

  /**
   * Returns the bid for the associated query.
   * @param query the query.
   * @return the bid.
   */
    public double getBid(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? PERSISTENT_BID : getBid(index);
    }

  /**
   * Returns the bid for the associated query.
   * @param index the index of the query.
   * @return the bid.
   */
    public double getBid(int index) {
        return getEntry(index).getBid();
    }

  /**
   * Returns the ad for the associated query.
   * @param query the query.
   * @return the ad.
   */
    public Ad getAd(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? null : getAd(index);
    }

  /**
   * Returns the ad for the associated query.
   * @param index the index of the query.
   * @return the ad.
   */
    public Ad getAd(int index) {
        return getEntry(index).getAd();
    }

  /**
   * Returns the daily spend limit for the associated query.
   * @param query the query.
   * @return the daily spend limit.
   */
    public double getDailyLimit(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? PERSISTENT_SPEND_LIMIT : getDailyLimit(index);
    }

  /**
   * Returns the daily spend limit for the associated query.
   * @param index the index of the query.
   * @return the daily spend limit.
   */
    public double getDailyLimit(int index) {
        return getEntry(index).getDailyLimit();
    }

  /**
   * Returns the campaign daily spend limit for the associated query.
   * @return the campaign daily spend limit.
   */
    public double getCampaignDailySpendLimit() {
        return campaignDailySpendLimit;
    }

  /**
   * Sets the campaign daily spend limit.
   * @param campaignDailySpendLimit the campaign daily spend limit.
   */
    public void setCampaignDailySpendLimit(double campaignDailySpendLimit) {
        lockCheck();
        this.campaignDailySpendLimit = campaignDailySpendLimit;
    }

    protected void readBeforeEntries(TransportReader reader) throws ParseException {
        this.campaignDailySpendLimit = reader.getAttributeAsDouble("campaignDailySpendLimit", PERSISTENT_SPEND_LIMIT);
    }

    protected void writeBeforeEntries(TransportWriter writer) {
        writer.attr("campaignDailySpendLimit", campaignDailySpendLimit);
    }

    protected void toStringBeforeEntries(StringBuilder builder) {
        builder.append(" limit: ").append(campaignDailySpendLimit);
    }    

 /** Each BidEntry contains:
  * <ul>
  *  <li> {@link Ad} - The ad to be used for the given query.</li>
  *  <li> Bid - The given bid to be used for the given query.</li>
  *  <li> Daily Limit - The daily spend limit to be used for the given query</li>
  * </ul>
  */ 
    public static class BidEntry extends AbstractQueryEntry {
        private Ad ad;
        private double bid;
        private double dailyLimit;

        public BidEntry() {
            this.bid = PERSISTENT_BID;
            this.dailyLimit = PERSISTENT_SPEND_LIMIT;
        }

        public Ad getAd() {
            return ad;
        }

        public void setAd(Ad ad) {
            this.ad = ad;
        }

        public double getBid() {
            return bid;
        }

        public void setBid(double bid) {
            this.bid = bid;
        }

        protected void readEntry(TransportReader reader) throws ParseException {
            this.bid = reader.getAttributeAsDouble("bid", PERSISTENT_BID);
            this.dailyLimit = reader.getAttributeAsDouble("dailyLimit",
                    PERSISTENT_SPEND_LIMIT);
            if (reader.nextNode("Ad", false)) {
                this.ad = (Ad) reader.readTransportable();
            }
        }

        protected void writeEntry(TransportWriter writer) {
            writer.attr("bid", bid);
            writer.attr("dailyLimit", dailyLimit);
            if (ad != null)
                writer.write(ad);
        }

        public double getDailyLimit() {
            return dailyLimit;
        }

        public void setDailyLimit(double dailyLimit) {
            this.dailyLimit = dailyLimit;
        }

        public String toString() {
            return String.format("(Bid query:%s ad:%s bid: %f limit: %f)",
                    getQuery(), ad, bid, dailyLimit);
        }
    }

}