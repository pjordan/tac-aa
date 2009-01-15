package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;

import java.io.Serializable;

/**
 *
 * This interface designates a {@link edu.umich.eecs.tac.props.KeyedEntry keyed entry} whose key is a query.
 * 
 * @author Patrick Jordan
 */
public interface QueryEntry extends KeyedEntry<Query> {

    /**
     * Returns the query for the entry.
     *
     * @return the query for the entry.
     */
    public Query getQuery();
}
