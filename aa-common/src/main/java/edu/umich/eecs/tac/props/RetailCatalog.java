package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

import java.text.ParseException;
import java.util.*;
import java.io.Serializable;

/**
 * @author Patrick Jordan
 */
public class RetailCatalog extends AbstractListCompositeEntryTransportable<Product, RetailCatalog.RetailCatalogEntry> {
    private Set<String> manufacturers;
    private Set<String> components;

    public RetailCatalog() {
        manufacturers = new TreeSet<String>();
        components = new TreeSet<String>();
    }


    public Set<String> getManufacturers() {
        return manufacturers;
    }

    public Set<String> getComponents() {
        return components;
    }

    public double getSalesProfit(Product product) {
        int index = findEntry(product);

        return index < 0 ? 0.0 : getSalesProfit(index);
    }

    public double getSalesProfit(int index) {
        return getEntry(index).getSalesProfit();
    }

    public void setSalesProfit(Product product, double salesProfit) {
        lockCheck();

        int index = findEntry(product);

        if (index < 0) {
            index = addProduct(product);
        }

        setSalesProfit(index, salesProfit);        
    }

    public void setSalesProfit(int index, double salesProfit) {
        lockCheck();
        getEntry(index).setSalesProfit(salesProfit);
    }

    public int addProduct(Product product) {
        return addKey(product);
    }

    protected int addEntry(RetailCatalogEntry entry) {
        int index = super.addEntry(entry);

        if (index >= 0) {
            manufacturers.add(entry.getProduct().getManufacturer());
            components.add(entry.getProduct().getComponent());                          
        }

        return index;
    }

    protected RetailCatalogEntry createEntry(Product key) {
        RetailCatalogEntry entry = new RetailCatalogEntry();
        entry.setProduct(key);
        return entry;
    }

    protected Class entryClass() {
        return RetailCatalogEntry.class;
    }

    public static class RetailCatalogEntry extends AbstractCompositeEntry<Product> {
        private static final long serialVersionUID = -1140097762238141476L;
        

        private double salesProfit;


        public Product getProduct() {
            return getKey();
        }

        protected void setProduct(Product product) {
            setKey(product);
        }


        public double getSalesProfit() {
            return salesProfit;
        }

        protected void setSalesProfit(double salesProfit) {
            this.salesProfit = salesProfit;
        }


        protected void readEntry(TransportReader reader) throws ParseException {
            salesProfit = reader.getAttributeAsDouble("salesProfit", 0.0);
        }

        protected void writeEntry(TransportWriter writer) {
            writer.attr("salesProfit", salesProfit);
        }

        protected String keyNodeName() {
            return Product.class.getSimpleName();
        }
    }
}
