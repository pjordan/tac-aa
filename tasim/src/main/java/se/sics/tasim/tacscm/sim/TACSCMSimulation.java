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
 * TACSCMSimulation
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Oct 14 17:52:19 2002
 * Updated : $Date: 2008-06-01 00:43:24 -0500 (Sun, 01 Jun 2008) $
 *           $Revision: 4679 $
 */
package se.sics.tasim.tacscm.sim;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.props.ActiveOrders;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.BankStatus;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.props.DeliveryNotice;
import se.sics.tasim.props.OfferBundle;
import se.sics.tasim.props.OrderBundle;
import se.sics.tasim.props.PriceReport;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.props.ServerConfig;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.sim.AgentChannel;
import se.sics.tasim.sim.LogWriter;
import se.sics.tasim.sim.Simulation;
import se.sics.tasim.sim.SimulationAgent;
import se.sics.tasim.tacscm.TACSCMConstants;

public class TACSCMSimulation extends Simulation implements TACSCMConstants
{
  
  private static final Logger log = Logger.getLogger(TACSCMSimulation.class.getName());
  
  private Bank bank;
  private String timeUnitName = "Day";
  private int currentTimeUnit = 0;
  private int numberOfDays = 250;
  private int secondsPerDay = 15;
  private int daysBeforeVoid = 5;
  
  private int numberOfManufacturers = TACSCMManager.NUMBER_OF_MANUFACTURERS;
  
  private int pingInterval = 0;
  private int nextPingRequest = 0;
  private int nextPingReport = 0;
  
  private Runnable afterTickTarget = new Runnable() {
    public void run()
    {
      handleAfterTick();
    }
  };
  
  private int marketReportInterval = 20;
  private int nextMarketReport = 0;
  private SimMarketReport marketReport;
  
  private BOMBundle bomBundle;
  private ComponentCatalog componentCatalog;
  
  private Hashtable factoryTable = new Hashtable();
  
  private boolean recoverAgents = false;
  
  public TACSCMSimulation(ConfigManager config)
  {
    super(config);
  }
  
