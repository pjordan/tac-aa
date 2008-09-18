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
 * ATPRFQ
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Feb 21 15:41:03 2005
 * Updated : $Date: 2008-03-07 09:59:58 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3824 $
 */
package se.sics.tasim.tacscm.atp;

import java.util.Arrays;
import java.util.Comparator;

/**
 */
public class ATPRFQ implements Comparator
{
  
  String manufacturer;
  double reputation;
  int id;
  int requestedQuantity;
  int originalQuantity;
  int dueDate;
  int finDate;
  double reservePrice;
  
  double reputationWeight;
  
  /** Cache when allocating capacity for earliest complete */
  int leftOverQuantity;
  
  int partialQuantity;
  int earliestComplete;
  
  int currentQuantity;
  double finalPrice;
  boolean finished;
  
  private ATPRFQ[] bundle;
  private int bundleCount;
  
  public ATPRFQ()
  {}
  
  public String getManufacturer()
  {
    return manufacturer;
  }
  
  public int getID()
  {
    return id;
  }
  
  public int getQuantity()
  {
    return currentQuantity;
  }
  
  public int getRequestedQuantity()
  {
    return originalQuantity;
  }
  
  public int getDueDate()
  {
    return dueDate;
  }
  
  public double getReservePricePerUnit()
  {
    return reservePrice;
  }
  
  // Note that this requires that the partial quantity is correctly set
  public double updateWeight(double reputationExpo)
  {
    return reputationWeight = partialQuantity / Math.pow(reputation, reputationExpo);
  }
  
  public void setRFQ(String manufacturer, double reputation, int rfqID, int quantity, int dueDate,
      double reservePricePerUnit)
  {
    this.manufacturer = manufacturer;
    this.reputation = reputation;
    this.id = rfqID;
    this.requestedQuantity = this.originalQuantity = quantity;
    this.dueDate = dueDate;
    this.finDate = dueDate - 1;
    this.reservePrice = reservePricePerUnit;
    
    this.currentQuantity = 0;
    
    this.finished = false;
    this.finalPrice = 0.0;
    this.partialQuantity = 0;
    this.earliestComplete = 0;
    
    this.reputationWeight = 0.0;
    
    clearBundle();
  }
  
  public int getComponentsBeforeWakeup(double price, int capacity, int inventory, int currentDay)
  {
    double pDiff = price - reservePrice;
    int compLeft;
    if (pDiff > 0)
    {
      compLeft = (int) Math.ceil(pDiff * 2.0 * (capacity * (finDate - currentDay)));
    }
    else
    {
      compLeft = 0;
    }
    return compLeft;
  }
  
  // -------------------------------------------------------------------
  // Bundle handling - for bundling RFQs with equal reputation, due
  // date, and reserve price
  // -------------------------------------------------------------------
  
  public boolean hasBundle()
  {
    return bundleCount > 0;
  }
  
  public int getBundleCount()
  {
    return bundleCount;
  }
  
  public ATPRFQ getBundledRFQ(int index)
  {
    if (index < 0 || index >= bundleCount)
    {
      throw new IndexOutOfBoundsException("index=" + index + ",size=" + bundleCount);
    }
    return bundle[index];
  }
  
  public void bundleRFQ(ATPRFQ rfq)
  {
    if (bundle == null)
    {
      bundle = new ATPRFQ[12];
    }
    else if (bundleCount >= bundle.length)
    {
      ATPRFQ[] tmp = new ATPRFQ[bundle.length + 10];
      System.arraycopy(bundle, 0, tmp, 0, bundleCount);
      bundle = tmp;
    }
    if (bundleCount == 0)
    {
      // The bundle always contains this RFQ
      bundle[bundleCount++] = this;
    }
    bundle[bundleCount++] = rfq;
    requestedQuantity += rfq.requestedQuantity;
  }
  
  public void processBundle()
  {
    if (bundleCount < 2)
    {
      // A bundle contains at least two RFQs
      throw new IllegalStateException("no bundle");
    }
    if (bundle[1].currentQuantity > 0)
    {
      throw new IllegalStateException("bundle already processed");
    }
    // Restore the requested quantity for this RFQ
    requestedQuantity = originalQuantity;
    
    int totalPartial = partialQuantity;
    int totalCurrent = currentQuantity;
    
    int totalDemand = 0;
    for (int i = 0; i < bundleCount; i++)
    {
      totalDemand += bundle[i].originalQuantity;
      bundle[i].earliestComplete = earliestComplete;
    }
    // System.out.println("TotalDemand: " + totalDemand + " count=" +
    // bundleCount);
    
    Arrays.sort(bundle, 0, bundleCount, OQ_COMPARATOR);
    
    int totalDist = 0;
    int totalCurDist = 0;
    for (int i = 0; i < bundleCount; i++)
    {
      ATPRFQ rfq = bundle[i];
      double weight = (((double) rfq.originalQuantity) / totalDemand);
      rfq.partialQuantity = (int) (totalPartial * weight);
      rfq.currentQuantity = (int) (totalCurrent * weight);
      totalDist += rfq.partialQuantity;
      totalCurDist += rfq.currentQuantity;
    }
    
    while ((totalPartial - totalDist) > 0 && (totalCurrent - totalCurDist) > 0)
    {
      boolean changed = false;
      for (int i = 0; i < bundleCount; i++)
      {
        ATPRFQ rfq = bundle[i];
        if ((totalPartial - totalDist) > 0 && rfq.partialQuantity < rfq.originalQuantity)
        {
          rfq.partialQuantity++;
          totalDist++;
          changed = true;
        }
        if ((totalCurrent - totalCurDist) > 0 && rfq.currentQuantity < rfq.originalQuantity)
        {
          rfq.currentQuantity++;
          totalCurDist++;
          changed = true;
        }
      }
      if (!changed)
      {
        System.err.println("ATPRFQ: ERROR failed to distribute "
            + ((totalPartial - totalDist) + (totalCurrent - totalCurDist))
            + " components in bundled (equal) RFQs");
        break;
      }
    }
  }
  
