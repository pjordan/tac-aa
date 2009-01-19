package edu.umich.eecs.tac.user;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * @author Patrick Jordan
 */
public class DefaultUsersInitializer implements UsersInitializer {

    protected Logger log = Logger.getLogger(DefaultUsersInitializer.class.getName());

    private UserTransitionManager userTransitionManager;

    public DefaultUsersInitializer(UserTransitionManager userTransitionManager) {

        if(userTransitionManager==null) {
            throw new NullPointerException("user transition manager cannot be null");
        }

        this.userTransitionManager = userTransitionManager;
    }

    public void initialize(Collection<? extends User> users, int virtualDays) {
        
        log.finer("Running virtual initialization for "+virtualDays+" days.");

        for (int d = virtualDays; d >= 1; d--) {

            userTransitionManager.nextTimeUnit(-d);

            for (User user : users) {
                user.setState(userTransitionManager.transition(user.getState(), false));
            }
            
        }
    }
}
