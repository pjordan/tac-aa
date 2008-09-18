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
 * AbstractCustomer
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Oct 30 22:46:35 2002
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.sim;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Logger;

import com.botbox.util.ArrayQueue;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.FormatUtils;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.props.ActiveOrders;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.props.DeliveryNotice;
import se.sics.tasim.props.OfferBundle;
import se.sics.tasim.props.OrderBundle;
import se.sics.tasim.props.PriceReport;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.tacscm.TACSCMConstants;

public abstract class AbstractCustomer extends Customer implements TACSCMConstants, TimeListener
{
  
  private final static Logger log = Logger.getLogger(AbstractCustomer.class.getName());
  
  private BOMBundle bomBundle;
  private ComponentCatalog componentCatalog;
  
  /** The number of days with missed deliveries before an order is void */
  private int daysBeforeVoid = 5;
  
  /** The current date */
  private int currentDate;
  
  private RFQBundle bundleOfTheDay;
  private Order[] currentOrders = null;
  private Hashtable orderTable = new Hashtable();
  
  private ArrayQueue orderQueue = new ArrayQueue();
  
  private ArrayList pendingOrders = new ArrayList();
  private ArrayList deliveryList = null;
  
  private boolean isInitialized = false;
  
  // -------------------------------------------------------------------
  // Setup handling
  // -------------------------------------------------------------------
  
  private void initialize()
  {
    if (!isInitialized && bomBundle != null && componentCatalog != null)
    {
      isInitialized = true;
    }
  }
  
  protected final void setup()
  {
    log.fine(getName() + " setup");
    customerSetup();
    addTimeListener(this);
  }
  
  protected final void stopped()
  {
    log.fine(getName() + " is stopping");
    removeTimeListener(this);
    
    // Request penalties for any remaining undelivered order (because
    // remaining orders can never be delivered).
    if (orderQueue.size() > 0)
    {
      int totalPenaltyQuantity = 0;
      long totalPenalty = 0L;
      for (int i = 0, n = orderQueue.size(); i < n; i++)
      {
        Order order = (Order) orderQueue.get(i);
        if (order.isDelivered())
        {
          // Order has already been delivered and simply needs to paid for
          pay(order.getSupplier(), order.getOrderID(), order.getQuantity(), order.getUnitPrice());
          
        }
        else
        {
          // The order can never be delivered and penalty must be charged!
          int dueDate = order.getDueDate();
          int penaltyDays = daysBeforeVoid;
          if (dueDate < currentDate)
          {
            penaltyDays = daysBeforeVoid - (currentDate - dueDate);
            if (penaltyDays < 0)
            {
              penaltyDays = 0;
            }
          }
          if (penaltyDays > 0)
          {
            String supplier = order.getSupplier();
            int orderID = order.getOrderID();
            int penalty = order.getPenalty() * penaltyDays;
            log.finest("final penalty for agent " + supplier + " order " + orderID + " due "
                + order.getDueDate() + " $" + penalty);
            penalty(supplier, orderID, penalty, true);
            totalPenaltyQuantity++;
            totalPenalty += penalty;
          }
        }
      }
      orderQueue.clear();
      if (totalPenaltyQuantity > 0)
      {
        sendWarningEvent("claimed $" + FormatUtils.formatAmount(totalPenalty) + " for "
            + totalPenaltyQuantity + " missed deliver" + (totalPenaltyQuantity > 1 ? "ies" : "y"));
      }
    }
    customerStopped();
  }
  
  protected final void shutdown()
  {
    log.fine(getName() + " shutting down");
    removeTimeListener(this);
    customerShutdown();
  }
  
  /**
   * Request that this customer adds the active orders for the specified
   * supplier.
   * 
   * @param supplier
   *          the supplier for which orders are searched
   * @param activeOrders
   *          the active order table to fill
   */
  protected void addActiveOrders(String agent, ActiveOrders activeOrders)
  {
    String address = getAddress();
    for (int i = 0, n = orderQueue.size(); i < n; i++)
    {
      Order order = (Order) orderQueue.get(i);
      if (!order.isDelivered() && agent.equals(order.getSupplier()))
      {
        activeOrders.addCustomerOrder(address, order.getOrderID(), order.getProductID(), order
            .getQuantity(), order.getUnitPrice(), order.getDueDate(), order.getPenalty());
      }
    }
    for (int i = 0, n = pendingOrders.size(); i < n; i++)
    {
      Order order = (Order) pendingOrders.get(i);
      if (!order.isDelivered() && agent.equals(order.getSupplier()))
      {
        activeOrders.addCustomerOrder(address, order.getOrderID(), order.getProductID(), order
            .getQuantity(), order.getUnitPrice(), order.getDueDate(), order.getPenalty());
      }
    }
  }
  
  // -------------------------------------------------------------------
  // Information retrieval
  // -------------------------------------------------------------------
  
