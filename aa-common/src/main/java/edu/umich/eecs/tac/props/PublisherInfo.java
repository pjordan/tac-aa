package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class contains the publisher information released to the advertisers at
 * the begining of the game.
 *
 * @author Patrick Jordan
 */
public class PublisherInfo extends AbstractTransportable {
    private double squashingParameter;

    public double getSquashingParameter() {
        return squashingParameter;
    }

    public void setSquashingParameter(double squashingParameter) {
        lockCheck();
        this.squashingParameter = squashingParameter;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        squashingParameter = reader.getAttributeAsDouble("squashingParameter",
                0.0);
    }

    protected void writeWithLock(TransportWriter writer) {
        writer.attr("squashingParameter", squashingParameter);
    }
}
