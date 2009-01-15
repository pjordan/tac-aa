package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;
import java.util.*;

/**
 * The class holds the available products, which the users have preferences over.  In addition, the advertiser sales
 * profit per conversion is given for each product.
 *
 * @author Patrick Jordan
 */
public class RetailCatalog extends AbstractKeyedEntryList<Product, RetailCatalog.RetailCatalogEntry> {
    private Set<String> manufacturers;
    private Set<String> components;

    public RetailCatalog() {
        manufacturers = new TreeSet<String>();
        components = new TreeSet<String>();
    }

    /**
     * Returns the set of manufacturers for the products.
     * @return the set of manufacturers for the products.
     */
    public Set<String> getManufacturers() {
        return manufacturers;
    }

    /**
     * Returns the set of components for the products.
     * @return the set of components for the products.
     */
    public Set<String> getComponents() {
        return components;
    }

    /**
     * Returns the advertiser sales profit for the product. The sales profit is zero if the product is not in the
     * retail catalog.
     *
     * @param product the product
     *
     * @return the advertiser sales profit for the product.
     */
    public double getSalesProfit(Product product) {
        int index = indexForEntry(product);

        return index < 0 ? 0.0 : getSalesProfit(index);
    }

    /**
     * Returns the advertiser sales profit for the product at the index.
     *
     * @param index the index for the product
     *
     * @return the advertiser sales profit for the product.
     */
    public double getSalesProfit(int index) {
        return getEntry(index).getSalesProfit();
    }

    /**
     * Sets the sales profit for the product.
     *
     * @param product the product whose sales profit is being set.
     *
     * @param salesProfit the sales profit for the product.
     *
     * @throws IllegalStateException if the retail catalog is locked.
     */
    public void setSalesProfit(Product product, double salesProfit) {
        lockCheck();

        int index = indexForEntry(product);

        if (index < 0) {
            index = addProduct(product);
        }

        setSalesProfit(index, salesProfit);        
    }

    /**
     * Sets the sales profit for the product.
     *
     * @param index the index for the product
     *
     * @param salesProfit the sales profit for the product.
     * 
     * @throws IllegalStateException if the retail catalog is locked.
     */
    public void setSalesProfit(int index, double salesProfit) {
        lockCheck();
        getEntry(index).setSalesProfit(salesProfit);
    }

    /**
     * Adds the product to the retail catalog.  This method delegates to {@link #addKey(Object)}}.
     *
     * @param product the product to add.
     *
     * @return the index of the newly added product.
     */
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

    public static class RetailCatalogEntry extends AbstractTransportableEntry<Product> {
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
