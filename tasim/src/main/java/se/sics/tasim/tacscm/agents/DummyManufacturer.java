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
 * DummyManufacturer
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Tue Nov 05 12:41:08 2002
 * Updated : $Date: 2008-03-07 10:01:29 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3825 $
 */
package se.sics.tasim.tacscm.agents;

import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Logger;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.props.DeliveryNotice;
import se.sics.tasim.props.DeliverySchedule;
import se.sics.tasim.props.FactoryStatus;
import se.sics.tasim.props.InventoryStatus;
import se.sics.tasim.props.OfferBundle;
import se.sics.tasim.props.OrderBundle;
import se.sics.tasim.props.ProductionSchedule;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;

public class DummyManufacturer extends Agent
{
  
  private final static int OFFER_RFQ_ID = 0;
  private final static int OFFER_PRODUCT_ID = 1;
  private final static int OFFER_QUANTITY = 2;
  private final static int OFFER_DUEDATE = 3;
  private final static int OFFER_PARTS = 4;
  
  private final static int ORDER_ID = 0;
  private final static int ORDER_OFFER_ID = 1;
  private final static int ORDER_PARTS = 2;
  
  private BOMBundle bomBundle;
  private ComponentCatalog catalog;
  private String factory;
  private int factoryCapacity;
  private int lowFactoryCapacity;
  private int hiFactoryCapacity;
  private int daysBeforeVoid = 5;
  private boolean isInitialized = false;
  
  private int rfqCounter = 0;
  
  private FactoryStatus currentInventory;
  
  // Information needed to calculate next days inventory
  private ProductionSchedule lastDayProduction;
  private DeliveryNotice[] deliveries = new DeliveryNotice[10];
  private int deliveryCount = 0;
  
  private InventoryStatus componentDemand = new InventoryStatus();
  
  private int[] customerOffers = new int[200 * OFFER_PARTS];
  private String[] customerOfferReceiver = new String[200];
  private int customerOfferCounter = 0;
  
  private double priceDiscountFactor = 0.3;
  private double bidProbability = 1.0;
  
  private int[] customerOrders = new int[100];
  private int customerOrderStartIndex = 0;
  private int customerOrderCounter = 0;
  
  private int currentDate;
  private int lastBidDueDate = -1;
  private Hashtable supplyTable = new Hashtable();
  
  // Uncomment this to make the dummy agents deterministic.
  // private long dummyManufacturerSeed = 112233445;
  // private Random random = new Random(dummyManufacturerSeed);
  private Random random = new Random();
  
  private Logger log = Logger.global;
  
  public DummyManufacturer()
  {}
  
  private void checkInitialized()
  {
    this.isInitialized = this.bomBundle != null && this.catalog != null && this.factory != null;
  }
  
  protected void simulationSetup()
  {
    // Add the manufacturer name to the logger name for convenient
    // logging. Note: this is usually a bad idea because the logger
    // objects will never be garbaged but since the dummy names always
    // are the same in TAC SCM games, only a few logger objects
    // will be created.
    this.log = Logger.getLogger(DummyManufacturer.class.getName() + '.' + getName());
    log.fine("dummy " + getName() + " simulationSetup");
  }
  
  protected void simulationFinished()
  {}
  
