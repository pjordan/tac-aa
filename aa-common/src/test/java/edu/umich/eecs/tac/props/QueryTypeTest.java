package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author Kemal Eren
 */
public class QueryTypeTest {

	@Test
	public void testValue() {
		Query query = new Query();
		assertEquals(QueryType.value(query), QueryType.FOCUS_LEVEL_ZERO);
		query.setComponent("c1");
		assertEquals(QueryType.value(query), QueryType.FOCUS_LEVEL_ONE);
		query.setManufacturer("m1");
		assertEquals(QueryType.value(query), QueryType.FOCUS_LEVEL_TWO);

		QueryType[] s = new QueryType[3];
		s = QueryType.values();
		assertEquals(s[0], QueryType.FOCUS_LEVEL_ZERO);
		assertEquals(s[1], QueryType.FOCUS_LEVEL_ONE);
		assertEquals(s[2], QueryType.FOCUS_LEVEL_TWO);
	}

	@Test
	public void testValueOf() {
		String name = "";
		int thrown = 0;
		try {
			QueryType result = QueryType.valueOf(name);
		} catch (IllegalArgumentException e) {
			thrown++;
		}

		if (thrown != 1) {
			fail("Empty strings should not work");
		}

		name = "FOCUS_LEVEL_ZERO";
		QueryType expResult = QueryType.FOCUS_LEVEL_ZERO;
		QueryType result = QueryType.valueOf(name);
		assertEquals(expResult, result);

		name = "FOCUS_LEVEL_ONE";
		expResult = QueryType.FOCUS_LEVEL_ONE;
		result = QueryType.valueOf(name);
		assertEquals(expResult, result);

		name = "FOCUS_LEVEL_TWO";
		expResult = QueryType.FOCUS_LEVEL_TWO;
		result = QueryType.valueOf(name);
		assertEquals(expResult, result);
	}
}