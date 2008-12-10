package edu.umich.eecs.tac.util.sampling;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class SynchronizedSamplerTest {
    @Test
    public void testConstructors() {
        Sampler sampler = new SynchronizedSampler(new WheelSampler());
        assertNotNull(sampler);
    }

    @Test
    public void testSample() {
        Random r = new Random(1);
        MutableSampler<Number> sampler = new WheelSampler<Number>(r);
        sampler.addState(1.0, 1);

        SynchronizedSampler<Number> ssampler = new SynchronizedSampler<Number>(sampler);


        // Test zero-slot sampler
        sampler.addState(1.0, ssampler.getSample());
    }
}
