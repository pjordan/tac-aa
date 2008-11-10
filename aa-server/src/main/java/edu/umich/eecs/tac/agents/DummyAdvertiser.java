package edu.umich.eecs.tac.agents;

/**
 * @author Lee Callender, Patrick Jordan
 */

import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.props.SimulationStatus;
import se.sics.isl.transport.Transportable;

import java.util.logging.Logger;
import java.util.Random;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import edu.umich.eecs.tac.props.*;

public class DummyAdvertiser extends Agent {

    private Logger log = Logger.global;

    private boolean initialized;
    private StartInfo startInfo;
    private RetailCatalog retailCatalog;
    private AdvertiserInfo advertiserInfo;
    private String publisherAddress;
    private BidBundle bidBundle;

    private Queue<SalesReport> salesReportQueue;
    private Queue<QueryReport> queryReportQueue;

    private int recentConversions;
    private int currentDate;
    private int distributionWindow;
    private double distributionDecay;
    private int distributionCapacity;
    private double baseConversionRate;

    private Random random = new Random();



    public DummyAdvertiser() {


    }

    protected void messageReceived(Message message) {
        Transportable content = message.getContent();

        if (content instanceof QueryReport) {
            handleQueryReport((QueryReport) content);
        } else if (content instanceof SalesReport) {
            handleSalesReport((SalesReport) content);
        } else if (content instanceof SimulationStatus) {
            handleSimulationStatus((SimulationStatus) content);
        } else if (content instanceof RetailCatalog) {
            handleRetailCatalog((RetailCatalog) content);
        } else if (content instanceof AdvertiserInfo) {
            handleAdvertiserInfo((AdvertiserInfo) content);
        } else if (content instanceof StartInfo) {
            handleStartInfo((StartInfo) content);
        }
    }

    private void handleSimulationStatus(SimulationStatus simulationStatus) {
        sendBidAndAds();
        // Now nothing more will be done until the next day
        currentDate = simulationStatus.getCurrentDate() + 1;
    }


    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.retailCatalog = retailCatalog;
        checkInitialized();
    }

    private void handleAdvertiserInfo(AdvertiserInfo advertiserInfo) {
        this.advertiserInfo = advertiserInfo;
        publisherAddress = advertiserInfo.getPublisherId();
        distributionWindow = advertiserInfo.getDistributionWindow();
        distributionDecay = advertiserInfo.getDecayRate();
        distributionCapacity = advertiserInfo.getDistributionCapacity();
        
        checkInitialized();
    }

    private void handleStartInfo(StartInfo startInfo) {
        this.startInfo = startInfo;
        checkInitialized();
    }

    private void handleQueryReport(QueryReport queryReport) {
        queryReportQueue.offer(queryReport);

        while(queryReportQueue.size()>distributionWindow && !queryReportQueue.isEmpty()) {
            queryReportQueue.remove();
        }

        recentConversions = calculateRecentConversions();
        baseConversionRate = calculateBaseConversionRate();
    }

    private void handleSalesReport(SalesReport salesReport) {
        salesReportQueue.offer(salesReport);

        while(salesReportQueue.size()>distributionWindow && !salesReportQueue.isEmpty()) {
            salesReportQueue.remove();
        }
    }


    private int calculateRecentConversions() {
        int conversions = 0;

        for(SalesReport report : salesReportQueue) {
            for(int i = 0 ; i < report.size(); i++) {
                conversions += report.getConversions(i);
            }
        }

        return conversions;
    }

    private double calculateBaseConversionRate() {
        return Math.pow(distributionDecay, Math.max(0, distributionCapacity - recentConversions));
    }

    private void checkInitialized() {
        //TODO: update this to reflect initialization state
        this.initialized = this.startInfo != null && this.retailCatalog != null && this.advertiserInfo != null;
    }

    protected void simulationSetup() {

        bidBundle = new BidBundle();

        salesReportQueue = new LinkedList<SalesReport>();
        queryReportQueue = new LinkedList<QueryReport>();
        
        // Add the advertiser name to the logger name for convenient
        // logging. Note: this is usually a bad idea because the logger
        // objects will never be garbaged but since the dummy names always
        // are the same in TAC AA games, only a few logger objects
        // will be created.
        this.log = Logger.getLogger(DummyAdvertiser.class.getName() + '.' + getName());
        log.fine("dummy " + getName() + " simulationSetup");
    }

    protected void simulationFinished() {
        bidBundle = null;
        salesReportQueue.clear();
        queryReportQueue.clear();
    }


    private boolean isInitialized() {
        return initialized;
    }

    protected void sendBidAndAds() {
        if (bidBundle != null && publisherAddress != null) {
            sendMessage(publisherAddress, bidBundle);
        }
    }
}
