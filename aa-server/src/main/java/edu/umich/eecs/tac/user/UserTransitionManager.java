package edu.umich.eecs.tac.user;

import se.sics.tasim.aw.TimeListener;

/**
 * @author Patrick Jordan
 */
public interface UserTransitionManager extends TimeListener {
	QueryState transition(User user, boolean transacted);
}
