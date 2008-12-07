package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.sampling.MutableSampler;
import edu.umich.eecs.tac.util.sampling.WheelSampler;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserTransitionManager implements UserTransitionManager {
    private Map<QueryState, MutableSampler<QueryState>> standardSamplers;
    private Map<QueryState, MutableSampler<QueryState>> burstSamplers;
    private boolean burst;
    private double burstProbability;
    private Random random;

    public DefaultUserTransitionManager() {
        this(new Random());
    }

    public DefaultUserTransitionManager(Random random) {
        standardSamplers = new HashMap<QueryState, MutableSampler<QueryState>>(QueryState.values().length);
        burstSamplers = new HashMap<QueryState, MutableSampler<QueryState>>(QueryState.values().length);
        this.random = random;

        updateBurst();
    }


    public void nextTimeUnit(int timeUnit) {
        updateBurst();
    }

    public void addStandardTransitionProbability(QueryState from, QueryState to, double probability) {
        MutableSampler<QueryState> sampler = standardSamplers.get(from);
        if(sampler==null) {
            sampler = new WheelSampler<QueryState>(random);
            standardSamplers.put(from,sampler);
        }

        sampler.addState(probability,to);
    }

    public void addBurstTransitionProbability(QueryState from, QueryState to, double probability) {
        MutableSampler<QueryState> sampler = burstSamplers.get(from);
        if(sampler==null) {
            sampler = new WheelSampler<QueryState>(random);
            burstSamplers.put(from,sampler);
        }

        sampler.addState(probability,to);
    }

    public double getBurstProbability() {
        return burstProbability;
    }

    public void setBurstProbability(double burstProbability) {
        this.burstProbability = burstProbability;
    }

    public QueryState transition(QueryState queryState, boolean transacted) {
        if(transacted)
            return QueryState.TRANSACTED;
        else if(burst)
            return burstSamplers.get(queryState).getSample();
        else
            return standardSamplers.get(queryState).getSample();
    }

    private void updateBurst() {
        burst = random.nextDouble() < burstProbability;
    }
}
