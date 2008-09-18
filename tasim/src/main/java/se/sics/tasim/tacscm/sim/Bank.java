/**
 * TAC Supply Chain Management Simulator
 * http://www.sics.se/tac/    tac-dev@sics.se
 *
 * Copyright (c) 2001-2005 SICS AB. All rights reserved.
 *
 * SICS grants you the right to use, modify, and redistribute this
 * software for noncommercial purposes, on the conditions that you:
 * (1) retain the original headers, including the copyright notice and
 * this text, (2) clearly document the difference between any derived
 * software and the original, and (3) acknowledge your use of this
 * software in pertaining publications and reports.  SICS provides
 * this software "as is", without any warranty of any kind.  IN NO
 * EVENT SHALL SICS BE LIABLE FOR ANY DIRECT, SPECIAL OR INDIRECT,
 * PUNITIVE, INCIDENTAL OR CONSEQUENTIAL LOSSES OR DAMAGES ARISING OUT
 * OF THE USE OF THE SOFTWARE.
 *
 * -----------------------------------------------------------------
 *
 * Bank
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Oct 30 13:29:21 2002
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */

package se.sics.tasim.tacscm.sim;

import java.util.Random;
import java.util.logging.Logger;

import com.botbox.util.ArrayUtils;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.props.BankStatus;

public class Bank
{
  
  private TACSCMSimulation simulation;
  private String[] accountNames;
  private long[] accountAmounts;
  private BankStatus[] bankStatus;
  private int accountNumber;
  
  protected int daysPerYear;
  protected int debtInterestRate;
  protected int depositInterestRate;
  protected double interestRateDayPositive;
  protected double interestRateDayNegative;
  
  public Bank(TACSCMSimulation simulation, int daysPerYear, int accountNumber)
  {
    ConfigManager config = simulation.getConfig();
    Random random = simulation.getBankRandom();
    this.simulation = simulation;
    this.daysPerYear = daysPerYear;
    // Make sure there is a positive number of days in one simulation year
    if (daysPerYear < 1)
    {
      daysPerYear = 1;
    }
    int minInterest = config.getPropertyAsInt("bank.minInterest", 10);
    int maxInterest = config.getPropertyAsInt("bank.maxInterest", 20);
    int depositDebtDiff = config.getPropertyAsInt("bank.depositDebtDiff", 0);
    double depositDebtRatio = config.getPropertyAsDouble("bank.depositDebtRatio", 1.0);
    
    if (minInterest < 0 || maxInterest < minInterest)
    {
      throw new IllegalArgumentException("illegal interest interval");
    }
    
    debtInterestRate = (int) Math.round(minInterest + random.nextDouble()
        * (maxInterest - minInterest));
    depositInterestRate = (int) (debtInterestRate * depositDebtRatio + depositDebtDiff);
    
    // TAC 04
    // interestRateDayNegative =
    // Math.pow(1.0 + debtInterestRate / 100.0, 1.0 / daysPerYear);
    // interestRateDayPositive =
    // Math.pow(1.0 + depositInterestRate / 100.0, 1.0 / daysPerYear);
    
    // TAC 05
    interestRateDayNegative = 1.0 + ((debtInterestRate / 100.0) / daysPerYear);
    interestRateDayPositive = 1.0 + ((depositInterestRate / 100.0) / daysPerYear);
    
    accountNames = new String[accountNumber];
    accountAmounts = new long[accountNumber];
    bankStatus = new BankStatus[accountNumber];
    
    // System.out.println("InterestNegative:" + interestRateDayNegative
    // + " InterestP: " + interestRateDayPositive
    // + " InterestRate: " + interestRate);
  }
  
  public int getDaysPerYear()
  {
    return daysPerYear;
  }
  
  public double getDebtInterestRate()
  {
    return debtInterestRate;
  }
  
  public double getDepositInterestRate()
  {
    return depositInterestRate;
  }
  
  public void addAccount(String name)
  {
    int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
    if (index < 0)
    {
      doAddAccount(name);
    }
  }
  
