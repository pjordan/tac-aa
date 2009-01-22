package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Query;
import se.sics.tasim.aw.TimeListener;

/**
 * @author Patrick Jordan
 */
public interface UserQueryManager extends TimeListener {
	public Query generateQuery(User user);
}
