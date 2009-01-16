package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Auction;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.AuctionInfo;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import se.sics.isl.util.ConfigManager;

/**
 * @author Patrick Jordan
 */
public interface AuctionFactory {

    Auction runAuction(Query query);

    public BidManager getBidManager();

    public PublisherInfo getPublisherInfo();

    public void setPublisherInfo(PublisherInfo publisherInfo);

    public void setBidManager(BidManager bidManager);

    public AuctionInfo getAuctionInfo();

    public void setAuctionInfo(AuctionInfo auctionInfo);

    public void configure(ConfigProxy configProxy);
}
