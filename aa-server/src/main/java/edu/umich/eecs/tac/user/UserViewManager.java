package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.UserClickModel;
import se.sics.tasim.aw.TimeListener;

/**
 * @author Patrick Jordan
 */
public interface UserViewManager extends TimeListener {
    public boolean processImpression(User user, Query query, Auction auction);

    public boolean addUserEventListener(UserEventListener listener);

    public boolean containsUserEventListener(UserEventListener listener);

    public boolean removeUserEventListener(UserEventListener listener);

    public UserClickModel getUserClickModel();

    public void setUserClickModel(UserClickModel userClickModel);
}