  protected int getNextRFQID()
  {
    return getNextID();
  }
  
  protected int getNextOrderID()
  {
    return getNextID();
  }
  
  protected BOMBundle getBOMBundle()
  {
    return bomBundle;
  }
  
  protected ComponentCatalog getComponentCatalog()
  {
    return componentCatalog;
  }
  
  public void nextTimeUnit(int date)
  {
    currentDate = date;
    
    if (!isInitialized)
    {
      // Nothing to do until the customer been initialized
      
    }
    else if (date > 0)
    {
      // Do not send any RFQs on first day because it is reserved for
      // setup purposes (agent planning).
      
      if (pendingOrders.size() > 0)
      {
        for (int i = 0, n = pendingOrders.size(); i < n; i++)
        {
          addOrder((Order) pendingOrders.get(i));
        }
        pendingOrders.clear();
      }
      
      checkPenaltiesAndPayment(date);
      acceptOffersAndSendReports(date);
      
      RFQBundle bundle = generateRFQs(date);
      bundle.lock();
      
      // Make sure the order list for this day is large enough
      if (currentOrders == null || currentOrders.length < bundle.size())
      {
        currentOrders = new Order[bundle.size()];
      }
      this.bundleOfTheDay = bundle;
      
      // Always send the RFQ bundle even if it is empty (some
      // manufacturers might expect this)
      sendToManufacturers(bundle);
      
      int totalQuantity = bundle.getTotalQuantity();
      sendEvent("requested " + totalQuantity + " PC" + (totalQuantity > 1 ? "s" : ""));
      
      // Handle any pending deliveries
      if (deliveryList != null)
      {
        for (int i = 0, n = deliveryList.size(); i < n; i++)
        {
          DeliveryNotice notice = (DeliveryNotice) deliveryList.get(i);
          handleDelivery(notice);
        }
        deliveryList.clear();
      }
    }
  }
  
  private void checkPenaltiesAndPayment(int date)
  {
    // int totalPaidQuantity = 0;
    // int totalPaid = 0;
    int totalPenaltyQuantity = 0;
    long totalPenalty = 0L;
    int totalOrdersCancelled = 0;
    long totalValueCancelled = 0L;
    
    for (int i = 0, n = orderQueue.size(); i < n; i++)
    {
      Order order = (Order) orderQueue.get(i);
      int dueDate = order.getDueDate();
      if (dueDate > date)
      {
        // No more orders to handle today
        break;
      }
      
      if (order.isDelivered())
      {
        // The order has been delivered and the order due date is today.
        // Time to pay and forget about the order.
        pay(order.getSupplier(), order.getOrderID(), order.getQuantity(), order.getUnitPrice());
        orderQueue.remove(i);
        i--;
        n--;
        
      }
      else if (dueDate < date)
      {
        // Not delivered yet but should have been delivered at least
        // yesterday. This means a penalty must be charged.
        boolean voidingOrder = false;
        String supplier = order.getSupplier();
        int orderID = order.getOrderID();
        int penalty = order.getPenalty();
        log.finest("penalty for agent " + supplier + " order " + orderID + " due " + dueDate + " $"
            + penalty);
        if (order.firePenalty() >= daysBeforeVoid)
        {
          log.finest("cancelling order " + orderID);
          // Order is voided
          orderQueue.remove(i);
          i--;
          n--;
          voidingOrder = true;
          totalOrdersCancelled++;
          totalValueCancelled += order.getQuantity() * order.getUnitPrice();
        }
        penalty(supplier, orderID, penalty, voidingOrder);
        
        totalPenaltyQuantity++;
        totalPenalty += penalty;
      }
    }
    
    if (totalPenaltyQuantity > 0)
    {
      sendWarningEvent("claimed $" + FormatUtils.formatAmount(totalPenalty) + " for "
          + totalPenaltyQuantity + " late deliver" + (totalPenaltyQuantity > 1 ? "ies" : "y"));
      if (totalOrdersCancelled > 0)
      {
        sendWarningEvent("cancelled " + totalOrdersCancelled + " order"
            + (totalOrdersCancelled > 1 ? "s" : "") + " for $"
            + FormatUtils.formatAmount(totalValueCancelled));
      }
    }
  }
  
