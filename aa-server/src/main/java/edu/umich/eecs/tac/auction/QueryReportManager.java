package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.sim.TACAASimulation;
import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.agents.DefaultPublisher;
import edu.umich.eecs.tac.user.UserEventListener;
import com.botbox.util.ArrayUtils;
import se.sics.tasim.is.EventWriter;

/**
 * @author Patrick Jordan
 */
public class QueryReportManager implements UserEventListener {
    private String[] advertisers;
    private int advertisersCount;
    private QueryReport[] queryReports;
    private Publisher publisher;

    public QueryReportManager(Publisher publisher, int advertisersCount) {
        this.publisher = publisher;
        advertisers = new String[advertisersCount];
        queryReports = new QueryReport[advertisersCount];
        this.advertisersCount = advertisersCount;
    }


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

    protected void addClick(String name, Query query, int clicks) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, name);
        if (index < 0) {
            index = doAddAccount(name);
        }

        if(queryReports[index]==null) {
            queryReports[index] = new QueryReport();
        }

        queryReports[index].addClicks(query,clicks);
    }

    protected void addImpression(String name, Query query, int impression) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, name);
        if (index < 0) {
            index = doAddAccount(name);
        }

        if(queryReports[index]==null) {
            queryReports[index] = new QueryReport();
        }

        queryReports[index].addImpressions(query,impression);
    }

    public void sendQueryReportToAll() {
        for (int i = 0; i < advertisersCount; i++) {
            QueryReport report = queryReports[i];
            if (report == null) {
                report = new QueryReport();
            } else {
                // Can not simply clear the bank report after sending it
                // because the message might be in a send queue or used in an
                // internal agent.  Only option is to simply forget about it
                // and create a new bank report for the agent the next day.
                queryReports[i] = null;
            }

            publisher.sendQueryReport(advertisers[i], report);
        }
    }



    public void queryIssued(Query query) {
    }

    public void viewed(Query query, Ad ad, int slot, String advertiser) {
        addImpression(advertiser,query,1);
    }

    public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
        addClick(advertiser,query,1);

    }

    public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
    }
}
