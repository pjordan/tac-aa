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
 * Factory
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Oct 28 17:26:34 2002
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.sim;

import java.util.ArrayList;
import java.util.logging.Logger;

import se.sics.isl.transport.Transportable;
import se.sics.isl.util.FormatUtils;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.BankStatus;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.props.DeliveryNotice;
import se.sics.tasim.props.DeliverySchedule;
import se.sics.tasim.props.FactoryStatus;
import se.sics.tasim.props.InventoryStatus;
import se.sics.tasim.props.OfferBundle;
import se.sics.tasim.props.OrderBundle;
import se.sics.tasim.props.ProductionSchedule;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.sim.MessageListener;
import se.sics.tasim.sim.SimulationAgent;

public class Factory extends Agent implements TimeListener, MessageListener
{
  
  private static final boolean DEBUG = true;
  
  private final TACSCMSimulation simulation;
  private final SimulationAgent ownerAgent;
  
  private BOMBundle bomBundle;
  private ComponentCatalog catalog;
  private int productionCapacity;
  
  // Next schedules are for the next day
  private ProductionSchedule nextProductionSchedule;
  private DeliverySchedule nextDeliverySchedule;
  
  private int currentDate = -1;
  private ArrayList deliveryList = null;
  
  private int storageCost;
  private double storageRateDay;
  private int daysPerYear;
  
  private InventoryStatus inventory = new InventoryStatus();
  
  /**
   * Only used for viewer information handling. Perhaps should be in another
   * object? FIX THIS!!!
   */
  private RFQBundle lastRFQsFromCustomer;
  private RFQBundle lastDaysRFQsFromCustomer;
  private OfferBundle lastOffersToCustomer;
  
  private Logger log = Logger.global;
  
  public Factory(TACSCMSimulation simulation, SimulationAgent ownerAgent, int productionCapacity,
      int daysPerYear, int storageCost, BOMBundle bomBundle, ComponentCatalog catalog)
  {
    if (productionCapacity < 1)
    {
      throw new IllegalArgumentException("productionCapacity must be positive");
    }
    if (simulation == null || ownerAgent == null || bomBundle == null || catalog == null)
    {
      throw new NullPointerException();
    }
    this.simulation = simulation;
    this.ownerAgent = ownerAgent;
    this.bomBundle = bomBundle;
    this.catalog = catalog;
    this.daysPerYear = daysPerYear;
    this.storageCost = storageCost;
    this.storageRateDay = Math.pow(1.0 + storageCost / 100.0, 1.0 / daysPerYear) - 1.0;
    this.productionCapacity = productionCapacity;
  }
  
  public SimulationAgent getOwner()
  {
    return ownerAgent;
  }
  
  public int getCapacity()
  {
    return productionCapacity;
  }
  
  public int getStorageCost()
  {
    return storageCost;
  }
  
  protected void simulationSetup()
  {
    // Add the factory name to the logger name for convenient logging.
    // Note: this is usually a bad idea because the logger objects
    // will never be garbaged but since the local factory names always
    // are the same in TAC 03 SCM games, only a few logger objects
    // will be created.
    this.log = Logger.getLogger(Factory.class.getName() + '.' + getName());
    
    addTimeListener(this);
    
    // Listen on all messages sent/received by the owner agent in
    // order to generate viewer information.
    ownerAgent.addMessageListener(this);
  }
  
  protected void simulationFinished()
  {
    removeTimeListener(this);
    ownerAgent.removeMessageListener(this);
  }
  
