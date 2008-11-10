package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.sim.Publisher;

/**
 * @author Patrick Jordan
 */
public interface User {
    QueryState getQueryState();
    void search(Publisher publisher);
    void addListener(UserEventListener listener);
    void removeListener(UserEventListener listener);    
}
