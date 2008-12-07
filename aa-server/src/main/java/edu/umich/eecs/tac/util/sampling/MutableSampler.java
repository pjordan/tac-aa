package edu.umich.eecs.tac.util.sampling;

/**
 * MutableSampler provides methods for adding the states to a discrete random variable's state space.
 *
 * @author Patrick Jordan
 *
 */
public interface MutableSampler<T> extends Sampler<T> {
    public void addState(double weight, T state);
}
