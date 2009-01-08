package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import edu.umich.eecs.tac.props.Product;

/**
 * @author Patrick Jordan
 */
public class UserTest {
    @Test
    public void testConstructors() {
        assertNotNull(new User());
        assertNotNull(new User(QueryState.NON_SEARCHING, new Product()));
    }

    @Test
    public void testGeneral() {
        User emptyUser = new User();

        assertNull(emptyUser.getState());
        assertNull(emptyUser.getProduct());


        emptyUser.setProduct(new Product());
        emptyUser.setState(QueryState.NON_SEARCHING);

        assertEquals(emptyUser.getState(), QueryState.NON_SEARCHING);
        assertEquals(emptyUser.getProduct(), new Product());

        assertFalse(emptyUser.isSearching());
        assertFalse(emptyUser.isTransacting());

        User user = new User(QueryState.FOCUS_LEVEL_ONE, new Product());

        assertTrue(user.isSearching());
        assertTrue(user.isTransacting());

        assertEquals(new User(), new User());
        assertEquals(user, user);
        assertFalse(user.equals(null));
        assertFalse(user.equals("test"));
        assertFalse(user.equals(new User()));
        assertFalse(new User().equals(user));
        assertFalse(new User(null, new Product()).equals(user));
        assertEquals(new User(QueryState.FOCUS_LEVEL_ONE, new Product()).hashCode(), user.hashCode());
        assertEquals(new User(QueryState.FOCUS_LEVEL_ONE, new Product()), user);


        new User(QueryState.NON_SEARCHING, null).hashCode();
        new User(null, new Product()).hashCode();
    }
}
