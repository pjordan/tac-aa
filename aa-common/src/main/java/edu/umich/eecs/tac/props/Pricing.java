package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class Pricing extends AbstractTransportable {
    private Map<AdLink,Double> prices;


    public Pricing() {
        prices = new HashMap<AdLink,Double>();
    }

    public void setPrice(AdLink ad, double price) throws NullPointerException {
        lockCheck();

        if(ad==null)
            throw new NullPointerException("ad cannot be null");
        
        prices.put(ad,price);
    }

    public double getPrice(AdLink ad) {
        Double price = prices.get(ad);

        if(price==null)
            return Double.NaN;
        else
            return price;
    }

    public Set<AdLink> ads() {
        return prices.keySet();
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        prices.clear();
        while(reader.nextNode("PriceEntry", false)) {
            double price = reader.getAttributeAsDouble("price",Double.NaN);
            AdLink ad = null;
            if(reader.nextNode(AdLink.class.getSimpleName(),false)) {
                ad = (AdLink)reader.readTransportable();
            }

            if(ad!=null) {
                setPrice(ad,price);
            }
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        for (Map.Entry<AdLink,Double> entry : prices.entrySet()) {
            writer. node("PriceEntry");
            if(entry.getValue()!=null)
                writer.attr("price",entry.getValue());
            if(entry.getKey()!=null)
                writer.write(entry.getKey());
            writer.endNode("PriceEntry");
        }
    }
}
