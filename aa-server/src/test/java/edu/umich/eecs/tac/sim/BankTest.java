package edu.umich.eecs.tac.sim;

/**
 * @author Ben Cassell
 */

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;

import edu.umich.eecs.tac.props.BankStatus;

public class BankTest {

	private Bank bank;
	
	@Before
	public void setUp() throws IllegalConfigurationException {
		bank = new Bank(null, 1);
	}

	@Test
	public void testAddAccount() {
		bank.addAccount("Joe's Plumbing");
		bank.deposit("Joe's Plumbing", 123.45);
		assertTrue(bank.getAccountStatus("Joe's Plumbing") == 123.45);
		assertTrue(bank.getAccountStatus("Tito's Building Supply") == 0.0);
		bank.addAccount("Joe's Plumbing");
		assertFalse(bank.getAccountStatus("Joe's Plumbing") == 0.0);
		bank.addAccount("Tito's Building Supply");
		assertTrue(bank.getAccountStatus("Tito's Building Supply") == 0.0);
	}

	@Test
	public void testDeposit() {
		bank.deposit("Maverick Enterprises", 23000000);
		assertTrue(bank.getAccountStatus("Maverick Enterprises") == 23000000);
		bank.deposit("Maverick Enterprises", -20000000);
		assertTrue(bank.getAccountStatus("Maverick Enterprises") == 3000000);
		bank.addAccount("Change Unlimited");
		bank.deposit("Change Unlimited", 00.01);
		assertTrue(bank.getAccountStatus("Maverick Enterprises") == 3000000);
		assertTrue(bank.getAccountStatus("Change Unlimited") == 00.01);
	}

	@Test
	public void testSendBankStatusToAll() {
		//TODO: figure out the simplest way to get a simulation for bank
	}

}
