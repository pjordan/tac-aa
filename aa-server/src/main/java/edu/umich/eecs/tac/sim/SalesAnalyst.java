package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.user.UserEventListener;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.TACAAConstants;
import com.botbox.util.ArrayUtils;

import java.util.logging.Logger;
import java.util.Map;

import se.sics.tasim.is.EventWriter;

/**
 * @author Patrick Jordan
 */
public class SalesAnalyst implements UserEventListener {
    private TACAASimulation simulation;
    private String[] accountNames;
    private int[][] accountConversions;
    private SalesReport[] salesReports;
    private int accountNumber;  //number of accounts

    public SalesAnalyst(TACAASimulation simulation, int accountNumber) {
        this.simulation = simulation;

        accountNames = new String[accountNumber];
        accountConversions = new int[accountNumber][];
        salesReports = new SalesReport[accountNumber];
    }

    public void addAccount(String name) {
        int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
        if (index < 0) {
            doAddAccount(name);
        }
    }

    private synchronized int doAddAccount(String name) {
        if (accountNumber == accountNames.length) {
            int newSize = accountNumber + 8;
            accountNames = (String[])
                    ArrayUtils.setSize(accountNames, newSize);
            accountConversions = (int[][]) ArrayUtils.setSize(accountConversions, newSize);
            salesReports = (SalesReport[]) ArrayUtils.setSize(salesReports, newSize);
        }
        accountNames[accountNumber] = name;
        accountConversions[accountNumber] = new int[getAdvertiserInfo().get(name).getDistributionWindow()];
        return accountNumber++;
    }

    public double getRecentConversions(String name) {
        int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);

        return index >= 0 ? sum(accountConversions[index]) : 0;
    }

    private int sum(int[] array) {
        int sum = 0;
        if (array != null) {
            for (int value : array) {
                sum += value;
            }
        }
        return sum;
    }

    protected int addConversions(String name, Query query, int conversions, double amount) {
        int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
        if (index < 0) {
            index = doAddAccount(name);
        }

        if(accountConversions[index]==null) {
            accountConversions[index] = new int[getAdvertiserInfo().get(name).getDistributionWindow()];
        }

        accountConversions[index][0] += conversions;

        if (salesReports[index] == null) {
            salesReports[index] = new SalesReport();
        }

        int queryIndex = salesReports[index].indexForEntry(query);
        if(queryIndex<0) {
            queryIndex = salesReports[index].addQuery(query);
        }
        salesReports[index].addConversions(queryIndex, conversions);
        salesReports[index].addRevenue(queryIndex, amount);

        return accountConversions[index][0];
    }

    public void sendSalesReportToAll() {
        EventWriter writer = simulation.getEventWriter();

        for (int i = 0; i < accountNumber; i++) {
            SalesReport report = salesReports[i];
            if (report == null) {
                report = new SalesReport();
            } else {
                // Can not simply clear the bank report after sending it
                // because the message might be in a send queue or used in an
                // internal agent.  Only option is to simply forget about it
                // and create a new bank report for the agent the next day.
                salesReports[i] = null;
            }

            simulation.sendSalesReport(accountNames[i], report);


            int index = simulation.agentIndex(accountNames[i]);
            writer.dataUpdated(index, TACAAConstants.DU_CONVERSIONS,accountConversions[i][0]);
        }

        updateConversionQueue();
    }

    private void updateConversionQueue() {
        for (int i = 0; i < accountConversions.length; i++) {
            for (int j = 0; j < accountConversions[i].length - 1; j++) {
                accountConversions[i][j + 1] = accountConversions[i][j];
            }
            accountConversions[i][0] = 0;
        }
    }

    public void queryIssued(Query query) {
    }

    public void viewed(Query query, Ad ad, int slot, String advertiser) {
    }

    public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
    }

    public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
        addConversions(advertiser, query, 1, salesProfit);
    }


    protected Map<String, AdvertiserInfo> getAdvertiserInfo() {
        return simulation.getAdvertiserInfo();
    }

    // DEBUG FINALIZE REMOVE THIS!!!
    protected void finalize() throws Throwable {
        Logger.global.info("SALESANALYST FOR SIMULATION "
                + simulation.getSimulationInfo().getSimulationID()
                + " IS BEING GARBAGED");
        super.finalize();
    }
}
