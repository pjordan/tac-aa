package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Patrick Jordan
 */
public class RetailCatalogTest {

    @Test
    public void testEmptyRetailCatalog() {
        RetailCatalog catalog = new RetailCatalog();

        assertEquals(catalog.getManufacturers().size(),0);
        assertEquals(catalog.getComponents().size(),0);

    }

    @Test
    public void testUnitRetailCatalog() {
        RetailCatalog catalog = new RetailCatalog();
        assertEquals(catalog.getManufacturers().size(),0);
        assertEquals(catalog.getComponents().size(),0);

        Product product = new Product("m1","c1");
        catalog.addProduct(product);

        assertEquals(catalog.getManufacturers().size(),1);
        assertEquals(catalog.getComponents().size(),1);
        assertEquals(catalog.size(),1);

        for(Product p : catalog) {
            p.equals(new Product("m1","c1"));
        }
    }
}
