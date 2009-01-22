package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class Pricing extends AbstractTransportable {
	private static final String PRICE_ENTRY_TRANSPORT_NAME = "PriceEntry";

	private Map<AdLink, Double> prices;

	public Pricing() {
		prices = new HashMap<AdLink, Double>();
	}

	public void setPrice(AdLink ad, double price) throws NullPointerException {
		lockCheck();

		if (ad == null)
			throw new NullPointerException("ad cannot be null");

		prices.put(ad, price);
	}

	public double getPrice(AdLink ad) {
		Double price = prices.get(ad);

		if (price == null)
			return Double.NaN;
		else
			return price;
	}

	public Set<AdLink> ads() {
		return prices.keySet();
	}

	protected void readWithLock(TransportReader reader) throws ParseException {
		prices.clear();
		while (reader.nextNode(PRICE_ENTRY_TRANSPORT_NAME, false)) {
			readPriceEntry(reader);
		}
	}

	protected void writeWithLock(TransportWriter writer) {
		for (Map.Entry<AdLink, Double> entry : prices.entrySet()) {
			writePriceEntry(writer, entry.getValue(), entry.getKey());
		}
	}

	protected void writePriceEntry(TransportWriter writer, Double price,
			AdLink adLink) {
		writer.node(PRICE_ENTRY_TRANSPORT_NAME);

		writer.attr("price", price);
		writer.write(adLink);

		writer.endNode(PRICE_ENTRY_TRANSPORT_NAME);
	}

	protected void readPriceEntry(TransportReader reader) throws ParseException {
		reader.enterNode();

		double price = reader.getAttributeAsDouble("price", Double.NaN);

		reader.nextNode(AdLink.class.getSimpleName(), true);

		AdLink adLink = (AdLink) reader.readTransportable();

		setPrice(adLink, price);

		reader.exitNode();
	}
}
