package edu.umich.eecs.tac.sim;

/**
 * @author Lee Callender, Patrick Jordan
 */

import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.PublisherInfo;
import se.sics.isl.transport.Transportable;

import java.util.logging.Logger;
import static edu.umich.eecs.tac.TACAAConstants.*;

public abstract class Publisher extends Builtin implements QueryReportSender,
		Auctioneer, PublisherInfoSender {
	private static final String CONF = "publisher.";

	protected Logger log = Logger.getLogger(Publisher.class.getName());

	public Publisher() {
		super(CONF);
	}

	protected void sendToAdvertisers(Transportable content) {
		sendToRole(ADVERTISER, content);
	}

	public abstract void sendQueryReportsToAll();

	// DEBUG FINALIZE REMOVE THIS!!! REMOVE THIS!!!
	protected void finalize() throws Throwable {
		Logger.global.info("PUBLISHER " + getName() + " IS BEING GARBAGED");
		super.finalize();
	}

	protected void charge(String advertiser, double amount) {
		getSimulation().transaction(advertiser, getAddress(), amount);
	}

	public void sendQueryReport(String advertiser, QueryReport report) {
		sendMessage(advertiser, report);
	}

	public void sendPublisherInfo(String advertiser) {
		sendMessage(advertiser, getPublisherInfo());
	}

	public void sendPublisherInfoToAll() {
		for (String advertiser : getAdvertiserAddresses()) {
			sendPublisherInfo(advertiser);
		}
	}

    public void broadcastImpressions(String advertiser, int impressions) {
        getSimulation().broadcastImpressions(advertiser, impressions);
    }

    public void broadcastClicks(String advertiser, int clicks) {
        getSimulation().broadcastClicks(advertiser, clicks);
    }

    public abstract void applyBidUpdates();
}
