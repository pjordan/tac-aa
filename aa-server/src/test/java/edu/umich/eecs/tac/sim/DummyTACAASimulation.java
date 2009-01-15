package edu.umich.eecs.tac.sim;

/**
 * @author Ben Cassell
 */

import edu.umich.eecs.tac.props.BankStatus;

public class DummyTACAASimulation implements BankStatusSender {

	public void sendBankStatus(String accountName, BankStatus status) {
		return;
	}

}
