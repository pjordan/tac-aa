/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
 * @author kemal
 */
public class AdLinkTest {
    @Test
    public void testAdvertiser() {
        AdLink instance = new AdLink();
        String result = instance.getAdvertiser();
        assertNull(result);
        instance.setAdvertiser("abc");
        result = instance.getAdvertiser();
        String expResult = "abc";
        assertEquals(expResult, result);
    }

    @Test
    public void testEmptyTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        AdLink instance = new AdLink();

        byte[] buffer = getBytesForTransportable(writer, instance);
        AdLink received = readFromBytes(reader, buffer, "AdLink");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance, received);

        instance.lock();
        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "AdLink");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance, received);
    }
        @Test
    public void testValidTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        Product product = new Product();
        product.setComponent("comp_1");
        product.setManufacturer("man_1");
        AdLink instance = new AdLink(product, "advertiser_1");

        byte[] buffer = getBytesForTransportable(writer, instance);
        AdLink received = readFromBytes(reader, buffer, "AdLink");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance, received);
        
        instance.lock();
        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "AdLink");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance, received);
    }

    @Test
    public void testEquals() {
        Object o = null;
        AdLink instance = new AdLink();
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);

        AdLink instance_2 = new AdLink();
        expResult = true;
        result = instance.equals(instance_2);
        assertEquals(expResult, result);

        instance_2.setAdvertiser("abc");
        instance_2.setProduct(new Product("123", "xyz"));
        expResult = false;
        result = instance.equals(instance_2);
        assertEquals(expResult, result);

        instance.setAdvertiser("abc");
        instance.setProduct(new Product("123", "xyz"));
        expResult = true;
        result = instance.equals(instance_2);
        assertEquals(expResult, result);
        
        instance.setAdvertiser("abcd");
        expResult = false;
        result = instance.equals(instance_2);
        assertEquals(expResult, result);
    }

    @Test
    public void testHashCode() {
        AdLink instance = new AdLink();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);

        String advertiser = "abc";
        instance.setAdvertiser(advertiser);
        Product product = new Product("123", "xyz");
        instance.setProduct(product);
        expResult = 31*product.hashCode() + advertiser.hashCode();
        result = instance.hashCode();
        assertEquals(expResult, result);
    }

    @Test
    public void testToString() {
        AdLink instance = new AdLink();
        String expResult = "(AdLink advertiser:null generic:true product:null)";
        String result = instance.toString();
        assertEquals(expResult, result);

        String advertiser = "abc";
        instance.setAdvertiser(advertiser);
        Product product = new Product("123", "xyz");
        instance.setProduct(product);
        expResult = "(AdLink advertiser:abc generic:false product:(Product (123,xyz)))";
        result = instance.toString();
        assertEquals(expResult, result);
    }

}