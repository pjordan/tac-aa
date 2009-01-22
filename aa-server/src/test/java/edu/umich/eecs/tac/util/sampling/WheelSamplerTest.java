package edu.umich.eecs.tac.util.sampling;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class WheelSamplerTest {

	@Test
	public void testConstructors() {
		WheelSampler sampler = new WheelSampler();
		assertNotNull(sampler);

		sampler = new WheelSampler(new Random());
		assertNotNull(sampler);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeWeight() {
		WheelSampler sampler = new WheelSampler();

		sampler.addState(-1.0, new Object());
	}

	@Test
	public void testSample() {
		Random r = new Random(1);
		WheelSampler<Number> sampler = new WheelSampler<Number>(r);

		// Test zero-slot sampler
		assertNull(sampler.getSample());

		// Test zero-slot sampler
		sampler.addState(1.0, 1);
		assertEquals(1, sampler.getSample());

		// Test zero-slot sampler
		sampler.addState(1.1, 2);
		assertEquals(1, sampler.getSample());
		assertEquals(2, sampler.getSample());

		sampler.addState(1.2, 3);
		assertEquals(3, sampler.getSample());
		assertEquals(3, sampler.getSample());
		assertEquals(1, sampler.getSample());
	}
}
