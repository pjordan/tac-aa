package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class SlotInfo extends AbstractTransportable {
    private int promotedSlots;
    private int regularSlots;
    private double promotedSlotBonus;

    public SlotInfo() {
    }

    public int getPromotedSlots() {
        return promotedSlots;
    }

    public void setPromotedSlots(int promotedSlots) {
        lockCheck();
        this.promotedSlots = promotedSlots;
    }

    public int getRegularSlots() {
        return regularSlots;
    }

    public void setRegularSlots(int regularSlots) {
        lockCheck();
        this.regularSlots = regularSlots;
    }

    public double getPromotedSlotBonus() {
        return promotedSlotBonus;
    }

    public void setPromotedSlotBonus(double promotedSlotBonus) {
        lockCheck();
        this.promotedSlotBonus = promotedSlotBonus;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        promotedSlots = reader.getAttributeAsInt("promotedSlots", 0);
        regularSlots = reader.getAttributeAsInt("regularSlots", 0);
        promotedSlotBonus = reader.getAttributeAsDouble("promotedSlotBonus",
                0.0);
    }

    protected void writeWithLock(TransportWriter writer) {
        writer.attr("promotedSlots", promotedSlots);
        writer.attr("regularSlots", regularSlots);
        writer.attr("promotedSlotBonus", promotedSlotBonus);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SlotInfo slotInfo = (SlotInfo) o;

        if (Double.compare(slotInfo.promotedSlotBonus, promotedSlotBonus) != 0)
            return false;
        if (promotedSlots != slotInfo.promotedSlots)
            return false;
        if (regularSlots != slotInfo.regularSlots)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        long temp;
        result = promotedSlots;
        result = 31 * result + regularSlots;
        temp = promotedSlotBonus != +0.0d ? Double
                .doubleToLongBits(promotedSlotBonus) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
