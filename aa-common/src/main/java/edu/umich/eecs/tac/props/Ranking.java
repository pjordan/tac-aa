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
    private List<Slot> slots;

    public Ranking() {
        slots = new ArrayList<Slot>();
    }

    public void add(AdLink ad, boolean promoted) {
        add(new Slot(ad,promoted));
    }

    public void add(AdLink ad) {
        add(ad,false);
    }

    protected void add(Slot slot) {
        lockCheck();
        slots.add(slot);
    }

    public void set(int position, AdLink ad, boolean promoted) {
        lockCheck();
        slots.set(position, new Slot(ad,promoted));
    }

    public AdLink get(int position) {
        return slots.get(position).getAdLink();
    }

    public boolean isPromoted(int position) {
        return slots.get(position).isPromoted();
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
        while (reader.nextNode(Slot.class.getSimpleName(), false)) {
            add((Slot) reader.readTransportable());
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        for (Slot slot : slots) {
            writer.write(slot);
        }
    }

    public static class Slot extends AbstractTransportable {
        private AdLink adLink;
        private boolean promoted;


        public Slot() {
        }

        public Slot(AdLink adLink, boolean promoted) {
            this.adLink = adLink;
            this.promoted = promoted;
        }

        public AdLink getAdLink() {
            return adLink;
        }

        public void setAdLink(AdLink adLink) {
            this.adLink = adLink;
        }

        public boolean isPromoted() {
            return promoted;
        }

        public void setPromoted(boolean promoted) {
            this.promoted = promoted;
        }


        protected void readWithLock(TransportReader reader) throws ParseException {
            promoted = reader.getAttributeAsInt("lock", 0) > 0;

            if(reader.nextNode(AdLink.class.getSimpleName(), false)) {
                adLink = (AdLink)reader.readTransportable();
            }
        }

        protected void writeWithLock(TransportWriter writer) {
            if(promoted) {
                writer.attr("promoted", 1);
            }

            if(adLink!=null) {
                writer.write(adLink);
            }
        }
    }
}