  public void clearBundle()
  {
    if (bundleCount > 0)
    {
      for (int i = 0; i < bundleCount; i++)
      {
        bundle[i] = null;
      }
      bundleCount = 0;
    }
  }
  
  // public static void main(String[] args) {
  // ATPRFQ r1 = new ATPRFQ();
  // ATPRFQ r2 = new ATPRFQ();
  // ATPRFQ r3 = new ATPRFQ();
  // ATPRFQ r4 = new ATPRFQ();
  // r1.setRFQ("", 1.0, // manufacturer, reputation
  // 1, // RFQ ID
  // 10, // quantity
  // 20, // due date
  // 1.0); // unit price
  // r2.setRFQ("", 1.0, // manufacturer, reputation
  // 2, // RFQ ID
  // 20, // quantity
  // 20, // due date
  // 1.0); // unit price
  // r3.setRFQ("", 1.0, // manufacturer, reputation
  // 3, // RFQ ID
  // 10, // quantity
  // 20, // due date
  // 1.0); // unit price
  // r4.setRFQ("", 1.0, // manufacturer, reputation
  // 4, // RFQ ID
  // 12, // quantity
  // 20, // due date
  // 1.0); // unit price
  // r1.bundleRFQ(r2);
  // r1.bundleRFQ(r3);
  // r1.bundleRFQ(r4);
  
  // r1.partialQuantity = 10;
  // r1.currentQuantity = 20;
  // r1.earliestComplete = 32;
  
  // System.out.println("PartialQ=" + r1.partialQuantity
  // + " CurrentQ=" + r1.currentQuantity
  // + " Earliest=" + r1.earliestComplete);
  
  // r1.processBundle();
  
  // for (int i = 0, n = r1.getBundleCount(); i < n; i++) {
  // System.out.println("RFQ: " + r1.getBundledRFQ(i));
  // }
  
  // r1.clearBundle();
  // }
  
  // -------------------------------------------------------------------
  // Comparator
  // -------------------------------------------------------------------
  
  public int compare(Object o1, Object o2)
  {
    ATPRFQ r1 = (ATPRFQ) o1;
    ATPRFQ r2 = (ATPRFQ) o2;
    if (r1.reputation != r2.reputation)
    {
      return (int) ((1000 * r2.reputation) - (1000 * r1.reputation));
      
    }
    else if (r1.dueDate != r2.dueDate)
    {
      return r1.dueDate - r2.dueDate;
      
    }
    else if (r1.reservePrice == r2.reservePrice)
    {
      return 0;
    }
    else if (r1.reservePrice == 0.0)
    {
      return 1;
    }
    else if (r2.reservePrice == 0.0)
    {
      return -1;
    }
    else
    {
      return (int) ((1000 * r1.reservePrice) - (1000 * r2.reservePrice));
    }
  }
  
  public boolean equals(Object o1, Object o2)
  {
    ATPRFQ r1 = (ATPRFQ) o1;
    ATPRFQ r2 = (ATPRFQ) o2;
    return r1.reputation == r2.reputation && r1.dueDate == r2.dueDate
        && r1.reservePrice == r2.reservePrice;
  }
  
  public String toHtml()
  {
    return "id=" + id + " Q=" + requestedQuantity + " P="
        + (((int) (reservePrice * 100 + 0.5)) / 100.0) + " due at day " + dueDate + "&nbsp;<br>"
        + "Reputation: " + reputation + "<br>" + "Current Quantity: " + currentQuantity + "<br>"
        + "Partial Offer: " + partialQuantity + "<br>" + "Earliest Complete: " + earliestComplete
        + "<br>";
  }
  
  public String toString()
  {
    return "ATPRFQ[" + id + ",reqQ=" + requestedQuantity + ",partQ=" + partialQuantity
        + ",quantity=" + currentQuantity + ",rPrice="
        + (((int) (reservePrice * 100 + 0.5)) / 100.0) + ",fPrice="
        + (((int) (finalPrice * 100 + 0.5)) / 100.0) + ",dueDate=" + dueDate + ",earliest="
        + earliestComplete + ",rep=" + reputation + ']';
  }
  
  // -------------------------------------------------------------------
  // Comparator
  // -------------------------------------------------------------------
  
  private static final Comparator OQ_COMPARATOR = new Comparator() {
    public int compare(Object o1, Object o2)
    {
      ATPRFQ r1 = (ATPRFQ) o1;
      ATPRFQ r2 = (ATPRFQ) o2;
      return r2.originalQuantity - r1.originalQuantity;
    }
  };
  
} // ATPRFQ
