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
 * Customer
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Oct 30 22:46:35 2002
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.sim;

import java.util.logging.Logger;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.props.ActiveOrders;
import se.sics.tasim.props.DeliveryNotice;
import se.sics.tasim.props.PriceReport;

public abstract class Customer extends Builtin
{
  
  private static final String CONF = "customer.";
  
  public Customer()
  {
    super(CONF);
  }
  
  /**
   * Request that this customer adds the active orders for the
   * specified supplier.
   *
   * @param supplier the supplier for which orders are searched
   * @param activeOrders the active order table to fill
   */
  protected abstract void addActiveOrders(String supplier, ActiveOrders activeOrders);
  
  protected void sendToManufacturers(Transportable content)
  {
    sendToRole(TACSCMSimulation.MANUFACTURER, content);
  }
  
  protected void pay(String supplier, int orderID, int quantity, int price)
  {
    getSimulation().transaction(supplier, getAddress(), orderID, quantity * (long) price);
  }
  
  protected void penalty(String supplier, int orderID, int penalty, boolean isOrderVoid)
  {
    getSimulation().penalty(getAddress(), supplier, orderID, penalty, isOrderVoid);
  }
  
  protected void addDeniedDelivery(String supplier, int orderID)
  {
    getSimulation().addDeniedDelivery(supplier, getAddress(), orderID);
  }
  
  protected void addDemandInfo(int productID, int quantityRequested, int quantityOrdered,
      int averageUnitPrice)
  {
    getSimulation().addDemandInfo(productID, quantityRequested, quantityOrdered, averageUnitPrice);
  }
  
  protected void sendPriceReport(PriceReport priceReport)
  {
    getSimulation().sendPriceReport(priceReport);
  }
  
  protected abstract void delivery(int date, DeliveryNotice notice);
  
  // -------------------------------------------------------------------
  // Product segments
  // -------------------------------------------------------------------
  
  public abstract String getProductSegmentName(int index);
  
  public abstract int[] getProductSegment(int index);
  
  public abstract int getProductSegmentCount();
  
  // DEBUG FINALIZE REMOVE THIS!!! REMOVE THIS!!!
  protected void finalize() throws Throwable
  {
    Logger.global.info("CUSTOMER " + getName() + " IS BEING GARBAGED");
    super.finalize();
  }
  
} // Customer