  private void acceptOffersAndSendReports(int date)
  {
    PriceReport priceReport = new PriceReport(date);
    RFQBundle rfqs = this.bundleOfTheDay;
    if (rfqs != null)
    {
      this.bundleOfTheDay = null;
      
      int totalQuantity = 0;
      long totalPrice = 0;
      for (int i = 0, n = rfqs.size(); i < n; i++)
      {
        Order order = currentOrders[i];
        int requestedQuantity = rfqs.getQuantity(i);
        if (order != null)
        {
          OrderBundle bundle = (OrderBundle) orderTable.get(order.getSupplier());
          if (bundle == null)
          {
            orderTable.put(order.getSupplier(), bundle = new OrderBundle());
          }
          bundle.addOrder(order.getOrderID(), order.getOfferID());
          currentOrders[i] = null;
          
          // Add order to pending order list because deliveries should
          // not be accepted until next day
          pendingOrders.add(order);
          
          int productID = order.getProductID();
          int quantity = order.getQuantity();
          int productUnitPrice = order.getUnitPrice();
          totalQuantity += quantity;
          totalPrice += quantity * productUnitPrice;
          
          // Add the product price
          priceReport.setPriceForProduct(productID, productUnitPrice);
          
          // Add this order to the market report
          addDemandInfo(productID, requestedQuantity, quantity, productUnitPrice);
        }
        else
        {
          addDemandInfo(rfqs.getProductID(i), requestedQuantity, 0, 0);
        }
      }
      
      if (orderTable.size() > 0)
      {
        sendMessages(orderTable);
        orderTable.clear();
      }
      
      if (totalQuantity > 0)
      {
        sendEvent("ordered " + totalQuantity + " PC" + (totalQuantity > 1 ? "s" : "") + " for $"
            + (totalPrice / totalQuantity) + "/unit");
        // + FormatUtils.formatAmount(totalPrice));
      }
      
      // Customer demand information
      EventWriter eventWriter = getEventWriter();
      int customerIndex = getIndex();
      eventWriter.dataUpdated(customerIndex, DU_CUSTOMER_DEMAND, rfqs.getTotalQuantity());
      eventWriter.dataUpdated(customerIndex, DU_CUSTOMER_ORDERED, totalQuantity);
    }
    sendPriceReport(priceReport);
  }
  
  private void addOrder(Order order)
  {
    int dueDate = order.getDueDate();
    int index = orderQueue.size();
    while (index > 0)
    {
      Order o = (Order) orderQueue.get(index - 1);
      if (o.getDueDate() <= dueDate)
      {
        // Must insert the element here
        break;
      }
      index--;
    }
    orderQueue.add(index, order);
    
    // for (int i = 0, n = orderQueue.size(); i < n; i++) {
    // Order o = (Order) orderQueue.get(i);
    // if (o.getDueDate() > dueDate) {
    // orderQueue.add(i, order);
    // return;
    // }
    // }
    // orderQueue.add(order);
  }
  
  // -------------------------------------------------------------------
  // Message handling
  // -------------------------------------------------------------------
  
  protected void messageReceived(Message message)
  {
    Transportable content = message.getContent();
    Class type = content.getClass();
    if (type == OfferBundle.class)
    {
      handleOffers(message.getSender(), (OfferBundle) content);
      
    }
    else if (COORDINATOR.equals(message.getSender()))
    {
      // Handle administration messages. For security reasons these
      // messages must not be accepted unless the coordinator is the
      // sender.
      if (type == StartInfo.class)
      {
        StartInfo info = (StartInfo) content;
        this.daysBeforeVoid = info
            .getAttributeAsInt("customer.daysBeforeVoid", this.daysBeforeVoid);
      }
      else if (type == BOMBundle.class)
      {
        this.bomBundle = (BOMBundle) content;
        initialize();
        
      }
      else if (type == ComponentCatalog.class)
      {
        this.componentCatalog = (ComponentCatalog) content;
        initialize();
      }
    }
  }
  
  private void handleOffers(String sender, OfferBundle offers)
  {
    RFQBundle rfq = this.bundleOfTheDay;
    if (rfq != null)
    {
      Random random = getTieBreakerRandom();
      for (int i = 0, n = offers.size(); i < n; i++)
      {
        int rfqID = offers.getRFQID(i);
        int index = rfq.getIndexFor(rfqID);
        if (index >= 0 && validateOffer(currentDate, rfq, index, offers, i))
        {
          if (currentOrders[index] == null)
          {
            currentOrders[index] = new Order(getNextOrderID(), sender, rfq, index, offers, i);
          }
          else
          {
            // The order will keep only the lowest bid. In case of
            // tie it will choose one of the orders in such way that
            // each order with the same price will have the same
            // probability of being chosen.
            currentOrders[index].add(random, sender, rfq, index, offers, i);
          }
        }
      }
    }
  }
  
  private boolean validateOffer(int currentDate, RFQBundle rfq, int rfqIndex, OfferBundle offers,
      int offerIndex)
  {
    // Check reservation price
    int reservePricePerUnit = rfq.getReservePricePerUnit(rfqIndex);
    int offeredUnitPrice = offers.getUnitPrice(offerIndex);
    if ((reservePricePerUnit > 0) && (offeredUnitPrice > reservePricePerUnit))
    {
      return false;
    }
    
    // Do not take offers with negative unit price seriously...
    if (offeredUnitPrice < 0)
    {
      return false;
    }
    
    // Check due date. Only accept the requested due date.
    int dueDate = offers.getDueDate(offerIndex);
    if (dueDate != rfq.getDueDate(rfqIndex))
    {
      return false;
    }
    // Check quantity
    if (offers.getQuantity(offerIndex) != rfq.getQuantity(rfqIndex))
    {
      return false;
    }
    return true;
  }
  
