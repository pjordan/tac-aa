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
 * ProductionLine
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Fri Feb 25 09:09:11 2005
 * Updated : $Date: 2008-03-07 10:01:29 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3825 $
 */
package se.sics.tasim.tacscm.agents;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Logger;

import se.sics.tasim.props.ActiveOrders;
import se.sics.tasim.props.DeliveryNotice;
import se.sics.tasim.props.OfferBundle;
import se.sics.tasim.props.OrderBundle;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.tacscm.atp.SupplierATP;
import se.sics.tasim.tacscm.atp.Promise;

/**
 */
public class ProductionLine
{
  
  private final DefaultSupplier supplier;
  
  // The product that is produced
  private final int productID;
  private String productName;
  
  private final int maxRFQsPerLine;
  
  // FOR ATP HANDLING
  private final SupplierATP supplierATP;
  
  private int productBasePrice;
  
  private int numberOfDays;
  private int[] capacityExpected;
  private int[] capacityCommitted;
  private int freeInventory;
  
  private Promise[] promiseList = new Promise[30 * 2];
  private int promiseCount = 0;
  
  private ArrayList orderList = new ArrayList(50);
  
  private int lastOrderedQuantity = 0;
  private long lastOrderedValue = 0L;
  
  private int currentDay;
  private int nominalCapacity;
  private int minimalCapacity = 1;
  
  private int currentCapacity;
  
  // All demand in the future
  private int futureDemand;
  
  /**
   * The number of units in the inventory. All deliveries are taken from the
   * inventory.
   */
  private int inventory;
  
  private Logger log;
  
  public ProductionLine(DefaultSupplier supplier, SupplierATP supplierATP, int numberOfDays,
      int startCapacity, int nominalCapacity, int productID, int maxRFQsPerLine)
  {
    this.supplier = supplier;
    this.supplierATP = supplierATP;
    
    this.numberOfDays = numberOfDays;
    this.currentCapacity = startCapacity;
    this.nominalCapacity = nominalCapacity;
    this.productID = productID;
    this.productName = "component-" + productID;
    this.maxRFQsPerLine = maxRFQsPerLine;
    this.capacityExpected = new int[numberOfDays];
    this.capacityCommitted = new int[numberOfDays];
    
    // Add the supplier name to the logger name for convenient
    // logging. Note: this is usually a bad idea because the logger
    // objects will never be garbaged but since the supplier names
    // always are the same in TAC SCM games, only a few logger objects
    // will be created.
    this.log = Logger.getLogger(ProductionLine.class.getName() + '.' + supplier.getName() + '#'
        + productID);
  }
  
  public int getProductID()
  {
    return productID;
  }
  
  public String getProductName()
  {
    return productName;
  }
  
  public void setProductName(String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.productName = name;
  }
  
  public void setProductBasePrice(int basePrice)
  {
    this.productBasePrice = basePrice;
    this.supplierATP.setBasePrice(basePrice);
  }
  
  public void setDay(int day)
  {
    currentDay = day;
  }
  
  public int getInventory()
  {
    return inventory;
  }
  
  public int getCurrentCapacity()
  {
    return currentCapacity;
  }
  
  public void simulationStopped(Hashtable deliveries)
  {
    int orderCount = orderList.size();
    if (inventory > 0 || orderCount > 0)
    {
      log.warning("inventory=" + inventory + " future demand=" + futureDemand + " orders="
          + orderCount);
    }
    
    if (orderCount > 0)
    {
      
      // Deliver and request payment for all remaining orders (the
      // manufacturer agents must pay for what they have ordered even
      // if they are delivered to late!).
      for (int i = 0; i < orderCount && inventory > 0; i++)
      {
        Promise p = (Promise) orderList.get(i);
        String agent = p.getCustomer();
        int orderID = p.getID();
        int quantity = p.getQuantity();
        if (quantity > inventory)
        {
          quantity = inventory;
        }
        
        long fullPrice = ((long) p.getUnitPrice()) * quantity - p.getDownpayment();
        if (fullPrice < 0)
        {
          fullPrice = 0;
        }
        // get a delivery notice to add this delivery to
        DeliveryNotice delivery = (DeliveryNotice) deliveries.get(agent);
        if (delivery == null)
        {
          delivery = new DeliveryNotice(supplier.getAddress(), agent);
          deliveries.put(agent, delivery);
        }
        
        inventory -= quantity;
        
        delivery.addDelivery(orderID, productID, quantity);
        supplier.requestPayment(agent, orderID, fullPrice);
        
        supplier.sendWarningEvent("late delivery of " + quantity + ' ' + productName + " to "
            + supplier.getAgentName(agent) + " for $" + p.getUnitPrice() + "/unit");
        // + FormatUtils.formatAmount(fullPrice));
      }
      orderList.clear();
    }
    
    if (inventory > 0)
    {
      supplier.sendWarningEvent("" + inventory + ' ' + productName + " left in inventory");
    }
    inventory = 0;
    freeInventory = 0;
  }
  