  protected void setupSimulation() throws IllegalConfigurationException
  {
    ConfigManager config = getConfig();
    SimulationInfo info = getSimulationInfo();
    int seconds = info.getParameter("secondsPerDay", 0);
    this.secondsPerDay = seconds <= 1 ? config
        .getPropertyAsInt("game.secondsPerDay", secondsPerDay) : seconds;
    if (this.secondsPerDay < 1)
      this.secondsPerDay = 1;
    this.numberOfDays = info.getSimulationLength() / (this.secondsPerDay * 1000);
    this.daysBeforeVoid = config.getPropertyAsInt("customer.daysBeforeVoid", 5);
    this.marketReportInterval = config.getPropertyAsInt("game.marketReport.interval",
        marketReportInterval);
    if (marketReportInterval > 0)
    {
      this.nextMarketReport = marketReportInterval;
      this.marketReport = new SimMarketReport(0, nextMarketReport - 1);
    }
    else
    {
      this.nextMarketReport = Integer.MAX_VALUE;
    }
    
    int pingIntervalSeconds = config.getPropertyAsInt("ping.interval", 0);
    if (pingIntervalSeconds > 0)
    {
      this.pingInterval = pingIntervalSeconds / this.secondsPerDay;
      if (this.pingInterval <= 1)
      {
        this.pingInterval = 1;
      }
      this.nextPingRequest = this.pingInterval;
      this.nextPingReport = this.pingInterval + 1;
    }
    else
    {
      this.pingInterval = 0;
    }
    
    // The number of participants should be specifiable in the
    // simulation parameters. FIX THIS!!! FIX THIS!!!
    this.numberOfManufacturers = config.getPropertyAsInt("game.numberOfManufacturers",
        TACSCMManager.NUMBER_OF_MANUFACTURERS);
    
    log.info("TACSCM Simulation " + info.getSimulationID() + " is setting up...");
    int daysPerYear = config.getPropertyAsInt("daysPerYear", 365);
    bank = new Bank(this, daysPerYear, numberOfManufacturers);
    createBuiltinAgents("supplier", SUPPLIER, Supplier.class);
    createBuiltinAgents("customer", CUSTOMER, Customer.class);
    
    SimulationAgent[] suppliers = getAgents(SUPPLIER);
    log.info("Created " + (suppliers == null ? 0 : suppliers.length) + " suppliers");
    SimulationAgent[] customers = getAgents(CUSTOMER);
    log.info("Created " + (customers == null ? 0 : customers.length) + " customers");
    
    ComponentCatalog catalog = new ComponentCatalog();
    
    // Initialization of builtin agents. Should be fixed nicer. FIX THIS!!!
    if (suppliers != null)
    {
      for (int i = 0, n = suppliers.length; i < n; i++)
      {
        SimulationAgent supplier = suppliers[i];
        Supplier supplierAgent = (Supplier) supplier.getAgent();
        supplierAgent.simulationSetup(this, suppliers[i].getIndex());
        
        int[] components = supplierAgent.getComponents();
        if (components != null)
        {
          catalog.addSupplier(supplier.getAddress(), components);
        }
      }
    }
    if (customers != null)
    {
      for (int i = 0, n = customers.length; i < n; i++)
      {
        ((Customer) customers[i].getAgent()).simulationSetup(this, customers[i].getIndex());
      }
    }
    // End of initialization of builtin agents. FIX THIS!!!
    
    // Set product names and base price in the component catalog
    for (int i = 0, n = catalog.size(); i < n; i++)
    {
      int productID = catalog.getProductID(i);
      String name = config.getProperty("product." + productID + ".name");
      int basePrice = config.getPropertyAsInt("product." + productID + ".basePrice", 0);
      if (basePrice <= 0)
      {
        throw new IllegalConfigurationException("no base price for component " + productID + ": "
            + basePrice);
      }
      
      if (name != null)
      {
        catalog.setProductName(i, name);
      }
      catalog.setProductBasePrice(i, basePrice);
    }
    catalog.lock();
    this.componentCatalog = catalog;
    this.bomBundle = createBOMBundle(config, catalog, customers);
    
    // Quick validation of the configuration
    validateConfiguration();
    
    // Create proxy agents for all participants
    for (int i = 0, n = info.getParticipantCount(); i < n; i++)
    {
      // Must associate a user id with the agent to connect it with an
      // agent identity (might be external). Only MANUFACTURER
      // participants are allowed to join the simulation for now which
      // means the participant role does not need to be checked.
      createExternalAgent("man" + (i + 1), MANUFACTURER, info.getParticipantID(i));
    }
    if (info.getParticipantCount() < numberOfManufacturers)
    {
      createDummies("dummy.manufacturer", MANUFACTURER, numberOfManufacturers
          - info.getParticipantCount());
    }
    
    int minStorageCost = config.getPropertyAsInt("manufacturer.storageCostMin", 25);
    int maxStorageCost = config.getPropertyAsInt("manufacturer.storageCostMax", 50);
    if (minStorageCost < 0 || maxStorageCost < minStorageCost)
    {
      throw new IllegalArgumentException("illegal storage cost interval");
    }
    
    int storageCost = (int) Math.round(minStorageCost + getStorageRandom().nextDouble()
        * (maxStorageCost - minStorageCost));
    int factoryCapacity = config.getPropertyAsInt("manufacturer.capacity", 2000);
    SimulationAgent[] manufacturers = getAgents(MANUFACTURER);
    if (manufacturers != null)
    {
      for (int i = 0, n = manufacturers.length; i < n; i++)
      {
        SimulationAgent agent = manufacturers[i];
        String agentAddress = agent.getAddress();
        String factoryName = "factory" + (i + 1);
        Factory factory = new Factory(this, agent, factoryCapacity, daysPerYear, storageCost,
            this.bomBundle, this.componentCatalog);
        factoryTable.put(agentAddress, factory);
        registerAgent(factory, factoryName, FACTORY, -1);
        
        // Create bank account for the manufacturer
        bank.addAccount(agentAddress);
      }
    }
  }
  
  protected String getTimeUnitName()
  {
    return timeUnitName;
  }
  
  protected int getTimeUnitCount()
  {
    return numberOfDays;
  }
  
