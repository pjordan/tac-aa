package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class provides a skeletal implementation of the {@link AdvertiserEntry} interface.
 * 
 * @author Patrick Jordan
 */
public abstract class AbstractAdvertiserEntry extends AbstractStringEntry implements AdvertiserEntry {

    /**
     * Returns the advertiser for the entry. This method delegates to {@link #getKey()}.
     * @return the advertiser for the entry.
     */
    public String getAdvertiser() {
        return getKey();
    }

    /**
     * Sets the advertiser for the entry. This method delegates to {@link #setKey(Object)}.
     *
     * @param advertiser the advertiser for the entry.
     */
    protected void setAdvertiser(String advertiser) {
        setKey(advertiser);
    }

}
