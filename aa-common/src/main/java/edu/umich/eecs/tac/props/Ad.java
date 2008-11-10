package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class Ad extends AbstractTransportable {
    private Product product;

    public Ad() {
    }

    public boolean isGeneric() {
        return product==null;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        if (reader.nextNode(Product.class.getSimpleName(), false)) {
            this.product = (Product)reader.readTransportable();
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        if(product!=null)
            writer.write(product);
    }

    public String toString() {
        return String.format("(Ad generic:%s product:%s)",isGeneric(),getProduct());
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ad ad = (Ad) o;

        if (product != null ? !product.equals(ad.product) : ad.product != null) return false;

        return true;
    }

    public int hashCode() {
        return (product != null ? product.hashCode() : 0);
    }
}
