package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Patrick Jordan
 */
public class QueryStateTest {

    @Test
    public void testStates() {
        assertFalse(QueryState.NON_SEARCHING.isSearching());
        assertFalse(QueryState.NON_SEARCHING.isTransacting());

        assertTrue(QueryState.INFORMATIONAL_SEARCH.isSearching());
        assertFalse(QueryState.INFORMATIONAL_SEARCH.isTransacting());

        assertTrue(QueryState.FOCUS_LEVEL_ZERO.isSearching());
        assertTrue(QueryState.FOCUS_LEVEL_ZERO.isTransacting());

        assertTrue(QueryState.FOCUS_LEVEL_ONE.isSearching());
        assertTrue(QueryState.FOCUS_LEVEL_ONE.isTransacting());

        assertTrue(QueryState.FOCUS_LEVEL_TWO.isSearching());
        assertTrue(QueryState.FOCUS_LEVEL_TWO.isTransacting());

        assertFalse(QueryState.TRANSACTED.isSearching());
        assertFalse(QueryState.TRANSACTED.isTransacting());
    }
}
