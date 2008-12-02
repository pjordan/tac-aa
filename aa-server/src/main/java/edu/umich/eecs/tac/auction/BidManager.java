package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.AdLink;
import se.sics.tasim.aw.TimeListener;

import java.util.Set;

/**
 * @author Patrick Jordan
 */
public interface BidManager extends TimeListener {
    void addAdvertiser(String advertiser);
    void initializeQuerySpace(Set<Query> space);
    double getBid(String advertiser, Query query);
    double getQualityScore(String advertiser, Query query);
    AdLink getAdLink(String advertiser, Query query);
    void updateBids(String advertiser, BidBundle bundle);
    Set<String> advertisers();
}
