package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class Ranking extends AbstractTransportable {
    private List<Ad> slots;


    public Ranking() {
        slots = new ArrayList<Ad>();
    }

    public void add(Ad ad) {
        slots.add(ad);
    }

    public void set(int position, Ad ad) {
        slots.set(position, ad);
    }

    public Ad get(int position) {
        return slots.get(position);
    }

    public int positionForAd(Ad ad) {
        for(int i = 0; i < size(); i++) {
            if(get(i).equals(ad))
                return i;
        }

        return -1;
    }

    public int size() {
        return slots.size();
    }


    protected void readWithLock(TransportReader reader) throws ParseException {
        while (reader.nextNode(Ad.class.getSimpleName(), false)) {
            add((Ad) reader.readTransportable());
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        for (Ad ad : slots) {
            writer.write(ad);
        }
    }
}
