package edu.umich.eecs.tac.props;

import java.util.ArrayList;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractAdvertiserKeyedReportTransportable <T extends AdvertiserEntry> extends AbstractKeyedEntryListTransportable<String,T> {
    public AbstractAdvertiserKeyedReportTransportable() {
        this.entries = new ArrayList<T>();
    }

    public int addAdvertiser(String advertiser) {
        return addKey(advertiser);
    }

    public boolean containsAdvertiser(String advertiser) {
        return containsKey(advertiser);
    }
}
