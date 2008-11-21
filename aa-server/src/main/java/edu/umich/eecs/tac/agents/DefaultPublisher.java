package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.auction.AuctionFactory;
import edu.umich.eecs.tac.auction.BidManager;
import edu.umich.eecs.tac.auction.BidManagerImpl;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.BidBundle;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.aw.Message;
import se.sics.isl.transport.Transportable;

import java.util.logging.Logger;

/**
 * @author Lee Callender, Patrick Jordan
 */
public class DefaultPublisher extends Publisher implements TACAAConstants, TimeListener {
    private AuctionFactory auctionFactory;
    private BidManager bidManager;

    public DefaultPublisher() {
    }

    public void nextTimeUnit(int date) {
        //Auctions should be updated here.
        if(bidManager!=null) {
            bidManager.nextTimeUnit(date);
        }

        Query query = new Query();
        query.setComponent("dvd");
        runAuction(query);
        
    }


    public BidManager getBidManager() {
        return bidManager;
    }

    protected void setup() {
        this.log = Logger.getLogger(DefaultPublisher.class.getName());

        bidManager = createBidManager();
        auctionFactory = createAuctionFactory();
        
        if(auctionFactory!=null) {
            auctionFactory.setBidManager(bidManager);
        }
    }

    private BidManager createBidManager() {
        BidManager bidManager = new BidManagerImpl();
        
        //TODO: initialize the bid manager


        return bidManager;
    }

    private AuctionFactory createAuctionFactory() {
        String auctionFactoryClass = getProperty("auctionfactory.class","edu.umich.eecs.tac.auction.LahaiePennockAuctionFactory");
        int slotLimit = getPropertyAsInt("auctionfactory.slotLimit",5);
        double squashValue = getPropertyAsDouble("auctionfactory.squashing",1.0);

        AuctionFactory factory = null;

        try {
            factory = (AuctionFactory)Class.forName(auctionFactoryClass).newInstance();
            factory.setSlotLimit(slotLimit);
            factory.setSquashValue(squashValue);
        } catch (InstantiationException e) {
            //TODO: log these
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return factory;
    }

    protected void stopped() {
    }

    protected void shutdown() {
    }

    // -------------------------------------------------------------------
    // Message handling
    // -------------------------------------------------------------------

    protected void messageReceived(Message message) {
        String sender = message.getSender();
        Transportable content = message.getContent();

        if(content instanceof BidBundle) {
            handleBidBundle(sender, (BidBundle)content);
        }
    }

    private void handleBidBundle(String advertiser, BidBundle bidBundle) {
        if(bidManager!=null) {
            bidManager.updateBids(advertiser,bidBundle);
        }
    }

    protected String getAgentName(String agentAddress) {
        return super.getAgentName(agentAddress);
    }

    protected void sendEvent(String message) {
        super.sendEvent(message);
    }

    protected void sendWarningEvent(String message) {
        super.sendWarningEvent(message);
    }


    public void sendQueryReportsToAll() {
    }


    public Auction runAuction(Query query) {
        if(auctionFactory!=null) {
            return auctionFactory.runAuction(query);
        }

        return null;
    }
}