  protected void messageReceived(Message message)
  {
    Transportable content = message.getContent();
    if (content instanceof FactoryStatus)
    {
      this.currentInventory = (FactoryStatus) content;
      
      double utilization = this.currentInventory.getUtilization();
      if (utilization > 0.9)
      {
        bidProbability -= 0.1;
        if (bidProbability < 0.0)
        {
          bidProbability = 0.0;
        }
      }
      else if (utilization < 0.4)
      {
        bidProbability += 0.05;
        if (bidProbability > 1.0)
        {
          bidProbability = 1.0;
        }
      }
      log.finest("Factory Utilization: " + (int) (utilization * 100) + " Bid probability: "
          + (int) (bidProbability * 100));
      
    }
    else if (content instanceof OfferBundle)
    {
      // Offer from supplier => always order for now
      OfferBundle offers = (OfferBundle) content;
      OrderBundle orders = new OrderBundle();
      // Earliest complete is always after partial offers so the offer
      // bundle is traversed backwards to always accept earliest offer
      // instead of the partial (the server will ignore the second
      // order for the same offer).
      for (int i = offers.size() - 1; i >= 0; i--)
      {
        // Only order if quantity > 0 (otherwise it is only a price quote)
        if (offers.getQuantity(i) > 0)
        {
          orders.addOrder(getNextID(), offers.getOfferID(i));
        }
      }
      if (orders.size() > 0)
      {
        sendMessage(message.createReply(orders));
      }
      
    }
    else if (content instanceof RFQBundle)
    {
      if (isInitialized)
      {
        String sender = message.getSender();
        RFQBundle rfq = (RFQBundle) content;
        OfferBundle offer = new OfferBundle();
        for (int i = 0, n = rfq.size(); i < n; i++)
        {
          int dueDate = rfq.getDueDate(i);
          // Only bid for quotes to which we have time to produce PCs
          // and where the delivery time is not beyond the end of the
          // game. Since this manufacturer use strict build to order
          // it will in optimal case be possible to deliver in 6 days
          // (including one day for the suppliers to produce the supply).
          // D + 0: send offer to customer
          // D + 1: receive order from customer and send RFQ to suppliers
          // D + 2: receive offers from suppliers and send orders for supply
          // D + 3: suppliers produce the requested supply
          // D + 4: delivery of supply
          // D + 5: assembling products
          // D + 6: delivery to customer
          if ((dueDate - currentDate) >= 6 && (dueDate <= lastBidDueDate))
          {
            int productID = rfq.getProductID(i);
            int resPrice = rfq.getReservePricePerUnit(i);
            int bomProductIndex = bomBundle.getIndexFor(productID);
            int basePrice = bomProductIndex >= 0 ? bomBundle.getProductBasePrice(bomProductIndex)
                : 0;
            // Never go more than 10% below the base price
            basePrice = (int) (basePrice * 0.90);
            if (resPrice > basePrice && (random.nextDouble() < bidProbability))
            {
              int quantity = rfq.getQuantity(i);
              int offeredPrice = (int) (basePrice + (resPrice - basePrice)
                  * (1.0 - random.nextDouble() * priceDiscountFactor));
              int offerID = addCustomerOffer(sender, rfq.getRFQID(i), productID, quantity, dueDate);
              offer.addOffer(offerID, rfq, i, offeredPrice);
            }
          }
        }
        if (offer.size() > 0)
        {
          sendMessage(message.createReply(offer));
        }
      }
      
    }
    else if (content instanceof OrderBundle)
    {
      addCustomerOrders((OrderBundle) content);
      
      // Order the components needed to fulfill these orders (only the
      // customer orders from the manufacturers which means only one
      // order bundle per day will be received).
      for (int i = 0, n = componentDemand.getProductCount(); i < n; i++)
      {
        int quantity = componentDemand.getQuantity(i);
        if (quantity > 0)
        {
          int productID = componentDemand.getProductID(i);
          String[] suppliers = catalog.getSuppliersForProduct(productID);
          if (suppliers != null)
          {
            // Take one supplier by random
            int supIndex = random.nextInt(suppliers.length);
            RFQBundle b = (RFQBundle) supplyTable.get(suppliers[supIndex]);
            if (b == null)
            {
              b = new RFQBundle();
              supplyTable.put(suppliers[supIndex], b);
            }
            int rfqID = ++rfqCounter;
            b.addRFQ(rfqID, productID, quantity, 0, currentDate + 2, 0);
            componentDemand.addInventory(productID, -quantity);
          }
          else
          {
            log.severe("no suppliers for product " + productID);
          }
        }
      }
      sendMessages(supplyTable);
      supplyTable.clear();
      
    }
    else if (content instanceof DeliveryNotice)
    {
      addDelivery((DeliveryNotice) content);
      
    }
    else if (content instanceof SimulationStatus)
    {
      if (factory != null && currentInventory != null)
      {
        // All messages for this day has been received. Time to create
        // the schedules.
        sendSchedules();
      }
      
      // Now nothing more will be done until the next day
      currentDate = ((SimulationStatus) content).getCurrentDate() + 1;
      
    }
    else if (content instanceof ComponentCatalog)
    {
      this.catalog = (ComponentCatalog) content;
      checkInitialized();
      
    }
    else if (content instanceof BOMBundle)
    {
      this.bomBundle = (BOMBundle) content;
      checkInitialized();
      
    }
    else if (content instanceof StartInfo)
    {
      // Start information for manufacturers
      StartInfo info = (StartInfo) content;
      // This is the last due date to bid for. Do not bid for quotes
      // with a due date the last days (just to be a little safer).
      this.lastBidDueDate = info.getNumberOfDays() - 2;
      this.daysBeforeVoid = info.getAttributeAsInt("customer.daysBeforeVoid", this.daysBeforeVoid);
      this.factory = info.getAttribute("factory.address");
      this.factoryCapacity = info.getAttributeAsInt("factory.capacity", 2000);
      this.lowFactoryCapacity = (int) (0.2 * this.factoryCapacity);
      this.hiFactoryCapacity = (int) (0.9 * this.factoryCapacity);
      checkInitialized();
    }
  }
  