  // -------------------------------------------------------------------
  // Some support functions for ATP handling
  // -------------------------------------------------------------------
  
  public void clearPromises()
  {
    supplierATP.clear();
    
    // Let the promises be garbaged
    for (int i = 0; i < promiseCount; i++)
    {
      promiseList[i] = null;
    }
    promiseCount = 0;
  }
  
  public void updateReputation()
  {
    for (int i = 0; i < promiseCount; i++)
    {
      Promise p = promiseList[i];
      ReputationManager rep = supplier.getReputationManager(p.getCustomer());
      rep.updateReputation(p);
    }
  }
  
  // -------------------------------------------------------------------
  // Delivery handling
  // -------------------------------------------------------------------
  
  public void addDeliveries(Hashtable deliveries)
  {
    int failedQuantity = 0;
    int totalQuantityDelivered = 0;
    int endDate = currentDay;
    
    for (int i = 0, n = orderList.size(); i < n; i++)
    {
      Promise p = (Promise) orderList.get(i);
      int dueDate = p.getDueDate();
      if (dueDate > currentDay)
        break;
      
      int quantity = p.getQuantity();
      if (inventory >= quantity && dueDate <= endDate)
      {
        // Update inventory and deliver!
        String agent = p.getCustomer();
        long fullPrice = ((long) p.getUnitPrice()) * quantity - p.getDownpayment();
        if (fullPrice < 0)
        {
          fullPrice = 0L;
        }
        // get a delivery notice to add this delivery to
        DeliveryNotice delivery = (DeliveryNotice) deliveries.get(agent);
        if (delivery == null)
        {
          delivery = new DeliveryNotice(supplier.getAddress(), agent);
          deliveries.put(agent, delivery);
        }
        
        // Free inventory is handled by produce() and handleOrder !!!
        inventory -= quantity;
        
        delivery.addDelivery(p.getID(), productID, quantity);
        supplier.requestPayment(agent, p.getID(), fullPrice);
        orderList.remove(i);
        i--;
        n--;
        
        totalQuantityDelivered += quantity;
        
        supplier.sendEvent("delivered " + quantity + ' ' + productName + " to "
            + supplier.getAgentName(agent) + " for $" + p.getUnitPrice() + "/unit");
        // + FormatUtils.formatAmount(fullPrice));
        
      }
      else
      {
        // Move this order-demand to next day!!! FIX THIS!!!
        failedQuantity += quantity;
        
        // Do not deliver any orders with later due date than this
        if (dueDate < endDate)
        {
          endDate = dueDate;
        }
      }
    }
    
    if (totalQuantityDelivered > 0)
    {
      supplier.addSupplyDelivered(productID, totalQuantityDelivered);
    }
    
    if (failedQuantity > 0)
    {
      String msg = "suspended delivery of " + failedQuantity + ' ' + productName;
      supplier.sendWarningEvent(msg);
      log.info(msg);
    }
  }
  
  // -------------------------------------------------------------------
  // RFQ handling
  // -------------------------------------------------------------------
  
  public void addRFQs(String agent, double reputation, RFQBundle rfqBundle)
  {
    for (int i = 0, count = 0, m = rfqBundle.size(); i < m && count < maxRFQsPerLine; i++)
    {
      if (rfqBundle.getProductID(i) == productID)
      {
        int quantity = rfqBundle.getQuantity(i);
        int dueDate = rfqBundle.getDueDate(i);
        
        if (rfqBundle.getPenalty(i) > 0)
        {
          // Do not send offers for any RFQs requiring a penalty
          // (suppliers will not pay penalties in TAC SCM)
          
        }
        else if (quantity < 0)
        {
          // Suppliers will not buy components in TAC SCM
          
        }
        else if (dueDate < currentDay + 1)
        {
          // Ignore RFQs with due date too soon to be
          // deliverable. Since the RFQs was received by the supplier
          // yesterday (all rfqs are collected before processing")
          // only those with a due date earlier than tomorrow are
          // ignored. It may be possible to deliver an order
          // currentDay + 1 with enough free components in inventory.
          
        }
        else if (dueDate >= numberOfDays)
        {
          // Ignore RFQs with a due date beyond the simulation end
          
        }
        else
        {
          int rfqID = rfqBundle.getRFQID(i);
          log.finest("processing RFQ " + rfqID + " from " + agent + " for " + quantity + " due at "
              + dueDate + " day=" + currentDay);
          supplierATP.addRFQ(agent, reputation, rfqID, quantity, dueDate, rfqBundle
              .getReservePricePerUnit(i));
          count++;
        }
      }
    }
  }
  
