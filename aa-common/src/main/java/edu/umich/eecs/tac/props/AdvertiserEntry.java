package edu.umich.eecs.tac.props;

/**
 * This interface designates a {@link edu.umich.eecs.tac.props.KeyedEntry keyed entry} whose key is an advertiser.
 * 
 * @author Patrick Jordan
 */
public interface AdvertiserEntry extends KeyedEntry<String>{

    /**
     * Returns the advertiser for the entry. 
     *
     * @return the advertiser for the entry.
     */
    public String getAdvertiser();
}
