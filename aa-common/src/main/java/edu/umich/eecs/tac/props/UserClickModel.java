package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

/**
 * @author Patrick Jordan
 */
public class UserClickModel extends AbstractTransportable {
    protected double[][] advertiserEffects;
    protected double[] continuationProbabilities;

    protected Query[] queries;
    protected String[] advertisers;


    public UserClickModel() {
        this(new Query[0], new String[0]);
    }

    public UserClickModel(Query[] queries, String[] advertisers) {
        if(queries==null)
            throw new NullPointerException("queries cannot be null");
        if(advertisers==null)
            throw new NullPointerException("advertisers cannot be null");

        this.queries = queries;
        this.advertisers = advertisers;
        advertiserEffects = new double[queries.length][advertisers.length];
        continuationProbabilities = new double[queries.length];
    }

    public int advertiserCount() {
        return advertisers.length;
    }
    public String advertiser(int index) {
        return advertisers[index];
    }

    public int advertiserIndex(String advertiser) {
        for(int index = 0; index < advertisers.length; index++) {
            if(advertisers[index].equals(advertiser))
                return index;
        }

        return -1;
    }

    public int queryCount() {
        return queries.length;
    }

    public Query query(int index) {
        return queries[index];
    }
    
    public int queryIndex(Query query) {
        for(int index = 0; index < queries.length; index++) {
            if(queries[index].equals(query))
                return index;
        }

        return -1;
    }

    public double getContinuationProbability(int queryIndex) {
        return continuationProbabilities[queryIndex];
    }

    public void setContinuationProbability(int queryIndex, double probability) {
        lockCheck();
        continuationProbabilities[queryIndex] = probability;
    }

    public double getAdvertiserEffect(int queryIndex, int advertiserIndex) {
        return advertiserEffects[queryIndex][advertiserIndex];
    }

    public void setAdvertiserEffect(int queryIndex, int advertiserIndex, double effect) {
        lockCheck();
        advertiserEffects[queryIndex][advertiserIndex] = effect;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        List<Query> queryList = new LinkedList<Query>();

        String queryName = Query.class.getSimpleName();
        while(reader.nextNode(queryName, false)) {
            queryList.add((Query)reader.readTransportable());
        }

        queries = queryList.toArray(new Query[0]);

        List<String> advertiserList = new LinkedList<String>();
        while(reader.nextNode("advertiser",false)) {
            advertiserList.add(reader.getAttribute("name"));
        }

        advertisers = advertiserList.toArray(new String[0]);

        advertiserEffects = new double[queries.length][advertisers.length];
        continuationProbabilities = new double[queries.length];


        while(reader.nextNode("continuationProbability",false)) {
            int queryIndex = reader.getAttributeAsInt("queryIndex");
            double probability = reader.getAttributeAsDouble("probability");

            setContinuationProbability(queryIndex, probability);
        }

        while(reader.nextNode("advertiserEffect",false)) {
            int advertiserIndex = reader.getAttributeAsInt("advertiserIndex");
            int queryIndex = reader.getAttributeAsInt("queryIndex");
            double effect = reader.getAttributeAsDouble("effect");

            setAdvertiserEffect(queryIndex, advertiserIndex, effect);
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        for(Query query : queries)
            writer.write(query);

        for(String advertiser : advertisers) {
            writer.node("advertiser").attr("name",advertiser).endNode("advertiser");
        }

        for(int queryIndex = 0; queryIndex < queries.length; queryIndex++) {
            writer.node("continuationProbability").attr("index",queryIndex).attr("probability",continuationProbabilities[queryIndex]).endNode("continuationProbability");
        }

        for(int queryIndex = 0; queryIndex < queries.length; queryIndex++) {
            for(int advertiserIndex = 0; advertiserIndex < advertisers.length; advertiserIndex++) {
                writer.node("advertiserEffect").attr("queryIndex",queryIndex).attr("advertiserIndex",advertiserIndex).attr("effect",advertiserEffects[queryIndex][advertiserIndex]).endNode("advertiserEffect");
            }
        }
    }
}
