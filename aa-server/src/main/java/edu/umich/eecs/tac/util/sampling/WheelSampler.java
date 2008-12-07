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
    private final Random random;
    private final List<Slot<T>> slots;
    private boolean dirty;
    private double sum;


    public WheelSampler() {
        this(new Random());
    }

    public WheelSampler(Random random) {
        this.random = random;
        this.slots = new ArrayList<Slot<T>>();
        dirty = true;
    }

    public void addState(double weight, T state) {
        slots.add(new Slot<T>(weight,state));
        dirty = true;
    }

    public T getSample() {
        if(slots.isEmpty())
            return null;

        if(slots.size()==1)
            return slots.get(0).getState();

        // JIT cleaning
        if(dirty)
            clean();

        double dart = random.nextDouble()*sum;

        int index = -1;

        do {
            index++;
            dart -= slots.get(index).getWeight();
        } while(dart > 0 && index < slots.size()-1);

        return slots.get(index).getState();
    }

    private void clean() {
        Collections.sort(slots,Collections.reverseOrder());

        sum = 0.0;
        for(Slot slot : slots) {
            sum += slot.getWeight();
        }

        dirty = false;
    }



    private static final class Slot<T> implements Comparable<Slot<T>> {
        protected final double weight;
        protected final T state;

        public Slot(double weight, T state) {
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
