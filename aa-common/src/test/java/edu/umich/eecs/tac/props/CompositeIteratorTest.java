package edu.umich.eecs.tac.props;

import org.junit.Test;

/**
 * 
 * @author Kemal Eren
 */
public class CompositeIteratorTest {

	@Test(expected = NullPointerException.class)
	public void testNullDelegate() {
		KeyIterator instance = new KeyIterator(null);
	}
}