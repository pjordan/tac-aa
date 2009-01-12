package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractKeyedEntryListTransportable<T, S extends KeyedEntry<T>> extends AbstractTransportable implements Iterable<T> {
    protected List<S> entries;


    protected AbstractKeyedEntryListTransportable() {
        this.entries = new ArrayList<S>();
    }

    public final int size() {
        return entries.size();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("(");
        builder.append(this.getClass().getSimpleName());

        for (S reportEntry : entries) {
            builder.append(' ').append(reportEntry);
        }
        builder.append(')');

        return builder.toString();
    }


    public final int indexForEntry(T key) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getKey().equals(key))
                return i;
        }
        return -1;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        while (reader.nextNode(entryClass().getSimpleName(), false)) {
            addEntry((S) reader.readTransportable());
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        for (S reportEntry : entries) {
            writer.write(reportEntry);
        }
    }

    public Iterator<T> iterator() {
        return new KeyIterator(entries.iterator());
    }

    public final boolean containsKey(T key) {
        return indexForEntry(key)>-1;
    }

    protected final S getEntry(int index) {
        return entries.get(index);
    }

    protected final void removeEntry(int index) {
        lockCheck();

        entries.remove(index);
    }

    protected int addEntry(S entry) {
        lockCheck();

        if(entries.add(entry))
            return size()-1;
        else
            return -1;
    }

    protected final int addKey(T key) {

        if(key==null) {
            throw new NullPointerException("Key cannot be null");
        }

        return addEntry(createEntry(key));
    }

    protected abstract S createEntry(T key);

    protected abstract Class entryClass();
}
