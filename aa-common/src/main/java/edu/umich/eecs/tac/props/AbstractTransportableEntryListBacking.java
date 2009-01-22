package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

import java.util.*;
import java.text.ParseException;

/**
 * This class provides a skeletal implementation of a list of transportables.
 * The class if backed by an {@link ArrayList}.
 *
 * @author Patrick Jordan
 */
public abstract class AbstractTransportableEntryListBacking<S extends Transportable>
        extends AbstractTransportable {
    protected List<S> entries;

    /**
     * Sole constructor. (For invocation by subclass constructors, typically
     * implicit.)
     * <p/>
     * Constructs an empty backing list with an initial capacity of ten.
     */
    protected AbstractTransportableEntryListBacking() {
        this.entries = new ArrayList<S>();
    }

    /**
     * Return the number of entries in the list.
     *
     * @return the number of entries in the list.
     */
    public int size() {
        return entries.size();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("(");
        builder.append(this.getClass().getSimpleName());

        for (S entry : entries) {
            builder.append(' ').append(entry);
        }
        builder.append(')');

        return builder.toString();
    }

    /**
     * Reads a list of entry transportables from the reader.
     *
     * @param reader the reader to read the data in.
     * @throws ParseException if a parse exception occurs when reading in the entries.
     */
    protected void readWithLock(TransportReader reader) throws ParseException {
        while (reader.nextNode(entryClass().getSimpleName(), false)) {
            addEntry((S) reader.readTransportable());
        }
    }

    /**
     * Writes a list of entry transportables to the writer.
     *
     * @param writer the writer to write the data out to.
     */
    protected void writeWithLock(TransportWriter writer) {
        for (S reportEntry : entries) {
            writer.write(reportEntry);
        }
    }

    /**
     * Returns the entry at the specified index in this list.
     *
     * @param index the index of entry to return
     * @return the entry at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   <code>(index < 0 || index >= size())</code>.
     */
    protected S getEntry(int index) {
        return entries.get(index);
    }

    /**
     * Removes the entry at the specified index in this list. Shifts any
     * subsequent entries to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to removed.
     * @throws IllegalStateException if the object is locked
     */
    protected void removeEntry(int index) {
        lockCheck();

        entries.remove(index);
    }

    /**
     * Appends the specified entry to the end of this list.
     *
     * @param entry the entry to be appended to the list
     * @return the index of the new entry. <code>-1</code> if the entry was not
     *         added.
     * @throws IllegalStateException if the object is locked
     */
    protected int addEntry(S entry) {
        lockCheck();

        // This will always return true for an ArrayList
        entries.add(entry);

        return size() - 1;
    }

    /**
     * Returns class of the entries. The {@link Class#getSimpleName()} simple
     * name} of the class will determine how the entries are read in by the
     * {@link TransportReader}. Implementing classes should return the class of
     * the generic parameter <code>T</code>.
     *
     * @return the class of the entries.
     */
	protected abstract Class entryClass();
}
