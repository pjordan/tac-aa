package edu.umich.eecs.tac.props;

import java.util.Iterator;

/**
 * @author Patrick Jordan
 */
public class CompositeIterator<T extends ManufacturerComponentComposable> implements Iterator<T>{
    private Iterator<? extends CompositeEntry<? extends T>> delegateIterator;


    public CompositeIterator(Iterator<? extends CompositeEntry<? extends T>> delegateIterator) {
        if(delegateIterator==null)
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
        throw new UnsupportedOperationException("remvoe is not supported in this iterator");
    }
}
