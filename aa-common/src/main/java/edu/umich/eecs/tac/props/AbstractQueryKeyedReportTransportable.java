package edu.umich.eecs.tac.props;

/**
 * This class provides a skeletal implementation of the {@link AbstractKeyedEntryList} abstract class, where the key is
 * a {@link Query} object.
 *
 * @param <T> the query entry class
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
    }

    /**
     * Add a query key.
     *
     * @param query the query key to be added.
     * @return the index of the key
     */
    public final int addQuery(final Query query) {
        return addKey(query);
    }

    /**
     * Check whether the query key exists in the key set.
     *
     * @param query the query to test containment.
     * @return <code>true</code> if the query key exists in the key set and
     *         <code>false</code> otherwise.
     */
    public final boolean containsQuery(final Query query) {
        return containsKey(query);
    }

    /**
     * Get the query key at the specified index.
     *
     * @param index the key index.
     * @return the query key at the specified index.
     */
    public final Query getQuery(final int index) {
        return getKey(index);
    }
}
