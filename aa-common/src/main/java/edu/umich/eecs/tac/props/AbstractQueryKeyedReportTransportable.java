package edu.umich.eecs.tac.props;

import java.util.ArrayList;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractQueryKeyedReportTransportable<T extends QueryEntry> extends AbstractKeyedEntryListTransportable<Query,T> {


    public AbstractQueryKeyedReportTransportable() {
        this.entries = new ArrayList<T>();
    }

    public int addQuery(Query query) {
        return addKey(query);
    }

    public boolean containsQuery(Query query) {
        return containsKey(query);   
    }

    public Query getQuery(int index) {
        return getEntry(index).getKey();
    }
}
