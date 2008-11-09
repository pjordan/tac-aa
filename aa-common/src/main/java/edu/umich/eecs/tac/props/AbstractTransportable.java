package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.io.Serializable;
import java.text.ParseException;

/**
 * This abstract transportable allows inheritence of the locking mechanism.  Inheriting classes should lock on a read
 * and issue {@link #lockCheck} when setting attributes.
 *  
 *
 * @author Patrick Jordan
 */
public abstract class AbstractTransportable implements Transportable, Serializable {
    /**
     * The locked variable designates whether or not the query is mutable.
     */
    private boolean locked;

    /**
     * Make the query immutable.
     */
    public void lock() {
        locked = true;
    }

    /**
     * Check whether the query is immutable
     *
     * @return <code>true</code> if the query is locked, <code>false</code> otherwise.
     */
    protected final boolean isLocked() {
        return locked;
    }

    /**
     * Before writing an attribute value, lockCheck should be called.  This method will throw an illegal state
     * exception if a write is called on a locked query.
     *
     * @throws IllegalStateException throws exception if object is locked.
     */
    protected final void lockCheck() throws IllegalStateException {
        if (isLocked()) {
            throw new IllegalStateException("locked");
        }
    }

    public final void read(TransportReader reader) throws ParseException {
        lockCheck();

        boolean lock = reader.getAttributeAsInt("lock", 0) > 0;

        readWithLock(reader);

        if (lock) {
            lock();
        }
    }

    public final void write(TransportWriter writer) {
        if (isLocked()) {
            writer.attr("lock", 1);
        }

        writeWithLock(writer);
    }


    public final String getTransportName() {
        return this.getClass().getSimpleName();
    }

    protected abstract void readWithLock(TransportReader reader) throws ParseException;

    protected abstract void writeWithLock(TransportWriter writer);
}
