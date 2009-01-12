package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractStringEntry extends AbstractKeyedEntry<String> {
    public static final String KEY_NODE = "AbstractStringEntryKeyNode";
    public static final String KEY_ATTRIBUTE = "AbstractStringEntryKey";

    protected void readKey(TransportReader reader) throws ParseException {
        if(reader.nextNode(KEY_NODE,true)) {
            setKey(reader.getAttribute(KEY_ATTRIBUTE, null));
        }
    }

    protected void writeKey(TransportWriter writer) {
        writer.node(KEY_NODE);

        if (getKey() != null) {

            writer.attr(KEY_ATTRIBUTE, getKey());

        }

        writer.endNode(KEY_NODE);
    }
}
