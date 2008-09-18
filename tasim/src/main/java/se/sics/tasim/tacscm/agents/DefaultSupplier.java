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
 * DefaultSupplier
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Dec 02 16:44:22 2002
 * Updated : $Date: 2008-03-07 10:01:29 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3825 $
 */
package se.sics.tasim.tacscm.agents;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Logger;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.props.ActiveOrders;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.props.DeliveryNotice;
import se.sics.tasim.props.OrderBundle;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.tacscm.TACSCMConstants;
import se.sics.tasim.tacscm.sim.Supplier;
import se.sics.tasim.tacscm.atp.Promise;
import se.sics.tasim.tacscm.atp.SupplierATP;

public class DefaultSupplier extends Supplier implements TACSCMConstants, TimeListener
{
  
  private int nominalCapacity = 500;
  private int startCapacityMin = 500;
  private int startCapacityDiff = 0;
  private int noSuppliers = 2;
  private int maxRFQsPerLine = 5;
  private int maxTotalRFQs = maxRFQsPerLine * 6;
  
  private int shortHorizon = 20;
  private double capacityReduction = 0.005;
  private double reputationExpo = 2.0;
  
  private double priceDiscountFactor = 0.5;
  private double downpayment = 0.0;
  
  private int numberOfDays;
  
  private double reputationRatioMax = 0.9;
  private int reputationRecovery = 100;
  private int initialReputationEndowment = 2000;
  
  private ReputationManager[] reputations;
  private int reputationCount;
  private Hashtable reputationTable = new Hashtable();
  
  private int currentDay;
  
  private ProductionLine[] productLine;
  private Hashtable messageSet = new Hashtable();
  
  public DefaultSupplier()
  {}
  
  protected void supplierSetup()
  {
    // Add the supplier name to the logger name for convenient
    // logging. Note: this is usually a bad idea because the logger
    // objects will never be garbaged but since the local supplier
    // names always are the same in TAC 03 SCM games, only a few
    // logger objects will be created.
    this.log = Logger.getLogger(DefaultSupplier.class.getName() + '.' + getName());
    
    int numberOfManufacturers = getNumberOfManufacturers();
    if (numberOfManufacturers <= 0)
    {
      throw new IllegalArgumentException("Number of manufacturers not " + "specified in config");
    }
    
    this.nominalCapacity = getPropertyAsInt("nominalCapacity", 500);
    if (nominalCapacity < 1)
    {
      nominalCapacity = 1;
    }
    this.startCapacityMin = getPropertyAsInt("startCapacityMin", nominalCapacity);
    this.startCapacityDiff = getPropertyAsInt("startCapacityMax", startCapacityMin)
        - startCapacityMin;
    check(startCapacityMin, startCapacityDiff, "start capacity");
    
    this.noSuppliers = getPropertyAsInt("noSuppliers", 2);
    if (this.noSuppliers < 1)
    {
      // At least one supplier
      this.noSuppliers = 1;
    }
    log.fine("Number of competing suppliers: " + this.noSuppliers);
    // First fetch default maximal reputation ratio
    this.reputationRatioMax = getPropertyAsDouble("reputationRatioMax", reputationRatioMax)
        / this.noSuppliers;
    // Use more specific maximal reputation ratio if configured
    this.reputationRatioMax = getPropertyAsDouble("reputationRatioMax." + this.noSuppliers,
        this.reputationRatioMax);
    this.reputationRecovery = getPropertyAsInt("reputationRecovery", reputationRecovery);
    this.initialReputationEndowment = getPropertyAsInt("initialReputationEndowment",
        initialReputationEndowment);
    
    this.reputations = new ReputationManager[numberOfManufacturers];
    for (int i = 0, n = numberOfManufacturers; i < n; i++)
    {
      reputations[i] = new ReputationManager(initialReputationEndowment, reputationRatioMax);
    }
    
    this.shortHorizon = getPropertyAsInt("shortHorizon", shortHorizon);
    this.capacityReduction = getPropertyAsDouble("capacityReduction", capacityReduction);
    
    this.reputationExpo = getPropertyAsDouble("reputationExpo", reputationExpo);
    
    this.downpayment = getPropertyAsDouble("downpayment", downpayment);
    this.priceDiscountFactor = getPropertyAsDouble("discountFactor", priceDiscountFactor);
    
    int lineCount = getComponentCount();
    this.maxRFQsPerLine = getPropertyAsInt("maxRFQs", maxRFQsPerLine * lineCount) / lineCount;
    if (this.maxRFQsPerLine < 1)
    {
      this.maxRFQsPerLine = 1;
    }
    this.maxTotalRFQs = this.maxRFQsPerLine * numberOfManufacturers;
    addTimeListener(this);
  }
  
