package edu.umich.eecs.tac.user;

import se.sics.tasim.aw.Message;
import se.sics.tasim.is.EventWriter;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.sim.Auctioneer;

/**
 * @author Patrick Jordan
 */
public interface UsersBehavior extends DistributionBroadcaster {
	void nextTimeUnit(int date);

	void setup();

	void stopped();

	void shutdown();

	void messageReceived(Message message);

	Ranking getRanking(Query query, Auctioneer auctioneer);

	boolean addUserEventListener(UserEventListener listener);

	boolean containsUserEventListener(UserEventListener listener);

	boolean removeUserEventListener(UserEventListener listener);
}
