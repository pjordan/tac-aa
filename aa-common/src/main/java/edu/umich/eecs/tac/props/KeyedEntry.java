package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;

import java.io.Serializable;

/**
 * This interface designates a list entry with a key.
 *
 * @param <T> key class
 *
 * @author Patrick Jordan
 */
public interface KeyedEntry<T> extends Serializable, Transportable {

    /**
     * Returns the key for the entry.
     *
     * @return the key for the entry.
     */
    T getKey();
}
