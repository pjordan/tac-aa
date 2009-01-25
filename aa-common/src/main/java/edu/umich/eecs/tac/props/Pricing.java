package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.text.ParseException;

/**
 * Pricing represents the CPC's charged to the advertisers when a user clicks.
 * @author Patrick Jordan
 * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
 */
public class Pricing extends AbstractTransportable {
    /**
     * The price entry transport name.
     */
    private static final String PRICE_ENTRY_TRANSPORT_NAME = "PriceEntry";

    /**
     * The ad link-price mapping for the auction.
     */
    private Map<AdLink, Double> prices;

    /**
     * Creates an empty pricing.
     */
    public Pricing() {
        prices = new HashMap<AdLink, Double>();
    }

    /**
     * Sets the price of the {@link AdLink}.
     * @param ad the ad link.
     * @param price the CPC.
     * @throws NullPointerException if the ad link is null.
     */
    public final void setPrice(final AdLink ad, final double price) throws NullPointerException {
        lockCheck();

        if (ad == null) {
            throw new NullPointerException("ad cannot be null");
        }

        prices.put(ad, price);
    }

    /**
     * Returns the CPC of the {@link AdLink}.
     * @param ad the ad link.
     * @return the CPC of the {@link AdLink}.
     */
    public final double getPrice(final AdLink ad) {
        Double price = prices.get(ad);

        if (price == null) {
            return Double.NaN;
        } else {
            return price;
        }
    }

    /**
     * Returns the set of ad links priced.
     * @return the set of ad links priced.
     */
    public final Set<AdLink> adLinks() {
        return prices.keySet();
    }

    /**
     * Reads the pricing information from the reader.
     * @param reader the reader to read data from.
     * @throws ParseException if exception occurs when reading the mapping.
     */
    @Override
    protected final void readWithLock(final TransportReader reader) throws ParseException {
        prices.clear();
        while (reader.nextNode(PRICE_ENTRY_TRANSPORT_NAME, false)) {
            readPriceEntry(reader);
        }
    }

    /**
     * Writes the pricing information to the reader.
     * @param writer the writer to write data to.
     */
    @Override
    protected final void writeWithLock(final TransportWriter writer) {
        for (Map.Entry<AdLink, Double> entry : prices.entrySet()) {
            writePriceEntry(writer, entry.getValue(), entry.getKey());
        }
    }

    /**
     * Writes the price entry to the writer.
     * @param writer the writer to write data to.
     * @param price the CPC
     * @param adLink the ad link
     */
    protected final void writePriceEntry(final TransportWriter writer, final Double price, final AdLink adLink) {
        writer.node(PRICE_ENTRY_TRANSPORT_NAME);

        writer.attr("price", price);
        writer.write(adLink);

        writer.endNode(PRICE_ENTRY_TRANSPORT_NAME);
    }

    /**
     * Reads the price entry from the writer.
     * @param reader the reader to read data from.
     * @throws ParseException if exception occurs when a price entry
     */
    protected final void readPriceEntry(final TransportReader reader) throws ParseException {
        reader.enterNode();

        double price = reader.getAttributeAsDouble("price", Double.NaN);

        reader.nextNode(AdLink.class.getSimpleName(), true);

        AdLink adLink = (AdLink) reader.readTransportable();

        setPrice(adLink, price);

        reader.exitNode();
    }
}