  public void nextTimeUnit(int date)
  {
    currentDate = date;
    
    // Calculate inventory cost
    double value = getInventoryValue();
    long cost = Math.round(value * storageRateDay);
    if (DEBUG)
      log.finest("Factory for " + ownerAgent.getName() + " claimed " + cost
          + " in storage cost with inventory value of $" + value);
    simulation.claimStorageCost(ownerAgent, cost);
    
    // Deliver everything scheduled to be delivered today and that is
    // in the inventory. This must be done before the production
    // scheduling is done because that might alter the inventory.
    if (nextDeliverySchedule != null)
    {
      String ownerAddress = ownerAgent.getAddress();
      DeliveryNotice notice = null;
      for (int i = 0, n = nextDeliverySchedule.size(); i < n; i++)
      {
        int productID = nextDeliverySchedule.getProductID(i);
        int quantity = nextDeliverySchedule.getQuantity(i);
        if (inventory.getInventoryQuantity(productID) >= quantity && quantity > 0)
        {
          // Delivery is possible
          String customer = nextDeliverySchedule.getCustomer(i);
          
          // Quick hack to bundle together most of the deliveries to
          // the same customer (because usually there is only one
          // customer). Should be fixed nicer. FIX THIS!!!
          if (notice == null)
          {
            notice = new DeliveryNotice(ownerAddress, customer);
            
          }
          else if (!customer.equals(notice.getCustomer()))
          {
            // Deliver to customer via TACSCMSimulation
            deliverToCustomer(date, notice);
            notice = new DeliveryNotice(ownerAddress, customer);
          }
          notice.addDelivery(nextDeliverySchedule.getOrderID(i), productID, quantity);
          
          inventory.removeInventory(productID, quantity);
        }
      }
      if (notice != null)
      {
        // Deliver to customer via TACSCMSimulation
        deliverToCustomer(date, notice);
      }
    }
    nextDeliverySchedule = null;
    
    // Must create new object because if the agent is builtin it must
    // not have the possibility to change the inventory!!!
    FactoryStatus inventoryToAgent = new FactoryStatus(inventory);
    if (nextProductionSchedule != null)
    {
      int minQuantity;
      int availCycles = productionCapacity;
      for (int i = 0, n = nextProductionSchedule.size(); i < n; i++)
      {
        int pid = nextProductionSchedule.getProductID(i);
        int quantity = nextProductionSchedule.getQuantity(i);
        if (quantity <= 0)
        {
          continue;
        }
        
        int pidIndex = bomBundle.getIndexFor(pid);
        if (pidIndex < 0)
        {
          log.warning("could not produce for unknown product " + pid);
          continue;
        }
        
        int cyclesReq = bomBundle.getAssemblyCyclesRequired(pidIndex);
        int[] components = bomBundle.getComponents(pidIndex);
        if ((quantity * cyclesReq) <= availCycles && (cyclesReq > 0) && (components != null)
            && hasAvailableComponents(components, quantity))
        {
          
          for (int j = 0, m = components.length; j < m; j++)
          {
            inventory.removeInventory(components[j], quantity);
            inventoryToAgent.removeInventory(components[j], quantity);
          }
          // This day's deliveries have already been made so we can
          // add the product to the inventory immediately (it can
          // still not be used until next day). However we do not
          // add it to the agents copy of the inventory because the
          // agent should not see the finished products until next
          // day.
          inventory.addInventory(pid, quantity);
          availCycles -= quantity * cyclesReq;
          if (availCycles <= 0)
          {
            break;
          }
        }
      }
      
      float currentUtilization = (float) (1.0 - ((double) availCycles / productionCapacity));
      inventoryToAgent.setUtilization(currentUtilization);
    }
    nextProductionSchedule = null;
    
    // Send the copy of the inventory to the agent (must be a copy
    // because otherwise the agent might change it if it is builtin)
    sendMessage(inventoryToAgent);
    
    // How should the inventory be given to the viewer??? FIX THIS!!!
    simulation.getEventWriter().dataUpdated(ownerAgent.getIndex(), TACSCMSimulation.TYPE_NONE,
        inventoryToAgent);
    
    // If we have pending deliveries we can add them now when all
    // production and deliveries have been made.
    if (deliveryList != null)
    {
      for (int i = 0, n = deliveryList.size(); i < n; i++)
      {
        DeliveryNotice delivery = (DeliveryNotice) deliveryList.get(i);
        addToInventory(delivery);
      }
      deliveryList.clear();
    }
  }
  
  private boolean hasAvailableComponents(int[] components, int quantity)
  {
    for (int j = 0, m = components.length; j < m; j++)
    {
      if (inventory.getInventoryQuantity(components[j]) < quantity)
      {
        return false;
      }
    }
    return true;
  }
  
