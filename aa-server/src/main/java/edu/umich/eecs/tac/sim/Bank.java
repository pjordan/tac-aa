package edu.umich.eecs.tac.sim;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Oct 28, 2008
 * Time: 4:02:45 PM
 * To change this template use File | Settings | File Templates.
 */
import java.util.logging.Logger;

import com.botbox.util.ArrayUtils;
import edu.umich.eecs.tac.props.BankStatus;

public class Bank {

  private TACAASimulation simulation;
  private String[] accountNames;
  private double[] accountAmounts;
  private BankStatus[] bankStatus;
  private int accountNumber;  //number of accounts

  public Bank(TACAASimulation simulation, int accountNumber) {
    this.simulation = simulation;

    accountNames = new String[accountNumber];
    accountAmounts = new double[accountNumber];
    bankStatus = new BankStatus[accountNumber];
  }

  public void addAccount(String name) {
    int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
    if (index < 0) {
      doAddAccount(name);
    }
  }

  private synchronized int doAddAccount(String name) {
    if (accountNumber == accountNames.length) {
      int newSize = accountNumber + 8;
      accountNames = (String[])
	ArrayUtils.setSize(accountNames, newSize);
      accountAmounts = ArrayUtils.setSize(accountAmounts, newSize);
      bankStatus = (BankStatus[]) ArrayUtils.setSize(bankStatus, newSize);
    }
    accountNames[accountNumber] = name;
    accountAmounts[accountNumber] = 0.0d;
    return accountNumber++;
  }

  public double getAccountStatus(String name) {
    int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
    return index >= 0
      ? accountAmounts[index]
      : 0.0d;
  }

  public double deposit(String name, double amount) {
    int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
    if (index < 0) {
      index = doAddAccount(name);
    }
    accountAmounts[index] += amount;
    return accountAmounts[index];
  }

  public double withdraw(String name, double amount) {
    return deposit(name, -amount);
  }

  public void sendBankStatusToAll() {
    for (int i = 0; i < accountNumber; i++) {
      BankStatus status = bankStatus[i];
      if (status == null) {
	      status = new BankStatus();
      } else {
	    // Can not simply clear the bank status after sending it
	    // because the message might be in a send queue or used in an
	    // internal agent.  Only option is to simply forget about it
	    // and create a new bank status for the agent the next day.
	      bankStatus[i] = null;
      }
      status.setAccountBalance(accountAmounts[i]);
      simulation.sendBankStatus(accountNames[i], status);
    }
  }

  // DEBUG FINALIZE REMOVE THIS!!!
  protected void finalize() throws Throwable {
    Logger.global.info("BANK FOR SIMULATION "
		       + simulation.getSimulationInfo().getSimulationID()
		       + " IS BEING GARBAGED");
    super.finalize();
  }

} // Bank
