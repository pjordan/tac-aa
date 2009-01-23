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
	public void testConstructor() {
		AdLink first = new AdLink();
		AdLink second = new AdLink((Ad) null, null);

		assertEquals(first, second);

		first = new AdLink();
		first.setAd(new Ad());

		second = new AdLink(new Ad(), null);

		assertEquals(first, second);
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

		AdLink instance = new AdLink();
		assertTrue(instance.equals(instance));

		assertFalse(instance.equals(new Product()));

		Object o = null;
		assertFalse(instance.equals(o));

		AdLink instance_2 = new AdLink();
		assertTrue(instance.equals(instance_2));

		instance_2.setAdvertiser("abc");
		instance_2.setAd(new Ad(new Product("123", "xyz")));
		assertFalse(instance.equals(instance_2));

		instance.setAdvertiser("abc");
		assertFalse(instance.equals(instance_2));

		instance.setAd(new Ad(new Product("123", "xyz")));
		assertTrue(instance.equals(instance_2));

		instance.setAdvertiser("abcd");
		assertFalse(instance.equals(instance_2));
	}

	@Test
	public void testHashCode() {
		AdLink instance = new AdLink();
		assertEquals(instance.hashCode(), new AdLink().hashCode());
	}

	@Test
	public void testToString() {
		AdLink instance = new AdLink();
		String expResult = "(AdLink advertiser:null ad:null)";
		String result = instance.toString();
		assertEquals(expResult, result);

		String advertiser = "abc";
		instance.setAdvertiser(advertiser);
		Product product = new Product("123", "xyz");
		instance.setAd(new Ad(product));
		expResult = "(AdLink advertiser:abc ad:(Ad generic:false product:(Product (123,xyz))))";
		result = instance.toString();
		assertEquals(expResult, result);
	}

}