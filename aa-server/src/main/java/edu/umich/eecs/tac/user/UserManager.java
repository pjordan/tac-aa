package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.sim.Auctioneer;
import edu.umich.eecs.tac.props.UserClickModel;
import se.sics.tasim.aw.TimeListener;

/**
 * UserManager provides a public interface for triggering and managing agent behavior.  Listeners may be added and
 * removed through this object.
 * 
 * @author Patrick Jordan
 */
public interface UserManager extends TimeListener {
    
    public void initialize(int virtualDays);

    public void triggerBehavior(Auctioneer auctioneer);

    public boolean addUserEventListener(UserEventListener listener);

    public boolean containsUserEventListener(UserEventListener listener);

    public boolean removeUserEventListener(UserEventListener listener);

    public int[] getStateDistribution();

    public UserClickModel getUserClickModel();

    public void setUserClickModel(UserClickModel userClickModel);
}
