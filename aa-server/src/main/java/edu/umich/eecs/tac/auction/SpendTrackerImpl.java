package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Query;

import java.util.Arrays;

import com.botbox.util.ArrayUtils;

/**
 * @author Patrick Jordan
 */
public class SpendTrackerImpl implements SpendTracker {
    private String[] advertisers;
    private int advertisersCount;
    private QueryBudget[] queryBudget;


    public SpendTrackerImpl() {
        this(0);
    }

    public SpendTrackerImpl(int advertisersCount) {
        this.advertisersCount = advertisersCount;
        advertisers = new String[advertisersCount];
        queryBudget = new QueryBudget[advertisersCount];
    }

    public void addAdvertiser(String advertiser) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);
        if (index < 0) {
            doAddAdvertiser(advertiser);
        }
    }


    private synchronized int doAddAdvertiser(String advertiser) {
        if (advertisersCount == advertisers.length) {
            int newSize = advertisersCount + 8;
            advertisers = (String[]) ArrayUtils.setSize(advertisers, newSize);
            queryBudget = (QueryBudget[])ArrayUtils.setSize(queryBudget, newSize);
        }

        advertisers[advertisersCount] = advertiser;

        return advertisersCount++;
    }

    public void addCost(String advertiser, Query query, double cost) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);

        if (index < 0) {
            index = doAddAdvertiser(advertiser);
        }

        if (queryBudget[index] == null) {
            queryBudget[index] = new QueryBudget(0);
        }

        this.queryBudget[index].addCost(query,cost);
    }

    public double getDailyCost(String advertiser) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);

        if (index < 0) {
            return 0.0;
        }

        if (queryBudget[index] == null) {
            queryBudget[index] = new QueryBudget(0);
        }

        return queryBudget[index].getTotalCost();
    }

    public double getDailyCost(String advertiser, Query query) {
        int index = ArrayUtils.indexOf(advertisers, 0, advertisersCount, advertiser);

        if (index < 0) {
            return 0.0;
        }
        else if (queryBudget[index] == null) {
            queryBudget[index] = new QueryBudget(0);
        }

        return queryBudget[index].getCost(query);

    }

    public void reset() {
        for(QueryBudget budget : queryBudget) {
            if(budget!=null)
                budget.reset();
        }
    }

    public int size() {
        return advertisersCount;
    }

    private static class QueryBudget {
        private Query[] queries;
        private double[] cost;
        private int queryCount;
        private double totalCost;

        public QueryBudget(int queryCount) {
            queries = new Query[queryCount];
            cost = new double[queryCount];
            this.queryCount = queryCount;
        }

        private synchronized int doAddQuery(Query query) {
            if (queryCount == queries.length) {
                int newSize = queryCount + 8;
                queries = (Query[]) ArrayUtils.setSize(queries, newSize);
                cost = ArrayUtils.setSize(cost, newSize);
            }
            queries[queryCount] = query;

            return queryCount++;
        }

        protected void addCost(Query query, double cost) {
            int index = ArrayUtils.indexOf(queries, 0, queryCount, query);

            if (index < 0) {
                index = doAddQuery(query);
            }

            this.cost[index] += cost;
            this.totalCost += cost;
        }

        protected double getCost(Query query) {
            int index = ArrayUtils.indexOf(queries, 0, queryCount, query);

            if (index < 0) {
                return 0.0;
            }

            return this.cost[index];
        }

        protected double getTotalCost() {
            return totalCost;
        }

        public void reset() {
            Arrays.fill(cost, 0.0);
            totalCost = 0.0;
        }

    }
}
