package edu.umich.eecs.tac.util.sampling;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class SynchronizedMutableSamplerTest {
    @Test
    public void testConstructors() {
        Sampler sampler = new SynchronizedMutableSampler(new WheelSampler());
        assertNotNull(sampler);
    }

    @Test
    public void testSample() {
        Random r = new Random(1);
        MutableSampler<Number> sampler = new SynchronizedMutableSampler<Number>(new WheelSampler<Number>(r));
        sampler.addState(1.0, 1);

        // Test zero-slot sampler
        sampler.addState(1.0, sampler.getSample());
    }
}