  private void addToInventory(DeliveryNotice delivery)
  {
    for (int i = 0, n = delivery.size(); i < n; i++)
    {
      inventory.addInventory(delivery.getProductID(i), delivery.getQuantity(i));
    }
  }
  
  private void deliverToCustomer(int date, DeliveryNotice notice)
  {
    // Since the products have been removed from inventory we always
    // handle it like they were delivered. If they were wrongly
    // delivered they were wasted.
    int totalQuantity = notice.getTotalQuantity();
    simulation.getEventWriter().dataUpdated(ownerAgent.getIndex(), TACSCMSimulation.TYPE_MESSAGE,
        "delivered " + totalQuantity + " PC" + (totalQuantity > 1 ? "s" : "") + " from inventory");
    
    // Deliver to customer via TACSCMSimulation
    if (!simulation.deliverToCustomer(notice.getCustomer(), date, notice))
    {
      log.warning("could not make delivery " + notice);
    }
  }
  
  protected void sendMessage(Transportable mc)
  {
    sendMessage(ownerAgent.getAddress(), mc);
  }
  
  protected void messageReceived(Message message)
  {
    if (message.getSender().equals(ownerAgent.getAddress()))
    {
      Transportable content = message.getContent();
      Class type = content.getClass();
      if (type == ProductionSchedule.class)
      {
        nextProductionSchedule = (ProductionSchedule) content;
      }
      else if (type == DeliverySchedule.class)
      {
        nextDeliverySchedule = (DeliverySchedule) content;
      }
      else
      {
        log.warning(ownerAgent.getName() + ": ignoring unexpected message " + message);
      }
    }
    else
    {
      // Return permission denied or do nothing? FIX THIS!!!
      log.warning(ownerAgent.getName() + ": ignoring message from non-owner " + message);
    }
  }
  
  final void delivery(int deliveryDate, DeliveryNotice delivery)
  {
    if (deliveryDate > currentDate)
    {
      // The factory has not yet received the morning notification
      // today and we need to delay the deliveries until this day has
      // been handled.
      if (deliveryList == null)
      {
        deliveryList = new ArrayList();
      }
      deliveryList.add(delivery);
      
    }
    else
    {
      // The factory has already received the morning notification
      // today and we can simply add the delivery to the inventory for
      // handling next morning.
      addToInventory(delivery);
    }
  }
  
  // DEBUG FINALIZE REMOVE THIS!!! REMOVE THIS!!!
  protected void finalize() throws Throwable
  {
    log.info("FACTORY " + getName() + " for " + ownerAgent.getName() + " IS BEING GARBAGED");
    super.finalize();
  }
  
  // -------------------------------------------------------------------
  // Inventory cost calculations
  // -------------------------------------------------------------------
  
  protected double getInventoryValue()
  {
    double value = 0.0;
    
    for (int i = 0, n = inventory.getProductCount(); i < n; i++)
    {
      int quantity = inventory.getQuantity(i);
      if (quantity > 0)
      {
        int productID = inventory.getProductID(i);
        int index = catalog.getIndexFor(productID);
        if (index >= 0)
        {
          // Component
          value += quantity * catalog.getProductBasePrice(index);
          
        }
        else if ((index = bomBundle.getIndexFor(productID)) >= 0)
        {
          // Assembled product
          value += quantity * bomBundle.getProductBasePrice(index);
          
        }
        else
        {
          log.warning("no product value known for product " + productID + " for manufacturer "
              + ownerAgent.getAddress());
        }
      }
    }
    return value;
  }
  
  // -------------------------------------------------------------------
  // MessageListener API
  // - Only for viewer information handling
  // -------------------------------------------------------------------
  
