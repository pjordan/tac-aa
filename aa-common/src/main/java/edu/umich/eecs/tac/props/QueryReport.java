package edu.umich.eecs.tac.props;

import java.text.ParseException;
import java.util.Set;
import java.util.Collections;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

/**
 * @author Ben Cassell, Patrick Jordan
 */
public class QueryReport extends AbstractQueryKeyedReportTransportable<QueryReport.QueryReportEntry> {

    private static final long serialVersionUID = -7957495904471250085L;


    protected QueryReportEntry createEntry(Query query) {
        QueryReportEntry entry = new QueryReportEntry();
        entry.setQuery(query);
        return entry;
    }

    protected Class entryClass() {
        return QueryReportEntry.class;
    }

    public QueryReport() {
    }

    public void addQuery(Query query, int impressions, int clicks, double cost, double positionSum) {
        lockCheck();

        int index = addQuery(query);
        QueryReportEntry entry = getEntry(index);
        entry.setImpressions(impressions);
        entry.setClicks(clicks);
        entry.setCost(cost);
        entry.setPositionSum(positionSum);
    }

    public void setPositionSum(Query query, double positionSum) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setPositionSum(index, positionSum);

    }

    public void setPositionSum(int index, double positionSum) {
        lockCheck();
        getEntry(index).setPositionSum(positionSum);
    }

    public void setCost(Query query, double cost) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setCost(index, cost);

    }

    public void setCost(int index, double cost) {
        lockCheck();
        getEntry(index).setCost(cost);
    }

    public void setImpressions(Query query, int impressions) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setImpressions(index, impressions);

    }

    public void setImpressions(Query query, int impressions, Ad ad, double position) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setImpressions(index, impressions, ad, position);

    }
    public void addImpressions(Query query, int impressions) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            setImpressions(query, impressions);
        } else {
            addImpressions(index, impressions);
        }
    }

    public void addImpressions(Query query, int impressions, Ad ad, double position) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            setImpressions(query, impressions, ad, position);
        } else {
            addImpressions(index, impressions, ad, position);
        }
    }

    public void addImpressions(int index,int impressions) {
        lockCheck();


        getEntry(index).addImpressions(impressions);

    }

    public void addImpressions(int index, int impressions, Ad ad, double position) {
        lockCheck();

        getEntry(index).addImpressions(impressions);
        getEntry(index).setAd(ad);
        getEntry(index).addPosition(position);
    }

    public void setImpressions(int index, int impressions) {
        lockCheck();
        getEntry(index).setImpressions(impressions);
    }

    public void setImpressions(int index, int impressions, Ad ad, double position) {
        lockCheck();
        getEntry(index).setImpressions(impressions);
        getEntry(index).setPositionSum(position);
        getEntry(index).setAd(ad);
    }

    public void setClicks(Query query, int clicks) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setClicks(index, clicks);

    }

    public void setClicks(Query query, int clicks, double cost) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setClicks(index, clicks, cost);

    }

    public void setClicks(int index, int clicks) {
        lockCheck();
        getEntry(index).setClicks(clicks);
    }

    public void setClicks(int index, int clicks, double cost) {
        lockCheck();
        getEntry(index).setClicks(clicks);
        getEntry(index).setCost(clicks);
    }
    
    public void addClicks(Query query, int clicks) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            setClicks(query, clicks);
        } else {
            addClicks(index, clicks);
        }
    }

    public void addClicks(Query query, int clicks, double cost) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            setClicks(query, clicks, cost);
        } else {
            addClicks(index, clicks, cost);
        }
    }

    public void addClicks(int index, int clicks) {
        lockCheck();
        getEntry(index).addClicks(clicks);
    }

    public void addClicks(int index, int clicks, double cost) {
        lockCheck();
        getEntry(index).addClicks(clicks);
        getEntry(index).addCost(clicks);
    }
    public void addCost(Query query, double cost) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            setCost(query, cost);
        } else {
            addCost(index, cost);
        }
    }

    public void addCost(int index, double cost) {
        lockCheck();
        getEntry(index).addCost(cost);
    }

    public double getPosition(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? Double.NaN : getPosition(index);
    }

    public double getPosition(int index) {
        return getEntry(index).getPosition();
    }

    public double getCPC(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? Double.NaN : getCPC(index);
    }

    public double getCPC(int index) {
        return getEntry(index).getCPC();
    }

    public int getImpressions(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? 0 : getImpressions(index);
    }

    public int getImpressions(int index) {
        return getEntry(index).getImpressions();
    }

    public int getClicks(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? 0 : getClicks(index);
    }

    public int getClicks(int index) {
        return getEntry(index).getClicks();
    }

    public double getCost(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? 0.0 : getCost(index);
    }

    public double getCost(int index) {
        return getEntry(index).getCost();
    }

    public double getPosition(Query query, String advertiser) {
        int index = indexForEntry(query);

        return index < 0 ? Double.NaN : getPosition(index, advertiser);
    }

    public double getPosition(int index, String advertiser) {
        return getEntry(index).getPosition(advertiser);
    }

    public void setPosition(Query query, String advertiser, double position) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setPosition(index, advertiser, position);
    }

    public void setPosition(int index, String advertiser, double position) {
        lockCheck();

        getEntry(index).setPosition(advertiser, position);
    }

    public Ad getAd(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? null : getAd(index);
    }

    public Ad getAd(int index) {
        return getEntry(index).getAd();
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

    public Ad getAd(Query query, String advertiser) {
        int index = indexForEntry(query);

        return index < 0 ? null : getAd(index, advertiser);
    }

    public Ad getAd(int index, String advertiser) {
        return getEntry(index).getAd(advertiser);
    }
    
    public void setAd(Query query, String advertiser, Ad ad) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setAd(index, advertiser, ad);
    }

    public void setAd(int index, String advertiser, Ad ad) {
        lockCheck();

        getEntry(index).setAd(advertiser, ad);
    }

    public void setAdAndPosition(Query query, String advertiser, Ad ad, double position) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setAd(index, advertiser, ad, position);
    }

    public void setAd(int index, String advertiser, Ad ad, double position) {
        lockCheck();

        getEntry(index).setAdAndPosition(advertiser, ad, position);
    }

    public Set<String> advertisers(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? Collections.EMPTY_SET : advertisers(index);
    }

    public Set<String> advertisers(int index) {
        return getEntry(index).advertisers();
    }
    /**
     *
     * @author Patrick Jordan
     */
    public static class QueryReportEntry extends AbstractQueryEntry {
        private int impressions;
        private int clicks;
        private double cost;
        private double positionSum;
        private Ad ad;

        private DisplayReport displayReport;

        public QueryReportEntry() {
            positionSum = 0.0;
            cost = 0.0;
            displayReport = new DisplayReport();
        }

        public int getImpressions() {
            return impressions;
        }

        void setImpressions(int impressions) {
            this.impressions = impressions;
        }

        void addImpressions(int impressions) {
            this.impressions += impressions;
        }

        public int getClicks() {
            return clicks;
        }

        void setClicks(int clicks) {
            this.clicks = clicks;
        }

        void addClicks(int clicks) {
            this.clicks += clicks;
        }

        public double getPosition() {
            return positionSum / impressions;
        }

        void addPosition(double position) {
            this.positionSum += position;
        }

        void setPositionSum(double positionSum) {
            this.positionSum = positionSum;
        }

        public double getCost() {
            return cost;
        }

        void setCost(double cost) {
            this.cost = cost;
        }

        void addCost(double cost) {
            this.cost += cost;
        }

        public double getCPC() {
            return cost / impressions;
        }

        public Ad getAd() {
            return ad;
        }

        public void setAd(Ad ad) {
            this.ad = ad;
        }

        public double getPosition(String advertiser) {
            return displayReport.getPosition(advertiser);
        }

        public void setPosition(String advertiser, double position) {
            displayReport.setPosition(advertiser, position);
        }

        public Ad getAd(String advertiser) {
            return displayReport.getAd(advertiser);
        }


        public void setAd(String advertiser, Ad ad) {
            displayReport.setAd(advertiser, ad);
        }


        public void setAdAndPosition(String advertiser, Ad ad, double position) {
            displayReport.setAdAndPosition(advertiser, ad, position);
        }

        public Set<String> advertisers() {
            return displayReport.keys();
        }

        protected void readEntry(TransportReader reader) throws ParseException {
            this.impressions = reader.getAttributeAsInt("impressions", 0);
            this.clicks = reader.getAttributeAsInt("clicks", 0);
            this.positionSum = reader.getAttributeAsDouble("positionSum", 0.0);
            this.cost = reader.getAttributeAsDouble("cost", 0.0);

            if (reader.nextNode(Ad.class.getSimpleName(), false)) {
                this.ad = (Ad) reader.readTransportable();
            }

            if (reader.nextNode(DisplayReport.class.getSimpleName(), true)) {
                this.displayReport = (DisplayReport) reader.readTransportable();
            }


        }


        protected void writeEntry(TransportWriter writer) {
            writer.attr("impressions", impressions);
            writer.attr("clicks", clicks);
            writer.attr("positionSum", positionSum);
            writer.attr("cpc", cost);

            if (ad != null) {
                writer.write(ad);
            }
            
            writer.write(displayReport);
        }


        public String toString() {
            return String.format("(%s impr: %d clicks: %d pos: %f cpc: %f)", getQuery(), impressions, clicks, getPosition(), getCPC());
        }
    }

    /**
     *
     * @author Patrick Jordan
     */
    public static class DisplayReportEntry extends AbstractAdvertiserEntry {
        private Ad ad;
        private double position;


        public DisplayReportEntry() {
            position = Double.NaN;
        }


        public Ad getAd() {
            return ad;
        }

        public void setAd(Ad ad) {
            this.ad = ad;
        }

        public double getPosition() {
            return position;
        }

        public void setPosition(double position) {
            this.position = position;
        }

        public void setAdAndPosition(Ad ad, double position) {
            this.ad = ad;
            this.position = position;
        }

        protected void readEntry(TransportReader reader) throws ParseException {
            this.position = reader.getAttributeAsDouble("position", Double.NaN);

            if (reader.nextNode(Ad.class.getSimpleName(), false)) {
                this.ad = (Ad) reader.readTransportable();
            }
        }

        protected void writeEntry(TransportWriter writer) {
            writer.attr("position", position);

            if (ad != null) {
                writer.write(ad);
            }
        }
    }

    /**
     * 
     * @author Patrick Jordan
     */
    public static class DisplayReport extends AbstractAdvertiserKeyedReportTransportable<QueryReport.DisplayReportEntry> {

        protected DisplayReportEntry createEntry(String advertiser) {
            DisplayReportEntry entry = new DisplayReportEntry();
            entry.setAdvertiser(advertiser);
            return entry;
        }

        protected Class entryClass() {
            return DisplayReportEntry.class;
        }


        public double getPosition(String advertiser) {
            int index = indexForEntry(advertiser);

            return index < 0 ? Double.NaN : getPosition(index);
        }

        public double getPosition(int index) {
            return getEntry(index).getPosition();
        }

        public void setPosition(String advertiser, double position) {
            lockCheck();

            int index = indexForEntry(advertiser);

            if (index < 0) {
                index = addAdvertiser(advertiser);
            }

            setPosition(index, position);

        }

        public void setPosition(int index, double position) {
            lockCheck();
            getEntry(index).setPosition(position);
        }

        public Ad getAd(String advertiser) {
            int index = indexForEntry(advertiser);

            return index < 0 ? null : getAd(index);
        }

        public Ad getAd(int index) {
            return getEntry(index).getAd();
        }

        public void setAd(String advertiser, Ad ad) {
            lockCheck();

            int index = indexForEntry(advertiser);

            if (index < 0) {
                index = addAdvertiser(advertiser);
            }

            setAd(index, ad);

        }

        public void setAd(int index, Ad ad) {
            lockCheck();
            getEntry(index).setAd(ad);
        }

        public void setAdAndPosition(String advertiser, Ad ad, double position) {
            lockCheck();

            int index = indexForEntry(advertiser);

            if (index < 0) {
                index = addAdvertiser(advertiser);
            }

            setAd(index, ad, position);

        }

        public void setAd(int index, Ad ad, double position) {
            lockCheck();
            getEntry(index).setAdAndPosition(ad, position);
        }

        
    }
}