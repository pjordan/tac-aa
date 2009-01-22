package edu.umich.eecs.tac.util.sampling;

/**
 * Sampler provides a method for sampling a discrete random variable.
 * 
 * <br/>
 * <br/>
 * Implementations of this interface maintain data structures that support an
 * opertation which samples the discrete random variable according to the
 * underlying probability distribution over the state space.
 * 
 * @author Patrick Jordan
 */
public interface Sampler<T> {
	/**
	 * Get a sample from a discrete random variable.
	 * 
	 * @return the sample.
	 */
	public T getSample();
}
