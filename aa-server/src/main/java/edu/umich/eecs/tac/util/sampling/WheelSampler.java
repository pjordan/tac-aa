package edu.umich.eecs.tac.util.sampling;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Roulette wheel sampling.
 * 
 * @author Patrick Jordan
 */
public class WheelSampler<T> implements MutableSampler<T> {
    /**
     * The random number generator used to select the values.
     */
    private final Random random;
    /**
     * The list of possible values and associated weights.
     * These are termed slots.
     */
    private final List<Slot<T>> slots;
    /**
     * The <code>dirty</cody> flag indicates whether sorting needs to be performed before sampling a value.
     */
    private boolean dirty;
    /**
     * The <code>sum</code> valuable holds the sum of the state weights.
     */
    private double sum;

    /**
     * Create a {@link WheelSampler} with a new random number generator.
     */
    public WheelSampler() {
        this(new Random());
    }

    /**
     * Create a {@link WheelSampler} with a given random number generator.
     * @param random the random number generator.
     */
    public WheelSampler(Random random) {
        this.random = random;
        this.slots = new ArrayList<Slot<T>>();
        dirty = true;
    }

    /**
     * Add a new state with its associated weight in the distribution.
     * @param weight the weight of the state in the distribution
     * @param state the state
     */
    public void addState(double weight, T state) {

        slots.add(new Slot<T>(weight,state));

        // Mark the sampler as dirty so that we sort before sampling.
        dirty = true;
    }

    /**
     * Get a sample from a discrete random variable.
     *
     * @return the sample.
     */
    public T getSample() {
        // Check for an empty state space.
        if(slots.isEmpty())
            return null;

        // Check for a trivial state space.
        if(slots.size()==1)
            return slots.get(0).getState();

        // JIT cleaning
        if(dirty)
            clean();

        // Determing the random cumulative value
        double dart = random.nextDouble()*sum;

        // Locate the slot in which the dart falls.
        // Note that all weights are positive.
        int index = -1;
        do {
            index++;
            dart -= slots.get(index).getWeight();
        } while(dart > 0 && index < slots.size()-1);

        return slots.get(index).getState();
    }

    /**
     * The clean method sorts the slots in decreasing order and sums the weights.
     */
    private void clean() {
        Collections.sort(slots,Collections.reverseOrder());

        sum = 0.0;
        for(Slot slot : slots) {
            sum += slot.getWeight();
        }

        dirty = false;
    }

    /**
     * The slot class holds a state and its respective weight in the distribution.
     */
    private static final class Slot<T> implements Comparable<Slot<T>> {
        /**
         * The weight (relative probability) that the state will occur when sampling.
         */
        protected final double weight;
        /**
         * The state a discrete random variable can take on.
         */
        protected final T state;

        /**
         * Create a new slot with the given weight and state.
         * @param weight the weight associated with the state.
         * @param state the state.
         * @throws IllegalArgumentException if the weight is negative.
         *
         */
        public Slot(double weight, T state) throws IllegalArgumentException {
            if(weight<0.0)
                throw new IllegalArgumentException("weight cannot be null");

            this.weight = weight;
            this.state = state;
        }
        
        public int compareTo(Slot<T> slot) {
            return Double.compare(weight,slot.weight);
        }


        public double getWeight() {
            return weight;
        }

        public T getState() {
            return state;
        }
    }
}
