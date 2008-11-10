package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class Auction extends AbstractTransportable {
    private Ranking ranking;
    private Pricing pricing;


    public Ranking getRanking() {
        return ranking;
    }

    public void setRanking(Ranking ranking) {
        this.ranking = ranking;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }


    protected void readWithLock(TransportReader reader) throws ParseException {
        if (reader.nextNode(Ranking.class.getSimpleName(), false)) {
            this.ranking = (Ranking)reader.readTransportable();
        }

        if (reader.nextNode(Pricing.class.getSimpleName(), false)) {
            this.pricing = (Pricing)reader.readTransportable();
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        if(ranking!=null)
            writer.write(ranking);

        if(pricing!=null)
            writer.write(pricing);
    }
}
