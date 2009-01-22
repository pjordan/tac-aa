package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.BinaryTransportReader;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

import java.text.ParseException;
import java.util.Iterator;

/**
 * @author Patrick Jordan
 */
public class BidBundleTest {

	@Test
	public void testBidBundle() {
		BidBundle bundle = new BidBundle();
		Query query = new Query();
		query.setComponent("c1");
		query.setManufacturer("m1");
		Ad ad = new Ad();
		Product product = new Product();
		product.setComponent("c1");
		ad.setProduct(product);

		double bid = 20.1;
		bundle.addQuery(query, bid, ad);

		Query query2 = new Query();
		query2.setComponent("c2");
		query2.setManufacturer("m2");
		Ad ad2 = new Ad();
		Product product2 = new Product();
		product2.setComponent("c2");
		ad2.setProduct(product2);

		double bid2 = 50.5;
		bundle.addQuery(query2, bid2, ad2);

		assertEquals(bundle.size(), 2);

		bundle.setCampaignDailySpendLimit(100.5);
		bundle.setDailyLimit(query, 15.0);
		bundle.setDailyLimit(1, 200.0);

		int index = 0;
		assertEquals(bundle.getQuery(index), query);
		assertEquals(bundle.getBid(query), bid);
		assertEquals(bundle.getBid(index), bid);
		assertEquals(bundle.getAd(query), ad);
		assertEquals(bundle.getAd(index), ad);
		assertEquals(bundle.getCampaignDailySpendLimit(), 100.5);
		assertEquals(bundle.getDailyLimit(query), 15.0);
		assertEquals(bundle.getDailyLimit(1), 200.0);

		bundle.setBid(query, 100.0);
		assertEquals(bundle.getBid(query), 100.0);

		Ad ad3 = new Ad();
		Product product3 = new Product();
		product3.setComponent("c3");
		ad3.setProduct(product3);
		bundle.setAd(query, ad3);

		assertEquals(bundle.getAd(query), ad3);

		Query query4 = new Query();
		query4.setComponent("c4");
		bundle.setBid(query4, 200.0);
		assertEquals(bundle.getBid(query4), 200.0);

		Query query5 = new Query();
		query5.setComponent("c5");
		Ad ad5 = new Ad();
		Product product5 = new Product();
		product.setComponent("c5");
		ad5.setProduct(product5);
		bundle.setAd(query5, ad5);
		assertEquals(bundle.getAd(query5), ad5);

		Query query6 = new Query();
		query6.setComponent("c6");
		bundle.setDailyLimit(query6, 57.2);
		assertEquals(bundle.getDailyLimit(query6), 57.2);

		Query query7 = new Query();
		query7.setComponent("c7");
		Ad ad7 = new Ad();
		Product product7 = new Product();
		product.setComponent("c7");
		ad7.setProduct(product7);
		bundle.setBidAndAd(query7, 64.3, ad7);
		assertEquals(bundle.getAd(query7), ad7);
		assertEquals(bundle.getBid(query7), 64.3);

		bundle.setBidAndAd(query7, 24.5, ad3);
		assertEquals(bundle.getAd(query7), ad3);
		assertEquals(bundle.getBid(query7), 24.5);

		Iterator<Query> i = bundle.iterator();
		assertEquals(i.next().getComponent(), "c1");
		assertEquals(i.next().getComponent(), "c2");

		boolean thrown = false;
		try {
			i.remove();
		} catch (UnsupportedOperationException e) {
			thrown = true;
		}
		if (!thrown)
			fail("remove should not be supported");
	}

	@Test
	public void testGets() {
		BidBundle instance = new BidBundle();
		assertNull(instance.getAd(null));
		assertEquals(instance.getBid(null), Double.NaN);
		assertEquals(instance.getDailyLimit(null), Double.NaN);
	}

	@Test
	public void testValidTransport() throws ParseException {
		BinaryTransportWriter writer = new BinaryTransportWriter();
		BinaryTransportReader reader = new BinaryTransportReader();
		reader.setContext(new AAInfo().createContext());

		BidBundle bundle = new BidBundle();

		assertNotNull(bundle);

		bundle.addQuery(new Query());
		Query q = new Query();
		q.setComponent("c1");
		q.setManufacturer("man1");
		Ad ad = new Ad();
		Product product = new Product();
		product.setComponent("c1");
		product.setManufacturer("man1");
		ad.setProduct(product);
		bundle.addQuery(q, 100.5, ad);

		assertEquals(bundle.size(), 2);

		byte[] buffer = getBytesForTransportable(writer, bundle);
		BidBundle received = readFromBytes(reader, buffer, "BidBundle");

		assertEquals(received.getCampaignDailySpendLimit(), Double.NaN);
		assertNotNull(bundle);
		assertNotNull(received);
		assertEquals(received.size(), 2);

		bundle.lock();
		buffer = getBytesForTransportable(writer, bundle);
		received = readFromBytes(reader, buffer, "BidBundle");

		assertNotNull(bundle);
		assertNotNull(received);
		assertEquals(received.size(), 2);

		assertEquals(bundle.getEntry(0).toString(), received.getEntry(0)
				.toString());
		assertEquals(bundle.toString(), received.toString());

	}

	@Test
	public void testEmptyTransport() throws ParseException {
		BinaryTransportWriter writer = new BinaryTransportWriter();
		BinaryTransportReader reader = new BinaryTransportReader();
		reader.setContext(new AAInfo().createContext());

		BidBundle bundle = new BidBundle();

		assertNotNull(bundle);
		assertEquals(bundle.size(), 0);

		byte[] buffer = getBytesForTransportable(writer, bundle);
		BidBundle received = readFromBytes(reader, buffer, "BidBundle");

		assertNotNull(bundle);
		assertNotNull(received);
		assertEquals(received.size(), 0);

		bundle.lock();
		buffer = getBytesForTransportable(writer, bundle);
		received = readFromBytes(reader, buffer, "BidBundle");

		assertNotNull(bundle);
		assertNotNull(received);
		assertEquals(received.size(), 0);
	}

	@Test(expected = IllegalStateException.class)
	public void testRemoveEntry() {
		BidBundle bundle = new BidBundle();

		assertEquals(bundle.size(), 0);

		bundle.addQuery(new Query());

		assertEquals(bundle.size(), 1);

		bundle.removeEntry(0);

		assertEquals(bundle.size(), 0);

		bundle.addQuery(new Query());
		bundle.lock();

		bundle.removeEntry(0);
	}

	@Test
	public void testGetEntry() {
		BidBundle bundle = new BidBundle();
		bundle.addQuery(new Query());

		assertNotNull(bundle.getEntry(new Query()));
		assertNull(bundle.getEntry(null));
	}
}
