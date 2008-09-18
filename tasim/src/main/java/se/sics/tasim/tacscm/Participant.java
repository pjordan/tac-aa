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
 * Participant
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Feb 27 15:42:09 2003
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm;

import java.util.Comparator;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.BankStatus;
import se.sics.tasim.props.FactoryStatus;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.props.StartInfo;

public class Participant
{
  
  private ParticipantInfo info;
  private long totalCost;
  private long totalRevenue;
  private long totalResult;
  private long totalPenalties;
  private long totalStorage;
  private long totalInterest;
  
  // private int totalProductDemand;
  // private int totalProductsOrdered;
  
  // Towards customer
  private int noOrders;
  private int noMissedDeliveries;
  private int noLateDeliveries;
  private int noAcceptedDeliveries;
  private int noRefusedDeliveries;
  
  private int[] orderList;
  private int orderNumber;
  
  private StartInfo startInfo;
  private double totalUtilization;
  private int utilizationCount;
  
  private int numberOfDays;
  
  private static Comparator resultComparator;
  
  public Participant(ParticipantInfo info)
  {
    this.info = info;
  }
  
  /**
   * Returns a comparator that puts the highest values first.
   */
  public static Comparator getResultComparator()
  {
    if (resultComparator == null)
    {
      resultComparator = new Comparator() {
        public int compare(Object o1, Object o2)
        {
          long r1 = ((Participant) o1).getResult();
          long r2 = ((Participant) o2).getResult();
          return (r1 > r2 ? -1 : (r1 == r2 ? 0 : 1));
        }
        
        public boolean equals(Object obj)
        {
          return obj == this;
        }
      };
    }
    return resultComparator;
  }
  
  public ParticipantInfo getInfo()
  {
    return info;
  }
  
  public StartInfo getStartInfo()
  {
    return startInfo;
  }
  
  // -------------------------------------------------------------------
  // These are per product
  // -------------------------------------------------------------------
  
  // public int getTotalProductDemand() {
  // return totalProductDemand;
  // }
  
  // public int getTotalProductsOrdered() {
  // return totalProductsOrdered;
  // }
  
  // -------------------------------------------------------------------
  // These are per order
  // -------------------------------------------------------------------
  
  public int getCustomerOrders()
  {
    return noOrders;
  }
  
  public int getCustomerMissedDeliveries()
  {
    return noMissedDeliveries;
  }
  
  public int getCustomerLateDeliveries()
  {
    return noLateDeliveries;
  }
  
  public int getCustomerDeliveries()
  {
    return noAcceptedDeliveries;
  }
  
  public int getCustomerRefusedDeliveries()
  {
    return noRefusedDeliveries;
  }
  
  // -------------------------------------------------------------------
  // Total
  // -------------------------------------------------------------------
  
  public long getCost()
  {
    return totalCost;
  }
  
  public void addCost(int orderID, long amount)
  {
    totalCost += amount;
  }
  
  public long getRevenue()
  {
    return totalRevenue;
  }
  
  public void addRevenue(int orderID, long amount)
  {
    totalRevenue += amount;
    
    // This order has been delivered
    int index = ArrayUtils.indexOf(orderList, 0, orderNumber, orderID);
    if (index >= 0)
    {
      orderNumber--;
      orderList[index] = orderList[orderNumber];
      orderList[orderNumber] = -1;
      noLateDeliveries++;
    }
    else
    {
      noOrders++;
    }
    noAcceptedDeliveries++;
  }
  
  public long getInterest()
  {
    return totalInterest;
  }
  
  public void addInterest(long amount)
  {
    totalInterest += amount;
  }
  
  public long getStorageCost()
  {
    return totalStorage;
  }
  
  public void addStorageCost(long amount)
  {
    totalStorage += amount;
  }
  
  public long getPenalties()
  {
    return totalPenalties;
  }
  
  public void addPenalty(int orderID, int amount, boolean isCancelled)
  {
    totalPenalties += amount;
    
    // This order has been delivered
    int index = ArrayUtils.indexOf(orderList, 0, orderNumber, orderID);
    if (index < 0)
    {
      if (orderList == null)
      {
        orderList = new int[25];
      }
      else if (orderNumber == orderList.length)
      {
        orderList = ArrayUtils.setSize(orderList, orderNumber + 25);
      }
      index = orderNumber;
      orderList[orderNumber++] = orderID;
      noOrders++;
    }
    if (isCancelled)
    {
      orderNumber--;
      orderList[index] = orderList[orderNumber];
      orderList[orderNumber] = -1;
      noMissedDeliveries++;
    }
  }
  
  public long getResult()
  {
    return totalResult;
  }
  
  public void setResult(long result)
  {
    this.totalResult = result;
  }
  
  public int getAverageUtilization()
  {
    return utilizationCount > 0 ? ((int) (totalUtilization / utilizationCount)) : 0;
  }
  
  // -------------------------------------------------------------------
  // Information through messages sent and received
  // -------------------------------------------------------------------
  
  public void messageReceived(int date, int sender, Transportable content)
  {
    if (content instanceof BankStatus)
    { 

    }
    else if (content instanceof FactoryStatus)
    {
      FactoryStatus status = (FactoryStatus) content;
      totalUtilization += status.getUtilization() * 100;
      utilizationCount++;
      
    }
    else if (content instanceof StartInfo)
    {
      this.startInfo = (StartInfo) content;
      this.numberOfDays = this.startInfo.getNumberOfDays();
    }
  }
  
  public void messageSent(int date, int receiver, Transportable content)
  {}
  
  public void messageSentToRole(int date, int role, Transportable content)
  {}
  
  // -------------------------------------------------------------------
  // For debug output
  // -------------------------------------------------------------------
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer().append("Participant[").append(totalCost).append(',')
        .append(totalPenalties).append(',').append(totalRevenue).append(',').append(totalResult);
    return sb.append(']').toString();
  }
  
} // Participant
