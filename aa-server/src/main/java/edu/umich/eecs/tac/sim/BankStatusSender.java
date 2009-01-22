package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.BankStatus;

/**
 * Created by IntelliJ IDEA. User: pjordan Date: Jan 15, 2009 Time: 2:09:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BankStatusSender {
	void sendBankStatus(String agentName, BankStatus status);
}