  private void check(int min, int diff, String name)
  {
    if (min < 0 || diff < 0)
    {
      throw new IllegalArgumentException("illegal " + name + " interval");
    }
  }
  
  private void initSupplier(StartInfo si)
  {
    EventWriter eventWriter = getEventWriter();
    int supplierIndex = getIndex();
    // Same nominal capacity for both lines in TAC SCM.
    eventWriter.dataUpdated(supplierIndex, DU_NOMINAL_CAPACITY_FLAG, nominalCapacity);
    
    this.numberOfDays = si.getNumberOfDays();
    
    Random random = getNominalCapacityRandom(); // supplier nominal capacity
                                                // (both lines)
    int count = getComponentCount();
    productLine = new ProductionLine[count];
    for (int i = 0; i < count; i++)
    {
      int componentID = getComponentID(i);
      int startCapacity = (startCapacityDiff == 0) ? startCapacityMin : startCapacityMin
          + random.nextInt(startCapacityDiff + 1);
      SupplierATP supplierATP = new SupplierATP(getName() + '#' + componentID, shortHorizon,
          capacityReduction, reputationExpo, nominalCapacity, priceDiscountFactor,
          // Base price will be set when the component
          // catalog is received
          1, numberOfDays, maxTotalRFQs);
      productLine[i] = new ProductionLine(this, supplierATP, numberOfDays, startCapacity,
          nominalCapacity, componentID, maxRFQsPerLine);
      eventWriter.dataUpdated(supplierIndex, DU_COMPONENT_ID_FLAG + i, componentID);
    }
  }
  
  protected void supplierStopped()
  {
    removeTimeListener(this);
    
    // Notify all ATPs that the simulation is about to end
    if (productLine != null)
    {
      messageSet.clear();
      for (int i = 0, n = getComponentCount(); i < n; i++)
      {
        productLine[i].simulationStopped(messageSet);
      }
      // Deliver any late orders to the manufacturers
      if (messageSet.size() > 0)
      {
        Enumeration key = messageSet.keys();
        while (key.hasMoreElements())
        {
          String agent = (String) key.nextElement();
          deliverToFactory(agent, currentDay, (DeliveryNotice) messageSet.get(agent));
        }
      }
      messageSet.clear();
    }
  }
  
  protected void supplierShutdown()
  {}
  
  protected void addActiveOrders(String customer, ActiveOrders activeOrders)
  {
    if (productLine != null)
    {
      for (int i = 0, n = productLine.length; i < n; i++)
      {
        productLine[i].addActiveOrders(customer, activeOrders);
      }
    }
  }
  
  public void nextTimeUnit(int date)
  {
    currentDay = date;
    
    if (productLine == null)
    {
      // Not initialized
      return;
    }
    
    // Clear the promises/offers that have not received orders!
    EventWriter eventWriter = getEventWriter();
    int supplierIndex = getIndex();
    for (int i = 0, n = getComponentCount(); i < n; i++)
    {
      ProductionLine line = productLine[i];
      line.setDay(currentDay);
      line.updateReputation();
      line.clearPromises();
      eventWriter.dataUpdated(supplierIndex, DU_INVENTORY_FLAG + i, line.getInventory());
    }
    
    for (int i = 0, n = reputations.length; i < n; i++)
    {
      reputations[i].updateReputation(reputationRecovery, reputationRecovery);
    }
    
    // Reputations have been updated. Log the current reputations
    for (int i = 0; i < reputationCount; i++)
    {
      ReputationManager rm = reputations[i];
      if (rm.checkReputationChange())
      {
        sendSupplierReputation(rm.getName(), rm.getReputation());
      }
    }
    
    // Prepare todays deliveries. This must be done after the
    // promised demand from yesterday has been cleared since failed
    // deliveries might add to todays promised demand.
    messageSet.clear();
    for (int i = 0, n = getComponentCount(); i < n; i++)
    {
      productLine[i].addDeliveries(messageSet);
    }
    // Deliver all the components to manufacturers
    if (messageSet.size() > 0)
    {
      Enumeration key = messageSet.keys();
      while (key.hasMoreElements())
      {
        String agent = (String) key.nextElement();
        deliverToFactory(agent, date, (DeliveryNotice) messageSet.get(agent));
      }
    }
    
    // Create and add all the promises/offers for today
    handleRFQs();
    
    // Finally process the RFQs, add any offers and then send out the
    // offer bundles. RFQs can arrive earliest on day 0 and since they
    // are not handled until the morning of next day, use the RFQ
    // arrival day in the calculations.
    if (date > 0)
    {
      processRFQs(date - 1);
    }
    
    // Produce for this day
    for (int i = 0, n = getComponentCount(); i < n; i++)
    {
      ProductionLine line = productLine[i];
      int currentProduction = line.produce();
      int currentCapacity = line.getCurrentCapacity();
      
      // Send the current capacity and production to the event listeners
      eventWriter.dataUpdated(supplierIndex, DU_CAPACITY_FLAG + i, currentCapacity);
      eventWriter.dataUpdated(supplierIndex, DU_PRODUCTION_FLAG + i, currentProduction);
      
      sendSupplierCapacity(line.getProductID(), currentCapacity);
      
      // addSupplyProduced(line.getProductID(), currentProduction);
    }
  }
  
