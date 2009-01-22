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
}
