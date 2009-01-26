package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * Auction contains a {@link Ranking} and a {@link Pricing} for a {@link Query}.
 *
 * @author Patrick Jordan, Lee Callender
 */
public class Auction extends AbstractTransportable {
    /**
     * The ranking for the auction.
     */
    private Ranking ranking;
    /**
     * The pricing for the auction.
     */
    private Pricing pricing;
    /**
     * The user query used to generate the auction.
     */
    private Query query;

    /**
     * Return the ranking.
     * @return the ranking.
     */
    public final Ranking getRanking() {
        return ranking;
    }

    /**
     * Sets the ranking.
     * @param ranking the ranking.
     */
    public final void setRanking(final Ranking ranking) {
        lockCheck();
        this.ranking = ranking;
    }

    /**
     * Returns the pricing.
     * @return the pricing.
     */
    public final Pricing getPricing() {
        return pricing;
    }

    /**
     * Sets the pricing.
     * @param pricing the pricing.
     */
    public final void setPricing(final Pricing pricing) {
        lockCheck();
        this.pricing = pricing;
    }

    /**
     * Returns the query.
     * @return the query.
     */
    public final Query getQuery() {
        return query;
    }

    /**
     * Sets the query.
     * @param query the query.
     */
    public final void setQuery(final Query query) {
        lockCheck();
        this.query = query;
    }

    /**
     * Reads the ranking and pricing from the reader.
     * @param reader the reader to read data from.
     * @throws ParseException if an exception occurs reading the ranking and pricing.
     */
    @Override
    protected final void readWithLock(final TransportReader reader) throws ParseException {
        if (reader.nextNode(Ranking.class.getSimpleName(), false)) {
            this.ranking = (Ranking) reader.readTransportable();
        }

        if (reader.nextNode(Pricing.class.getSimpleName(), false)) {
            this.pricing = (Pricing) reader.readTransportable();
        }

        if (reader.nextNode(Query.class.getSimpleName(), false)) {
            this.query = (Query) reader.readTransportable();
        }
    }

    /**
     * Writes the ranking and pricing to the writer.
     * @param writer the writer to write data to.
     */
    @Override
    protected final void writeWithLock(final TransportWriter writer) {
        if (ranking != null) {
            writer.write(ranking);
        }

        if (pricing != null) {
            writer.write(pricing);
        }

        if (query != null) {
            writer.write(query);
        }
    }
}
