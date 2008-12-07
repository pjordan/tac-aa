package edu.umich.eecs.tac.util.sampling;

/**
 * SynchronizedSampler provides synchronization support to a MutableSampler.
 *
 * @author Patrick Jordan
 */
public class SynchronizedMutableSampler<T> implements MutableSampler<T> {
    private MutableSampler<T> mutableSampler;

    private final Object lock;

    public SynchronizedMutableSampler(MutableSampler<T> mutableSampler) {

        this.mutableSampler = mutableSampler;

        lock = new Object();

    }


    public void addState(double weight, T state) {

        synchronized (lock) {

            mutableSampler.addState(weight, state);

        }

    }

    public T getSample() {

        synchronized (lock) {

            return mutableSampler.getSample();

        }

    }
}
