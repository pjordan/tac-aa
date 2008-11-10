package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;

import java.io.Serializable;

/**
 * @author Patrick Jordan
 */
public interface CompositeEntry<T extends ManufacturerComponentComposable> extends Serializable, Transportable {
    public T getKey();

}