  private int addCustomerOffer(String receiver, int rfqID, int productID, int quantity, int dueDate)
  {
    int index = customerOfferCounter * OFFER_PARTS;
    if (index >= customerOffers.length)
    {
      customerOffers = ArrayUtils.setSize(customerOffers, index + 200 * OFFER_PARTS);
      customerOfferReceiver = (String[]) ArrayUtils.setSize(customerOfferReceiver,
          customerOfferCounter + 200);
    }
    customerOffers[index + OFFER_RFQ_ID] = rfqID;
    customerOffers[index + OFFER_PRODUCT_ID] = productID;
    customerOffers[index + OFFER_QUANTITY] = quantity;
    customerOffers[index + OFFER_DUEDATE] = dueDate;
    customerOfferReceiver[customerOfferCounter] = receiver;
    return customerOfferCounter++;
  }
  
  private void addCustomerOrders(OrderBundle bundle)
  {
    int orderCount = bundle.size();
    int index = customerOrderStartIndex + customerOrderCounter * ORDER_PARTS;
    // Ensure capacity for the new orders
    if ((index + orderCount * ORDER_PARTS) >= customerOrders.length)
    {
      int[] tmp = (customerOrderStartIndex > (ORDER_PARTS * (orderCount + 100))) ? customerOrders
          : new int[index + ORDER_PARTS * (orderCount + 100)];
      System.arraycopy(customerOrders, customerOrderStartIndex, tmp, 0, customerOrderCounter
          * ORDER_PARTS);
      customerOrders = tmp;
      customerOrderStartIndex = 0;
      index = customerOrderCounter * ORDER_PARTS;
    }
    
    for (int i = 0; i < orderCount; i++)
    {
      int orderID = bundle.getOrderID(i);
      int offerID = bundle.getOfferID(i);
      int offerIndex = offerID * OFFER_PARTS;
      int dueDate = customerOffers[offerIndex + OFFER_DUEDATE];
      
      if (dueDate < 0)
      {
        log.severe("offer " + offerID + " for new order " + orderID + " has already been handled");
      }
      else
      {
        // Add the new order in due date order
        int insertIndex = index;
        int previousOfferID;
        while (insertIndex > customerOrderStartIndex
            && ((previousOfferID = customerOrders[insertIndex - ORDER_PARTS + ORDER_OFFER_ID]) >= 0)
            && ((customerOffers[previousOfferID * OFFER_PARTS + OFFER_DUEDATE]) > dueDate))
        {
          customerOrders[insertIndex + ORDER_ID] = customerOrders[insertIndex - ORDER_PARTS
              + ORDER_ID];
          customerOrders[insertIndex + ORDER_OFFER_ID] = previousOfferID;
          insertIndex -= ORDER_PARTS;
        }
        
        customerOrders[insertIndex + ORDER_ID] = orderID;
        customerOrders[insertIndex + ORDER_OFFER_ID] = offerID;
        
        index += ORDER_PARTS;
        customerOrderCounter++;
        
        // Add the new component demand
        int productID = customerOffers[offerIndex + OFFER_PRODUCT_ID];
        int quantity = customerOffers[offerIndex + OFFER_QUANTITY];
        int[] components = bomBundle.getComponentsForProductID(productID);
        if (components != null)
        {
          for (int j = 0, m = components.length; j < m; j++)
          {
            componentDemand.addInventory(components[j], quantity);
          }
        }
      }
    }
  }
  
  private void addDelivery(DeliveryNotice notice)
  {
    if (deliveryCount == deliveries.length)
    {
      deliveries = (DeliveryNotice[]) ArrayUtils.setSize(deliveries, deliveryCount + 20);
    }
    deliveries[deliveryCount++] = notice;
  }
  
