package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserTransitionManagerTest {
	private DefaultUserTransitionManager userTransitionManager;
	private Random random;

	@Before
	public void setup() {
		random = new Random(100);
		userTransitionManager = new DefaultUserTransitionManager(random);

		userTransitionManager.setBurstProbability(0.3);

		userTransitionManager.addStandardTransitionProbability(
				QueryState.NON_SEARCHING, QueryState.INFORMATIONAL_SEARCH, 0.2);
		userTransitionManager.addStandardTransitionProbability(
				QueryState.NON_SEARCHING, QueryState.NON_SEARCHING, 0.8);
		userTransitionManager.addBurstTransitionProbability(
				QueryState.NON_SEARCHING, QueryState.INFORMATIONAL_SEARCH, 0.4);
		userTransitionManager.addBurstTransitionProbability(
				QueryState.NON_SEARCHING, QueryState.NON_SEARCHING, 0.6);
	}

	@Test
	public void testConstructors() {
		assertNotNull(new DefaultUserTransitionManager());
		assertNotNull(userTransitionManager);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorRandomNull() {
		new DefaultUserTransitionManager(null);
	}

	@Test
	public void testBurst() {
		assertFalse(userTransitionManager.isBurst());

		int bursts = 0;
		int regulars = 0;
		for (int i = 0; i < 1000; i++) {
			userTransitionManager.nextTimeUnit(0);

			if (userTransitionManager.isBurst()) {
				bursts++;
			} else {
				regulars++;
			}
		}

		assertEquals(bursts, 295, 0);
		assertEquals(regulars, 705, 0);
	}

	@Test
	public void testTransitions() {
		assertEquals(userTransitionManager.getBurstProbability(), 0.3, 0.00001);

		boolean burst = userTransitionManager.isBurst();

		int nsCount = 0;
		int isCount = 0;

		for (int i = 0; i < 1000; i++) {
			QueryState state = userTransitionManager.transition(
					QueryState.NON_SEARCHING, false);

			switch (state) {
			case NON_SEARCHING:
				nsCount++;
				break;
			case INFORMATIONAL_SEARCH:
				isCount++;
				break;
			}
		}

		if (burst) {
			assertEquals(nsCount, 600, 0);
			assertEquals(isCount, 400, 0);
		} else {
			assertEquals(nsCount, 809, 0);
			assertEquals(isCount, 191, 0);
		}

		while (userTransitionManager.isBurst() == burst) {
			userTransitionManager.nextTimeUnit(0);
		}

		nsCount = 0;
		isCount = 0;

		for (int i = 0; i < 1000; i++) {
			QueryState state = userTransitionManager.transition(
					QueryState.NON_SEARCHING, false);

			switch (state) {
			case NON_SEARCHING:
				nsCount++;
				break;
			case INFORMATIONAL_SEARCH:
				isCount++;
				break;
			}
		}

		if (!burst) {
			assertEquals(nsCount, 594, 0);
			assertEquals(isCount, 406, 0);
		} else {
			assertEquals(nsCount, 800, 0);
			assertEquals(isCount, 200, 0);
		}

		assertEquals(userTransitionManager.transition(QueryState.NON_SEARCHING,
				true), QueryState.TRANSACTED);
	}
}
