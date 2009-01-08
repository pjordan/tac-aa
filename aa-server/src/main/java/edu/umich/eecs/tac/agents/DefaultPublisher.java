package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.sim.Users;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.user.UserEventListener;
import edu.umich.eecs.tac.auction.*;
import edu.umich.eecs.tac.props.*;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.aw.Message;
import se.sics.tasim.sim.SimulationAgent;
import se.sics.isl.transport.Transportable;

import java.util.logging.Logger;
import java.util.*;

/**
 * @author Lee Callender, Patrick Jordan
 */
public class DefaultPublisher extends Publisher implements TACAAConstants {
    private AuctionFactory auctionFactory;
    private BidManager bidManager;
    private RetailCatalog retailCatalog;
    private Set<Query> possibleQueries;
    private UserClickModel userClickModel;
    private QueryReportManager queryReportManager;
    private BudgetManager budgetManager;

    public DefaultPublisher() {

    }

    public void nextTimeUnit(int date) {
        //Auctions should be updated here.
        if (bidManager != null) {
            bidManager.nextTimeUnit(date);
        }

        if (date != 0) {
            //Update auctions, send AuctionUpdatedEvent
            //log.finest("Running Auction");
            //Iterator it = possibleQueries.iterator();
            //Query query = (Query) it.next();
            //Auction a = runAuction(query);
            //log.finest("Auction Complete: " + a.getRanking().toString());
        }
    }

    public BidManager getBidManager() {
        return bidManager;
    }

    protected void setup() {
        this.log = Logger.getLogger(DefaultPublisher.class.getName());

        bidManager = createBidManager();
        budgetManager = createBudgetManager();
        auctionFactory = createAuctionFactory();
        queryReportManager = createQueryReportManager();

        if (auctionFactory != null) {
            auctionFactory.setBidManager(bidManager);
        }


        addTimeListener(this);

        for(SimulationAgent agent : getSimulation().getUsers()) {
            Users users = (Users)agent.getAgent();
            users.addUserEventListener(new ClickMonitor());
        }
    }

    private BudgetManager createBudgetManager() {
        BudgetManager budgetManager = new BudgetManagerImpl(0);

        for (String advertiser : getAdvertiserAddresses()) {
            budgetManager.addAdvertiser(advertiser);
        }


        return budgetManager;
    }

    private QueryReportManager createQueryReportManager() {
        QueryReportManager queryReportManager = new QueryReportManager(this,0);

        for (String advertiser : getAdvertiserAddresses()) {
            queryReportManager.addAdvertiser(advertiser);
        }

        for(SimulationAgent agent : getSimulation().getUsers()) {
            Users users = (Users)agent.getAgent();
            users.addUserEventListener(queryReportManager);
        }

        return queryReportManager;
    }

    private BidManager createBidManager() {
        BidManager bidManager = new BidManagerImpl(userClickModel);
        //TODO: initialize the bid manager

        //All advertisers should be known to the bidManager
        String[] advertisers = getAdvertiserAddresses();
        for (int i = 0, n = advertisers.length; i < n; i++) {
            bidManager.addAdvertiser(advertisers[i]);
        }

        return bidManager;
    }

    private AuctionFactory createAuctionFactory() {
        String auctionFactoryClass = getProperty("auctionfactory.class", "edu.umich.eecs.tac.auction.LahaiePennockAuctionFactory");
        int slotLimit = getPropertyAsInt("auctionfactory.slotLimit", 5);
        double squashValue = getPropertyAsDouble("auctionfactory.squashing", 1.0);

        AuctionFactory factory = null;

        try {
            factory = (AuctionFactory) Class.forName(auctionFactoryClass).newInstance();
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

        if (content instanceof BidBundle) {
            handleBidBundle(sender, (BidBundle) content);
        } else if (content instanceof UserClickModel) {
            handleUserClickModel((UserClickModel) content);
        } else if (content instanceof RetailCatalog) {
            handleRetailCatalog((RetailCatalog) content);
        }
    }

    private void handleUserClickModel(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;
    }

    private void handleBidBundle(String advertiser, BidBundle bidBundle) {
        if (bidManager == null) {
            // Not yet initialized => ignore the RFQ
            log.warning("Received BidBundle from " + advertiser + " before initialization");
        } else {
            bidManager.updateBids(advertiser, bidBundle);

            int advertiserIndex = getSimulation().agentIndex(advertiser);
            EventWriter writer = getEventWriter();
            writer.dataUpdated(advertiserIndex,TACAAConstants.DU_BIDS,bidBundle);
        }
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.retailCatalog = retailCatalog;
        generatePossibleQueries();
        bidManager.initializeQuerySpace(possibleQueries);
    }

    private void generatePossibleQueries() {
        if (retailCatalog != null && possibleQueries == null) {
            possibleQueries = new HashSet<Query>();

            for (Product product : retailCatalog) {
                Query f0 = new Query();
                Query f1_manufacturer = new Query(product.getManufacturer(), null);
                Query f1_component = new Query(null, product.getComponent());
                Query f2 = new Query(product.getManufacturer(), product.getComponent());

                possibleQueries.add(f0);
                possibleQueries.add(f1_manufacturer);
                possibleQueries.add(f1_component);
                possibleQueries.add(f2);
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
        if(queryReportManager!=null)
            queryReportManager.sendQueryReportToAll();
    }

    public Auction runAuction(Query query) {
        //TODO: Check against possible queries
        if (auctionFactory != null) {
            return auctionFactory.runAuction(query);
        }

        return null;
    }

    protected class ClickMonitor implements UserEventListener {

        public void queryIssued(Query query) {
        }

        public void viewed(Query query, Ad ad, int slot, String advertiser) {
        }

        public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
            DefaultPublisher.this.charge(advertiser,cpc);
        }

        public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
        }
    }


    public void sendQueryReport(String advertiser, QueryReport report) {
        super.sendQueryReport(advertiser, report);

        int index = getSimulation().agentIndex(advertiser);

        int impressions = 0;
        int clicks = 0;

        for(int i = 0; i < report.size(); i++) {
            impressions += report.getImpressions(i);
            clicks += report.getClicks(i);
        }

        EventWriter writer = getEventWriter();
        writer.dataUpdated(index,DU_IMPRESSIONS, impressions);
        writer.dataUpdated(index,DU_CLICKS, clicks);

    }
}