  // -------------------------------------------------------------------
  // Delivery handling
  // -------------------------------------------------------------------
  
  protected void delivery(int date, DeliveryNotice notice)
  {
    if (date > currentDate)
    {
      // This delivery is for tomorrow and we must delay the handling
      // until we have received the morning notification and processed
      // todays deliveries
      if (deliveryList == null)
      {
        deliveryList = new ArrayList();
      }
      deliveryList.add(notice);
      
    }
    else
    {
      // Do a transaction for the specified agent!!!
      handleDelivery(notice);
    }
  }
  
  private void handleDelivery(DeliveryNotice notice)
  {
    String supplier = notice.getSupplier();
    log.finest("received delivery " + notice);
    
    int deliveries = 0;
    int deliveredQuantity = 0;
    int deniedDeliveries = 0;
    int deniedQuantity = 0;
    for (int j = 0, m = notice.size(); j < m; j++)
    {
      int orderID = notice.getOrderID(j);
      int productID = notice.getProductID(j);
      int quantity = notice.getQuantity(j);
      if (handleDelivery(supplier, orderID, productID, quantity, deniedQuantity < 10))
      {
        deliveries++;
        deliveredQuantity += quantity;
      }
      else
      {
        deniedDeliveries++;
        deniedQuantity += quantity;
        addDeniedDelivery(supplier, orderID);
      }
    }
    
    if (deliveries > 0)
    {
      String msg = "received " + deliveredQuantity + " PC" + (deliveredQuantity > 1 ? "s" : "")
          + " in " + deliveries + " deliver" + (deliveries > 1 ? "ies" : "y") + " from "
          + getAgentName(supplier);
      log.finest(msg);
      // Probably too much. DEBUG OUTPUT! REMOVE THIS!!!
      sendEvent(msg);
    }
    if (deniedDeliveries > 0)
    {
      sendWarningEvent("denied " + deniedQuantity + " PC" + (deniedQuantity > 1 ? "s" : "")
          + " in " + deniedDeliveries + " deliver" + (deniedDeliveries > 1 ? "ies" : "y")
          + " from " + getAgentName(supplier));
    }
  }
  
  private boolean handleDelivery(String supplier, int orderID, int productID, int quantity,
      boolean logDenies)
  {
    for (int j = 0, m = orderQueue.size(); j < m; j++)
    {
      Order order = (Order) orderQueue.get(j);
      if (order.getOrderID() == orderID)
      {
        if (!supplier.equals(order.getSupplier()))
        {
          if (logDenies)
          {
            String msg = "wrong supplier for order " + orderID + ": " + getAgentName(supplier)
                + "<=>" + getAgentName(order.getSupplier());
            log.warning(msg);
            sendWarningEvent(msg);
          }
          return false;
        }
        if (order.getProductID() != productID)
        {
          if (logDenies)
          {
            String msg = "wrong product for order " + orderID + ": " + productID + "<=>"
                + order.getProductID();
            log.warning(msg);
            sendWarningEvent(msg);
          }
          return false;
        }
        if (order.getQuantity() != quantity)
        {
          if (logDenies)
          {
            String msg = "wrong quantity for order " + orderID + ": " + quantity + "<=>"
                + order.getQuantity();
            log.warning(msg);
            sendWarningEvent(msg);
          }
          return false;
        }
        if (order.isDelivered())
        {
          if (logDenies)
          {
            // Order has already been delivered
            String msg = "order " + orderID + " has already been delivered!";
            log.warning(msg);
            sendWarningEvent(msg);
          }
          return false;
        }
        
        if (currentDate < order.getDueDate())
        {
          // Delivery ok but before due date.  Must keep the order
          // until it is time to pay for it.
          order.setDelivered();
        }
        else
        {
          // Correct delivery and on due date.  Pay and forget about
          // the order.
          pay(supplier, orderID, quantity, order.getUnitPrice());
          orderQueue.remove(j);
        }
        return true;
      }
    }
    log
        .warning("could not find order " + orderID + " for which a delivery was made by "
            + supplier);
    return false;
  }
  
  // -------------------------------------------------------------------
  //  Strategy implementation
  // -------------------------------------------------------------------
  
  protected abstract void customerSetup();
  
  protected void customerStopped()
  {}
  
  protected void customerShutdown()
  {}
  
  protected abstract RFQBundle generateRFQs(int date);
  
} // AbstractCustomer
