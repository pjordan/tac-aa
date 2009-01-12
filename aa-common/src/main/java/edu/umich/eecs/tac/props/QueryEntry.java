package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;

import java.io.Serializable;

/**
 *
 * @author Patrick Jordan
 */
public interface QueryEntry extends KeyedEntry<Query> {
    public Query getQuery();
}