  protected void startSimulation()
  {
    LogWriter logWriter = getLogWriter();
    
    // Save the server configuration to the log.
    ConfigManager config = getConfig();
    ServerConfig serverConfig = new ServerConfig(config);
    logWriter.write(serverConfig);
    
    // Log the Bill-of-Materials and component catalogs
    logWriter.dataUpdated(TYPE_NONE, this.bomBundle);
    logWriter.dataUpdated(TYPE_NONE, this.componentCatalog);
    
    SimulationInfo simInfo = getSimulationInfo();
    StartInfo startInfo = createStartInfo(simInfo);
    startInfo.lock();
    
    logWriter.dataUpdated(TYPE_NONE, startInfo);
    
    sendToRole(SUPPLIER, startInfo);
    sendToRole(CUSTOMER, startInfo);
    
    // If a new agent arrives now it will be recovered
    recoverAgents = true;
    
    SimulationAgent[] factories = getAgents(FACTORY);
    long startTime = simInfo.getStartTime();
    if (factories != null)
    {
      for (int i = 0, n = factories.length; i < n; i++)
      {
        SimulationAgent factoryAgent = factories[i];
        Factory factory = (Factory) factoryAgent.getAgent();
        SimulationAgent agent = factory.getOwner();
        StartInfo info = createManufacturerInfo(simInfo, factory);
        logWriter.message(COORDINATOR_INDEX, agent.getIndex(), info, startTime);
        sendMessage(new Message(agent.getAddress(), info));
      }
    }
    
    // Send the BOM bundle to the manufacturer and customers
    sendToRole(MANUFACTURER, this.bomBundle);
    sendToRole(CUSTOMER, this.bomBundle);
    // Send the component catalog to the manufacturer and customers
    sendToRole(MANUFACTURER, this.componentCatalog);
    sendToRole(CUSTOMER, this.componentCatalog);
    sendToRole(SUPPLIER, this.componentCatalog);
    
    startTickTimer(startTime, secondsPerDay * 1000);
    
    logWriter.commit();
  }
  
  protected BOMBundle createBOMBundle(ConfigManager config, ComponentCatalog catalog,
      SimulationAgent[] customers) throws IllegalConfigurationException
  {
    // Create a simple Bill of materials for the fake setup
    // Will just contain 4 PC's to produce
    BOMBundle bom = new BOMBundle();
    int[] products = parseProducts(config.getProperty("bom.products"));
    if (products == null)
    {
      throw new IllegalConfigurationException("no BOM specified");
    }
    for (int i = 0, n = products.length; i < n; i++)
    {
      int id = products[i];
      int[] components = parseProducts(config.getProperty("bom." + id + ".components"));
      int cycles = config.getPropertyAsInt("bom." + id + ".cyclesRequired", 0);
      String name = config.getProperty("bom." + id + ".name");
      if (components == null)
      {
        throw new IllegalConfigurationException("no components for product " + id);
      }
      if (cycles <= 0)
      {
        throw new IllegalConfigurationException("no assembly cycles for " + "product " + id);
      }
      // Calculate base price
      int basePrice = 0;
      for (int j = 0, m = components.length; j < m; j++)
      {
        int index = catalog.getIndexFor(components[j]);
        if (index < 0)
        {
          throw new IllegalConfigurationException("component " + components[j] + " for product "
              + id + " not found in catalog");
        }
        int price = catalog.getProductBasePrice(index);
        if (price <= 0)
        {
          throw new IllegalConfigurationException("no base price for " + "component "
              + components[j] + " in catalog");
        }
        basePrice += price;
      }
      bom.addBOM(id, cycles, components, null, name, basePrice);
    }
    // Initialization of product segments. Should be fixed nicer. FIX THIS!!!
    if (customers != null && customers.length > 0)
    {
      // Add product segments
      Customer c = (Customer) customers[0].getAgent();
      for (int i = 0, n = c.getProductSegmentCount(); i < n; i++)
      {
        bom.addSegment(c.getProductSegmentName(i), c.getProductSegment(i));
      }
    }
    // End of initialization of product segments. FIX THIS!!!
    bom.lock();
    return bom;
  }
  
