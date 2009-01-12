package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractKeyedEntry<T> implements KeyedEntry<T> {
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

        readKey(reader);
    }

    public void write(TransportWriter writer) {
        writeEntry(writer);

        writeKey(writer);
    }

    protected abstract void readEntry(TransportReader reader) throws ParseException;

    protected abstract void readKey(TransportReader reader) throws ParseException;

    protected abstract void writeEntry(TransportWriter writer);

    protected abstract void writeKey(TransportWriter writer);

    
}
