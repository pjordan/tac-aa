package edu.umich.eecs.tac.util.sampling;

/**
 * SynchronizedSampler provides synchronization support to a {@link Sampler}.
 * 
 * @author Patrick Jordan
 */
public class SynchronizedSampler<T> implements Sampler<T> {
	/**
	 * The sampler used to delegate sampling
	 */
	private Sampler<T> sampler;

	private final Object lock;

	public SynchronizedSampler(Sampler<T> sampler) {

		this.sampler = sampler;

		lock = new Object();

	}

	public T getSample() {

		synchronized (lock) {

			return sampler.getSample();

		}
	}
}
