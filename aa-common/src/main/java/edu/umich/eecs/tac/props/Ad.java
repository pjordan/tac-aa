package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class represents an advertisement in the TAC/AA scenario. Advertisements
 * can be generic or targeted depending on whether the ad specifies a product.
 * Advertisers will primarily use this class with {@link BidBundle} in specifying
 * which advertisements to display for individual queries.
 * 
 *
 * @author Patrick Jordan, Lee Callender
 */
public class Ad extends AbstractTransportable {
    protected Product product;

    /**
     * Creates a generic ad.
     */
    public Ad() {
    }

    /**
     * Creates a targeted ad if <code>product</code> is not null. The ad is
     * generic if the <code>product</code> is null.
     */
    public Ad(Product product) {
        this.product = product;
    }

    /**
     * Returns <code>true</code> if the ad is generic and <code>false</code> if
     * the ad is targeted.
     *
     * @return <code>true</code> if the ad is generic and <code>false</code> if
     *         the ad is targeted.
     */
    public boolean isGeneric() {
        return product == null;
    }

    /**
     * Returns the product the ad is targeting. The product is <code>null</code>
     * if the ad is generic.
     *
     * @return the product the ad is targeting. The product is <code>null</code>
     *         if the ad is generic.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product for the ad. Setting the product to <code>null</code>
     * sets the ad as generic.
     *
     * @param product the product for the ad. Setting the product to
     *                <code>null</code> sets the ad as generic.
     * @throws IllegalStateException if the ad is locked.
     */
    public void setProduct(Product product) {
        lockCheck();
        this.product = product;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        if (reader.nextNode(Product.class.getSimpleName(), false)) {
            this.product = (Product) reader.readTransportable();
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        if (product != null)
            writer.write(product);
    }

    public String toString() {
        return String.format("(Ad generic:%s product:%s)", isGeneric(),
                getProduct());
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Ad ad = (Ad) o;

        return !(product != null ? !product.equals(ad.product)
                : ad.product != null);

    }

    public int hashCode() {
        return (product != null ? product.hashCode() : 0);
	}
}
