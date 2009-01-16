package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.PublisherInfo;

/**
 * @author Patrick Jordan
 */
public interface PublisherInfoSender {
    void sendPublisherInfoToAll();

    PublisherInfo getPublisherInfo();

    void sendPublisherInfo(String advertiser);
}
