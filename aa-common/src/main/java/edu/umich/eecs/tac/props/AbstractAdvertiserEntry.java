package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractAdvertiserEntry extends AbstractStringEntry implements AdvertiserEntry {

    public String getAdvertiser() {
        return getKey();
    }

    protected void setAdvertiser(String advertiser) {
        setKey(advertiser);
    }

}
