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
    private List<AdLink> slots;

    public Ranking() {
        slots = new ArrayList<AdLink>();
    }

    public void add(AdLink ad) {
        lockCheck();
        slots.add(ad);
    }

    public void set(int position, AdLink ad) {
        lockCheck();
        slots.set(position, ad);
    }

    public AdLink get(int position) {
        return slots.get(position);
    }

    public int positionForAd(AdLink ad) {
        for(int i = 0; i < size(); i++) {
            if(get(i).equals(ad))
                return i;
        }
        return -1;
    }

    public int size() {
        return slots.size();
    }

    public String toString(){
      StringBuffer sb = new StringBuffer().append('[');
      for (int i = 0, n = size(); i < n; i++) {
        sb.append('[').append(i).append(": ").append(get(i)).append(']');
      }
      sb.append(']');

      return sb.toString();
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        while (reader.nextNode(AdLink.class.getSimpleName(), false)) {
            add((AdLink) reader.readTransportable());
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        for (AdLink ad : slots) {
            writer.write(ad);
        }
    }
}
