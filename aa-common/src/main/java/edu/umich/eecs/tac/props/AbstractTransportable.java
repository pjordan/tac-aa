package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.io.Serializable;
import java.text.ParseException;

/**
 * This class provides a skeletal implementation of the {@link Transportable}
 * interface by providing a locking mechanism. Inheriting classes should issue
 * {@link #lockCheck} when setting attributes.
 * <p/>
 * Inheriting classes should implement the {@link #readWithLock} and
 * {@link #writeWithLock} methods.
 *
 * @author Patrick Jordan
 */
public abstract class AbstractTransportable implements Transportable,
        Serializable {
    /**
     * The locked variable designates whether or not the transportable is
     * mutable.
     */
    private boolean locked;

    /**
     * Make the transportable immutable.
     */
    public final void lock() {
        locked = true;
    }

    /**
     * Returns whether the transportable is immutable.
     *
     * @return <code>true</code> if the transportable is locked,
     *         <code>false</code> otherwise.
     */
    protected final boolean isLocked() {
        return locked;
    }

    /**
     * Before writing an attribute value, {@link #lockCheck} should be called.
     * This method will throw an {@link IllegalStateException illegal state
     * exception} if a write is called on a locked object.
     *
     * @throws IllegalStateException throws exception if object is locked.
     */
    protected final void lockCheck() throws IllegalStateException {

        if (isLocked()) {
            throw new IllegalStateException("locked");
        }

    }

    /**
     * Reads the state for this transportable from the specified reader.
     *
     * @param reader the reader to read data from
     * @throws ParseException if a parse error occurs
     */
    public final void read(final TransportReader reader) throws ParseException {
        lockCheck();

        boolean lock = reader.getAttributeAsInt("lock", 0) > 0;

        readWithLock(reader);

        if (lock) {
            lock();
        }
    }

    /**
     * Writes the state for this transportable to the specified writer.
     *
     * @param writer the writer to write data to
     */
    public final void write(final TransportWriter writer) {

        if (isLocked()) {

            writer.attr("lock", 1);

        }

        writeWithLock(writer);
    }

    /**
     * Returns the transport name for externalization of an implementing
     * {@link AbstractTransportable} will return the {@link Class#getSimpleName
     * simple name} of the implementing class.
     *
     * @return the transport name
     */
    public final String getTransportName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Reads the state of the {@link Transportable transportable}. Implementing
     * classes should read in attributes first and then any sub-nodes.
     *
     * @param reader the reader to read data from.
     * @throws ParseException if a parse error occurs
     */
    protected abstract void readWithLock(TransportReader reader)
            throws ParseException;

    /**
     * Writes the state of the {@link Transportable transportable}. Implementing
     * classes should write out attributes first and then any sub-nodes.
     *
     * @param writer the writer to write data to.
     */
    protected abstract void writeWithLock(TransportWriter writer);
}
