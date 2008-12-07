package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.auction.AuctionFactory;
import edu.umich.eecs.tac.auction.BidManager;
import edu.umich.eecs.tac.auction.BidManagerImpl;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.RetailCatalog;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.aw.Message;
import se.sics.isl.transport.Transportable;

import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Lee Callender, Patrick Jordan
 */
public class DefaultPublisher extends Publisher implements TACAAConstants {
    private AuctionFactory auctionFactory;
    private BidManager bidManager;
    private RetailCatalog retailCatalog;
    private Set<Query> possibleQueries;

    public DefaultPublisher() {
    }

    public void nextTimeUnit(int date) {
        //Auctions should be updated here.
        if(bidManager!=null) {
            bidManager.nextTimeUnit(date);
        }

        if(date != 0){
          //Update auctions, send AuctionUpdatedEvent
          log.finest("Running Auction");
          Iterator it = possibleQueries.iterator();
          Query query = (Query) it.next(); 
          Auction a = runAuction(query);
          log.finest("Auction Complete: "+a.getRanking().toString());
        }
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

       addTimeListener(this);
    }

    private BidManager createBidManager() {
        BidManager bidManager = new BidManagerImpl();
        //TODO: initialize the bid manager

        //All advertisers should be known to the bidManager
        String[] advertisers = getAdvertiserAddresses();
        for(int i = 0, n = advertisers.length; i < n; i++){
          bidManager.addAdvertiser(advertisers[i]);
        }

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
      removeTimeListener(this);
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
        } else if (content instanceof RetailCatalog) {
            handleRetailCatalog((RetailCatalog) content);
        } 
    }

    private void handleBidBundle(String advertiser, BidBundle bidBundle) {
        if(bidManager==null) {
          // Not yet initialized => ignore the RFQ
          log.warning("Received BidBundle from " + advertiser + " before initialization");
        } else {
          bidManager.updateBids(advertiser, bidBundle);
        }
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog){
      this.retailCatalog = retailCatalog;
      generatePossibleQueries();
      bidManager.initializeQuerySpace(possibleQueries);
    }

    private void generatePossibleQueries(){
      if(retailCatalog != null && possibleQueries == null){
        int cSize = retailCatalog.getComponents().size();
        int mSize = retailCatalog.getManufacturers().size();
        possibleQueries = new HashSet<Query>();
        String[] mans = retailCatalog.getManufacturers().toArray(new String[mSize]);
        String[] comps = retailCatalog.getComponents().toArray(new String[cSize]);

        possibleQueries.add(new Query(null, null));
        for(int i = 0; i < mSize; i++){
          possibleQueries.add(new Query(mans[i], null));
        }

        for(int i = 0; i < cSize; i++){
          possibleQueries.add(new Query(null, comps[i]));
        }

        for(int i = 0; i < mSize; i++){
          for(int j = 0; j < cSize; j++){
            possibleQueries.add(new Query(mans[i], comps[j]));
          }
        }

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
      //TODO: Generate Query Reports
    }

    public Auction runAuction(Query query) {
        //TODO: Check against possible queries
        if(auctionFactory!=null) {
            return auctionFactory.runAuction(query);
        }

        return null;
    }
}
