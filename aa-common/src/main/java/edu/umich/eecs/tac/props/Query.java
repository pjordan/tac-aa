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
public class Query extends AbstractTransportable {
    private static final String MANUFACTURER_KEY = "manufacturer";
    private static final String COMPONENT_KEY = "component";
    private static final String QUERY_KEY = "Query";

    
    /**
     * Cached hashcode
     */
    private int hashCode;

    /**
     * The string representing the manufacturer.
     * May be <code>null</code>.
     */
    private String manufacturer;
    
    /**
     * The string representing the component.
     * May be <code>null</code>.
     */
    private String component;


    public Query() {
        calculateHashCode();
    }

    /**
     * The string representing the manufacturer.
     *
     * @return the string representing the manufacturer.  May be <code>null</code>.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Set the string representing the manufacturer.
     *
     * @param manufacturer the string representing the manufacturer.  May be <code>null</code>
     *                     if the manufacturer is not included in the query.
     */
    public void setManufacturer(String manufacturer) {
        lockCheck();
        this.manufacturer = manufacturer;
        calculateHashCode();
    }

    /**
     * The string representing the component.
     *
     * @return the string representing the component.  May be <code>null</code>.
     */
    public String getComponent() {
        return component;
    }

    /**
     * Set the string representing the component.
     *
     * @param component the string representing the component.  May be <code>null</code>
     *                  if the component is not included in the query.
     */
    public void setComponent(String component) {
        lockCheck();
        this.component = component;
        calculateHashCode();
    }



    protected void readWithLock(TransportReader reader) throws ParseException {
        this.setManufacturer(reader.getAttribute(MANUFACTURER_KEY,null));
        this.setComponent(reader.getAttribute(COMPONENT_KEY,null));
        calculateHashCode();
    }

    protected void writeWithLock(TransportWriter writer) {
        if(getManufacturer()!=null)
            writer.attr(MANUFACTURER_KEY, getManufacturer());
        if(getComponent()!=null)
            writer.attr(COMPONENT_KEY, getComponent());        
    }


    public String toString() {
        return String.format("(%s (%s,%s))", QUERY_KEY, getManufacturer(), getComponent());
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || hashCode() != o.hashCode()|| getClass() != o.getClass()) return false;

        Query query = (Query) o;
        
        if (isLocked() != query.isLocked()) return false;
        if (component != null ? !component.equals(query.component) : query.component != null) return false;
        if (manufacturer != null ? !manufacturer.equals(query.manufacturer) : query.manufacturer != null) return false;

        return true;
    }

    public int hashCode() {
        return hashCode;
    }

    private void calculateHashCode() {
        int result;
        result = (manufacturer != null ? manufacturer.hashCode() : 0);
        result = 31 * result + (component != null ? component.hashCode() : 0);        

        hashCode = result;
    }


}
