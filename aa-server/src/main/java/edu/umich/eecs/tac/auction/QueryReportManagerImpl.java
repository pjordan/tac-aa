package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.sim.QueryReportSender;
import edu.umich.eecs.tac.user.UserEventListener;
import com.botbox.util.ArrayUtils;

/**
 * @author Patrick Jordan, Lee Callender
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
     *
     * @param queryReportSender the query report sender
     * @param advertisersCount  the initial advertiser count
     */
    public QueryReportManagerImpl(QueryReportSender queryReportSender, int advertisersCount) {
        this.queryReportSender = queryReportSender;
        advertisers = new String[advertisersCount];
        queryReports = new QueryReport[advertisersCount];
        this.advertisersCount = advertisersCount;
    }

    /**
     * Add an advertiser to the dataset
     *
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

        if (queryReports[index] == null) {
            queryReports[index] = new QueryReport();
        }

        queryReports[index].addClicks(query, clicks, cost);
    }

    protected void addImpressions(String name, Query query, int nonPromoted, int promoted, Ad ad, double positionSum) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, name);
        if (index < 0) {
            index = doAddAccount(name);
        }

        if (queryReports[index] == null) {
            queryReports[index] = new QueryReport();
        }

        queryReports[index].addImpressions(query, nonPromoted, promoted, ad, positionSum);
    }

    /**
     * Send each advertiser their report.
     */
    public void sendQueryReportToAll() {
        // Make sure all query reports are non-null
        for (int i = 0; i < advertisersCount; i++) {
            if (queryReports[i] == null) {
                queryReports[i] = new QueryReport();
            }
        }

        // For each advertiser, tell the other advertisers about their positions and ads
        for (int advertiserIndex = 0; advertiserIndex < advertisersCount; advertiserIndex++) {

            QueryReport baseReport = queryReports[advertiserIndex];

            String baseAdvertiser = advertisers[advertiserIndex];

            for (int index = 0; index < baseReport.size(); index++) {
                Query query = baseReport.getQuery(index);

                double position = baseReport.getPosition(index);

                Ad ad = baseReport.getAd(index);

                for (int otherIndex = 0; otherIndex < advertisersCount; otherIndex++) {
                    queryReports[otherIndex].setAdAndPosition(query, baseAdvertiser, ad, position);
                }
            }
        }

        // Send the query reports
        for (int i = 0; i < advertisersCount; i++) {
            QueryReport report = queryReports[i];

            queryReports[i] = null;

            queryReportSender.sendQueryReport(advertisers[i], report);
        }
    }


    public int size() {
        return advertisersCount;
    }

    public void queryIssued(Query query) {
    }

    public void viewed(Query query, Ad ad, int slot, String advertiser, boolean isPromoted) {
        if(isPromoted){
          addImpressions(advertiser, query, 0, 1, ad, slot);
        }else{
          addImpressions(advertiser, query, 1, 0, ad, slot); 
        }
    }

    public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
        addClicks(advertiser, query, 1, cpc);
    }

    public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
    }
}
