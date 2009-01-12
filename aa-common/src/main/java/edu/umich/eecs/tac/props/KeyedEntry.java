package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;

import java.io.Serializable;

/**
 * @author Patrick Jordan
 */
public interface KeyedEntry<T> extends Serializable, Transportable {
    public T getKey();
}
