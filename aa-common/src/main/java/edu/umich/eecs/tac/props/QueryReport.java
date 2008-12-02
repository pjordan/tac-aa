package edu.umich.eecs.tac.props;

import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

/**
 * @author Ben Cassell, Patrick Jordan
 */
public class QueryReport extends AbstractReportTransportable<QueryReport.QueryReportEntry> {

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

    public void addQuery(Query query, int impressions, int clicks, double cpc, double position) {
        lockCheck();
        
        int index = addQuery(query);
        QueryReportEntry entry = getEntry(index);
        entry.setImpressions(impressions);
        entry.setClicks(clicks);
        entry.setCPC(cpc);
        entry.setPosition(position);
    }

    public void setPosition(Query query, double position) {
        lockCheck();

        int index = findEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setPosition(index, position);

    }

    public void setPosition(int index, double position) {
        lockCheck();
        getEntry(index).setPosition(position);
    }

    public void setCPC(Query query, double cpc) {
        lockCheck();

        int index = findEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setCPC(index, cpc);

    }

    public void setCPC(int index, double cpc) {
        lockCheck();
        getEntry(index).setCPC(cpc);
    }

    public void setImpressions(Query query, int impressions) {
        lockCheck();

        int index = findEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setImpressions(index, impressions);

    }

    public void addImpressions(Query query, int impressions) {
        lockCheck();

        int index = findEntry(query);

        if (index < 0) {
            setImpressions(query,impressions);
        } else {
            addImpressions(index,impressions);
        }
    }

    public void addImpressions(int index, int impressions) {
        lockCheck();

        getEntry(index).addImpressions(impressions);

    }

    public void setImpressions(int index, int impressions) {
        lockCheck();
        getEntry(index).setImpressions(impressions);
    }

    public void setClicks(Query query, int clicks) {
        lockCheck();

        int index = findEntry(query);

        if (index < 0) {
            index = addQuery(query);
        }

        setClicks(index, clicks);

    }

    public void setClicks(int index, int clicks) {
        lockCheck();
        getEntry(index).setClicks(clicks);
    }

    public void addClicks(Query query, int clicks) {
        lockCheck();

        int index = findEntry(query);

        if (index < 0) {
            setClicks(query,clicks);
        } else {
            addClicks(index,clicks);
        }
    }

    public void addClicks(int index, int clicks) {
        lockCheck();
        getEntry(index).addClicks(clicks);
    }

    public double getPosition(Query query) {
        int index = findEntry(query);

        return index < 0 ? 0.0 : getPosition(index);
    }

    public double getPosition(int index) {
        return getEntry(index).getPosition();
    }

    public double getCPC(Query query) {
        int index = findEntry(query);

        return index < 0 ? 0.0 : getCPC(index);
    }

    public double getCPC(int index) {
        return getEntry(index).getCPC();
    }

    public int getImpressions(Query query) {
        int index = findEntry(query);

        return index < 0 ? 0 : getImpressions(index);
    }

    public int getImpressions(int index) {
        return getEntry(index).getImpressions();
    }

    public int getClicks(Query query) {
        int index = findEntry(query);

        return index < 0 ? 0 : getClicks(index);
    }

    public int getClicks(int index) {
        return getEntry(index).getClicks();
    }

    public static class QueryReportEntry extends AbstractQueryEntry {
        private int impressions;
        private int clicks;
        private double position;
        private double cpc;

        public QueryReportEntry() {
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
            return position;
        }

        void setPosition(double position) {
            this.position = position;
        }        

        public double getCPC() {
            return cpc;
        }

        void setCPC(double avgCPC) {
            this.cpc = avgCPC;
        }

        protected void readEntry(TransportReader reader) throws ParseException {
            this.impressions = reader.getAttributeAsInt("impressions", 0);
            this.clicks = reader.getAttributeAsInt("clicks", 0);
            this.position = reader.getAttributeAsDouble("position", 0.0);
            this.cpc = reader.getAttributeAsDouble("cpc", 0.0);
        }


        protected void writeEntry(TransportWriter writer) {
            writer.attr("impressions", impressions);
            writer.attr("clicks", clicks);
            writer.attr("position", position);
            writer.attr("cpc", cpc);
        }


        public String toString() {
            return String.format("(%s impr: %d clicks: %d pos: %f cpc: %f)", getQuery(), impressions, clicks, position, cpc);
        }
    }

}