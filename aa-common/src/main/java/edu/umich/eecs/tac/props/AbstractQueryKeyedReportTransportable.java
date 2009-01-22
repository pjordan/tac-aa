package edu.umich.eecs.tac.props;

import java.util.ArrayList;

/**
 * This class provides a skeletal implementation of the
 * {@link AbstractKeyedEntryList} abstract class, where the key is a
 * {@link Query} object.
 *
 * @author Patrick Jordan
 */
public abstract class AbstractQueryKeyedReportTransportable<T extends QueryEntry>
        extends AbstractKeyedEntryList<Query, T> {

    /**
     * Sole constructor. (For invocation by subclass constructors, typically
     * implicit.)
     */
    public AbstractQueryKeyedReportTransportable() {
        this.entries = new ArrayList<T>();
    }

    /**
     * Add a query key.
     *
     * @param query the query key to be added.
     * @return the index of the key
     */
    public int addQuery(Query query) {
        return addKey(query);
    }

    /**
     * Check whether the query key exists in the key set.
     *
     * @param query the query to test containment.
     * @return <code>true</code> if the query key exists in the key set and
     *         <code>false</code> otherwise.
     */
    public boolean containsQuery(Query query) {
        return containsKey(query);
    }

    /**
     * Get the query key at the specified index.
     *
     * @param index the key index.
     * @return the query key at the specified index.
     */
    public Query getQuery(int index) {
		return getKey(index);
	}
}