  private StartInfo createStartInfo(SimulationInfo info)
  {
    StartInfo startInfo = new StartInfo(info.getSimulationID(), info.getStartTime(), info
        .getSimulationLength(), secondsPerDay);
    startInfo.setAttribute("customer.daysBeforeVoid", daysBeforeVoid);
    return startInfo;
  }
  
  private StartInfo createManufacturerInfo(SimulationInfo info, Factory factory)
  {
    StartInfo startInfo = createStartInfo(info);
    startInfo.setAttribute("factory.address", factory.getAddress());
    startInfo.setAttribute("factory.capacity", factory.getCapacity());
    startInfo.setAttribute("factory.storageCost", factory.getStorageCost());
    // Bank debt interest rate
    startInfo.setAttribute("bank.interestRate", (int) bank.getDebtInterestRate());
    // Bank deposit interest rate
    startInfo.setAttribute("bank.depositInterestRate", (int) bank.getDepositInterestRate());
    SimulationAgent[] manufacturers = getAgents(MANUFACTURER);
    if (manufacturers != null)
    {
      startInfo.setAttribute("manufacturer.count", manufacturers.length);
      for (int i = 0, n = manufacturers.length; i < n; i++)
      {
        startInfo.setAttribute("manufacturer." + (i + 1), manufacturers[i].getName());
      }
    }
    else
    {
      startInfo.setAttribute("manufacturer.count", 0);
    }
    return startInfo;
  }
  
  private void validateConfiguration() throws IllegalConfigurationException
  {
    for (int i = 0, n = bomBundle.size(); i < n; i++)
    {
      int[] components = bomBundle.getComponents(i);
      if (components != null)
      {
        for (int j = 0, m = components.length; j < m; j++)
        {
          if (componentCatalog.getIndexFor(components[j]) < 0)
          {
            throw new IllegalConfigurationException("component " + components[j]
                + " is missing in " + "component catalog");
          }
        }
      }
    }
    if (daysBeforeVoid <= 0)
    {
      throw new IllegalConfigurationException("days before void for customers "
          + "must be positive: " + daysBeforeVoid);
    }
  }
  
  // Package protected to allow Supplier to use this methods for
  // parsing its components
  int[] parseProducts(String names)
  {
    if (names == null)
    {
      return null;
    }
    
    try
    {
      StringTokenizer tok = new StringTokenizer(names, ", \t");
      int len = tok.countTokens();
      if (len > 0)
      {
        int[] products = new int[len];
        for (int i = 0; i < len; i++)
        {
          products[i] = Integer.parseInt(tok.nextToken());
        }
        return products;
      }
    }
    catch (Exception e)
    {
      log.log(Level.SEVERE, "could not parse products '" + names + '\'', e);
    }
    return null;
  }
  
  protected void prepareStopSimulation()
  {
    // No longer any need to recover agents
    recoverAgents = false;
    // The bank needs to send its final account statuses
    bank.sendBankStatusToAll();
    
    // Send the final simulation status
    int millisConsumed = (int) (getServerTime() - getSimulationInfo().getEndTime());
    SimulationStatus status = new SimulationStatus(numberOfDays, millisConsumed, true);
    // SimulationStatus only to manufacturers or all agents??? FIX THIS!!! TODO
    sendToRole(MANUFACTURER, status);
  }
  
  protected void completeStopSimulation()
  {
    LogWriter writer = getLogWriter();
    writer.commit();
  }
  
  /**
   * Called when entering a new time unit similar to time listeners but this
   * method is guaranteed to be called before the time listeners.
   * 
   * @param timeUnit
   *          the current time unit
   */
  protected void nextTimeUnitStarted(int timeUnit)
  {
    this.currentTimeUnit = timeUnit;
    
    LogWriter writer = getLogWriter();
    writer.nextTimeUnit(timeUnit, getServerTime());
    
    if (timeUnit >= numberOfDays)
    {
      // Time to stop the simulation
      requestStopSimulation();
      
    }
    else
    {
      // Let the bank send their first messages
      bank.addInterests(timeUnit);
      bank.sendBankStatusToAll();
      
      // Send market reports
      if (timeUnit >= nextMarketReport && marketReportInterval > 0)
      {
        nextMarketReport += marketReportInterval;
        sendToRole(MANUFACTURER, marketReport.createMarketReport());
        marketReport = new SimMarketReport(timeUnit, nextMarketReport - 1);
      }
    }
  }
  
