package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.QueryReport;

/**
 * @author Patrick Jordan
 */
public interface QueryReportSender {
	/**
	 * Send query report to the advertiser.
	 * 
	 * @param advertiser
	 *            the advertiser whose report is being sent
	 * @param report
	 *            the report being sent
	 */
	void sendQueryReport(String advertiser, QueryReport report);

    /**
	 * Broadcast the impressions received by the advertiser
	 *
	 * @param advertiser
	 *            the advertiser
	 * @param impressions the number of conversions
	 */
	void broadcastImpressions(String advertiser, int impressions);

    /**
	 * Broadcast the clicks received by the advertiser
	 *
	 * @param advertiser the advertiser
	 * @param clicks the number of conversions
	 */
	void broadcastClicks(String advertiser, int clicks);
}
