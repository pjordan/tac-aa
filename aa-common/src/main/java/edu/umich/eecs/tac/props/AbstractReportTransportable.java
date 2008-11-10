package edu.umich.eecs.tac.props;

import java.util.ArrayList;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractReportTransportable<T extends QueryEntry> extends AbstractListCompositeEntryTransportable<Query,T> {


    public AbstractReportTransportable() {
        this.entries = new ArrayList<T>();
    }

    public int addQuery(Query query) {
        return addKey(query);
    }

    public boolean containsQuery(Query query) {
        return containsKey(query);   
    }
}