  // Process any RFQs and add offers to the offer set
  public void processRFQs(Hashtable offerSet, int day)
  {
    int totalQuantity = 0;
    long totalPrice = 0;
    int priceWarningCount = 0;
    
    updateExpectedCapacity();
    updateCommittedCapacity();
    
    log.finer("Processing " + supplierATP.getRFQCount() + " RFQs for day " + currentDay);
    Promise[] promises = supplierATP.processRFQs(day, currentCapacity, freeInventory,
        capacityExpected, capacityCommitted);
    if (promises != null)
    {
      if (promiseList.length < promiseCount + promises.length)
      {
        Promise[] tmp = new Promise[promiseList.length + promises.length];
        System.arraycopy(promiseList, 0, tmp, 0, promiseCount);
        promiseList = tmp;
      }
      
      for (int i = 0, n = promises.length; i < n; i++)
      {
        Promise p = promises[i];
        String agent = p.getCustomer();
        
        // get the offer bundle to add this offer to!
        OfferBundle offerBundle = (OfferBundle) offerSet.get(agent);
        if (offerBundle == null)
        {
          offerBundle = new OfferBundle(currentDay);
          offerSet.put(agent, offerBundle);
        }
        
        int quantity = p.getQuantity();
        int componentUnitPrice = p.getUnitPrice();
        if (p.getOtherPromise() == null || (quantity > p.getOtherPromise().getQuantity()))
        {
          // Both partial and earliest complete offer will be sent and
          // the offered quantity is the maximal of these two (the
          // earliest complete offer).
          totalQuantity += quantity;
          totalPrice += componentUnitPrice * quantity;
        }
        p.setID(supplier.createOfferID());
        
        promiseList[promiseCount++] = p;
        
        offerBundle.addOffer(p.getID(), p.getRFQID(), componentUnitPrice, p.getDueDate(), quantity);
      }
    }
    
    if (totalQuantity > 0)
    {
      // Should perhaps break this up per agent and send one event per
      // agent receiving an offer. FIX THIS!!!
      supplier.sendEvent("offered " + totalQuantity + ' ' + productName + " for $"
          + (totalPrice / totalQuantity) + "/unit");
      // + FormatUtils.formatAmount(totalPrice));
    }
  }
  
  protected void addActiveOrders(String customer, ActiveOrders activeOrders)
  {
    if (orderList.size() > 0)
    {
      String supplierAddress = supplier.getAddress();
      for (int i = 0, n = orderList.size(); i < n; i++)
      {
        Promise p = (Promise) orderList.get(i);
        if (customer.equals(p.getCustomer()))
        {
          activeOrders.addSupplierOrder(supplierAddress, p.getID(), productID, p.getQuantity(), p
              .getUnitPrice(), p.getDueDate(), 0); // Suppliers have no penalty
        }
      }
    }
  }
  
  // -------------------------------------------------------------------
  // Order handling
  // -------------------------------------------------------------------
  
  public boolean handleOrder(String agent, OrderBundle orders, int oIndex)
  {
    int offerID = orders.getOfferID(oIndex);
    int index = Promise.getIndexOfOffer(promiseList, 0, promiseCount, offerID);
    if (index >= 0)
    {
      Promise promise = promiseList[index];
      if (promise.isFor(agent))
      {
        Promise otherPromise = promise.getOtherPromise();
        if (otherPromise != null && otherPromise.isOrdered())
        {
          // The other offer has already been accepted
          log.info("other offer for RFQ " + promise.getRFQID() + " already accepted (order "
              + orders.getOrderID(oIndex) + ')');
          
        }
        else
        {
          promise.setID(orders.getOrderID(oIndex));
          promise.setOrdered();
          promise.setOtherPromise(null);
          
          // If quantity is zero it is only a price quote and there is
          // no need to actually place the order
          int quantity = promise.getQuantity();
          if (quantity > 0)
          {
            int unitPrice = promise.getUnitPrice();
            addOrder(promise);
            
            futureDemand += quantity;
            lastOrderedQuantity += quantity;
            lastOrderedValue += ((long) unitPrice) * quantity;
            
            supplier.addSupplyOrdered(productID, quantity, unitPrice);
            supplier.requestDownpayment(agent, promise);
          }
        }
      }
      else
      {
        // Wrong agent ordered this offer
        log.warning("manufacturer " + agent + " placed an order for an offer sent to "
            + promise.getCustomer());
      }
      return true;
    }
    else
    {
      // Perhaps the order was for another assembly line
      return false;
    }
  }
  
