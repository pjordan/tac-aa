package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.sim.Publisher;
import se.sics.isl.util.ConfigManager;

/**
 * @author Patrick Jordan
 */
public interface AuctionFactory {
    Auction runAuction(Query query);
    public BidManager getBidManager();
    public double getSquashValue();
    public void setSquashValue(double squash);
    public int getSlotLimit();
    public void setSlotLimit(int slotLimit);
    public void setBidManager(BidManager bidManager);
}
