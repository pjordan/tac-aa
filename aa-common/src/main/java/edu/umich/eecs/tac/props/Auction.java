package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * Auction contains a {@link Ranking} and a {@link Pricing} for a {@link Query}.
 * Auctions are instantied by the Publisher. 
 * @author Patrick Jordan, Lee Callender
 */
public class Auction extends AbstractTransportable {
    private Ranking ranking;
    private Pricing pricing;
    private Query query;

    public Ranking getRanking() {
        return ranking;
    }

    public void setRanking(Ranking ranking) {
        lockCheck();
        this.ranking = ranking;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        lockCheck();
        this.pricing = pricing;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        lockCheck();
        this.query = query;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
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

    protected void writeWithLock(TransportWriter writer) {
        if (ranking != null)
            writer.write(ranking);

        if (pricing != null)
            writer.write(pricing);

        if (query != null)
            writer.write(query);
    }
}
