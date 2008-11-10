package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.io.Serializable;
import java.text.ParseException;

/**
 * The query class represents a set of information the user is revealing during a search.  A query can contain
 * a manufacturer and a component, a manufacturer, a component, or neither.
 *
 * @author Patrick Jordan
 */
public class Query extends ManufacturerComponentComposable {



    public Query() {
        calculateHashCode();
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || hashCode() != o.hashCode()|| getClass() != o.getClass()) return false;

        Query query = (Query) o;
        
        return composableEquals(query);
    }


}
