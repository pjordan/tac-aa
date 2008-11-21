package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.sim.Users;
import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.SalesReport;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.aw.Message;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * @author Lee Callender
 */
public class DefaultUsers extends Users implements TACAAConstants, TimeListener {
    private Hashtable<String, SalesReport> salesTable; 
    private int salesReportCount;

    public DefaultUsers() {
    }

    public void nextTimeUnit(int date) {
    }

    protected void setup() {
      super.setup();
      this.log = Logger.getLogger(DefaultUsers.class.getName());

      int numberOfAdvertisers = getNumberOfAdvertisers();
      if(numberOfAdvertisers <= 0){
        throw new IllegalArgumentException("Number of advertisers not specified in config.");
      }

      salesTable = new Hashtable<String, SalesReport>(numberOfAdvertisers);
      String[] advertisers = getAdvertiserAddresses();
      for(int i = 0; i < advertisers.length; i++){
        log.finest("Adding Sales Report for"+advertisers[i]);
        SalesReport report = new SalesReport();
        Query query = new Query();
        query.setComponent(String.valueOf(salesReportCount));
        query.setManufacturer("Blah");
        report.addQuery(query);

        salesTable.put(advertisers[i], report);
      }
      

    }

    protected void stopped() {
    }

    protected void shutdown() {
    }
    
    // -------------------------------------------------------------------
    // Message handling
    // -------------------------------------------------------------------

    protected void messageReceived(Message message) {

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


    public void sendSalesReportsToAll() {

      for (Iterator<String> it=salesTable.keySet().iterator(); it.hasNext(); ) {
        String key = it.next();

        sendMessage(key, salesTable.get(key));
        salesTable.put(key, new SalesReport()); //Initialize new Sales Report for every day
      }
    }

    // ------------------
    //
    // ------------------
    protected final Ranking getRanking(Query query, Publisher publisher){
      //THIS MAY NOT BE HOW WE WANT TO ACCESS RANKINGS!
      return publisher.runAuction(query).getRanking();
    }

    // ------------------
    // Utility Methods
    // ------------------

   /* protected SalesReportManager getSalesReportManager(String agent){
      SalesReportManager rep = (SalesReportManager) salesTable.get(agent);
      if (rep != null) {
        return rep;
      }

      if (salesReportCount < sales.length) {
        rep = sales[salesReportCount++];
        rep.setName(agent);
        salesTable.put(agent, rep);
        return rep;
      } else {
        // More agents than manufacturers. Should NOT be possible!
        log.severe("AGENT " + agent + " CLAIMS TO BE ADVERTISER BUT "
		      + "ALREADY HAVE " + sales.length + " ADVERTISERS!!!");
        SalesReportManager r = new SalesReportManager();
        r.setName(agent);
        salesTable.put(agent, r);
        return r;
      }

    }
    */
}
