package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;

/**
 * Created by IntelliJ IDEA.
 * User: pjordan
 * Date: Jan 8, 2009
 * Time: 7:35:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Auctioneer {
    Auction runAuction(Query query);
}