  /**
   * Called when a new time unit has begun similar to time listeners but this
   * method is guaranteed to be called after the time listeners.
   * 
   * @param timeUnit
   *          the current time unit
   */
  protected void nextTimeUnitFinished(int timeUnit)
  {
    if (timeUnit < numberOfDays)
    {
      int millisConsumed = (int) (getServerTime() - getSimulationInfo().getStartTime() - timeUnit
          * secondsPerDay * 1000);
      SimulationStatus status = new SimulationStatus(timeUnit, millisConsumed);
      sendToRole(MANUFACTURER, status);
    }
    
    invokeLater(afterTickTarget);
  }
  
  /**
   * Called each day after all morning messages has been sent.
   */
  private void handleAfterTick()
  {
    if (pingInterval > 0 && currentTimeUnit < numberOfDays)
    {
      if (currentTimeUnit >= nextPingRequest)
      {
        nextPingRequest += pingInterval;
        
        SimulationAgent[] manufacturers = getAgents(MANUFACTURER);
        if (manufacturers != null)
        {
          for (int i = 0, n = manufacturers.length; i < n; i++)
          {
            manufacturers[i].requestPing();
          }
        }
      }
      
      if (currentTimeUnit >= nextPingReport)
      {
        nextPingReport += pingInterval;
        
        SimulationAgent[] manufacturers = getAgents(MANUFACTURER);
        if (manufacturers != null)
        {
          EventWriter writer = getEventWriter();
          synchronized (writer)
          {
            for (int i = 0, n = manufacturers.length; i < n; i++)
            {
              SimulationAgent sa = manufacturers[i];
              if (sa.getPingCount() > 0)
              {
                int index = sa.getIndex();
                writer.dataUpdated(index, DU_NETWORK_AVG_RESPONSE, sa.getAverageResponseTime());
                writer.dataUpdated(index, DU_NETWORK_LAST_RESPONSE, sa.getLastResponseTime());
              }
            }
          }
        }
      }
    }
    
    // Since all day start handling now is finished for this day and
    // the manufacturer agents will have some time to respond when
    // requesting pings, it is a good time to do some memory
    // management.
    System.gc();
    System.gc();
  }
  
  protected int getAgentRecoverMode(SimulationAgent agent)
  {
    if (!recoverAgents)
    {
      return RECOVERY_NONE;
    }
    if (agent.hasAgentBeenActive())
    {
      // The agent has been active and we must use the simulation
      // thread to retrieve all active orders (the information may
      // only be accessed using the simulation thread)
      return RECOVERY_AFTER_NEXT_TICK;
    }
    // The agent has not been active i.e. not sent any messages in
    // this simulation. This means the agent can not have any active
    // orders and only the startup messages needs to be sent. This can
    // be done using any thread.
    recoverAgent(agent, false);
    return RECOVERY_NONE;
  }
  
  protected void recoverAgent(SimulationAgent agent)
  {
    recoverAgent(agent, true);
  }
  
  private void recoverAgent(SimulationAgent agent, boolean recoverOrders)
  {
    if (recoverAgents)
    {
      String agentName = agent.getName();
      Factory factory = getFactoryForAgent(agent);
      log.warning("recovering agent " + agentName);
      
      if (factory != null)
      {
        String agentAddress = agent.getAddress();
        StartInfo info = createManufacturerInfo(getSimulationInfo(), factory);
        sendMessage(new Message(agentAddress, info));
        
        if (agent.isSupported(AgentChannel.ACTIVE_ORDERS))
        {
          ActiveOrders activeOrders = new ActiveOrders(currentTimeUnit);
          if (recoverOrders)
          {
            recoverActiveOrders(agentAddress, activeOrders);
          }
          sendMessage(new Message(agentAddress, activeOrders));
        }
        
        sendMessage(new Message(agentAddress, bomBundle));
        sendMessage(new Message(agentAddress, componentCatalog));
        
      }
      else
      {
        // No use to recover the agent at all since no factory was found
        log.severe("could not find factory when recovering agent " + agentName);
      }
    }
  }
  
