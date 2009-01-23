package edu.umich.eecs.tac.props;

/**
 * This class provides a skeletal implementation of a list containing
 * {@link edu.umich.eecs.tac.props.AdvertiserEntry advertiser entries} with
 * supporting methods for interacting entries specified by a given advertiser.
 *
 * @param <T> the advertiser entry class
 *
 * @author Patrick Jordan
 */
public abstract class AbstractAdvertiserKeyedReportTransportable<T extends AdvertiserEntry>
        extends AbstractKeyedEntryList<String, T> {

    /**
     * Adds a new key to the list. The {@link #createEntry} method creates the
     * new {@link AdvertiserEntry entry} with the specified advertiser. This
     * method delegates to {@link #addKey(Object)}.
     *
     * @param advertiser the advertiser used to add the new {@link AdvertiserEntry
     *                   advertiser entry}.
     * @return the index of the newly generated {@link AdvertiserEntry entry}.
     * @throws NullPointerException if the <code>advertiser</code> is <code>null</code>.
     */
    public final int addAdvertiser(final String advertiser) throws NullPointerException {
        return addKey(advertiser);
    }

    /**
     * Returns <code>true</code> if the advertiser key is in the list and
     * <code>false</code> otherwise. This method delegates to
     * {@link #containsKey(Object)}.
     *
     * @param advertiser the advertiser key to check for containment.
     * @return <code>true</code> if the key is in the list and
     *         <code>false</code> otherwise.
     */
    public final boolean containsAdvertiser(final String advertiser) {
        return containsKey(advertiser);
    }
}
