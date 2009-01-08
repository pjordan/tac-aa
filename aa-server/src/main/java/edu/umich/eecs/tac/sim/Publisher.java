package edu.umich.eecs.tac.sim;

/**
 * @author Lee Callender, Patrick Jordan
 */

import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import se.sics.isl.transport.Transportable;

import java.util.logging.Logger;

public abstract class Publisher extends Builtin implements QueryReportSender {
    private static final String CONF = "publisher.";

    protected Logger log = Logger.getLogger(Publisher.class.getName());

    public Publisher() {
        super(CONF);
    }

    protected void sendToAdvertisers(Transportable content){
      sendToRole(TACAASimulation.ADVERTISER,  content);
    }

    public abstract void sendQueryReportsToAll();

    public abstract Auction runAuction(Query query);

    // DEBUG FINALIZE REMOVE THIS!!! REMOVE THIS!!!
    protected void finalize() throws Throwable {
        Logger.global.info("PUBLISHER " + getName() + " IS BEING GARBAGED");
        super.finalize();
    }

    protected void charge(String advertiser, double amount) {
        getSimulation().transaction(advertiser, getAddress(), amount);
    }

    public void sendQueryReport(String advertiser, QueryReport report) {
        sendMessage(advertiser,report);
    }
}
