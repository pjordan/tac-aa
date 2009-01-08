package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.AdLink;
import edu.umich.eecs.tac.props.BidBundle;

import java.util.Set;

/**
 * @author Patrick Jordan
 */
public interface BidTracker {
    /**
     * Add the advertiser
     *
     * @param advertiser the advertiser
     */
    void addAdvertiser(String advertiser);

    /**
     * Sets the query space for the bids
     *
     * @param space the query space
     */
    void initializeQuerySpace(Set<Query> space);

    /**
     * Get the daily spend limit for the advertiser.
     *
     * @param advertiser the advertiser
     *
     * @return the daily spend limit for the advertiser.
     */
    double getDailySpendLimit(String advertiser);

    /**
     * Get the bid for the advertiser for a given query.
     *
     * @param advertiser the advertiser
     * @param query the query
     * @return the bid for the advertiser for a given query.
     */
    double getBid(String advertiser, Query query);

    /**
     * Get the daily spend limit for the advertiser for a given query.
     *
     * @param advertiser the advertiser
     * @param query the query
     * @return the daily spend limit for the advertiser for a given query.
     */
    double getDailySpendLimit(String advertiser, Query query);

    /**
     * Get the ad link for the given advertiser and query
     * @param advertiser the advertiser
     * @param query the query
     * @return the ad link for the given advertiser and query
     */
    AdLink getAdLink(String advertiser, Query query);

    /**
     * Update the bid information
     *
     * @param advertiser the advertiser
     * @param bundle the bid bundle
     */
    void updateBids(String advertiser, BidBundle bundle);

    /**
     * Get the number of advertisers tracked.
     *
     * @return the number of advertisers tracked.
     */
    int size();
}
