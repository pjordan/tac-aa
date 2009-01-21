package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.util.config.ConfigProxy;

/**
 * @author Patrick Jordan
 */
public interface AuctionFactory {

    Auction runAuction(Query query);

    public BidManager getBidManager();

    public PublisherInfo getPublisherInfo();

    public void setPublisherInfo(PublisherInfo publisherInfo);

    public void setBidManager(BidManager bidManager);

    public SlotInfo getSlotInfo();

    public void setSlotInfo(SlotInfo slotInfo);

    public ReserveInfo getReserveInfo();

    public void setReserveInfo(ReserveInfo reserveInfo);

    public void configure(ConfigProxy configProxy);
}
