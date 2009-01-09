package edu.umich.eecs.tac.props;

import java.text.ParseException;
import org.junit.Test;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import static org.junit.Assert.*;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

/**
 *
 * @author Kemal Eren
 */
public class ManufacturerComponentComposableTest {

    /**
     * Test methods setManufacturer and getManufacturer
     */
    @Test
    public void testManufacturer() {
        ManufacturerComponentComposable instance = new ManufacturerComponentComposable();
        String expResult = null;
        String result = instance.getManufacturer();
        assertEquals(expResult, result);
        
        String manufacturer = "";
        expResult = "";
        instance.setManufacturer(manufacturer);
        result = instance.getManufacturer();
        assertEquals(expResult, result);

        manufacturer = "test_manufacturer";
        expResult = manufacturer;
        instance.setManufacturer(manufacturer);
        result = instance.getManufacturer();
        assertEquals(expResult, result);
    }

    /**
     * Test methods getComonent and setComponent
     */
    @Test
    public void testComponent() {
        ManufacturerComponentComposable instance = new ManufacturerComponentComposable();
        String expResult = null;
        String result = instance.getComponent();
        assertEquals(expResult, result);
        
        String component = "";
        instance.setComponent(component);
        assertEquals(expResult, result);

        component = "test_component";
        instance.setComponent(component);
        assertEquals(expResult, result);
    }

    @Test
    public void testEmptyTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        ManufacturerComponentComposable instance = new ManufacturerComponentComposable();

        byte[] buffer = getBytesForTransportable(writer, instance);
        ManufacturerComponentComposable received = readFromBytes(reader, buffer, "ManufacturerComponentComposable");

        assertNotNull(instance);
        assertNotNull(received);
        assertNull(instance.getComponent());

        instance.lock();
        received = new ManufacturerComponentComposable();

        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "ManufacturerComponentComposable");

        assertNotNull(instance);
        assertNotNull(received);
        assertNull(instance.getComponent());
    }

        @Test
    public void testValidTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        ManufacturerComponentComposable instance = new ManufacturerComponentComposable();
        String advertisor = "advertisor_1";
        String component = "componenent_1";
        instance.setComponent(component);
        instance.setComponent(component);

        byte[] buffer = getBytesForTransportable(writer, instance);
        ManufacturerComponentComposable received = readFromBytes(reader, buffer, "ManufacturerComponentComposable");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance.getComponent(), received.getComponent());
        assertEquals(instance.getManufacturer(), received.getManufacturer());

        instance.lock();
        received = new ManufacturerComponentComposable();

        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "ManufacturerComponentComposable");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance.getComponent(), received.getComponent());
        assertEquals(instance.getManufacturer(), received.getManufacturer());
    }

    @Test
    public void testHashCode() {
        ManufacturerComponentComposable instance = new ManufacturerComponentComposable();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(result, expResult);

        String component = "test_component";
        String manufacturer = "test_manufacturer";
        instance.setComponent(component);
        instance.setManufacturer(manufacturer);

        expResult = manufacturer.hashCode()*31 + component.hashCode();
        result = instance.hashCode();
        assertEquals(result, expResult);
    }

    @Test
    public void testToString() {
        ManufacturerComponentComposable instance = new ManufacturerComponentComposable();
        String expResult = "(ManufacturerComponentComposable (null,null))";
        String result = instance.toString();
        assertEquals(expResult, result);

        String component = "test_component";
        String manufacturer = "test_manufacturer";
        instance.setComponent(component);
        instance.setManufacturer(manufacturer);
        expResult = "(ManufacturerComponentComposable (test_manufacturer,test_component))";

        result = instance.toString();
        assertEquals(expResult, result);
    }

    @Test
    public void testComposableEquals() {
        ManufacturerComponentComposable o = null;
        ManufacturerComponentComposable instance = new ManufacturerComponentComposable();
        boolean expResult = false;
        boolean result = instance.composableEquals(o);
        assertEquals(expResult, result);

        o = new ManufacturerComponentComposable();
        expResult = true;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);

        o.lock();
        expResult = false;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);

        instance.lock();
        expResult = true;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);

        instance = new ManufacturerComponentComposable();
        o = new ManufacturerComponentComposable();
        String manufacturer = "test_manufacturer";
        String component = "test_component";

        instance.setManufacturer(manufacturer);
        expResult = false;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);

        o.setManufacturer(manufacturer);
        expResult = true;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);

        manufacturer = "manufacturer_2";
        o.setManufacturer(manufacturer);
        expResult = false;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);

        instance.setComponent(component);
        expResult = false;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);

        o.setComponent(component);
        expResult = false;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);

        instance.setManufacturer(manufacturer);
        expResult = true;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);

        component = "test_component_2";
        o.setComponent(component);
        expResult = false;
        result = instance.composableEquals(o);
        assertEquals(expResult, result);
    }

}