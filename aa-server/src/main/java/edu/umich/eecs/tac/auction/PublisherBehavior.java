package edu.umich.eecs.tac.auction;

import se.sics.tasim.aw.Message;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.PublisherInfo;

/**
 * @author Patrick Jordan
 */
public interface PublisherBehavior {

	void nextTimeUnit(int date);

	void setup();

	void stopped();

	void shutdown();

	void messageReceived(Message message);

	void sendQueryReportsToAll();

	public Auction runAuction(Query query);

	PublisherInfo getPublisherInfo();

	void setPublisherInfo(PublisherInfo publisherInfo);
}
