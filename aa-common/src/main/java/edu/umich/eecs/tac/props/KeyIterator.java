package edu.umich.eecs.tac.props;

import java.util.Iterator;

/**
 * Key iterator provides an iteration method that wraps a keyed entry iterator and returns the keys of those entries.
 *
 * @param <T> the key class
 *
 * @author Patrick Jordan
 */
public class KeyIterator<T> implements Iterator<T> {
    /**
     * The delegatee.
     */
    private Iterator< ? extends KeyedEntry< ? extends T>> delegateIterator;

    /**
     * Create a new key iterator that delegates to the supplied iterator.
     *
     * @param delegateIterator a new key iterator that delegates to the supplied iterator.
     */
    public KeyIterator(final Iterator< ? extends KeyedEntry< ? extends T>> delegateIterator) {

        if (delegateIterator == null) {
            throw new NullPointerException("delegate iterator cannot be null");
        }

        this.delegateIterator = delegateIterator;
    }

    /**
     * Returns whether another key is available.  This will return <code>true</code> if the delegated iterator has
     * another entry.
     *
     * @return whether another key is available.
     */
    public final boolean hasNext() {
        return delegateIterator.hasNext();
    }

    /**
     * Returns tne next key.
     * @return tne next key.
     */
    public final T next() {
        return delegateIterator.next().getKey();
    }

    /**
     * Throws an {@link UnsupportedOperationException} if called.
     *
     * @throws UnsupportedOperationException if the method is invoked.
     */
    public final void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "remove is not supported in this iterator");
    }
}
