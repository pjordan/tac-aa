package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.sim.QueryReportSender;
import edu.umich.eecs.tac.user.UserEventListener;
import com.botbox.util.ArrayUtils;

/**
 * @author Patrick Jordan
 */
public class QueryReportManagerImpl implements QueryReportManager {

    /**
     * The advertisers
     */
    private String[] advertisers;

    /**
     * The advertisers indexed so far
     */
    private int advertisersCount;

    /**
     * The query reports
     */
    private QueryReport[] queryReports;

    /**
     * Query report sender
     */
    private QueryReportSender queryReportSender;

    /**
     * Create a new query report manager
     * @param queryReportSender the query report sender
     * @param advertisersCount the initial advertiser count
     */
    public QueryReportManagerImpl(QueryReportSender queryReportSender, int advertisersCount) {
        this.queryReportSender = queryReportSender;
        advertisers = new String[advertisersCount];
        queryReports = new QueryReport[advertisersCount];
        this.advertisersCount = advertisersCount;
    }

    /**
     * Add an advertiser to the dataset
     * @param name the name of the advertiser to add
     */
    public void addAdvertiser(String name) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, name);
        if (index < 0) {
            doAddAccount(name);
        }
    }


    private synchronized int doAddAccount(String name) {
        if (advertisersCount == advertisers.length) {
            int newSize = advertisersCount + 8;
            advertisers = (String[]) ArrayUtils.setSize(advertisers, newSize);
            queryReports = (QueryReport[]) ArrayUtils.setSize(queryReports, newSize);
        }
        advertisers[advertisersCount] = name;

        return advertisersCount++;
    }

    protected void addClicks(String name, Query query, int clicks, double cost) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, name);
        if (index < 0) {
            index = doAddAccount(name);
        }

        if(queryReports[index]==null) {
            queryReports[index] = new QueryReport();
        }

        queryReports[index].addClicks(query,clicks,cost);
    }

    protected void addImpressions(String name, Query query, int impression, Ad ad, double positionSum) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, name);
        if (index < 0) {
            index = doAddAccount(name);
        }

        if(queryReports[index]==null) {
            queryReports[index] = new QueryReport();
        }

        queryReports[index].addImpressions(query,impression,ad,positionSum);
    }

    /**
     * Send each advertiser their report.
     */
    public void sendQueryReportToAll() {
        for (int i = 0; i < advertisersCount; i++) {
            QueryReport report = queryReports[i];
            if (report == null) {
                report = new QueryReport();
            } else {
                // Can not simply reset the bank report after sending it
                // because the message might be in a send queue or used in an
                // internal agent.  Only option is to simply forget about it
                // and create a new bank report for the agent the next day.
                queryReports[i] = null;
            }

            queryReportSender.sendQueryReport(advertisers[i], report);
        }
    }


    public int size() {
        return advertisersCount;
    }
    
    public void queryIssued(Query query) {
    }

    public void viewed(Query query, Ad ad, int slot, String advertiser) {
        addImpressions( advertiser, query, 1, ad, slot);
    }

    public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
        addClicks( advertiser, query, 1, cpc);
    }

    public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
    }
}