  private synchronized int doAddAccount(String name)
  {
    if (accountNumber == accountNames.length)
    {
      int newSize = accountNumber + 8;
      accountNames = (String[]) ArrayUtils.setSize(accountNames, newSize);
      accountAmounts = ArrayUtils.setSize(accountAmounts, newSize);
      bankStatus = (BankStatus[]) ArrayUtils.setSize(bankStatus, newSize);
    }
    accountNames[accountNumber] = name;
    accountAmounts[accountNumber] = 0L;
    return accountNumber++;
  }
  
  public long getAccountStatus(String name)
  {
    int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
    return index >= 0 ? accountAmounts[index] : 0L;
  }
  
  // public synchronized int transaction(String accountName, String otherPart,
  // int orderID, int amount) {
  // int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
  // if (index < 0) {
  // index = doAddAccount(name);
  // }
  // int value = (accountAmounts[index] += amount);
  // BankStatus status = bankStatus[index];
  // if (status == null) {
  // bankStatus[index] = status = new BankStatus();
  // }
  // status.addTransaction(otherPart, orderID, amount);
  // return value;
  // }
  
  public long penalty(String name, String otherPart, int orderID, int amount)
  {
    int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
    if (index < 0)
    {
      index = doAddAccount(name);
    }
    long value = (accountAmounts[index] -= amount);
    BankStatus status = bankStatus[index];
    if (status == null)
    {
      bankStatus[index] = status = new BankStatus();
    }
    status.addPenalty(otherPart, orderID, amount);
    return value;
  }
  
  public void addDeniedDelivery(String name, String denier, int orderID)
  {
    int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
    if (index < 0)
    {
      index = doAddAccount(name);
    }
    BankStatus status = bankStatus[index];
    if (status == null)
    {
      bankStatus[index] = status = new BankStatus();
    }
    status.addDeniedDelivery(denier, orderID);
  }
  
  public long deposit(String name, long amount)
  {
    int index = ArrayUtils.indexOf(accountNames, 0, accountNumber, name);
    if (index < 0)
    {
      index = doAddAccount(name);
    }
    accountAmounts[index] += amount;
    return accountAmounts[index];
  }
  
  public long withdraw(String name, long amount)
  {
    return deposit(name, -amount);
  }
  
  public void addInterests(int date)
  {
    for (int i = 0; i < accountNumber; i++)
    {
      long cash = accountAmounts[i];
      long newCash;
      if (cash < 0)
      {
        newCash = (long) Math.round(cash * interestRateDayNegative);
      }
      else
      {
        newCash = (long) Math.round(cash * interestRateDayPositive);
      }
      accountAmounts[i] = newCash;
      // System.out.println("interest for " + accountNames[i] + " oldAccount="
      // + cash + " newAccount=" + newCash
      // + " ineg=" + interestRateDayNegative
      // + " ipos=" + interestRateDayPositive);
      if (newCash != cash)
      {
        simulation.sendInterestInfo(accountNames[i], newCash - cash);
      }
    }
  }
  
  public void sendBankStatusToAll()
  {
    for (int i = 0; i < accountNumber; i++)
    {
      BankStatus status = bankStatus[i];
      if (status == null)
      {
        status = new BankStatus();
      }
      else
      {
        // Can not simply clear the bank status after sending it
        // because the message might be in a send queue or used in an
        // internal agent. Only option is to simply forget about it
        // and create a new bank status for the agent the next day.
        bankStatus[i] = null;
      }
      status.setAccountStatus(accountAmounts[i]);
      simulation.sendBankStatus(accountNames[i], status);
    }
  }
  
  // DEBUG FINALIZE REMOVE THIS!!!
  protected void finalize() throws Throwable
  {
    Logger.global.info("BANK FOR SIMULATION " + simulation.getSimulationInfo().getSimulationID()
        + " IS BEING GARBAGED");
    super.finalize();
  }
  
} // Bank
