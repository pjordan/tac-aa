package edu.umich.eecs.tac.util.sampling;

/**
 * Sampler provides a method for sampling a discrete random variable
 * 
 * @author Patrick Jordan
 */
public interface Sampler<T> {
    public T getSample();
}