  private void recoverActiveOrders(String agentAddress, ActiveOrders activeOrders)
  {
    SimulationAgent[] customers = getAgents(CUSTOMER);
    if (customers != null)
    {
      for (int i = 0, n = customers.length; i < n; i++)
      {
        Agent agent = customers[i].getAgent();
        if (agent instanceof Customer)
        {
          ((Customer) agent).addActiveOrders(agentAddress, activeOrders);
        }
      }
    }
    SimulationAgent[] suppliers = getAgents(SUPPLIER);
    if (suppliers != null)
    {
      for (int i = 0, n = suppliers.length; i < n; i++)
      {
        Agent agent = suppliers[i].getAgent();
        if (agent instanceof Supplier)
        {
          ((Supplier) agent).addActiveOrders(agentAddress, activeOrders);
        }
      }
    }
  }
  
  protected void messageReceived(Message message)
  {
    log.warning("received (ignoring) " + message);
  }
  
  public static String getSimulationRoleName(int simRole)
  {
    return simRole >= 0 && simRole < ROLE_NAME.length ? ROLE_NAME[simRole] : null;
  }
  
  public static int getSimulationRole(String role)
  {
    return ArrayUtils.indexOf(ROLE_NAME, role);
  }
  
  // -------------------------------------------------------------------
  // Logging handling
  // -------------------------------------------------------------------
  
  protected boolean validateMessage(SimulationAgent receiverAgent, Message message)
  {
    String sender = message.getSender();
    SimulationAgent senderAgent = getAgent(sender);
    int senderIndex;
    if (senderAgent == null)
    {
      // Messages from or the coordinator or administration are always
      // allowed.
      senderIndex = COORDINATOR_INDEX;
      
    }
    else if (senderAgent.getRole() == receiverAgent.getRole())
    {
      // No two agents with the same role in the simulation may
      // communicate with each other. A simple security measure to
      // avoid manufacturer agents to communicate or deceive each
      // other.
      return false;
      
    }
    else
    {
      senderIndex = senderAgent.getIndex();
    }
    
    int receiverIndex = receiverAgent.getIndex();
    Transportable content = message.getContent();
    Class contentType = content.getClass();
    if (logContentType(contentType))
    {
      LogWriter writer = getLogWriter();
      writer.message(senderIndex, receiverIndex, content, getServerTime());
      writer.commit();
    }
    
    int type = getContentType(contentType);
    if (type != TYPE_NONE)
    {
      getEventWriter().interaction(senderIndex, receiverIndex, type);
    }
    return true;
  }
  
  protected boolean validateMessageToRole(SimulationAgent senderAgent, int role,
      Transportable content)
  {
    // Only customer broadcast of RFQBundle to manufacturers are
    // allowed for now.
    if (role == MANUFACTURER && senderAgent.getRole() == CUSTOMER
        && content.getClass() == RFQBundle.class)
    {
      logToRole(senderAgent.getIndex(), role, content);
      return true;
    }
    return false;
  }
  
  protected boolean validateMessageToRole(int role, Transportable content)
  {
    // Broadcasts from the coordinator are always allowed
    logToRole(COORDINATOR_INDEX, role, content);
    return true;
  }
  
  private void logToRole(int senderIndex, int role, Transportable content)
  {
    // Log this broadcast
    Class contentType = content.getClass();
    if (logContentType(contentType))
    {
      LogWriter writer = getLogWriter();
      writer.messageToRole(senderIndex, role, content, getServerTime());
      writer.commit();
    }
    
    int type = getContentType(contentType);
    if (type != TYPE_NONE)
    {
      getEventWriter().interactionWithRole(senderIndex, role, type);
    }
  }
  
  private boolean logContentType(Class type)
  {
    if (type == BOMBundle.class)
    {
      return false;
    }
    else if (type == ComponentCatalog.class)
    {
      return false;
    }
    else if (type == StartInfo.class)
    {
      return false;
    }
    return true;
  }
  
