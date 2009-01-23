package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class contains the auction reserve information released to the user and publisher agents at the begining of the
 * game.
 *
 * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
 * @author Patrick Jordan
 */
public class ReserveInfo extends AbstractTransportable {
    /**
     * The promoted reserve score (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private double promotedReserve;
    /**
     * The regular reserve score (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private double regularReserve;

    /**
     * Returns the promoted reserve score.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     * @return the promoted reserve score.
     */
    public final double getPromotedReserve() {
        return promotedReserve;
    }

    /**
     * Sets the promoted reserve score.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     * @param promotedReserve the promoted reserve score.
     */
    public final void setPromotedReserve(final double promotedReserve) {
        lockCheck();
        this.promotedReserve = promotedReserve;
    }

    /**
     * Returns the regular reserve score.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     * @return the regular reserve score.
     */
    public final double getRegularReserve() {
        return regularReserve;
    }

    /**
     * Returns the regular reserve score.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     * @param regularReserve the regular reserve score.
     */
    public final void setRegularReserve(final double regularReserve) {
        lockCheck();
        this.regularReserve = regularReserve;
    }

    /**
     * Reads the reserve parameter information from the reader.
     * @param reader the reader to read data from.
     * @throws ParseException if exception occured while reading parameters.
     */
    @Override
    protected final void readWithLock(final TransportReader reader) throws ParseException {
        promotedReserve = reader.getAttributeAsDouble("promotedReserve", 0.0);
        regularReserve = reader.getAttributeAsDouble("regularReserve", 0.0);
    }

    /**
     * Writes the reserve parameter information to the writer.
     * @param writer the writer to write data to.
     */
    @Override
    protected final void writeWithLock(final TransportWriter writer) {
        writer.attr("promotedReserve", promotedReserve);
        writer.attr("regularReserve", regularReserve);
    }

    /**
     * Checks to see if the parameter configuration is the same.
     *
     * @param o the object to check equality
     * @return whether the object has the same parameter configuration.
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o)  {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReserveInfo that = (ReserveInfo) o;

        return Double.compare(that.promotedReserve, promotedReserve) == 0
               && Double.compare(that.regularReserve, regularReserve) == 0;

    }

    /**
     * Returns the hash code from the reserve parameters.
     * @return the hash code from the reserve parameters.
     */
    @Override
    public final int hashCode() {
        int result;
        long temp;
        temp = promotedReserve != +0.0d ? Double.doubleToLongBits(promotedReserve) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = regularReserve != +0.0d ? Double.doubleToLongBits(regularReserve) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
