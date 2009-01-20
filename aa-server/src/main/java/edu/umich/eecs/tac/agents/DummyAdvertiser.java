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
import java.util.*;

import edu.umich.eecs.tac.props.*;

public class DummyAdvertiser extends Agent {

    private Logger log = Logger.global;

    private RetailCatalog retailCatalog;
    private String publisherAddress;
    private BidBundle bidBundle;

    private Query[] queries;
    private double[] impressions;
    private double[] clicks;
    private double[] conversions;
    private double[] values;

    public DummyAdvertiser() {
    }

    protected void messageReceived(Message message) {
    	try {
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
            }
    	}
    	catch(NullPointerException e){
    		System.err.println("Null message");
    		return;
    	}
    }

    private void handleSimulationStatus(SimulationStatus simulationStatus) {
        sendBidAndAds();
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.retailCatalog = retailCatalog;
        generateQuerySpace();
    }

    private void handleAdvertiserInfo(AdvertiserInfo advertiserInfo) {
        publisherAddress = advertiserInfo.getPublisherId();

    }

    private void handleQueryReport(QueryReport queryReport) {
        for(int i = 0; i < queries.length; i++) {
            Query query = queries[i];

            int index = queryReport.indexForEntry(query);
            if(index >= 0) {
                impressions[i] += queryReport.getImpressions(index);
                clicks[i] += queryReport.getClicks(index);
            }
        }
    }

    private void handleSalesReport(SalesReport salesReport) {
        for(int i = 0; i < queries.length; i++) {
            Query query = queries[i];

            int index = salesReport.indexForEntry(query);
            if(index >= 0) {
                conversions[i] += salesReport.getConversions(index);
                values[i] += salesReport.getRevenue(index);
            }
        }
    }

    protected void simulationSetup() {

        bidBundle = new BidBundle();

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
    }

    protected void sendBidAndAds() {
        bidBundle = new BidBundle();
        Ad ad = new Ad(null);

        for (int i = 0; i < queries.length; i++) {
            bidBundle.addQuery(queries[i], values[i]/clicks[i], ad);
        }

        if (bidBundle != null && publisherAddress != null) {
            sendMessage(publisherAddress, bidBundle);
        }
    }

    private void generateQuerySpace() {
        if (retailCatalog != null && queries == null) {
            Set<Query> queryList = new HashSet<Query>();

            for (Product product : retailCatalog) {
                // Create f0
                Query f0 = new Query();

                // Create f1's
                Query f1_manufacturer = new Query(product.getManufacturer(), null);
                Query f1_component = new Query(null, product.getComponent());

                // Create f2
                Query f2 = new Query(product.getManufacturer(), product.getComponent());

                queryList.add(f0);
                queryList.add(f1_manufacturer);
                queryList.add(f1_component);
                queryList.add(f2);
            }

            queries = queryList.toArray(new Query[0]);
            impressions = new double[queries.length];
            clicks= new double[queries.length];
            conversions = new double[queries.length];
            values = new double[queries.length];

            for(int i = 0; i < queries.length; i++) {
                impressions[i] = 100;
                clicks[i] = 30;
                conversions[i] = 1;
                values[i] = retailCatalog.getSalesProfit(0);
            }
        }
    }
}