  private int getContentType(Class type)
  {
    if (type == DeliveryNotice.class)
    {
      return TYPE_DELIVERY;
    }
    else if (type == OrderBundle.class)
    {
      return TYPE_ORDER;
    }
    else if ((type == RFQBundle.class) || (type == OfferBundle.class))
    {
      return TYPE_NEGOTIATION;
    }
    else
    {
      return TYPE_NONE;
    }
  }
  
  // -------------------------------------------------------------------
  // API to TACSCM builtin agents (trusted components)
  // -------------------------------------------------------------------
  
  final int getNumberOfManufacturers()
  {
    return numberOfManufacturers;
  }
  
  final String getAgentName(String agentAddress)
  {
    SimulationAgent agent = getAgent(agentAddress);
    return agent != null ? agent.getName() : agentAddress;
  }
  
  final void transaction(String supplier, String customer, int orderID, long amount)
  {
    // log.finer("Transacted " + amount + " from " + customer + " to " +
    // supplier);
    
    SimulationAgent supplierAgent = getAgent(supplier);
    SimulationAgent customerAgent = getAgent(customer);
    
    if (supplierAgent != null && supplierAgent.getRole() == MANUFACTURER)
    {
      bank.deposit(supplier, amount);
    }
    if (customerAgent != null && customerAgent.getRole() == MANUFACTURER)
    {
      bank.withdraw(customer, amount);
    }
    
    int supplierIndex = supplierAgent != null ? supplierAgent.getIndex() : COORDINATOR_INDEX;
    int customerIndex = customerAgent != null ? customerAgent.getIndex() : COORDINATOR_INDEX;
    LogWriter writer = getLogWriter();
    synchronized (writer)
    {
      writer.node("transaction").attr("supplier", supplierIndex).attr("customer", customerIndex)
          .attr("orderID", orderID).attr("amount", amount).endNode("transaction");
    }
  }
  
  final void penalty(String customer, String supplier, int orderID, int penalty, boolean isOrderVoid)
  {
    // log.finer("Penalty " + penalty + " from " + supplier + " to " +
    // customer);
    
    SimulationAgent supplierAgent = getAgent(supplier);
    SimulationAgent customerAgent = getAgent(customer);
    if (supplierAgent != null && supplierAgent.getRole() == MANUFACTURER)
    {
      bank.penalty(supplier, customer, orderID, penalty);
    }
    if (customerAgent != null && customerAgent.getRole() == MANUFACTURER)
    {
      // Only the one receiving the penalty gets the penalty log for now.
      bank.deposit(customer, penalty);
    }
    
    int supplierIndex = supplierAgent != null ? supplierAgent.getIndex() : COORDINATOR_INDEX;
    int customerIndex = customerAgent != null ? customerAgent.getIndex() : COORDINATOR_INDEX;
    LogWriter writer = getLogWriter();
    synchronized (writer)
    {
      writer.node("penalty").attr("supplier", supplierIndex).attr("customer", customerIndex).attr(
          "orderID", orderID).attr("amount", penalty);
      if (isOrderVoid)
      {
        writer.attr("orderVoid", 1);
      }
      writer.endNode("penalty");
    }
  }
  
  final void claimStorageCost(SimulationAgent owner, long cost)
  {
    bank.withdraw(owner.getAddress(), cost);
    
    LogWriter writer = getLogWriter();
    synchronized (writer)
    {
      writer.node("storageCost").attr("agent", owner.getIndex()).attr("amount", cost).endNode(
          "storageCost");
    }
  }
  
  final void addDeniedDelivery(String supplier, String customer, int orderID)
  {
    bank.addDeniedDelivery(supplier, customer, orderID);
  }
  
  // Customers are responsible for reporting demand
  final void addDemandInfo(int productID, int quantityRequested, int quantityOrdered,
      int averageUnitPrice)
  {
    if (marketReport != null)
    {
      marketReport.addDemandForProduct(productID, quantityRequested, quantityOrdered,
          averageUnitPrice);
    }
  }
  
  // Customers sends price reports
  final void sendPriceReport(PriceReport priceReport)
  {
    sendToRole(MANUFACTURER, priceReport);
  }
  
  private Factory getFactoryForAgent(String agentName)
  {
    return (Factory) factoryTable.get(agentName);
  }
  
