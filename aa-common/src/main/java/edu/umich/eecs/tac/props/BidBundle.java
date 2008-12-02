package edu.umich.eecs.tac.props;

import java.io.Serializable;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

/**
 * @author Ben Cassell, Patrick Jordan, Lee Callender
 */
public class BidBundle extends AbstractReportTransportable<BidBundle.BidEntry>{

    private static final long serialVersionUID = 5057969669832603679L;

    public BidBundle() {
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

        int index = findEntry(query);

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

        int index = findEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setAd(index, ad);
    }

    public void setAd(int index, Ad ad) {
        lockCheck();
        getEntry(index).setAd(ad);
    }

    public void setConversionsAndRevenue(Query query, double bid, Ad ad) {
        lockCheck();

        int index = findEntry(query);

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
        int index = findEntry(query);

        //return index < 0 ? Double.NaN : getBid(index);
        return index < 0 ? 0.0 : getBid(index);
    }

    public double getBid(int index) {
        return getEntry(index).getBid();
    }

    public Ad getAd(Query query) {
        int index = findEntry(query);

        return index < 0 ? null : getAd(index);
    }

    public Ad getAd(int index) {
        return getEntry(index).getAd();
    }


    public static class BidEntry extends AbstractQueryEntry {
        private Ad ad;
        private double bid;


        public BidEntry() {
            //this.bid = Double.NaN;
            this.bid = 0.0;
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
            this.bid = reader.getAttributeAsDouble("bid", Double.NaN);

            if (reader.nextNode("Ad", false)) {
                this.ad = (Ad) reader.readTransportable();
            }
        }

        protected void writeEntry(TransportWriter writer) {
            writer.attr("bid", bid);

            if (ad != null)
                writer.write(ad);
        }
    }

}