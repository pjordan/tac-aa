package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class ManufacturerComponentComposable extends AbstractTransportable {
    private static final String MANUFACTURER_KEY = "manufacturer";
    private static final String COMPONENT_KEY = "component";
    /**
     * Cached hashcode
     */
    private int hashCode;
    /**
     * The string representing the manufacturer. May be <code>null</code>.
     */
    protected String manufacturer;
    /**
     * The string representing the component. May be <code>null</code>.
     */
    protected String component;

    public ManufacturerComponentComposable() {
        calculateHashCode();
    }

    /**
     * The string representing the manufacturer.
     *
     * @return the string representing the manufacturer. May be
     *         <code>null</code>.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Set the string representing the manufacturer.
     *
     * @param manufacturer the string representing the manufacturer. May be
     *                     <code>null</code> if the manufacturer is not included in the
     *                     query.
     */
    public void setManufacturer(String manufacturer) {
        lockCheck();
        this.manufacturer = manufacturer;
        calculateHashCode();
    }

    /**
     * Returns the string representing the component.
     *
     * @return the string representing the component. May be <code>null</code>.
     */
    public String getComponent() {
        return component;
    }

    /**
     * Sets the string representing the component.
     *
     * @param component the string representing the component. May be
     *                  <code>null</code> if the component is not included in the
     *                  query.
     * @throws IllegalStateException if the object is locked.
     */
    public void setComponent(String component) {
        lockCheck();
        this.component = component;
        calculateHashCode();
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        this.setManufacturer(reader.getAttribute(MANUFACTURER_KEY, null));
        this.setComponent(reader.getAttribute(COMPONENT_KEY, null));
        calculateHashCode();
    }

    protected void writeWithLock(TransportWriter writer) {
        if (getManufacturer() != null)
            writer.attr(MANUFACTURER_KEY, getManufacturer());
        if (getComponent() != null)
            writer.attr(COMPONENT_KEY, getComponent());
    }

    public int hashCode() {
        return hashCode;
    }

    protected void calculateHashCode() {
        int result;
        result = (manufacturer != null ? manufacturer.hashCode() : 0);
        result = 31 * result + (component != null ? component.hashCode() : 0);

        hashCode = result;
    }

    public String toString() {
        return String.format("(%s (%s,%s))", this.getClass().getSimpleName(),
                getManufacturer(), getComponent());
    }

    protected boolean composableEquals(ManufacturerComponentComposable o) {
        if (o == null)
            return false;
        if (isLocked() != o.isLocked())
            return false;
        if (component != null ? !component.equals(o.component)
                : o.component != null)
            return false;
        if (manufacturer != null ? !manufacturer.equals(o.manufacturer)
                : o.manufacturer != null)
            return false;

        return true;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || hashCode() != o.hashCode()
                || getClass() != o.getClass())
            return false;

        return composableEquals((ManufacturerComponentComposable) o);
	}
}
