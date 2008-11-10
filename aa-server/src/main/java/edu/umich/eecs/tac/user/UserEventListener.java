package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;

/**
 * @author Patrick Jordan
 */
public interface UserEventListener {
    void queryIssued(User user, Query query);
    void adViewed(User user, Query query, Ad ad);
    void adClicked(User user, Query query, Ad ad);
    void converted(User user, Query query, Ad ad);
}
