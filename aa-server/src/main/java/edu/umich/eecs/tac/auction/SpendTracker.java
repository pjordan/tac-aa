package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Query;

/**
 * The spend tracker tracks the daily spend each advertiser incurs.
 *
 * @author Patrick Jordan
 */
public interface SpendTracker {
    /**
     * Adds the advertiser
     *
     * @param advertiser the advertiser
     */
    void addAdvertiser(String advertiser);

    /**
     * Returns the daily spend the advertiser incurred.
     *
     * @param advertiser the advertiser
     * 
     * @return the daily spend the advertiser incurred.
     */
    double getDailyCost(String advertiser);

    /**
     * Returns the daily spend the advertiser incurred for a given query.
     *
     * @param advertiser the advertiser
     * @param query the query
     * @return the daily spend the advertiser incurred for a given query.
     */
    double getDailyCost(String advertiser, Query query);

    /**
     * Sets the current cost to zero for all advertisers.
     */
    void reset();

    /**
     * Adds the cost to the advertiser and query.
     *
     * @param advertiser the advertiser
     * @param query the query
     * @param cost the cost to add
     */
    void addCost(String advertiser, Query query, double cost);

    /**
     * Get the number of advertisers tracked.
     *
     * @return the number of advertisers tracked.
     */
    int size();
}
