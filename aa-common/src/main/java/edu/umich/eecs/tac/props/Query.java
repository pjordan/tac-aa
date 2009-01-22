package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.io.Serializable;
import java.text.ParseException;

/**
 * The query class represents a set of information the user is revealing during
 * a search. A query can contain a manufacturer and a component, a manufacturer,
 * a component, or neither.
 * 
 * @author Patrick Jordan, Lee Callender
 */
public class Query extends ManufacturerComponentComposable {

	/**
	 * Creates a new query with the given manufacturer and component.
	 * 
	 * @param manufacturer
	 *            the manufacturer in the query
	 * @param component
	 *            the component in the query
	 */
	public Query(String manufacturer, String component) {
		this.manufacturer = manufacturer;
		this.component = component;
		calculateHashCode();
	}

	/**
	 * Creates a new query with without manufacturer and component information.
	 */
	public Query() {
		calculateHashCode();
	}

	/**
	 * Returns the query type of the query. This method delegates to
	 * {@link QueryType#value(Query)}.
	 * 
	 * @return the query type of the query.
	 */
	public QueryType getType() {
		return QueryType.value(this);
	}
}