  // -------------------------------------------------------------------
  // Message handling
  // -------------------------------------------------------------------
  
  protected void messageReceived(Message message)
  {
    Transportable content = message.getContent();
    Class type = content.getClass();
    String sender = message.getSender();
    if (type == RFQBundle.class)
    {
      RFQBundle rfq = (RFQBundle) content;
      addRFQs(sender, rfq);
      
    }
    else if (type == OrderBundle.class)
    {
      // Deliver order bundle to all the assembly lines ATP's
      if (productLine != null)
      {
        OrderBundle orders = (OrderBundle) content;
        int lineCount = productLine.length;
        int warningCount = 0;
        for (int i = 0, n = orders.size(); i < n; i++)
        {
          // This is a slow process but allows the logging of errors
          // such as the corresponding offer was not found
          boolean orderHandled = false;
          for (int j = 0; j < lineCount; j++)
          {
            if (productLine[j].handleOrder(sender, orders, i))
            {
              // Order has been handled
              orderHandled = true;
              break;
            }
          }
          if (!orderHandled && warningCount < 8)
          {
            log.warning("no offer " + orders.getOfferID(i) + " found for order "
                + orders.getOrderID(i) + " from manufacturer " + sender);
            warningCount++;
          }
        }
        for (int j = 0; j < lineCount; j++)
        {
          productLine[j].finishOrders(sender);
        }
      }
      
    }
    else if (COORDINATOR.equals(sender))
    {
      // Handle administration messages. For security reasons these
      // messages must not be accepted unless the coordinator is the
      // sender.
      if (type == StartInfo.class)
      {
        initSupplier((StartInfo) content);
        
      }
      else if (type == ComponentCatalog.class)
      {
        ComponentCatalog catalog = (ComponentCatalog) content;
        for (int i = 0, n = getComponentCount(); i < n; i++)
        {
          ProductionLine atp = productLine[i];
          int index = catalog.getIndexFor(atp.getProductID());
          if (index >= 0)
          {
            atp.setProductName(catalog.getProductName(index));
            atp.setProductBasePrice(catalog.getProductBasePrice(index));
          }
        }
      }
      else
      {
        // DEBUG OUTPUT: REMOVE THIS!!!
        log.finest("IGNORED MESSAGE FROM " + sender);
      }
    }
    else
    {
      // DEBUG OUTPUT: REMOVE THIS!!!
      log.finest("IGNORED MESSAGE FROM " + sender);
    }
  }
  
  // -------------------------------------------------------------------
  // RFQ handling
  // -------------------------------------------------------------------
  
  private void addRFQs(String sender, RFQBundle rfq)
  {
    int validDate = rfq.getValidDate();
    if ((validDate > 0) && (currentDay != validDate))
    {
      log.warning("wrong valid date in RFQ from agent " + sender + " (valid=" + validDate
          + ", currentDate=" + currentDay + ')');
    }
    else if (productLine == null)
    {
      // Not yet initialized => ignore the RFQ
      log.warning("received RFQ from " + sender + " before initialization");
      
    }
    else
    {
      ReputationManager rfqd = getReputationManager(sender);
      if (!rfqd.setRFQBundle(rfq))
      {
        log.warning("RFQ already received from agent " + getAgentName(sender) + " today");
      }
      
      // Should RFQ info be sent as events??? FIX THIS!!!
      // int totalQuantity = rfq.getTotalQuantity();
      // int count = rfq.size();
      // sendEvent(totalQuantity + " component"
      // + (totalQuantity > 1 ? "s" : "")
      // + " requested in " + count
      // + " RFQ" + (count > 1 ? "s" : ""));
    }
  }
  
