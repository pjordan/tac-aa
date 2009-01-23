package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class provides a skeletal implementation of the
 * {@link AbstractKeyedEntry} abstract class, where the key is a
 * {@link se.sics.isl.transport.Transportable Transportable} object.
 *
 * @author Patrick Jordan
 */
public abstract class AbstractStringEntry extends AbstractKeyedEntry<String> {
    /**
     * The key node name used for reading and writing as a transport.
     */
    private static final String KEY_NODE = "AbstractStringEntryKeyNode";

    /**
     * The key attribue used for reading and writing the key as an attribute.
     */
    private static final String KEY_ATTRIBUTE = "AbstractStringEntryKey";

    /**
     * Reads in a "key" node and sets the key to the value of the backing
     * attribute.
     *
     * @param reader the reader to read data from.
     * @throws ParseException if exeption occurs reading the key node and attribute.
     */
    protected final void readKey(final TransportReader reader) throws ParseException {

        // Read in the key node. The node must exist.
        reader.nextNode(KEY_NODE, true);

        // Grab the key attribute value
        setKey(reader.getAttribute(KEY_ATTRIBUTE, null));
    }

    /**
     * Creates and writes a "key" node and sets the backing attribute value to
     * the key.
     *
     * @param writer the writer to write data to.
     */
    protected final void writeKey(final TransportWriter writer) {

        // Create a "key" node.
        writer.node(KEY_NODE);

        // Write the key if it is non-null
        if (getKey() != null) {

            // Write the non-null key atribute
            writer.attr(KEY_ATTRIBUTE, getKey());

        }

        // Close the key node.
        writer.endNode(KEY_NODE);
    }
}
