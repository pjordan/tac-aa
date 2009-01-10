package edu.umich.eecs.tac.props;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kemal Eren
 */
public class CompositeIteratorTest {

    @Test (expected=NullPointerException.class)
    public void testNullDelegate() {
        CompositeIterator instance = new CompositeIterator(null);
    }
}