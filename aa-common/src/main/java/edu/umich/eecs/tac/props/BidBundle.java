package edu.umich.eecs.tac.props;

import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

/**
 * @author Ben Cassell, Patrick Jordan, Lee Callender
 */
public class BidBundle extends
        AbstractQueryKeyedReportTransportable<BidBundle.BidEntry> {
    private static final double DEFAULT_SPEND_LIMIT = Double.NaN;
    private static final double DEFAULT_BID = Double.NaN;

    private static final long serialVersionUID = 5057969669832603679L;

    private double campaignDailySpendLimit;

    public BidBundle() {
        campaignDailySpendLimit = DEFAULT_SPEND_LIMIT;
    }

    protected BidEntry createEntry(Query key) {
        BidEntry entry = new BidEntry();
        entry.setQuery(key);
        return entry;
    }

    protected Class entryClass() {
        return BidEntry.class;
    }

    public void addQuery(Query query, double bid, Ad ad) {
        int index = addQuery(query);
        BidEntry entry = getEntry(index);
        entry.setQuery(query);
        entry.setBid(bid);
        entry.setAd(ad);
    }

    public void setBid(Query query, double bid) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setBid(index, bid);
    }

    public void setBid(int index, double bid) {
        lockCheck();
        getEntry(index).setBid(bid);
    }

    public void setAd(Query query, Ad ad) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setAd(index, ad);
    }

    public void setAd(int index, Ad ad) {
        lockCheck();
        getEntry(index).setAd(ad);
    }

    public void setDailyLimit(Query query, double dailyLimit) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setDailyLimit(index, dailyLimit);
    }

    public void setDailyLimit(int index, double dailyLimit) {
        lockCheck();
        getEntry(index).setDailyLimit(dailyLimit);
    }

    public void setBidAndAd(Query query, double bid, Ad ad) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setBidAndAd(index, bid, ad);

    }

    public void setBidAndAd(int index, double bid, Ad ad) {
        lockCheck();
        BidEntry entry = getEntry(index);
        entry.setBid(bid);
        entry.setAd(ad);
    }

    public double getBid(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? DEFAULT_BID : getBid(index);
    }

    public double getBid(int index) {
        return getEntry(index).getBid();
    }

    public Ad getAd(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? null : getAd(index);
    }

    public Ad getAd(int index) {
        return getEntry(index).getAd();
    }

    public double getDailyLimit(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? DEFAULT_SPEND_LIMIT : getDailyLimit(index);
    }

    public double getDailyLimit(int index) {
        return getEntry(index).getDailyLimit();
    }

    public double getCampaignDailySpendLimit() {
        return campaignDailySpendLimit;
    }

    public void setCampaignDailySpendLimit(double campaignDailySpendLimit) {
        lockCheck();
        this.campaignDailySpendLimit = campaignDailySpendLimit;
    }


    protected void readBeforeEntries(TransportReader reader) throws ParseException {
        this.campaignDailySpendLimit = reader.getAttributeAsDouble("campaignDailySpendLimit", DEFAULT_SPEND_LIMIT);
    }


    protected void writeBeforeEntries(TransportWriter writer) {
        writer.attr("campaignDailySpendLimit", campaignDailySpendLimit);
    }


    protected void toStringBeforeEntries(StringBuilder builder) {
        builder.append(" limit: ").append(campaignDailySpendLimit);
    }    

    public static class BidEntry extends AbstractQueryEntry {
        private Ad ad;
        private double bid;
        private double dailyLimit;

        public BidEntry() {
            this.bid = DEFAULT_BID;
            this.dailyLimit = DEFAULT_SPEND_LIMIT;
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
            this.bid = reader.getAttributeAsDouble("bid", DEFAULT_BID);
            this.dailyLimit = reader.getAttributeAsDouble("dailyLimit",
                    DEFAULT_SPEND_LIMIT);
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