  private void sendSchedules()
  {
    // Calculate the inventory for next day
    InventoryStatus inventory = new InventoryStatus(this.currentInventory);
    // Add todays deliveries to the inventory because these components
    // can be used in tomorrows production
    if (deliveryCount > 0)
    {
      for (int i = 0, n = deliveryCount; i < n; i++)
      {
        DeliveryNotice notice = deliveries[i];
        deliveries[i] = null;
        
        for (int j = 0, m = notice.size(); j < m; j++)
        {
          inventory.addInventory(notice.getProductID(j), notice.getQuantity(j));
        }
      }
      deliveryCount = 0;
    }
    if (lastDayProduction != null)
    {
      // Add the production for the current day to the inventory for
      // tomorrow
      for (int i = 0, n = lastDayProduction.size(); i < n; i++)
      {
        inventory.addInventory(lastDayProduction.getProductID(i), lastDayProduction.getQuantity(i));
      }
      lastDayProduction = null;
    }
    
    // Send any production and delivery schedules
    ProductionSchedule production = new ProductionSchedule();
    DeliverySchedule delivery = new DeliverySchedule();
    int latestDueDate = currentDate - daysBeforeVoid + 2;
    
    int orderEndIndex = customerOrderStartIndex + customerOrderCounter * ORDER_PARTS;
    int freeCapacity = factoryCapacity;
    for (int index = customerOrderStartIndex; index < orderEndIndex; index += ORDER_PARTS)
    {
      int offerID = customerOrders[index + ORDER_OFFER_ID];
      if (offerID >= 0)
      {
        int orderID = customerOrders[index + ORDER_ID];
        int offerIndex = offerID * OFFER_PARTS;
        int productID = customerOffers[offerIndex + OFFER_PRODUCT_ID];
        int quantity = customerOffers[offerIndex + OFFER_QUANTITY];
        int dueDate = customerOffers[offerIndex + OFFER_DUEDATE];
        int inventoryQuantity = inventory.getInventoryQuantity(productID);
        int pidIndex;
        int cyclesReq;
        int[] components;
        
        if ((currentDate >= (dueDate - 1)) && (inventoryQuantity >= quantity))
        {
          delivery.addDelivery(productID, quantity, orderID, customerOfferReceiver[offerID]);
          inventory.addInventory(productID, -quantity);
          // Mark this offer as been finished
          customerOffers[offerIndex + OFFER_RFQ_ID] = -1;
          // Mark this order as been finished
          customerOrders[index + ORDER_OFFER_ID] = -1;
          
        }
        else if (dueDate <= latestDueDate)
        {
          // It is too late to produce and deliver this order
          log.info("cancelling to late order " + orderID + " (dueDate=" + dueDate + ",date="
              + currentDate + ')');
          // Mark this offer as been finished
          customerOffers[offerIndex + OFFER_RFQ_ID] = -1;
          // Mark this order as been finished
          customerOrders[index + ORDER_OFFER_ID] = -1;
          
          // The components for the order is now available for other
          // orders.
          components = bomBundle.getComponentsForProductID(productID);
          if (components != null)
          {
            for (int j = 0, m = components.length; j < m; j++)
            {
              componentDemand.addInventory(components[j], -quantity);
            }
          }
          
        }
        else if (inventoryQuantity >= quantity)
        {
          // There already is enough finished products to deliver this
          // order. Reserve these finished products for this order.
          inventory.addInventory(productID, -quantity);
          
        }
        else if ((pidIndex = bomBundle.getIndexFor(productID)) < 0)
        {
          log.warning("could not produce for unknown product " + productID);
          
        }
        else if ((cyclesReq = bomBundle.getAssemblyCyclesRequired(pidIndex)) <= 0)
        {
          // No assembly cycles for product
          
        }
        else if ((quantity * cyclesReq) > freeCapacity)
        {
          // No available capacity in the factory to assemble for this order
          
        }
        else if ((components = bomBundle.getComponents(pidIndex)) == null)
        {
          // No component specification
          
        }
        else if (hasAvailableComponents(components, quantity - inventoryQuantity, inventory))
        {
          freeCapacity -= quantity * cyclesReq;
          for (int j = 0, m = components.length; j < m; j++)
          {
            inventory.addInventory(components[j], -(quantity - inventoryQuantity));
          }
          
          // Allocate any remaining finished products to this order
          if (inventoryQuantity > 0)
          {
            inventory.addInventory(productID, -inventoryQuantity);
          }
          
          // Add to the factory production schedule
          production.addProduction(productID, quantity - inventoryQuantity);
        }
      }
    }
    
    // Simple hack to decrease the number of new orders on high
    // factory utilization.
    if (freeCapacity < lowFactoryCapacity)
    {
      this.priceDiscountFactor = 0.03;
    }
    else if (freeCapacity >= hiFactoryCapacity)
    {
      this.priceDiscountFactor = 0.3;
    }
    else
    {
      this.priceDiscountFactor = 0.2;
    }
    
    // Modify the start index to point after each initial finished orders
    while (customerOrderStartIndex < orderEndIndex
        && customerOrders[customerOrderStartIndex + ORDER_OFFER_ID] < 0)
    {
      customerOrderStartIndex += ORDER_PARTS;
      customerOrderCounter--;
    }
    
    // No need to send a delivery schedule if nothing should be delivered
    if (delivery.size() > 0)
    {
      sendMessage(factory, delivery);
    }
    // No need to send a production schedule if nothing should be produced
    if (production.size() > 0)
    {
      // Remember this production because it is needed when
      // calculating the inventory for next day
      lastDayProduction = production;
      sendMessage(factory, production);
    }
  }
  
  private boolean hasAvailableComponents(int[] components, int quantity, InventoryStatus inventory)
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
  
} // DummyManufacturer
