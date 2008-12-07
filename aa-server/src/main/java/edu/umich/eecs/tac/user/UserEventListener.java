package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;

/**
 * @author Patrick Jordan
 */
public interface UserEventListener {
    void queryIssued(Query query);
    void viewed(Query query, Ad ad, int slot, String advertiser);
    void clicked(Query query, Ad ad, int slot, double cpc, String advertiser);
    void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser);
}
