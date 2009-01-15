package edu.umich.eecs.tac.sim;

/**
 * @author Ben Cassell
 */

import edu.umich.eecs.tac.props.BankStatus;

public class DummyTACAASimulation extends TACAASimulation {

	public DummyTACAASimulation() {
		super(null);
	}
	
	@Override
	public void sendBankStatus(String accountName, BankStatus status) {
		return;
	}

}
