package edu.umich.eecs.tac.props;

/**
 * This interface designates a {@link edu.umich.eecs.tac.props.KeyedEntry keyed
 * entry} whose key is a query.
 *
 * @author Patrick Jordan
 */
public interface QueryEntry extends KeyedEntry<Query> {

    /**
     * Returns the query for the entry.
     *
     * @return the query for the entry.
     */
    Query getQuery();
}