  public void messageReceived(SimulationAgent receivingAgent, String sender, Transportable content)
  {
    Class type = content.getClass();
    if (receivingAgent != ownerAgent)
    {
      // Wrong agent!!!
    }
    else if (type == RFQBundle.class)
    {
      // Assume only customer sends RFQs to manufacturers for now. FIX THIS!!!
      RFQBundle bundle = (RFQBundle) content;
      // Since both orders from customers and rfqs comes from the
      // customer in the morning, we must remember last days rfqs too.
      lastDaysRFQsFromCustomer = lastRFQsFromCustomer;
      lastRFQsFromCustomer = bundle;
      
      // Should this be logged??? Is in the customer log anyway?? FIX THIS!!!
      int count = bundle.size();
      int totalQuantity = bundle.getTotalQuantity();
      simulation.getEventWriter().dataUpdated(
          ownerAgent.getIndex(),
          TACSCMSimulation.TYPE_MESSAGE,
          totalQuantity + " PC" + (totalQuantity > 1 ? "s" : "") + " requested in " + count
              + " RFQ" + (count > 1 ? "s" : ""));
      
    }
    else if (type == OrderBundle.class)
    {
      handleOrders(sender, (OrderBundle) content);
      
    }
    else if (type == BankStatus.class)
    {
      // Assume only the coordinator sends bank status for now! FIX THIS!!!
      handleBankStatus((BankStatus) content);
      
    }
    else if (type == DeliveryNotice.class)
    {
      // Assume only suppliers delivers to manufacturers for now. FIX THIS!!!
      // Solution: place this in addToInventory() above? FIX THIS!!!
      DeliveryNotice notice = (DeliveryNotice) content;
      int count = notice.size();
      String supplierName = simulation.getAgentName(sender);
      EventWriter eventWriter = simulation.getEventWriter();
      int agentIndex = ownerAgent.getIndex();
      if (count < 3)
      {
        int index;
        for (int i = 0; i < count; i++)
        {
          int quantity = notice.getQuantity(i);
          int productID = notice.getProductID(i);
          if ((index = catalog.getIndexFor(productID)) >= 0)
          {
            eventWriter.dataUpdated(agentIndex, TACSCMSimulation.TYPE_MESSAGE, "" + quantity + ' '
                + supplierName + ' ' + catalog.getProductName(index) + " delivered");
          }
          else
          {
            eventWriter.dataUpdated(agentIndex, TACSCMSimulation.TYPE_MESSAGE, "" + quantity + ' '
                + supplierName + " component" + (quantity > 1 ? "s" : "") + " delivered");
          }
        }
      }
      else
      {
        int totalQuantity = notice.getTotalQuantity();
        eventWriter.dataUpdated(agentIndex, TACSCMSimulation.TYPE_MESSAGE, "" + totalQuantity + ' '
            + supplierName + " component" + (totalQuantity > 1 ? "s" : "") + " delivered");
      }
    }
  }
  
  private void handleOrders(String sender, OrderBundle orders)
  {
    OfferBundle offers = this.lastOffersToCustomer;
    int orderNumber = orders.size();
    EventWriter eventWriter = simulation.getEventWriter();
    int agentIndex = ownerAgent.getIndex();
    
    if (offers == null)
    {
      // No previously sent offers??? FIX THIS!!!
      eventWriter.dataUpdated(agentIndex, TACSCMSimulation.TYPE_MESSAGE, "got " + orderNumber
          + " order" + (orderNumber > 1 ? "s" : "") + " (info missing)");
      log.warning("could not find any offers for " + orderNumber + " orders");
      
    }
    else if (orderNumber < 3)
    {
      for (int i = 0; i < orderNumber; i++)
      {
        int offerID = orders.getOfferID(i);
        int index = offers.getIndexFor(offerID);
        if (index >= 0)
        {
          int quantity = offers.getQuantity(index);
          long unitPrice = offers.getUnitPrice(index);
          String message = "" + quantity + ' ' + getProductName(offers.getRFQID(index))
              + " ordered for $" + unitPrice + "/unit";
          // + FormatUtils.formatAmount(quantity * unitPrice);
          eventWriter.dataUpdated(agentIndex, TACSCMSimulation.TYPE_MESSAGE, message);
          
        }
        else
        {
          // What should be done if no offer was found??? FIX THIS!!!
          log.warning("could not find offer " + offerID + " for order " + orders.getOrderID(i));
        }
      }
      
    }
    else
    {
      int totalQuantity = 0;
      long totalPrice = 0L;
      for (int i = 0; i < orderNumber; i++)
      {
        int offerID = orders.getOfferID(i);
        int index = offers.getIndexFor(offerID);
        if (index >= 0)
        {
          int quantity = offers.getQuantity(index);
          totalQuantity += quantity;
          totalPrice += quantity * offers.getUnitPrice(index);
        }
        else
        {
          // What should be done if no offer was found??? FIX THIS!!!
          log.warning("could not find offer " + offerID + " for order " + orders.getOrderID(i));
        }
      }
      if (totalQuantity > 0)
      {
        eventWriter.dataUpdated(agentIndex, TACSCMSimulation.TYPE_MESSAGE, "" + totalQuantity
            + " PCs for $" + (totalPrice / totalQuantity) + "/unit"
            // + FormatUtils.formatAmount(totalPrice)
            + " ordered");
      }
    }
  }
  
