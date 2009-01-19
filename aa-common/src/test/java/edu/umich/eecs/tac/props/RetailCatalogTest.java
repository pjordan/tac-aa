package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.RetailCatalog.RetailCatalogEntry;
import java.text.ParseException;
import org.junit.Test;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import static org.junit.Assert.*;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

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

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBounds() {
        RetailCatalog catalog = new RetailCatalog();
        catalog.getSalesProfit(-1);

    }

    @Test
    public void testSalesProfitForUncontainedProduct() {
        RetailCatalog catalog = new RetailCatalog();
        assertEquals(catalog.getSalesProfit(null),0.0);

    }

    @Test
    public void testUnitRetailCatalog() {
        RetailCatalog catalog = new RetailCatalog();
        assertEquals(catalog.getManufacturers().size(),0);
        assertEquals(catalog.getComponents().size(),0);

        Product product = new Product("m1","c1");
        catalog.addProduct(product);
        catalog.setSalesProfit(product, 10.5);
        Product product2 = new Product("m1","c2");
        catalog.addProduct(product2);
        catalog.setSalesProfit(1, 15.5);

        Product product3 = new Product("m2","c3");
        catalog.setSalesProfit(product3, 15.5);

        assertEquals(catalog.getManufacturers().size(),2);
        assertEquals(catalog.getComponents().size(),3);
        assertEquals(catalog.size(),3);

        assertEquals(catalog.getSalesProfit(product), 10.5);
        assertEquals(catalog.getSalesProfit(1), 15.5);
        assertEquals(catalog.getSalesProfit(new Product()), 0.0);


        for(Product p : catalog) {
            p.equals(new Product("m1","c1"));
        }

        assertEquals(catalog.entryClass(), RetailCatalogEntry.class);
    }
    
    @Test
    public void testEmptyTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        RetailCatalog instance = new RetailCatalog();

        byte[] buffer = getBytesForTransportable(writer, instance);
        RetailCatalog received = readFromBytes(reader, buffer, "RetailCatalog");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(0, received.size());

        instance.lock();
        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "RetailCatalog");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(0, received.size());
    }
    
    @Test
    public void testValidTransport() throws ParseException, IndexOutOfBoundsException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        RetailCatalog instance = new RetailCatalog();
        Product product = new Product("m1", "c1");
        instance.addProduct(product);

        byte[] buffer = getBytesForTransportable(writer, instance);
        RetailCatalog received = readFromBytes(reader, buffer, "RetailCatalog");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(received.getManufacturers().size(),1);
        assertEquals(received.getComponents().size(),1);
        assertEquals(received.size(),1);

        instance.lock();
        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "RetailCatalog");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(received.getManufacturers().size(), 1);
        assertEquals(received.getComponents().size(),1);
        assertEquals(received.size(),1);
        assertTrue(received.isLocked());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveEntry() {
        new RetailCatalog().removeEntry(0);
    }
}
