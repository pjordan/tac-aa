package edu.umich.eecs.tac.user;

import java.util.Collection;

/**
 * @author Patrick Jordan
 */
public interface UsersInitializer {
    void initialize(Collection<? extends User> users, int virtualDays);
}
