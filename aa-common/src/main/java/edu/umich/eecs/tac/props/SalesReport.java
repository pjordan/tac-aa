package edu.umich.eecs.tac.props;

import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

/**
 * Sales report.
 *
 * @author Ben Cassell, Patrick Jordan, Lee Callender
 */
public class SalesReport extends AbstractReportTransportable<SalesReport.SalesReportEntry> {

    private static final long serialVersionUID = 3473199640271355791L;

    public SalesReport() {
    }


    protected SalesReportEntry createEntry(Query query) {        
        SalesReportEntry entry = new SalesReportEntry();
        entry.setQuery(query);
        return entry;
    }

    protected Class entryClass() {
        return SalesReportEntry.class;
    }

    protected void addQuery(Query query, int conversions, double revenue) {
        int index = addQuery(query);
        SalesReportEntry entry = getEntry(index);
        entry.setQuery(query);
        entry.setConversions(conversions);
        entry.setRevenue(revenue);
    }

    public void addConversions(Query query, int conversions) {
        int index = indexForEntry(query);

        if(index < 0) {
            setConversions(query,conversions);
        } else {
            addConversions(index,conversions);
        }
    }

    public void addConversions(int index, int conversions) {
        lockCheck();
        getEntry(index).addConversions(conversions);
    }

    public void addRevenue(Query query, double revenue) {
        int index = indexForEntry(query);

        if(index < 0) {
            setRevenue(query,revenue);
        } else {
            addRevenue(index,revenue);
        }
    }

    public void addRevenue(int index, double revenue) {
        lockCheck();
        getEntry(index).addRevenue(revenue);
    }

    public void setConversions(Query query, int conversions) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            addQuery(query,conversions,0.0);
        } else {
            setConversions(index, conversions);
        }
    }

    public void setConversions(int index, int conversions) {
        lockCheck();
        getEntry(index).setConversions(conversions);
    }

    public void setRevenue(Query query, double revenue) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            addQuery(query,0,revenue);
        } else {
            setRevenue(index, revenue);
        }
    }

    public void setRevenue(int index, double revenue) {
        lockCheck();
        getEntry(index).setRevenue(revenue);
    }

    public void setConversionsAndRevenue(Query query, int conversions, double revenue) {
        lockCheck();

        int index = indexForEntry(query);

        if (index < 0) {
            addQuery(query,conversions,revenue);
        } else {
            setConversionsAndRevenue(index, conversions, revenue);
        }
    }

    public void setConversionsAndRevenue(int index, int conversions, double revenue) {
        lockCheck();
        SalesReportEntry entry = getEntry(index);
        entry.setConversions(conversions);
        entry.setRevenue(revenue);
    }

    public int getConversions(Query query) {
        int index = indexForEntry(query);

        return index < 0 ? 0 : getConversions(index);
    }

    public int getConversions(int index) {
        return getEntry(index).getConversions();
    }

    public double getRevenue(Query query) {
        int index = indexForEntry(query);
        
        return index < 0 ? 0.0 : getRevenue(index);
    }

    public double getRevenue(int index) {
        return getEntry(index).getRevenue();
    }

    public static class SalesReportEntry extends AbstractQueryEntry {
        private static final long serialVersionUID = -3012145053844178964L;

        private int conversions = 0;
        private double revenue = 0.0;
        
        public SalesReportEntry() {
        }

        public int getConversions() {
            return conversions;
        }

        void setConversions(int conversions) {
            this.conversions = conversions;
        }

        void addConversions(int conversions) {
            this.conversions += conversions;
        }

        public double getRevenue() {
            return revenue;
        }

        void setRevenue(double revenue) {
            this.revenue = revenue;
        }

        void addRevenue(double revenue) {
            this.revenue += revenue;
        }

        protected void readEntry(TransportReader reader) throws ParseException {
            this.conversions = reader.getAttributeAsInt("conversions", 0);
            this.revenue = reader.getAttributeAsDouble("revenue", 0.0);
        }

        protected void writeEntry(TransportWriter writer) {
            writer.attr("conversions", conversions);
            writer.attr("revenue", revenue);
        }

        public String toString() {
            return String.format("(%s conv: %d rev: %f)",getQuery(),conversions,revenue);
        }
    }
}
