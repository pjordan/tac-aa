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
 * Order
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Tue Jan 28 13:17:47 2003
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.sim;

import java.util.Random;

import se.sics.tasim.props.OfferBundle;
import se.sics.tasim.props.RFQBundle;

/**
 * Container for order information for internal use in AbstractCustomer.
 */
final class Order
{
  
  private String supplier;
  private int orderID;
  private int offerID;
  private int productID;
  private int quantity;
  private int unitPrice;
  private int dueDate;
  private int penalty;
  private int penaltyCounter;
  private int tieCounter = 1;
  private boolean isDelivered = false;
  
  public Order(int orderID, String supplier, RFQBundle rfq, int rfqIndex, OfferBundle offers,
      int offerIndex)
  {
    this.orderID = orderID;
    set(supplier, rfq, rfqIndex, offers, offerIndex);
  }
  
  public String getSupplier()
  {
    return supplier;
  }
  
  public int getOrderID()
  {
    return orderID;
  }
  
  public int getOfferID()
  {
    return offerID;
  }
  
  public int getProductID()
  {
    return productID;
  }
  
  public int getQuantity()
  {
    return quantity;
  }
  
  public int getUnitPrice()
  {
    return unitPrice;
  }
  
  public int getDueDate()
  {
    return dueDate;
  }
  
  public int getPenalty()
  {
    return penalty;
  }
  
  public int firePenalty()
  {
    return ++penaltyCounter;
  }
  
  public void setDelivered()
  {
    this.isDelivered = true;
  }
  
  public boolean isDelivered()
  {
    return isDelivered;
  }
  
  public boolean add(Random random, String supplier, RFQBundle rfq, int rfqIndex,
      OfferBundle offers, int offerIndex)
  {
    int newPrice = offers.getUnitPrice(offerIndex);
    if (newPrice < unitPrice)
    {
      set(supplier, rfq, rfqIndex, offers, offerIndex);
      return true;
      
    }
    else if ((newPrice == unitPrice) && changeOfferWhenTie(random))
    {
      set(supplier, rfq, rfqIndex, offers, offerIndex);
      return true;
      
    }
    else
    {
      return false;
    }
  }
  
  /**
   * Determines which bid to choose when there is a tie in regard to price. The
   * bid is chosen among all the tie bids with equal probability.
   * 
   * First tie there is 1/2 chance to keep first bid, 1/2 to take second <br>
   * Second tie there is 2/3 to keep the previous bid, 1/3 to take the new bid
   * <br>
   * Third tie there is 3/4 to keep the previous bid, 1/4 to take the new bid
   * <br>
   * etc
   * 
   * @return returns true if the new bid should be chosen and false if the old
   *         bid should remain
   */
  protected boolean changeOfferWhenTie(Random random)
  {
    tieCounter++;
    return random.nextDouble() < (1d / tieCounter);
  }
  
  private void set(String supplier, RFQBundle rfq, int rfqIndex, OfferBundle offers, int offerIndex)
  {
    this.supplier = supplier;
    this.offerID = offers.getOfferID(offerIndex);
    this.productID = rfq.getProductID(rfqIndex);
    this.quantity = offers.getQuantity(offerIndex);
    this.unitPrice = offers.getUnitPrice(offerIndex);
    this.dueDate = offers.getDueDate(offerIndex);
    this.penalty = rfq.getPenalty(rfqIndex);
  }
  
} // Order
