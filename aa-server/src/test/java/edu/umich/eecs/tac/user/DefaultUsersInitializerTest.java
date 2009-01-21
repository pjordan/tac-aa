package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.Mockery;
import org.jmock.Expectations;

import java.util.List;
import java.util.LinkedList;

import edu.umich.eecs.tac.props.Product;

/**
 * @author Patrick Jordan
 */
@RunWith(JMock.class)
public class DefaultUsersInitializerTest {
    private Mockery context;
    
    private UserTransitionManager userTransitionManager;

    private DefaultUsersInitializer initializer;

    private List<User> users;

    @Before
    public void setup() {
        context = new JUnit4Mockery();
        
        userTransitionManager = context.mock(UserTransitionManager.class);
        
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
        context.checking(new Expectations() {{
            atLeast(1).of(userTransitionManager).nextTimeUnit(-1);
            atLeast(1).of(userTransitionManager).transition(QueryState.NON_SEARCHING, false); will(returnValue(QueryState.INFORMATIONAL_SEARCH));
        }});

        for(User user : users) {
            assertEquals(user.getState(),QueryState.NON_SEARCHING);
        }

        initializer.initialize(users,1);

        for(User user : users) {
            assertEquals(user.getState(),QueryState.INFORMATIONAL_SEARCH);
        }
    }
}
