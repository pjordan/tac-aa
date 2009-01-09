package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.user.UserEventListener;

/**
 * @author Patrick Jordan
 */
public interface SalesAnalyst extends UserEventListener, RecentConversionsTracker {
    void addAccount(String name);

    void sendSalesReportToAll();
}
