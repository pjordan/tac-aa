package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.util.Random;

import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Product;

/**
 * @author Patrick Jordan
 */
public class DefaultUserTransitionManagerTest {
	private DefaultUserTransitionManager userTransitionManager;
	private Random random;
    private RetailCatalog retailCatalog;
    private User user;
    private Product product;

	@Before
	public void setup() {
        product = new Product("man","com");
        retailCatalog = new RetailCatalog();
        retailCatalog.addProduct(product);
        user = new User(QueryState.NON_SEARCHING, product);
		random = new Random(100);
		userTransitionManager = new DefaultUserTransitionManager(retailCatalog, random);

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
		assertNotNull(new DefaultUserTransitionManager(retailCatalog));
		assertNotNull(userTransitionManager);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorRandomNull() {
		new DefaultUserTransitionManager(null);
	}



	@Test
	public void testTransitions() {
		assertEquals(userTransitionManager.getBurstProbability(), 0.3, 0.00001);

		boolean burst = userTransitionManager.isBurst(user.getProduct());

		int nsCount = 0;
		int isCount = 0;

		for (int i = 0; i < 1000; i++) {
			QueryState state = userTransitionManager.transition(user, false);

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

		while (userTransitionManager.isBurst(product) == burst) {
			userTransitionManager.nextTimeUnit(0);
		}

		nsCount = 0;
		isCount = 0;

		for (int i = 0; i < 1000; i++) {
			QueryState state = userTransitionManager.transition(user, false);

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

		assertEquals(userTransitionManager.transition(user,true), QueryState.TRANSACTED);
	}
}