  private void handleRFQs()
  {
    for (int j = 0, m = reputationCount; j < m; j++)
    {
      ReputationManager data = reputations[j];
      RFQBundle rfqBundle = data.getBundle();
      if (rfqBundle != null)
      {
        String agent = data.getName();
        double reputation = data.getReputation();
        for (int i = 0, n = getComponentCount(); i < n; i++)
        {
          productLine[i].addRFQs(agent, reputation, rfqBundle);
        }
      }
      data.clear();
    }
  }
  
  private void processRFQs(int rfqDay)
  {
    messageSet.clear();
    for (int i = 0, n = getComponentCount(); i < n; i++)
    {
      productLine[i].processRFQs(messageSet, rfqDay);
    }
    if (messageSet.size() > 0)
    {
      sendMessages(messageSet);
    }
  }
  
  // -------------------------------------------------------------------
  // Utility methods
  // -------------------------------------------------------------------
  
  public int createOfferID()
  {
    return getNextID();
  }
  
  protected ReputationManager getReputationManager(String agent)
  {
    ReputationManager rep = (ReputationManager) reputationTable.get(agent);
    if (rep != null)
    {
      return rep;
    }
    
    if (reputationCount < reputations.length)
    {
      rep = reputations[reputationCount++];
      rep.setName(agent);
      reputationTable.put(agent, rep);
      return rep;
    }
    else
    {
      // More agents than manufacturers. Should NOT be possible!
      log.severe("AGENT " + agent + " CLAIMS TO BE MANUFACTURER BUT " + "ALREADY HAVE "
          + reputations.length + " MANUFACTURERS!!!");
      ReputationManager r = new ReputationManager(initialReputationEndowment, reputationRatioMax);
      r.setName(agent);
      reputationTable.put(agent, r);
      return r;
    }
  }
  
  // protected Random getRandom() {
  // return super.getRandom();
  // }
  
  protected Random getNominalCapacityRandom()
  {
    int supplierIndex = getIndex();
    return super.getNominalCapacityRandom(supplierIndex);
  }
  
  protected Random getDailyCapacityRandom(ProductionLine prodLine)
  {
    int prodLineIndex;
    int supplierIndex = getIndex();
    
    if (productLine[0] == prodLine)
      prodLineIndex = 0;
    else
      prodLineIndex = 1;
    
    return super.getDailyCapacityRandom(supplierIndex, prodLineIndex);
  }
  
  protected void requestPayment(String receiver, int orderID, long totalPrice)
  {
    super.requestPayment(receiver, orderID, totalPrice);
  }
  
  protected void requestDownpayment(String receiver, Promise promise)
  {
    if (downpayment > 0)
    {
      long totalPrice = ((long) promise.getUnitPrice()) * promise.getQuantity();
      long payment = (long) (totalPrice * downpayment);
      if (payment > 0)
      {
        super.requestPayment(receiver, promise.getID(), payment);
        
        // Set the remaining value after subtracting the made payment
        promise.setDownpayment(payment);
      }
    }
  }
  
  //   protected void addSupplyProduced(int productID, int quantityProduced) {
  //     super.addSupplyProduced(productID, quantityProduced);
  //   }
  
  protected void addSupplyOrdered(int productID, int quantityOrdered, int averageUnitPrice)
  {
    super.addSupplyOrdered(productID, quantityOrdered, averageUnitPrice);
  }
  
  protected void addSupplyDelivered(int productID, int quantityDelivered)
  {
    super.addSupplyDelivered(productID, quantityDelivered);
  }
  
  protected String getAgentName(String agentAddress)
  {
    return super.getAgentName(agentAddress);
  }
  
  protected void sendEvent(String message)
  {
    super.sendEvent(message);
  }
  
  protected void sendWarningEvent(String message)
  {
    super.sendWarningEvent(message);
  }
  
} // DefaultSupplier
