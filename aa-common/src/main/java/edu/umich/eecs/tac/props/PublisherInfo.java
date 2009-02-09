package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class contains the publisher information released to the advertisers at the beginning of the game.
 *
 * @author Patrick Jordan
 */
public class PublisherInfo extends AbstractTransportable {
    /**
     * The squashing parameter used in the auctions.
     */
    private double squashingParameter;

    /**
     * Returns the squashing parameter value used in the simulation.
     * @return the squashing parameter value used in the simulation.
     */
    public final double getSquashingParameter() {
        return squashingParameter;
    }

    /**
     * Sets the squashing parameter value used in the simulation.
     *
     * @param squashingParameter the squashing parameter value used in the simulation.
     */
    public final void setSquashingParameter(final double squashingParameter) {
        lockCheck();
        this.squashingParameter = squashingParameter;
    }

    /**
     * Reads the squashing parameter value from the reader.
     * @param reader the reader to read data from.
     *
     * @throws ParseException if an exception is thrown when reading the attribute
     */
    @Override
    protected final void readWithLock(final TransportReader reader) throws ParseException {
        squashingParameter = reader.getAttributeAsDouble("squashingParameter", 0.0);
    }

    /**
     * Writes the squashing parameter value to the writer.
     * @param writer the writer to write data to.
     */
    @Override
    protected final void writeWithLock(final TransportWriter writer) {
        writer.attr("squashingParameter", squashingParameter);
    }
}
