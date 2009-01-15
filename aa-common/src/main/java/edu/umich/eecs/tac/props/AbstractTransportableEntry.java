package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class provides a skeletal implementation of the {@link AbstractKeyedEntry} abstract class, where
 * the key is a {@link Transportable} object.
 * 
 * @author Patrick Jordan
 */
public abstract class AbstractTransportableEntry<T extends Transportable> extends AbstractKeyedEntry<T> {

    /**
     * Reads in the key from the {@link TransportReader reader}.
     *
     * @param reader the reader to read the data in.
     *
     * @throws ParseException if a parse exception occurs when reading in the key.
     */
    protected void readKey(TransportReader reader) throws ParseException {
        if (reader.nextNode(keyNodeName(), false)) {
            setKey((T)reader.readTransportable());
        }
    }

    /**
     * Writes the key out to the {@link TransportWriter writer},
     *
     * @param writer the writer to write the data out to.
     */
    protected void writeKey(TransportWriter writer) {
        if (getKey() != null) {
            writer.write( getKey() );
        }
    }

    /**
     * Returns the transport name of the key node for externalization. 
     *
     * @return the transport name of the key node.
     */
    protected abstract String keyNodeName();
}
