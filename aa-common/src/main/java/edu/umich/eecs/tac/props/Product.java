package edu.umich.eecs.tac.props;

/**
 * @author Patrick Jordan
 */
public class Product extends ManufacturerComponentComposable {

    public Product() {
        calculateHashCode();
    }

    public Product(String manufacturer, String component) {
        setManufacturer(manufacturer);
        setComponent(component);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || hashCode() != o.hashCode() || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return composableEquals(product);
    }
}
