package edu.umich.eecs.tac.props;

import java.util.Iterator;

/**
 * @author Patrick Jordan
 */
public class KeyIterator<T> implements Iterator<T> {
    private Iterator<? extends KeyedEntry<? extends T>> delegateIterator;

    public KeyIterator(
            Iterator<? extends KeyedEntry<? extends T>> delegateIterator) {
        if (delegateIterator == null)
            throw new NullPointerException("delegate iterator cannot be null");
        this.delegateIterator = delegateIterator;
    }

    public boolean hasNext() {
        return delegateIterator.hasNext();
    }

    public T next() {
        return delegateIterator.next().getKey();
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "remove is not supported in this iterator");
    }
}
