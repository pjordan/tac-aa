package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;
import se.sics.isl.transport.Context;

/**
 * Unit test suite for the AAInfo class.
 *
 * @author Patrick Jordan
 */
public class AAInfoTest {

    @Test
    public void testConstructor() {
        new AAInfo();
    }

    @Test
    public void testEmptyCreateContext() {
        AAInfo info = new AAInfo();
        assertNotNull(info.createContext());
    }

    @Test
    public void testParentCreateContext() {
        AAInfo info = new AAInfo();
        Context context = new Context("parent");

        Context childContext = info.createContext(context);
        assertNotNull(childContext);

        /*
         * The same object should be returned if the last context already exists with the same parent.
         */
        assertSame(childContext, info.createContext(context));
    }
}
