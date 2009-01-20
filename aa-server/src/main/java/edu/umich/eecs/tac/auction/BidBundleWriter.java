package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.BidBundle;

/**
 * @author Patrick Jordan
 */
public interface BidBundleWriter {
    void writeBundle(String advertiser, BidBundle bundle);
}
