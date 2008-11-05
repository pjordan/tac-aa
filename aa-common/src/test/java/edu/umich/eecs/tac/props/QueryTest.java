package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;
import static edu.umich.eecs.tac.props.TransportableTestUtils.*;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class QueryTest {
    @Test
    public void testConstructor() {
        Query q = new Query();
        assertNotNull(q);
    }

    @Test
    public void testFocusLevelZeroQuery() {
        Query query = new Query();
        assertNotNull(query);
        assertNull(query.getManufacturer());
        assertNull(query.getComponent());
    }

    @Test
    public void testFocusLevelOneQuery() {
        String manufacturer = "Alice";
        String component = "Bob";

        Query manufacturerQuery = new Query();
        assertNotNull(manufacturerQuery);
        assertNull(manufacturerQuery.getManufacturer());
        assertNull(manufacturerQuery.getComponent());
        manufacturerQuery.setManufacturer(manufacturer);
        assertEquals(manufacturerQuery.getManufacturer(), manufacturer);
        assertNull(manufacturerQuery.getComponent());

        Query componentQuery = new Query();
        assertNotNull(componentQuery);
        assertNull(componentQuery.getManufacturer());
        assertNull(componentQuery.getComponent());
        componentQuery.setComponent(component);
        assertEquals(componentQuery.getComponent(), component);
        assertNull(componentQuery.getManufacturer());
    }

    @Test
    public void testFocusLevelTwoQuery() {
        String manufacturer = "Alice";
        String component = "Bob";

        Query query = new Query();
        assertNotNull(query);
        assertNull(query.getManufacturer());
        assertNull(query.getComponent());
        query.setManufacturer(manufacturer);
        query.setComponent(component);
        assertEquals(query.getManufacturer(), manufacturer);
        assertEquals(query.getComponent(), component);
    }

    @Test
    public void testTransportName() {
        assertEquals(new Query().getTransportName(), "query");
    }

    @Test
    public void testToString() {
        Query query = new Query();
        query.setManufacturer("a");
        query.setComponent("b");
        assertEquals(query.toString(), "(query (a,b))");
    }

    @Test
    public void testEquals() {
        Query f0 = new Query();

        Query f1 = new Query();
        f1.setManufacturer("Alice");

        Query f1Duplicate = new Query();
        f1Duplicate.setManufacturer("Alice");

        Query f1NotDuplicate = new Query();
        f1NotDuplicate.setComponent("Bob");

        Query f2 = new Query();
        f2.setManufacturer("Alice");
        f2.setComponent("Bob");

        Query f2Duplicate = new Query();
        f2Duplicate.setManufacturer("Alice");
        f2Duplicate.setComponent("Bob");

        Query f2DuplicateLocked = new Query();
        f2Duplicate.setManufacturer("Alice");
        f2Duplicate.setComponent("Bob");
        f2DuplicateLocked.lock();

        assertNotNull(f0);
        assertEquals(f0, new Query());
        assertEquals(f1, f1Duplicate);
        assertEquals(f2, f2Duplicate);
        assertFalse(f1.equals(f2));
        assertTrue(f1.equals(f1));
        assertFalse(f1.equals(null));
        assertFalse(f1.equals(new Object()));
        assertEquals(f0.hashCode(), new Query().hashCode());
        assertEquals(f1.hashCode(), f1Duplicate.hashCode());
        assertFalse(f1.hashCode() == f1NotDuplicate.hashCode());
        assertFalse(f2.equals(f2DuplicateLocked));
        assertFalse(f1.equals(f1NotDuplicate));
        assertFalse(f1NotDuplicate.equals(f2));
        assertFalse(f1NotDuplicate.equals(f1));
        assertFalse(f1NotDuplicate.equals(f0));
        assertFalse(f0.equals(f1));
        assertFalse(f0.equals(f1NotDuplicate));
        assertFalse(f2.equals(f1));
        assertFalse(f2.equals(f1NotDuplicate));
        assertFalse(f2DuplicateLocked.hashCode() == f2.hashCode());
    }

    @Test
    public void testValidTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        Query query = new Query();
        query.setManufacturer("a");
        query.setComponent("b");


        byte[] buffer = getBytesForTransportable(writer,query);
        Query received = readFromBytes(reader,buffer,"query");


        assertNotNull(query);
        assertNotNull(received);
        assertEquals(query, received);


        Query lockedQuery = new Query();
        query.setManufacturer("aa");
        query.setComponent("b");
        lockedQuery.lock();

        buffer = getBytesForTransportable(writer,lockedQuery);
        received = readFromBytes(reader,buffer,"query");
        
        assertNotNull(lockedQuery);
        assertNotNull(received);
        assertEquals(lockedQuery, received);
    }

    @Test
    public void testEmptyTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        byte[] buffer = writer.getBytes();
        Query received = readFromBytes(reader,buffer,"query");

        assertNull(received);
    }

    @Test(expected = IllegalStateException.class)
    public void testWriteToLocked() {
        Query query = new Query();
        query.lock();
        query.setManufacturer("a");
    }

}
