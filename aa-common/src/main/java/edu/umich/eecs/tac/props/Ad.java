package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan, Lee Callender
 */
public class Ad extends AbstractTransportable {
    private Product product;
    private String advertiser;

    public Ad() {
    }

    public Ad(Product product, String advertiser){
      this.product = product;
      this.advertiser = advertiser;
    }

    public boolean isGeneric() {
        return product==null;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(String advertiser) {
        this.advertiser = advertiser;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public String toString() {
        return String.format("(Ad generic:%s product:%s)",isGeneric(),getProduct());
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ad ad = (Ad) o;

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
}
