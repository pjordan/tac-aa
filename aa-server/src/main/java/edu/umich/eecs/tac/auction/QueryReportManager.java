package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.user.UserEventListener;

/**
 * The query report manager stores and distributes the query reports.
 * 
 * @author Patrick Jordan
 */
public interface QueryReportManager extends UserEventListener {
	/**
	 * Add an advertiser to the dataset.
	 * 
	 * @param name
	 *            the name of the advertiser
	 */
	void addAdvertiser(String name);

	/**
	 * Distributed all of the query reports to the advertisers.
	 */
	void sendQueryReportToAll();

	/**
	 * The number of advertisers in the dataset.
	 * 
	 * @return number of advertisers in the dataset.
	 */
	int size();
}
