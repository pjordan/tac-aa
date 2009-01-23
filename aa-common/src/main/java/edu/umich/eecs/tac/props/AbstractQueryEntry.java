package edu.umich.eecs.tac.props;

/**
 * This class provides a skeletal implementation of the {@link QueryEntry}
 * interface.
 *
 * @author Patrick Jordan
 */
public abstract class AbstractQueryEntry extends
        AbstractTransportableEntry<Query> implements QueryEntry {

    /**
     * Returns the query key.
     *
     * @return the query key.
     */
    public final Query getQuery() {
        return getKey();
    }

    /**
     * Sets the key to the given query.
     *
     * @param query the query key.
     */
    public final void setQuery(final Query query) {
        setKey(query);
    }

    /**
     * Returns the transport name of the query key for externalization.
     *
     * @return the {@link Class#getSimpleName() simple name} of the
     *         {@link Query} class.
     */
    protected final String keyNodeName() {
        return Query.class.getSimpleName();
    }
}