  public void finishOrders(String agent)
  {
    if (lastOrderedQuantity > 0)
    {
      supplier.sendEvent("" + lastOrderedQuantity + ' ' + productName + " ordered for $"
          + (lastOrderedValue / lastOrderedQuantity) + "/unit"
          // + FormatUtils.formatAmount(lastOrderedValue)
          + " by " + supplier.getAgentName(agent));
      lastOrderedQuantity = 0;
      lastOrderedValue = 0L;
    }
  }
  
  private void addOrder(Promise promise)
  {
    int dueDate = promise.getDueDate();
    for (int i = 0, n = orderList.size(); i < n; i++)
    {
      Promise o = (Promise) orderList.get(i);
      if (o.getDueDate() > dueDate)
      {
        orderList.add(i, promise);
        return;
      }
    }
    orderList.add(promise);
  }
  
  // -------------------------------------------------------------------
  // Production
  // -------------------------------------------------------------------
  
  public int produce()
  {
    // Update capacity for the new day
    updateCapacity();
    
    int todaysProduction = futureDemand > currentCapacity ? currentCapacity : futureDemand;
    log.finest("Current capacity=" + currentCapacity + " production=" + todaysProduction
        + " inventory=" + inventory + " futureDemand=" + futureDemand);
    
    futureDemand -= todaysProduction;
    inventory += todaysProduction;
    return todaysProduction;
  }
  
  // -------------------------------------------------------------------
  // Capacity and inventory handling
  // -------------------------------------------------------------------
  
  // The random walk...
  protected void updateCapacity()
  {
    Random random = supplier.getDailyCapacityRandom(this);
    // These values should be in the configuration file. FIX THIS!!!
    currentCapacity = (int) (currentCapacity + nominalCapacity
        * (0.1 * (random.nextDouble() - 0.5)) + 0.01 * (nominalCapacity - currentCapacity));
    
    if (minimalCapacity > currentCapacity)
    {
      currentCapacity = minimalCapacity;
    }
  }
  
  protected void updateExpectedCapacity()
  {
    double expected = currentCapacity;
    for (int i = currentDay; i < numberOfDays; i++)
    {
      capacityExpected[i] = (int) expected;
      expected = 0.99 * expected + 0.01 * nominalCapacity;
    }
  }
  
  // This method assumes the expected capacity have been updated!
  protected void updateCommittedCapacity()
  {
    int orderCount = orderList.size() - 1;
    Promise nextOrder = orderCount >= 0 ? (Promise) orderList.get(orderCount--) : null;
    int nextDueDate = nextOrder != null ? nextOrder.getDueDate() : Integer.MIN_VALUE;
    int currentNeed = 0;
    
    for (int i = numberOfDays - 1; i >= currentDay; i--)
    {
      while (i == nextDueDate)
      {
        currentNeed += nextOrder.getQuantity();
        if (orderCount >= 0)
        {
          nextOrder = (Promise) orderList.get(orderCount--);
          nextDueDate = nextOrder.getDueDate();
        }
        else
        {
          nextDueDate = Integer.MIN_VALUE;
        }
      }
      int expected = capacityExpected[i];
      if (currentNeed > expected)
      {
        capacityCommitted[i] = expected;
        currentNeed -= expected;
      }
      else
      {
        capacityCommitted[i] = currentNeed;
        currentNeed = 0;
      }
    }
    
    while (orderCount > 0)
    {
      currentNeed += ((Promise) orderList.get(orderCount--)).getQuantity();
    }
    
    if (currentNeed > 0)
    {
      if (inventory > currentNeed)
      {
        freeInventory = inventory - currentNeed;
        currentNeed = 0;
      }
      else
      {
        currentNeed -= inventory;
        freeInventory = 0;
        
        for (int i = currentDay, n = numberOfDays; i < n; i++)
        {
          int free = capacityExpected[i] - capacityCommitted[i];
          if (free > 0)
          {
            if (currentNeed > free)
            {
              capacityCommitted[i] += free;
              currentNeed -= free;
            }
            else
            {
              capacityCommitted[i] += currentNeed;
              currentNeed = 0;
              break;
            }
          }
        }
      }
    }
  }
  
} // ProductionLine
