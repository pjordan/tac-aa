package edu.umich.eecs.tac.props;

/**
 * The product class represents a manufacturer and component pair.
 *
 * @author Patrick Jordan
 */
public class Product extends ManufacturerComponentComposable {

    /**
     * Creates a product without the manufacturer nor component specified.
     */
    public Product() {
        calculateHashCode();
    }

    /**
     * Creates a product with the manufacturer and component.
     *
     * @param manufacturer the manufacturer
     * @param component    the component
     */
    public Product(String manufacturer, String component) {
        setManufacturer(manufacturer);
        setComponent(component);
	}
}