  private Factory getFactoryForAgent(SimulationAgent ownerAgent)
  {
    // SHOULD BE OPTIMIZED. FIX THIS!!!
    SimulationAgent[] factories = getAgents(FACTORY);
    if (factories != null)
    {
      for (int i = 0, n = factories.length; i < n; i++)
      {
        Factory f = (Factory) factories[i].getAgent();
        if (ownerAgent == f.getOwner())
        {
          return f;
        }
      }
    }
    return null;
  }
  
  // Suppliers are responsible for reporting supply info
  // final void addSupplyProduced(int productID, int quantityProduced) {
  // if (marketReport != null) {
  // marketReport.addSupplyProduced(productID, quantityProduced);
  // }
  // }
  
  // Suppliers are responsible for reporting supply info
  final void addSupplyOrdered(int productID, int quantityOrdered, int averageUnitPrice)
  {
    if (marketReport != null)
    {
      marketReport.addSupplyOrdered(productID, quantityOrdered, averageUnitPrice);
    }
  }
  
  // Suppliers are responsible for reporting supply info
  final void addSupplyDelivered(int productID, int quantityDelivered)
  {
    if (marketReport != null)
    {
      marketReport.addSupplyDelivered(productID, quantityDelivered);
    }
  }
  
  // Suppliers are responsible for reporting supplier capacities for
  // their production lines
  final void sendSupplierCapacity(String supplier, int productID, int capacity)
  {
    if (marketReport != null)
    {
      marketReport.addSupplierCapacity(supplier, productID, capacity);
    }
  }
  
  // Suppliers are responsible for reporting reputations for their customers
  final void sendSupplierReputation(String supplierAddress, String agentAddress, double reputation)
  {
    SimulationAgent supplier = getAgent(supplierAddress);
    SimulationAgent agent = getAgent(agentAddress);
    if (supplier == null)
    {
      log.severe("supplier " + supplierAddress + " not found for reputation update");
    }
    else if (agent == null)
    {
      log.severe("manufacturer " + supplierAddress + " not found for reputation update");
    }
    else
    {
      LogWriter writer = getLogWriter();
      synchronized (writer)
      {
        writer.node("reputation").attr("supplier", supplier.getIndex()).attr("customer",
            agent.getIndex()).attr("reputation", (float) reputation).endNode("reputation");
      }
    }
  }
  
  // Suppliers delivers to manufacturer factories
  final boolean deliverToFactory(String agentName, int date, DeliveryNotice notice)
  {
    Factory factory = getFactoryForAgent(agentName);
    if (factory != null)
    {
      // Make sure the delivery notice no longer can be changed
      notice.lock();
      factory.delivery(date, notice);
      return true;
    }
    return false;
  }
  
  final boolean deliverToCustomer(String customer, int date, DeliveryNotice notice)
  {
    SimulationAgent a = getAgent(customer);
    if (a != null)
    {
      Agent agent = a.getAgent();
      if (agent instanceof Customer)
      {
        // Make sure the delivery notice no longer can be changed
        notice.lock();
        
        ((Customer) agent).delivery(date, notice);
        
        // Need to send an interaction event because no delivery
        // notice is sent to anyone.
        SimulationAgent sender = getAgent(notice.getSupplier());
        if (sender != null)
        {
          getEventWriter().interaction(sender.getIndex(), a.getIndex(), TYPE_DELIVERY);
        }
        return true;
      }
    }
    return false;
  }
  
  // -------------------------------------------------------------------
  //  API to Bank to allow it to send bank statuses
  // -------------------------------------------------------------------
  
  final void sendBankStatus(String agentName, BankStatus status)
  {
    sendMessage(agentName, status);
  }
  
  final void sendInterestInfo(String agentName, long amount)
  {
    SimulationAgent agent = getAgent(agentName);
    if (agent != null)
    {
      LogWriter writer = getLogWriter();
      synchronized (writer)
      {
        writer.node("interest").attr("agent", agent.getIndex()).attr("amount", amount).endNode(
            "interest");
      }
      
    }
    else
    {
      log.severe("could not find agent " + agentName + " for interest info");
    }
  }
  
} // TACSCMSimulation
