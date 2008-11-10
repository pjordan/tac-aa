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
    private Map<Ad,Double> prices;


    public Pricing() {
        prices = new HashMap<Ad,Double>();
    }

    public void setPrice(Ad ad, double price) {
        lockCheck();
        prices.put(ad,price);
    }

    public double getPrice(Ad ad) {
        Double price = prices.get(ad);

        if(price==null)
            return Double.NaN;
        else
            return price;
    }

    public Set<Ad> ads() {
        return prices.keySet();
    }


    protected void readWithLock(TransportReader reader) throws ParseException {
        prices.clear();
        while(reader.nextNode("PriceEntry", false)) {
            double price = reader.getAttributeAsDouble("price",Double.NaN);
            Ad ad = null;
            if(reader.nextNode(Ad.class.getSimpleName(),false)) {
                ad = (Ad)reader.readTransportable();
            }

            if(ad!=null) {
                setPrice(ad,price);
            }
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        for (Map.Entry<Ad,Double> entry : prices.entrySet()) {
            writer. node("PriceEntry");
            if(entry.getValue()!=null)
                writer.attr("price",entry.getValue());
            if(entry.getKey()!=null)
                writer.write(entry.getKey());
            writer.endNode("PriceEntry");
        }
    }
}
