package edu.umich.eecs.tac.props;

import java.util.ArrayList;

/**
 * This class provides a skeletal implementation of a list containing
 * {@link edu.umich.eecs.tac.props.AdvertiserEntry advertiser entries} with
 * supporting methods for interacting entries specified by a given advertiser.
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
	 * @param advertiser
	 *            the advertiser used to add the new {@link AdvertiserEntry
	 *            advertiser entry}.
	 * 
	 * @return the index of the newly generated {@link AdvertiserEntry entry}.
	 * 
	 * @throws NullPointerException
	 *             if the <code>advertiser</code> is <code>null</code>.
	 */
	public int addAdvertiser(String advertiser) {
		return addKey(advertiser);
	}

	/**
	 * Returns <code>true</code> if the advertiser key is in the list and
	 * <code>false</code> otherwise. This method delegates to
	 * {@link #containsKey(Object)}.
	 * 
	 * @param advertiser
	 *            the advertiser key to check for containment.
	 * 
	 * @return <code>true</code> if the key is in the list and
	 *         <code>false</code> otherwise.
	 */
	public boolean containsAdvertiser(String advertiser) {
		return containsKey(advertiser);
	}
}