  private void handleBankStatus(BankStatus status)
  {
    EventWriter eventWriter = simulation.getEventWriter();
    int agentIndex = ownerAgent.getIndex();
    int count = status.getPenaltyCount();
    
    eventWriter
        .dataUpdated(agentIndex, TACSCMSimulation.DU_BANK_ACCOUNT, status.getAccountStatus());
    if (count == 0)
    {
      // No penalties => do nothing more
    }
    else if (count > 2)
    {
      long totalPenalty = status.getTotalPenaltyAmount();
      eventWriter.dataUpdated(agentIndex, TACSCMSimulation.TYPE_WARNING, "penalty $"
          + FormatUtils.formatAmount(totalPenalty) + " for " + count + " late deliver"
          + (count > 1 ? "ies" : "y"));
    }
    else
    {
      for (int i = 0; i < count; i++)
      {
        eventWriter.dataUpdated(agentIndex, TACSCMSimulation.TYPE_WARNING, "penalty $"
            + FormatUtils.formatAmount(status.getPenaltyAmount(i)) + " for late delivery");
      }
    }
  }
  
  public void messageSent(SimulationAgent sendingAgent, String receiver, Transportable content)
  {
    Class type = content.getClass();
    if (sendingAgent != ownerAgent)
    {
      // Wrong agent!!!
    }
    else if (type == OfferBundle.class)
    {
      handleOffer((OfferBundle) content);
    }
  }
  
  public void messageSent(SimulationAgent sendingAgent, int role, Transportable content)
  {
    Class type = content.getClass();
    if (sendingAgent != ownerAgent)
    {
      // Wrong agent!!!
    }
    else if (type == OfferBundle.class)
    {
      handleOffer((OfferBundle) content);
    }
  }
  
  private void handleOffer(OfferBundle bundle)
  {
    // Assume for now that all offers are to customers (the
    // manufacturer should never send offers to any suppliers). FIX THIS!!!
    this.lastOffersToCustomer = bundle;
    
    int count = bundle.size();
    int totalQuantity = 0;
    long totalPrice = 0;
    for (int i = 0; i < count; i++)
    { // SHOULD PERHAPS BE in OFFERBUNDLE. FIX THIS!!!
      int quantity = bundle.getQuantity(i);
      totalQuantity += quantity;
      totalPrice += quantity * bundle.getUnitPrice(i);
    }
    if (totalQuantity > 0)
    {
      simulation.getEventWriter().dataUpdated(
          ownerAgent.getIndex(),
          TACSCMSimulation.TYPE_MESSAGE,
          "offered " + totalQuantity + " PC" + (totalQuantity > 1 ? "s" : "") + " for $"
              + (totalPrice / totalQuantity) + "/unit");
      // 		     + FormatUtils.formatAmount(totalPrice));
    }
  }
  
  private String getProductName(int rfqID)
  {
    String name = getProductName(lastDaysRFQsFromCustomer, rfqID);
    if (name == null)
    {
      name = getProductName(lastRFQsFromCustomer, rfqID);
      if (name == null)
      {
        name = "PCs";
      }
    }
    return name;
  }
  
  private String getProductName(RFQBundle bundle, int rfqID)
  {
    if (bundle != null)
    {
      int index = bundle.getIndexFor(rfqID);
      if (index >= 0)
      {
        int productID = bundle.getProductID(index);
        return "PC-" + productID; // Add better product name. FIX THIS!!!
      }
    }
    return null;
  }
  
} // Factory
