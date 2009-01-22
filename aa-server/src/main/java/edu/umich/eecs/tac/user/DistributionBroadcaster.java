package edu.umich.eecs.tac.user;

import se.sics.tasim.is.EventWriter;

/**
 * @author Patrick Jordan
 */
public interface DistributionBroadcaster {
	void broadcastUserDistribution(int usersIndex, EventWriter eventWriter);
}
