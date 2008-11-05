package edu.umich.eecs.tac.props;

import java.io.Serializable;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

/**
 * Sales report.
 *
 * @author Ben Cassell, Patrick Jordan
 */
public class SalesReport extends AbstractTransportable {

    private static final long serialVersionUID = 3473199640271355791L;

    private List<SalesReportEntry> sales;


    public String getTransportName() {
        return "salesReport";
    }

    public SalesReport() {
        sales = new LinkedList<SalesReportEntry>();
    }

    protected void addQuery(Query query) {
        addQuery(query,0,0.0);
    }

    protected void addQuery(Query query, int conversions, double revenue) {
        lockCheck();

        if(query==null) {
            throw new NullPointerException("Query cannot be null");
        }

        SalesReportEntry entry = new SalesReportEntry();
        entry.setQuery(query);
        entry.setConversions(conversions);
        entry.setRevenue(revenue);
        
        sales.add(entry);
    }

    public void addConversions(Query query, int conversions) {
        int index = findSalesReportEntry(query);

        if(index < 0) {
            setConversions(query,conversions);
        } else {
            addConversions(index,conversions);
        }
    }

    public void addConversions(int index, int conversions) {
        setConversions(index,conversions+getConversions(index));
    }

    public void addRevenue(Query query, double revenue) {
        int index = findSalesReportEntry(query);

        if(index < 0) {
            setRevenue(query,revenue);
        } else {
            addRevenue(index,revenue);
        }
    }

    public void addRevenue(int index, double revenue) {
        setRevenue(index,revenue+getRevenue(index));
    }

    public void setConversions(Query query, int conversions) {
        lockCheck();

        int index = findSalesReportEntry(query);

        if (index < 0) {
            addQuery(query,conversions,0.0);
        } else {
            setConversions(index, conversions);
        }
    }

    public void setConversions(int index, int conversions) {
        lockCheck();
        sales.get(index).setConversions(conversions);
    }

    public void setRevenue(Query query, double revenue) {
        lockCheck();

        int index = findSalesReportEntry(query);

        if (index < 0) {
            addQuery(query,0,revenue);
        } else {
            setRevenue(index, revenue);
        }
    }

    public void setRevenue(int index, double revenue) {
        lockCheck();
        sales.get(index).setRevenue(revenue);
    }

    public void setConversionsAndRevenue(Query query, int conversions, double revenue) {
        lockCheck();

        int index = findSalesReportEntry(query);

        if (index < 0) {
            addQuery(query,conversions,revenue);
        } else {
            setConversionsAndRevenue(index, conversions, revenue);
        }
    }

    public void setConversionsAndRevenue(int index, int conversions, double revenue) {
        lockCheck();
        SalesReportEntry entry = sales.get(index);
        entry.setConversions(conversions);
        entry.setRevenue(revenue);
    }

    public int size() {
        return sales.size();
    }

    public int getConversions(Query query) {
        int index = findSalesReportEntry(query);

        return index < 0 ? 0 : getConversions(index);
    }

    public int getConversions(int index) {
        return sales.get(index).getConversions();
    }

    public double getRevenue(Query query) {
        int index = findSalesReportEntry(query);
        
        return index < 0 ? 0.0 : getRevenue(index);
    }

    public double getRevenue(int index) {
        return sales.get(index).getRevenue();
    }

    public boolean containsQuery(Query query) {
        return findSalesReportEntry(query)>-1;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder("(sales-report");

        for (SalesReportEntry salesReportEntry : sales) {
            builder.append(' ').append(salesReportEntry);
        }
        builder.append(')');
        
        return builder.toString();
    }

    private int findSalesReportEntry(Query query) {
        for(int i = 0; i < sales.size(); i++) {
            if(sales.get(i).getQuery().equals(query))
                return i;
        }
        return -1;
    }

    public void readWithLock(TransportReader reader) throws ParseException {

        boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
        while (reader.nextNode("salesReportEntry", false)) {
            sales.add((SalesReportEntry)reader.readTransportable());
        }

        if (lock) {
            lock();
        }
    }

    public void write(TransportWriter writer) {
        if (isLocked()) {
            writer.attr("lock", 1);
        }

        for (SalesReportEntry salesReportEntry : sales) {
            writer.write(salesReportEntry);
        }
    }

    public static class SalesReportEntry implements Serializable, Transportable {
        private static final long serialVersionUID = -3012145053844178964L;

        private Query query;
        private int conversions;
        private double revenue;
        
        public SalesReportEntry() {
        }

        public Query getQuery() {
            return query;
        }

        void setQuery(Query query) {
            this.query = query;
        }

        public int getConversions() {
            return conversions;
        }

        void setConversions(int conversions) {
            this.conversions = conversions;
        }

        public double getRevenue() {
            return revenue;
        }

        void setRevenue(double revenue) {
            this.revenue = revenue;
        }


        public String getTransportName() {
            return "salesReportEntry";
        }

        public void read(TransportReader reader) throws ParseException {
            this.conversions = reader.getAttributeAsInt("conversions", 0);
            this.revenue = reader.getAttributeAsDouble("revenue", 0.0);
            if (reader.nextNode("query",false)) {
                this.query = (Query)reader.readTransportable();
            }
        }

        public void write(TransportWriter writer) {
            writer.attr("conversions", conversions);
            writer.attr("revenue", revenue);
            if(query!=null) {
                writer.write(query);
            }
        }


        public String toString() {
            return String.format("(%s conv: %d rev: %f)",query!=null?query.toString():"null",conversions,revenue);
        }
    }
}
