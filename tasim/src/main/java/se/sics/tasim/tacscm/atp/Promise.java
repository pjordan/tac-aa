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
 * Promise
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Feb 24 15:56:13 2005
 * Updated : $Date: 2008-03-07 09:59:58 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3824 $
 */
package se.sics.tasim.tacscm.atp;

/**
 */
public class Promise
{
  
  public static final int PARTIAL = 1 << 0;
  public static final int EARLIEST_COMPLETE = 1 << 1;
  public static final int FULL = 1 << 2;
  
  private static final int ORDERED = 1 << 3;
  
  private int id = -1;
  private int rfqID = -1;
  private String manufacturer;
  
  private int reducedQuantity;
  private int quantity;
  private int unitPrice;
  private int dueDate;
  
  private long downpayment;
  
  private int mode = 0;
  
  private Promise otherPromise;
  
  public Promise()
  {}
  
  public void set(ATPRFQ rfq, double basePrice, int mode)
  {
    this.mode = mode;
    if ((mode & PARTIAL) != 0)
    {
      set(rfq.manufacturer, rfq.id, rfq.currentQuantity, rfq.partialQuantity, (int) (basePrice
          * rfq.finalPrice + 0.5), rfq.dueDate);
      // if (rfq.earliestComplete == rfq.dueDate) {
      // System.out.println("DueDate and EarliestComplete are the same: " +
      // rfqID);
      // }
    }
    else
    {
      set(rfq.manufacturer, rfq.id, rfq.currentQuantity, rfq.currentQuantity, (int) (basePrice
          * rfq.finalPrice + 0.5), (rfq.earliestComplete > 0 ? rfq.earliestComplete : rfq.dueDate));
    }
  }
  
  private void set(String manufacturer, int rfqID, int reducedQuantity, int quantity,
      int unitPrice, int dueDate)
  {
    this.manufacturer = manufacturer;
    this.rfqID = rfqID;
    this.reducedQuantity = reducedQuantity;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.dueDate = dueDate;
    this.downpayment = 0L;
  }
  
  public int getID()
  {
    return id;
  }
  
  public void setID(int id)
  {
    this.id = id;
  }
  
  public boolean isFor(String agent)
  {
    return this.manufacturer.equals(agent);
  }
  
  public String getCustomer()
  {
    return manufacturer;
  }
  
  public int getRFQID()
  {
    return rfqID;
  }
  
  public int getReducedQuantity()
  {
    return reducedQuantity;
  }
  
  public int getQuantity()
  {
    return quantity;
  }
  
  public int getUnitPrice()
  {
    return unitPrice;
  }
  
  public long getDownpayment()
  {
    return downpayment;
  }
  
  public void setDownpayment(long downpayment)
  {
    this.downpayment = downpayment;
  }
  
  public boolean isOnDueDate()
  {
    return (mode & EARLIEST_COMPLETE) == 0;
  }
  
  public int getDueDate()
  {
    return dueDate;
  }
  
  public Promise getOtherPromise()
  {
    return otherPromise;
  }
  
  public void setOtherPromise(Promise otherPromise)
  {
    this.otherPromise = otherPromise;
    if (otherPromise != null)
    {
      // Set the other promise to refer to this because they should
      // always refer to each other
      otherPromise.otherPromise = this;
    }
  }
  
  public boolean isPartial()
  {
    return (mode & PARTIAL) != 0;
  }
  
  public boolean isOrdered()
  {
    return (mode & ORDERED) != 0;
  }
  
  public void setOrdered()
  {
    mode |= ORDERED;
  }
  
  public String toString()
  {
    return "Promise[" + manufacturer + ',' + id + ',' + rfqID + ',' + quantity + ',' + unitPrice
        + ',' + dueDate + ',' + mode + ']';
  }
  
  // -------------------------------------------------------------------
  // Utility methods
  // -------------------------------------------------------------------
  
  public static int getIndexOfOffer(Promise[] array, int start, int end, int offerID)
  {
    for (int i = start; i < end; i++)
    {
      if (array[i].id == offerID && (array[i].mode & ORDERED) == 0)
      {
        return i;
      }
    }
    return -1;
  }
  
} // Promise
