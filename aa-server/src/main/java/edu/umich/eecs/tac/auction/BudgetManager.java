package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Query;

/**
 * The budget manager tracks the daily spend each advertiser incurs.
 *
 * @author Patrick Jordan
 */
public interface BudgetManager {
    /**
     * Add the advertiser
     *
     * @param advertiser the advertiser
     */
    void addAdvertiser(String advertiser);

    /**
     * Get the daily spend the advertiser incurred.
     *
     * @param advertiser the advertiser
     * 
     * @return the daily spend the advertiser incurred.
     */
    double getDailyCost(String advertiser);

    /**
     * Get the daily spend the advertiser incurred for a given query.
     *
     * @param advertiser the advertiser
     * @param query the query
     * @return the daily spend the advertiser incurred for a given query.
     */
    double getDailyCost(String advertiser, Query query);

    /**
     * Set the current cost to zero for all advertisers.
     */
    void reset();

    /**
     * Get the number of advertisers tracked.
     *
     * @return the number of advertisers tracked.
     */
    int size();
}
