package edu.umich.eecs.tac.sim;

/**
 * @author Ben Cassell
 */

import edu.umich.eecs.tac.props.BankStatus;
import se.sics.isl.util.ConfigManager;

public class DummyTACAASimulation extends TACAASimulation {

	private DummyTACAASimulation(ConfigManager config) {
		super(config);
	}

	@Override
	public void sendBankStatus(String accountName, BankStatus status) {
		
	}

}
