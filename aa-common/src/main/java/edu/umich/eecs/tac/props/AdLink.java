package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class represents an ad link. It contains the {@link Product} of the
 * {@link Ad} as well as a string for the advertiser's address. Note that
 * AdLinks are created by the publisher from ads specified in an advertiser's
 * bid bundle and thus are not directly used by the advertiser.
 *
 *  
 *
 * @author Lee Callender
 */

public class AdLink extends Ad {
    protected String advertiser;

    /**
     * Creates a generic ad link. Advertiser's address is initialized
     * to <code> null </code>.
     */
    public AdLink() {
    }

    public AdLink(Product product, String advertiser) {
        this.product = product;
        this.advertiser = advertiser;
    }

    /**
     * Creates an ad link from a given ad. 
     */
    public AdLink(Ad ad, String advertiser) {
        this(ad == null ? null : ad.getProduct(), advertiser);
    }

    public String getAdvertiser() {
        return advertiser;
    }

    /**
   * Specify and advertiser's address for this ad link.
   *
   * @param advertiser
   *  the advertiser's address contained in the ad link.
   *
   * @throws IllegalStateException if the ad link is locked.
   */
    public void setAdvertiser(String advertiser) {
        lockCheck();
        this.advertiser = advertiser;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        advertiser = reader.getAttribute("advertiser", null);

        if (reader.nextNode(Product.class.getSimpleName(), false)) {
            this.product = (Product) reader.readTransportable();
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        if (advertiser != null)
            writer.attr("advertiser", advertiser);

        if (product != null)
            writer.write(product);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AdLink ad = (AdLink) o;

        if (advertiser != null ? !advertiser.equals(ad.advertiser)
                : ad.advertiser != null)
            return false;
        if (product != null ? !product.equals(ad.product) : ad.product != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (product != null ? product.hashCode() : 0);
        result = 31 * result + (advertiser != null ? advertiser.hashCode() : 0);
        return result;
    }

    public String toString() {
        return String.format("(AdLink advertiser:%s generic:%s product:%s)",
                getAdvertiser(), isGeneric(), getProduct());
    }
}
