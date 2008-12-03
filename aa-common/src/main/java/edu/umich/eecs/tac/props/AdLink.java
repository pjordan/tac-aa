package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Dec 2, 2008
 * Time: 1:33:46 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * @author Lee Callender
 */

//This class represents an ad link. It contains the Product of the Ad
//as well as a string for the advertiser's address.
public class AdLink extends Ad{
    protected String advertiser;

    public AdLink() {}

    public AdLink(Product product, String advertiser){
      this.product = product;
      this.advertiser = advertiser;
    }

    public String getAdvertiser() {
      return advertiser;
    }

    public void setAdvertiser(String advertiser) {
      this.advertiser = advertiser;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        advertiser = reader.getAttribute("advertiser",null);

        if (reader.nextNode(Product.class.getSimpleName(), false)) {
            this.product = (Product)reader.readTransportable();
        }
    }

   protected void writeWithLock(TransportWriter writer) {
        if(advertiser!=null)
            writer.attr("advertiser", advertiser);

        if(product!=null)
            writer.write(product);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdLink ad = (AdLink) o;

        if (advertiser != null ? !advertiser.equals(ad.advertiser) : ad.advertiser != null) return false;
        if (product != null ? !product.equals(ad.product) : ad.product != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (product != null ? product.hashCode() : 0);
        result = 31 * result + (advertiser != null ? advertiser.hashCode() : 0);
        return result;
    }

    public String toString() {
        return String.format("(AdLink advertiser:%s generic:%s product:%s)",getAdvertiser(),isGeneric(),getProduct());
    }
}
