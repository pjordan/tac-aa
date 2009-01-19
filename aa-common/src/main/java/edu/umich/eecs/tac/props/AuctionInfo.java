package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class AuctionInfo extends AbstractTransportable {
    private int promotedSlots;
    private int regularSlots;
    private double promotedReserve;
    private double regularReserve;
    private double promotedSlotBonus;
    
    public AuctionInfo() {
    }

    public int getPromotedSlots() {
        return promotedSlots;
    }

    public void setPromotedSlots(int promotedSlots) {
        lockCheck();
        this.promotedSlots = promotedSlots;
    }

    public double getPromotedReserve() {
        return promotedReserve;
    }

    public void setPromotedReserve(double promotedReserve) {
        lockCheck();
        this.promotedReserve = promotedReserve;
    }

    public double getRegularReserve() {
        return regularReserve;
    }

    public void setRegularReserve(double regularReserve) {
        lockCheck();
        this.regularReserve = regularReserve;
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
        promotedReserve = reader.getAttributeAsDouble("promotedReserve", 0.0);
        regularReserve = reader.getAttributeAsDouble("regularReserve", 0.0);
        promotedSlots = reader.getAttributeAsInt("promotedSlots", 0);
        regularSlots = reader.getAttributeAsInt("regularSlots", 0);
        promotedSlotBonus = reader.getAttributeAsDouble("promotedSlotBonus", 0.0);
    }

    protected void writeWithLock(TransportWriter writer) {
        writer.attr("promotedReserve", promotedReserve);
        writer.attr("regularReserve", regularReserve);
        writer.attr("promotedSlots", promotedSlots);
        writer.attr("regularSlots", regularSlots);
        writer.attr("promotedSlotBonus", promotedSlotBonus);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuctionInfo that = (AuctionInfo) o;

        if (Double.compare(that.promotedReserve, promotedReserve) != 0) return false;
        if (promotedSlots != that.promotedSlots) return false;
        if (Double.compare(that.promotedSlotBonus, promotedSlotBonus) != 0) return false;
        if (Double.compare(that.regularReserve, regularReserve) != 0) return false;
        if (regularSlots != that.regularSlots) return false;

        return true;
    }

    public int hashCode() {
        int result;
        long temp;
        result = promotedSlots;
        result = 31 * result + regularSlots;
        temp = promotedReserve != +0.0d ? Double.doubleToLongBits(promotedReserve) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = regularReserve != +0.0d ? Double.doubleToLongBits(regularReserve) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = promotedSlotBonus != +0.0d ? Double.doubleToLongBits(promotedSlotBonus) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
