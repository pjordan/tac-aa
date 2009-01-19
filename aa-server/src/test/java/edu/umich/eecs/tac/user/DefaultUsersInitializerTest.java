package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.util.List;
import java.util.LinkedList;

import edu.umich.eecs.tac.props.Product;

/**
 * @author Patrick Jordan
 */
public class DefaultUsersInitializerTest {
    private UserTransitionManager userTransitionManager;

    private DefaultUsersInitializer initializer;

    private List<User> users;

    @Before
    public void setup() {
        userTransitionManager = new SimpleUserTransitionManager();
        
        initializer = new DefaultUsersInitializer(userTransitionManager);

        users = new LinkedList<User>();

        for(int i = 0; i < 2; i++) {
            users.add(new User(QueryState.NON_SEARCHING,new Product()));
        }
    }

    @Test(expected=NullPointerException.class)
    public void testConstructor() {
        assertNotNull(initializer);
        new DefaultUsersInitializer(null);
    }

    @Test
    public void testInitialized() {

        for(User user : users) {
            assertEquals(user.getState(),QueryState.NON_SEARCHING);
        }

        initializer.initialize(users,1);

        for(User user : users) {
            assertEquals(user.getState(),QueryState.INFORMATIONAL_SEARCH);
        }
    }

    private static class SimpleUserTransitionManager implements UserTransitionManager {

        public QueryState transition(QueryState queryState, boolean transacted) {
            return QueryState.INFORMATIONAL_SEARCH;
        }

        public void nextTimeUnit(int timeUnit) {
        }
    }
}
