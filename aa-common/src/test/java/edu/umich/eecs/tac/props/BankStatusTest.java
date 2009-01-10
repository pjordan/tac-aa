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
public class BankStatusTest {

    @Test
    public void testIsLocked() {
        BankStatus instance = new BankStatus();
        boolean expResult = false;
        boolean result = instance.isLocked();
        assertEquals(expResult, result);
    }

    @Test
    public void testLock() {
        BankStatus instance = new BankStatus();
        instance.lock();
        boolean expResult = true;
        boolean result = instance.isLocked();
        assertEquals(expResult, result);
    }

    @Test
    public void testAccountBalance() {
        BankStatus instance = new BankStatus();
        double expResult = 0.0;
        double result = instance.getAccountBalance();
        assertEquals(expResult, result);
    
        double b = 0.0;
        instance.setAccountBalance(b);
        result = instance.getAccountBalance();
        assertEquals(b, result);

        b = 100.5;
        instance.setAccountBalance(b);
        result = instance.getAccountBalance();
        assertEquals(b, result);

        instance = new BankStatus(100.0);
        assertEquals(instance.getAccountBalance(), 100.0);
    }

    @Test
    public void testToString() {
        BankStatus instance = new BankStatus();
        String expResult = "BankStatus[0.0]";
        String result = instance.toString();
        assertEquals(expResult, result);

        double b = 10.5;
        instance.setAccountBalance(b);
        expResult = "BankStatus[10.5]";
        result = instance.toString();
        assertEquals(expResult, result);
    }

    @Test
    public void testValidTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        BankStatus instance = new BankStatus();
        instance.setAccountBalance(100.5);

        byte[] buffer = getBytesForTransportable(writer, instance);
        BankStatus received = readFromBytes(reader, buffer, "BankStatus");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance.getAccountBalance(), received.getAccountBalance());

        instance.lock();
        received = new BankStatus();

        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "BankStatus");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance.getAccountBalance(), received.getAccountBalance());
    }

    @Test
    public void testEmptyTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        BankStatus instance = new BankStatus();

        byte[] buffer = getBytesForTransportable(writer, instance);
        BankStatus received = readFromBytes(reader, buffer, "BankStatus");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance.getAccountBalance(), received.getAccountBalance());

        instance.lock();
        received = new BankStatus();

        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "BankStatus");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance.getAccountBalance(), received.getAccountBalance());
    }


    @Test(expected = IllegalStateException.class)
    public void testWriteToLocked() {
        BankStatus instance = new BankStatus();
        instance.lock();
        instance.setAccountBalance(100.0);
    }
}