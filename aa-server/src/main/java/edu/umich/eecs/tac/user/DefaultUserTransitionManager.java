package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.sampling.MutableSampler;
import edu.umich.eecs.tac.util.sampling.WheelSampler;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Product;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserTransitionManager implements UserTransitionManager {
	private Map<QueryState, MutableSampler<QueryState>> standardSamplers;
	private Map<QueryState, MutableSampler<QueryState>> burstSamplers;

    private double burstProbability;
    private boolean[] bursts;
	private Random random;
    private RetailCatalog retailCatalog;

	public DefaultUserTransitionManager(RetailCatalog retailCatalog) {
		this(retailCatalog, new Random());
	}

	public DefaultUserTransitionManager(RetailCatalog retailCatalog, Random random) {
		if (random == null) {
			throw new NullPointerException("Random number generator cannot be null");
		}

        if(retailCatalog==null) {
            throw new NullPointerException("retail catalog cannot be null");
        }
		standardSamplers = new HashMap<QueryState, MutableSampler<QueryState>>(QueryState.values().length);
		burstSamplers = new HashMap<QueryState, MutableSampler<QueryState>>(QueryState.values().length);
		this.random = random;

        this.retailCatalog = retailCatalog;
        bursts = new boolean[retailCatalog.size()];

		updateBurst();
	}

	public void nextTimeUnit(int timeUnit) {
		updateBurst();
	}

	public void addStandardTransitionProbability(QueryState from, QueryState to, double probability) {
		MutableSampler<QueryState> sampler = standardSamplers.get(from);
		if (sampler == null) {
			sampler = new WheelSampler<QueryState>(random);
			standardSamplers.put(from, sampler);
		}

		sampler.addState(probability, to);
	}

	public void addBurstTransitionProbability(QueryState from, QueryState to, double probability) {
		MutableSampler<QueryState> sampler = burstSamplers.get(from);
		if (sampler == null) {
			sampler = new WheelSampler<QueryState>(random);
			burstSamplers.put(from, sampler);
		}

		sampler.addState(probability, to);
	}

	public double getBurstProbability() {
		return burstProbability;
	}

	public void setBurstProbability(double burstProbability) {
		this.burstProbability = burstProbability;
	}

	public QueryState transition(User user, boolean transacted) {
		if (transacted)
			return QueryState.TRANSACTED;
		else if (bursts[retailCatalog.indexForEntry(user.getProduct())])
			return burstSamplers.get(user.getState()).getSample();
		else
			return standardSamplers.get(user.getState()).getSample();
	}

	private void updateBurst() {
        for(int i = 0 ; i < bursts.length; i++) {
            bursts[i] = random.nextDouble() < burstProbability;
        }
	}

	public boolean isBurst(Product product) {
        return bursts[retailCatalog.indexForEntry(product)];
    }
}
