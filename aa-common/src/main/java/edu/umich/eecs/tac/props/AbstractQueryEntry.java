package edu.umich.eecs.tac.props;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractQueryEntry extends AbstractTransportableEntry<Query> implements QueryEntry {
    public Query getQuery() {
        return getKey();
    }

    protected void setQuery(Query query) {
        setKey(query);
    }


    protected String keyNodeName() {
        return Query.class.getSimpleName();
    }

    
}
