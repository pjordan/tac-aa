package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractTransportableEntry<T extends Transportable> extends AbstractKeyedEntry<T> {

    protected void readKey(TransportReader reader) throws ParseException {
        if (reader.nextNode(keyNodeName(), false)) {
            setKey((T)reader.readTransportable());
        }
    }

    protected void writeKey(TransportWriter writer) {
        if (getKey() != null) {
            writer.write( getKey() );
        }
    }

    protected abstract String keyNodeName();
}
