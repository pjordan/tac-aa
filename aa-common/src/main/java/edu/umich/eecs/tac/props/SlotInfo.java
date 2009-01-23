package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class contains the auction slot information released to the agents at the begining of the game.
 *
 * @author Patrick Jordan
 */
public class SlotInfo extends AbstractTransportable {
    /**
     * The number of promoted slots available for each auction
     * (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private int promotedSlots;
    /**
     * The number of regular slots available for each auction
     * (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private int regularSlots;
    /**
     * The promoted slot bonus (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private double promotedSlotBonus;

    /**
     * Creates a new slot information object.
     */
    public SlotInfo() {
    }

    /**
     * Returns the number of promoted slots.
     * @return the number of promoted slots.
     */
    public final int getPromotedSlots() {
        return promotedSlots;
    }

    /**
     * Sets the number of promoted slots.
     * @param promotedSlots the number of promoted slots.
     */
    public final void setPromotedSlots(final int promotedSlots) {
        lockCheck();
        this.promotedSlots = promotedSlots;
    }

    /**
     * Returns the number of regular slots.
     * @return the number of regular slots.
     */
    public final int getRegularSlots() {
        return regularSlots;
    }

    /**
     * Sets the number of regular slots.
     * @param regularSlots the number of regular slots.
     */
    public final void setRegularSlots(final int regularSlots) {
        lockCheck();
        this.regularSlots = regularSlots;
    }

    /**
     * Returns the promoted slot bonus.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     * @return the promoted slot bonus.
     */
    public final double getPromotedSlotBonus() {
        return promotedSlotBonus;
    }

    /**
     * Sets the promoted slot bonus.
     * @param promotedSlotBonus the promoted slot bonus.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final void setPromotedSlotBonus(final double promotedSlotBonus) {
        lockCheck();
        this.promotedSlotBonus = promotedSlotBonus;
    }

    /**
     * Reads the promoted slot, regular slot, and promoted slot bonus parameters from the reader.
     *
     * @param reader the reader to read data from.
     * @throws ParseException if an exception occurs reading the attributes.
     */
    @Override
    protected final void readWithLock(final TransportReader reader) throws ParseException {
        promotedSlots = reader.getAttributeAsInt("promotedSlots", 0);
        regularSlots = reader.getAttributeAsInt("regularSlots", 0);
        promotedSlotBonus = reader.getAttributeAsDouble("promotedSlotBonus", 0.0);
    }

    /**
     * Writes the promoted slot, regular slot, and promoted slot bonus parameters to the writer.
     *
     * @param writer the writer to write data to.
     */
    @Override
    protected final void writeWithLock(final TransportWriter writer) {
        writer.attr("promotedSlots", promotedSlots);
        writer.attr("regularSlots", regularSlots);
        writer.attr("promotedSlotBonus", promotedSlotBonus);
    }

    /**
     * Checks to see if the parameter configuration is the same.
     *
     * @param o the object to check equality
     * @return whether the object has the same parameter configuration.
     */
    @Override
    public final boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SlotInfo slotInfo = (SlotInfo) o;

        if (Double.compare(slotInfo.promotedSlotBonus, promotedSlotBonus) != 0) {
            return false;
        }

        if (promotedSlots != slotInfo.promotedSlots) {
            return false;
        }

        if (regularSlots != slotInfo.regularSlots) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code from the promoted slot, regular slot, and promoted slot bonus parameters.
     * @return the hash code from the promoted slot, regular slot, and promoted slot bonus parameters.
     */
    @Override
    public final int hashCode() {
        int result;
        long temp;
        result = promotedSlots;
        result = 31 * result + regularSlots;
        temp = promotedSlotBonus != +0.0d ? Double.doubleToLongBits(promotedSlotBonus) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
