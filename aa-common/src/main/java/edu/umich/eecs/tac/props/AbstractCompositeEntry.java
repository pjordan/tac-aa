package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractCompositeEntry<T extends ManufacturerComponentComposable> implements CompositeEntry<T> {
    private T key;


    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public String getTransportName() {
        return this.getClass().getSimpleName();
    }

    public void read(TransportReader reader) throws ParseException {
        readEntry(reader);

        if (reader.nextNode(keyNodeName(), false)) {
            this.key = (T) reader.readTransportable();
        }
    }

    public void write(TransportWriter writer) {
        writeEntry(writer);

        if (key != null) {
            writer.write(key);
        }
    }

    protected abstract void readEntry(TransportReader reader) throws ParseException;

    protected abstract void writeEntry(TransportWriter writer);

    protected abstract String keyNodeName();
}
