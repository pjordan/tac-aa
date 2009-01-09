package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.SalesReport;

/**
 * @author Patrick Jordan
 */
public interface SalesReportSender {
    /**
     * Send sales report to the advertiser.
     *
     * @param advertiser the advertiser whose report is being sent
     * @param report the report being sent
     */
    void sendSalesReport(String advertiser, SalesReport report);

    /**
     * Broadcast the conversions made by the advertiser
     * @param advertiser the advertiser
     * @param conversions the number of conversions
     */
    void broadcastConversions(String advertiser, int conversions);
}
