package edu.umich.eecs.tac.sim;


import java.util.logging.Logger;
import java.util.*;

import com.botbox.util.ArrayUtils;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.props.ServerConfig;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.sim.LogWriter;
import se.sics.tasim.sim.Simulation;
import se.sics.tasim.sim.SimulationAgent;
import se.sics.tasim.aw.Message;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.Product;


/**
 * @author Lee Callender, Patrick Jordan
 */
public class TACAASimulation extends Simulation implements TACAAConstants {
    private Bank bank;
    private String timeUnitName = "Day";
    private int currentTimeUnit = 0;
    private int secondsPerDay = 10;
    private int numberOfDays = 60;

    private int numberOfAdvertisers = TACAAManager.NUMBER_OF_ADVERTISERS;

    private int pingInterval = 0;
    private int nextPingRequest = 0;
    private int nextPingReport = 0;

    private RetailCatalog retailCatalog;
    private String[] manufacturers;
    private String[] components;
    private Map<String, AdvertiserInfo> advertiserInfoMap;

    private Runnable afterTickTarget = new Runnable() {
        public void run() {
            handleAfterTick();
        }
    };

    private boolean recoverAgents = false;

    private static final Logger log = Logger.getLogger(TACAASimulation.class.getName());

    public TACAASimulation(ConfigManager config) {
        super(config);

        advertiserInfoMap = new HashMap<String, AdvertiserInfo>();
    }

    protected void setupSimulation() throws IllegalConfigurationException {
        ConfigManager config = getConfig();
        SimulationInfo info = getSimulationInfo();

        int seconds = info.getParameter("secondsPerDay", 0);
        this.secondsPerDay = seconds <= 1
                ? config.getPropertyAsInt("game.secondsPerDay", secondsPerDay)
                : seconds; //SimulationInfo gets priority over ConfigManager
        if (this.secondsPerDay < 1) this.secondsPerDay = 1;

        this.numberOfDays =     //Make sure this is correct.
                info.getSimulationLength() / (this.secondsPerDay * 1000);
        /*this.daysBeforeVoid =
          config.getPropertyAsInt("customer.daysBeforeVoid", 5);
        this.marketReportInterval =
          config.getPropertyAsInt("game.marketReport.interval",
                      marketReportInterval);
        if (marketReportInterval > 0) {
          this.nextMarketReport = marketReportInterval;
          this.marketReport = new SimMarketReport(0, nextMarketReport - 1);
        } else {
          this.nextMarketReport = Integer.MAX_VALUE;
        }*/

        int pingIntervalSeconds =
                config.getPropertyAsInt("ping.interval", 0);
        if (pingIntervalSeconds > 0) {
            this.pingInterval = pingIntervalSeconds / this.secondsPerDay;
            if (this.pingInterval <= 1) {
                this.pingInterval = 1;
            }
            this.nextPingRequest = this.pingInterval;
            this.nextPingReport = this.pingInterval + 1;
        } else {
            this.pingInterval = 0;
        }

        // The number of participants should be specifiable in the
        // simulation parameters. FIX THIS!!! FIX THIS!!!
        this.numberOfAdvertisers =
                config.getPropertyAsInt("game.numberOfAdvertisers",
                        TACAAManager.NUMBER_OF_ADVERTISERS);

        log.info("TACAA Simulation " + info.getSimulationID()
                + " is setting up...");

        //Initialize in-game agents, bank etc.
        bank = new Bank(this, numberOfAdvertisers);
        createBuiltinAgents("users", USERS, Users.class);
        createBuiltinAgents("publisher", PUBLISHER, Publisher.class);


        SimulationAgent[] publishers = getAgents(PUBLISHER);
        log.info("Created " + (publishers == null ? 0 : publishers.length) + " publishers");
        SimulationAgent[] users = getAgents(USERS);
        log.info("Created " + (users == null ? 0 : users.length) + " users");

        if(publishers != null){
          for(int i = 0, n = publishers.length; i < n; i++){
            SimulationAgent publisher = publishers[i];
            Publisher publisherAgent = (Publisher) publisher.getAgent();
            publisherAgent.simulationSetup(this, publishers[i].getIndex());
          }
        }
        if(users != null){
          for(int i = 0, n = users.length; i < n; i++){
            SimulationAgent user = users[i];
            Users userAgent = (Users) user.getAgent();
            userAgent.simulationSetup(this, users[i].getIndex());
          }
        }
        
        this.retailCatalog = createRetailCatalog();
        this.manufacturers = createManufacturers();
        this.components = createComponents();

        validateConfiguration();

        // Create proxy agents for all participants
        for (int i = 0, n = info.getParticipantCount(); i < n; i++) {
            // Must associate a user id with the agent to connect it with an
            // agent identity (might be external). Only ADVERTISER participants
            // are allowed to join the simulation for now which means the
            // participant role does not need to be checked
            createExternalAgent("adv" + (i + 1), ADVERTISER, info.getParticipantID(i));
        }
        if (info.getParticipantCount() < numberOfAdvertisers) {
            createDummies("dummy.advertiser", ADVERTISER,
                    numberOfAdvertisers - info.getParticipantCount());
        }

        initializeAdvertisers();
    }


    private void initializeAdvertisers() {
        ConfigManager config = getConfig();

        Random r = new Random();

        SimulationAgent[] publishers = getAgents(PUBLISHER);


        String publisherAddress = "publisher";
        if(publishers!=null && publishers.length>0 && publishers[0]!=null) {
            publisherAddress = publishers[0].getAddress();
        }



        int highValue = config.getPropertyAsInt("advertiser.capacity.high",0);
        int medValue = config.getPropertyAsInt("advertiser.capacity.med",0);
        int lowValue = config.getPropertyAsInt("advertiser.capacity.low",0);
        
        int highCount = config.getPropertyAsInt("advertiser.capacity.highCount",0);
        int lowCount = config.getPropertyAsInt("advertiser.capacity.lowCount",0);

        double manufacturerBonus = config.getPropertyAsDouble("advertiser.specialization.manufacturerBonus",0.0);
        double componentBonus = config.getPropertyAsDouble("advertiser.specialization.componentBonus",0.0);
        double decayRate = config.getPropertyAsDouble("advertiser.capacity.decay_rate",1.0);
        int window = config.getPropertyAsInt("advertiser.capacity.window",7);

        //Initialize advertisers..
        SimulationAgent[] advertisers = getAgents(ADVERTISER);
        if (advertisers != null) {


            // Create capacities and randomize
            int[] capacities = new int[advertisers.length];

            for(int i = 0; i < highCount && i < capacities.length; i++) {
                capacities[i] = highValue;
            }

            for(int i = highCount; i < highCount+lowCount && i < capacities.length; i++) {
                capacities[i] = lowValue;
            }

            for(int i = highCount+lowCount; i < capacities.length; i++) {
                capacities[i] = medValue;
            }


            for(int i = 0; i < capacities.length; i++) {
                int rindex = i + r.nextInt(capacities.length-i);

                int sw = capacities[i];
                capacities[i] = capacities[rindex];
                capacities[rindex] = sw;

            }


            for (int i = 0, n = advertisers.length; i < n; i++) {
                SimulationAgent agent = advertisers[i];
                String agentAddress = agent.getAddress();

                AdvertiserInfo advertiserInfo = new AdvertiserInfo();
                advertiserInfo.setAdvertiserId(agentAddress);
                advertiserInfo.setPublisherId(publisherAddress);
                advertiserInfo.setDistributionCapacity(capacities[i]);
                advertiserInfo.setDecayRate(decayRate);
                advertiserInfo.setComponentSpecialty(components[r.nextInt(components.length)]);
                advertiserInfo.setComponentBonus(componentBonus);
                advertiserInfo.setManufacturerSpecialty(manufacturers[r.nextInt(manufacturers.length)]);
                advertiserInfo.setManufacturerBonus(manufacturerBonus);
                advertiserInfo.setDistributionWindow(window);

                advertiserInfoMap.put(agentAddress,advertiserInfo);

                // Create bank account for the advertiser
                bank.addAccount(agentAddress);
            }
        }
    }

    protected String getTimeUnitName() {
        return timeUnitName;
    }

    protected int getTimeUnitCount() {
        return numberOfDays;
    }

    protected void startSimulation() {
        LogWriter logWriter = getLogWriter();

        // Save the server configuration to the log.
        ConfigManager config = getConfig();
        ServerConfig serverConfig = new ServerConfig(config);
        logWriter.write(serverConfig);

        //Log the retailCatalog
        logWriter.dataUpdated(TYPE_NONE, this.retailCatalog);
        // logWriter.dataUpdated(TYPE_NONE, this.componentCatalog);

        SimulationInfo simInfo = getSimulationInfo();
        StartInfo startInfo = createStartInfo(simInfo);
        startInfo.lock();

        logWriter.dataUpdated(TYPE_NONE, startInfo);

        sendToRole(PUBLISHER, startInfo);
        sendToRole(USERS, startInfo);
        sendToRole(ADVERTISER, startInfo);  //startInfo may want to contain more information for advertiser

        // If a new agent arrives now it will be recovered
        recoverAgents = true;

        for(Map.Entry<String,AdvertiserInfo> entry : advertiserInfoMap.entrySet()) {
            sendMessage(entry.getKey(), entry.getValue());
        }


        // Send the component catalog to the manufacturer and customers
        sendToRole(PUBLISHER, this.retailCatalog);
        sendToRole(USERS, this.retailCatalog);
        sendToRole(ADVERTISER, this.retailCatalog);

      
        startTickTimer(simInfo.getStartTime(), secondsPerDay * 1000);

        logWriter.commit();

    }

    private StartInfo createStartInfo(SimulationInfo info) {
        StartInfo startInfo = new StartInfo(info.getSimulationID(),
                info.getStartTime(),
                info.getSimulationLength(),
                secondsPerDay);
        return startInfo;
    }

    /**
     * Notification when this simulation is preparing to stop. Called after the
     * agents have been stopped but still can receive messages.
     */
    protected void prepareStopSimulation() {
        // No longer any need to recover agents
        recoverAgents = false;
        // The bank needs to send its final account statuses
        bank.sendBankStatusToAll();

        // Send the final simulation status
        int millisConsumed = (int)
                (getServerTime() - getSimulationInfo().getEndTime());
        SimulationStatus status =
                new SimulationStatus(numberOfDays, millisConsumed, true);
        // SimulationStatus only to manufacturers or all agents??? FIX THIS!!!
        sendToRole(ADVERTISER, status);

    }

    /**
     * Notification when this simulation has been stopped. Called after the agents
     * shutdown.
     */
    protected void completeStopSimulation() {
        LogWriter writer = getLogWriter();
        writer.commit();
    }

    /**
     * Called when entering a new time unit similar to time listeners
     * but this method is guaranteed to be called before the time
     * listeners.
     *
     * @param timeUnit the current time unit
     */
    protected void nextTimeUnitStarted(int timeUnit) {
        this.currentTimeUnit = timeUnit;

        LogWriter writer = getLogWriter();
        writer.nextTimeUnit(timeUnit, getServerTime());

        if (timeUnit >= numberOfDays) {
            // Time to stop the simulation
            requestStopSimulation();
        } else {
            // Let the bank send their first messages
            bank.sendBankStatusToAll();

            for(SimulationAgent agent : getAgents(PUBLISHER) ) {
                if(agent.getAgent() instanceof Publisher) {
                    Publisher publisher = (Publisher)agent.getAgent();
                    publisher.sendQueryReportsToAll();
                }
            }

            for(SimulationAgent agent : getAgents(USERS) ) {
                if(agent.getAgent() instanceof Users) {
                    Users users = (Users)agent.getAgent();
                    users.sendSalesReportsToAll();
                }
            }
        }
    }


    
    /**
     * Called when a new time unit has begun similar to time listeners
     * but this method is guaranteed to be called after the time
     * listeners.
     *
     * @param timeUnit the current time unit
     */
    protected void nextTimeUnitFinished(int timeUnit) {
        if (timeUnit < numberOfDays) {
            int millisConsumed = (int)
                    (getServerTime() - getSimulationInfo().getStartTime()
                            - timeUnit * secondsPerDay * 1000);

            SimulationStatus status = new SimulationStatus(timeUnit, millisConsumed);
            sendToRole(ADVERTISER, status);
        }

        invokeLater(afterTickTarget);  //?
    }


    /**
     * Called each day after all morning messages has been sent.
     */
    private void handleAfterTick() {
        if (pingInterval > 0 && currentTimeUnit < numberOfDays) {
            if (currentTimeUnit >= nextPingRequest) {
                nextPingRequest += pingInterval;

                SimulationAgent[] advertisers = getAgents(ADVERTISER);
                if (advertisers != null) {
                    for (int i = 0, n = advertisers.length; i < n; i++) {
                        advertisers[i].requestPing();
                    }
                }
            }

            if (currentTimeUnit >= nextPingReport) {
                nextPingReport += pingInterval;

                SimulationAgent[] advertisers = getAgents(ADVERTISER);
                if (advertisers != null) {
                    EventWriter writer = getEventWriter();
                    synchronized (writer) {
                        for (int i = 0, n = advertisers.length; i < n; i++) {
                            SimulationAgent sa = advertisers[i];
                            if (sa.getPingCount() > 0) {
                                int index = sa.getIndex();
                                writer.dataUpdated(index, DU_NETWORK_AVG_RESPONSE,
                                        sa.getAverageResponseTime());
                                writer.dataUpdated(index, DU_NETWORK_LAST_RESPONSE,
                                        sa.getLastResponseTime());
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


    private String[] createManufacturers() {
        return getConfig().getPropertyAsArray("manufacturer.names");    
    }

    private String[] createComponents() {
        return getConfig().getPropertyAsArray("component.names");
    }

    private RetailCatalog createRetailCatalog() {
        ConfigManager config = getConfig();

        RetailCatalog catalog = new RetailCatalog();

        String[] skus = config.getPropertyAsArray("catalog.sku");

        for(String sku : skus) {
            String manufacturer = config.getProperty(String.format("catalog.%s.manufacturer",sku));
            String component = config.getProperty(String.format("catalog.%s.component",sku));
            double salesProfit = config.getPropertyAsDouble(String.format("catalog.%s.salesProfit",sku),0.0);

            Product product = new Product(manufacturer,component);

            catalog.setSalesProfit(product,salesProfit);
        }                

        catalog.lock();

        return catalog;
    }

    private void validateConfiguration() throws IllegalConfigurationException {
        //TODO: do validation
    }

    /**
     * Called whenever an external agent has logged in and needs to recover its
     * state. The simulation should respond with the current recover mode (none,
     * immediately, or after next time unit). This method should return
     * <code>RECOVERY_NONE</code> if the simulation not yet have been started.
     * <p/>
     * <p/>
     * The simulation might recover the agent using this method if recovering the
     * agent can be done using the agent communication thread. In that case
     * <code>RECOVERY_NONE</code> should be returned. If any other recover mode
     * is returned, the simulation will later be asked to recover the agent using
     * the simulation thread by a call to <code>recoverAgent</code>.
     * <p/>
     * A common case might be when an agent reestablishing a lost connection to
     * the server.
     *
     * @param agent the <code>SimulationAgent</code> to be recovered.
     * @return the recovery mode for the agent
     * @see #RECOVERY_NONE
     * @see #RECOVERY_IMMEDIATELY
     * @see #RECOVERY_AFTER_NEXT_TICK
     * @see #recoverAgent(se.sics.tasim.sim.SimulationAgent)
     */
    protected int getAgentRecoverMode(SimulationAgent agent) {
        if (!recoverAgents) {
            return RECOVERY_NONE;
        }
        if (agent.hasAgentBeenActive()) {
            // The agent has been active and we must use the simulation
            // thread to retrieve all active orders (the information may
            // only be accessed using the simulation thread)
            return RECOVERY_AFTER_NEXT_TICK;
        }
        // The agent has not been active i.e. not sent any messages in
        // this simulation. This means the agent can not have any active
        // orders and only the startup messages needs to be sent. This can
        // be done using any thread.
        recoverAgent(agent);
        return RECOVERY_NONE;
    }

    /**
     * Called whenever an external agent has logged in and needs to recover its
     * state. The simulation should respond with the setup messages together with
     * any other state information the agent needs to continue playing in the
     * simulation (orders, inventory, etc). This method should not do anything if
     * the simulation not yet have been started.
     * <p/>
     * <p/>
     * A common case might be when an agent reestablishing a lost connection to
     * the server.
     *
     * @param agent the <code>SimulationAgent</code> to be recovered.
     */
    protected void recoverAgent(SimulationAgent agent) {
      //TODO-Write this....
    }

    /**
     * Delivers a message to the coordinator (the simulation). The coordinator
     * must self validate the message.
     *
     * @param message the message
     */
    protected void messageReceived(Message message) {
        log.warning("received (ignoring) " + message);
    }

    public static String getSimulationRoleName(int simRole) {
        return simRole >= 0 && simRole < ROLE_NAME.length
                ? ROLE_NAME[simRole]
                : null;
    }

    public static int getSimulationRole(String role) {
        return ArrayUtils.indexOf(ROLE_NAME, role);
    }

    // -------------------------------------------------------------------
    // Logging handling
    // -------------------------------------------------------------------

    /**
     * Validates this message to ensure that it may be delivered to the agent.
     * Messages to the coordinator and the administration are never validated.
     *
     * @param receiverAgent the agent to deliver the message to
     * @param message       the message to validate
     * @return true if the message should be delivered and false otherwise
     */
    protected boolean validateMessage(SimulationAgent receiverAgent, Message message) {
        String sender = message.getSender();
        SimulationAgent senderAgent = getAgent(sender);
        int senderIndex;
        if (senderAgent == null) {
            // Messages from or the coordinator or administration are always
            // allowed.
            senderIndex = COORDINATOR_INDEX;

        } else if (senderAgent.getRole() == receiverAgent.getRole()) {
            // No two agents with the same role in the simulation may
            // communicate with each other.  A simple security measure to
            // avoid manufacturer agents to communicate or deceive each
            // other.
            return false;

        } else {
            senderIndex = senderAgent.getIndex();
        }

        int receiverIndex = receiverAgent.getIndex();
        Transportable content = message.getContent();
        Class contentType = content.getClass();
        if (logContentType(contentType)) {
            LogWriter writer = getLogWriter();
            writer.message(senderIndex, receiverIndex, content, getServerTime());
            writer.commit();
        }

        int type = getContentType(contentType);
        if (type != TYPE_NONE) {
            getEventWriter().interaction(senderIndex, receiverIndex, type);
        }
        return true;

    }

    /**
     * Validates this message to ensure that it may be broadcasted to all agents
     * with the specified role.
     * <p/>
     * This method can also be used to log messages
     *
     * @param sender  the agent sender the message
     * @param role    the role of all receiving agents
     * @param content the message content
     * @return true if the message should be delivered and false otherwise
     */
    protected boolean validateMessageToRole(SimulationAgent sender, int role, Transportable content) {
        // Only customer broadcast of RFQBundle to manufacturers are
        // allowed for now.
        //  if (role == MANUFACTURER && senderAgent.getRole() == CUSTOMER
        //      && content.getClass() == RFQBundle.class) {
        //  logToRole(senderAgent.getIndex(), role, content);
        //return true;
        //}
        return false;

    }

    /**
     * Validates this message from the coordinator to ensure that it may be
     * broadcasted to all agents with the specified role.
     * <p/>
     * This method can also be used to log messages
     *
     * @param role    the role of all receiving agents
     * @param content the message content
     * @return true if the message should be delivered and false otherwise
     */
    protected boolean validateMessageToRole(int role, Transportable content) {
        // Broadcasts from the coordinator are always allowed
        logToRole(COORDINATOR_INDEX, role, content);
        return true;
    }


    private void logToRole(int senderIndex, int role, Transportable content) {
        // Log this broadcast
        Class contentType = content.getClass();
        if (logContentType(contentType)) {
            LogWriter writer = getLogWriter();
            writer.messageToRole(senderIndex, role, content, getServerTime());
            writer.commit();
        }

        int type = getContentType(contentType);
        if (type != TYPE_NONE) {
            getEventWriter().interactionWithRole(senderIndex, role, type);
        }
    }

    private boolean logContentType(Class type) {
        if (type == StartInfo.class) {
            return false;
        }
        return true;
    }

    private int getContentType(Class type) {
        //if (type == DeliveryNotice.class) {
        //return TYPE_DELIVERY;
        //} else {
        return TYPE_NONE;
        //}
    }

    // -------------------------------------------------------------------
    // API to TACSCM builtin agents (trusted components)
    // -------------------------------------------------------------------

    final int getNumberOfAdvertisers() {
        return numberOfAdvertisers;
    }

    final String getAgentName(String agentAddress) {
        SimulationAgent agent = getAgent(agentAddress);
        return agent != null ? agent.getName() : agentAddress;
    }

    final void transaction(String supplier, String customer, int orderID, long amount) {
        log.finer("Transacted " + amount + " from " + customer + " to " + supplier);

        SimulationAgent supplierAgent = getAgent(supplier);
        SimulationAgent customerAgent = getAgent(customer);

        if (supplierAgent != null && supplierAgent.getRole() == ADVERTISER) {
            bank.deposit(supplier, amount);
        }
        if (customerAgent != null && customerAgent.getRole() == ADVERTISER) {
            bank.withdraw(customer, amount);
        }

        int supplierIndex = supplierAgent != null
                ? supplierAgent.getIndex()
                : COORDINATOR_INDEX;
        int customerIndex = customerAgent != null
                ? customerAgent.getIndex()
                : COORDINATOR_INDEX;
        LogWriter writer = getLogWriter();
        synchronized (writer) {
            writer.node("transaction").attr("supplier", supplierIndex)
                    .attr("customer", customerIndex)
                    .attr("orderID", orderID)
                    .attr("amount", amount)
                    .endNode("transaction");
        }
    }

/*    // Customers are responsible for reporting demand
    final void addDemandInfo(int productID,
                 int quantityRequested,
                 int quantityOrdered,
                 int averageUnitPrice) {
      if (marketReport != null) {
        marketReport.addDemandForProduct(productID, quantityRequested,
                         quantityOrdered, averageUnitPrice);
      }
    }

    // Customers sends price reports
    final void sendPriceReport(PriceReport priceReport) {
      sendToRole(MANUFACTURER, priceReport);
    }

    private Factory getFactoryForAgent(String agentName) {
      return (Factory) factoryTable.get(agentName);
    }

    private Factory getFactoryForAgent(SimulationAgent ownerAgent) {
      // SHOULD BE OPTIMIZED. FIX THIS!!!
      SimulationAgent[] factories = getAgents(FACTORY);
      if (factories != null) {
        for (int i = 0, n = factories.length; i < n; i++) {
      Factory f = (Factory) factories[i].getAgent();
      if (ownerAgent == f.getOwner()) {
        return f;
      }
        }
      }
      return null;
    }

    // Suppliers are responsible for reporting supply info
//   final void addSupplyProduced(int productID, int quantityProduced) {
//     if (marketReport != null) {
//       marketReport.addSupplyProduced(productID, quantityProduced);
//     }
//   }

    // Suppliers are responsible for reporting supply info
    final void addSupplyOrdered(int productID, int quantityOrdered,
                    int averageUnitPrice) {
      if (marketReport != null) {
        marketReport.addSupplyOrdered(productID, quantityOrdered,
                      averageUnitPrice);
      }
    }

    // Suppliers are responsible for reporting supply info
    final void addSupplyDelivered(int productID, int quantityDelivered) {
      if (marketReport != null) {
        marketReport.addSupplyDelivered(productID, quantityDelivered);
      }
    }

    // Suppliers are responsible for reporting supplier capacities for
    // their production lines
    final void sendSupplierCapacity(String supplier, int productID,
                   int capacity) {
      if (marketReport != null) {
        marketReport.addSupplierCapacity(supplier, productID, capacity);
      }
    }

    // Suppliers are responsible for reporting reputations for their customers
    final void sendSupplierReputation(String supplierAddress,
                      String agentAddress, double reputation) {
      SimulationAgent supplier = getAgent(supplierAddress);
      SimulationAgent agent = getAgent(agentAddress);
      if (supplier == null) {
        log.severe("supplier " + supplierAddress
           + " not found for reputation update");
      } else if (agent == null) {
        log.severe("manufacturer " + supplierAddress
           + " not found for reputation update");
      } else {
        LogWriter writer = getLogWriter();
        synchronized (writer) {
      writer.node("reputation")
        .attr("supplier", supplier.getIndex())
        .attr("customer", agent.getIndex())
        .attr("reputation", (float) reputation)
        .endNode("reputation");
        }
      }
    }

    // Suppliers delivers to manufacturer factories
    final boolean deliverToFactory(String agentName, int date,
                   DeliveryNotice notice) {
      Factory factory = getFactoryForAgent(agentName);
      if (factory != null) {
        // Make sure the delivery notice no longer can be changed
        notice.lock();
        factory.delivery(date, notice);
        return true;
      }
      return false;
    }

    final boolean deliverToCustomer(String customer, int date,
                    DeliveryNotice notice) {
      SimulationAgent a = getAgent(customer);
      if (a != null) {
        Agent agent = a.getAgent();
        if (agent instanceof Customer) {
      // Make sure the delivery notice no longer can be changed
      notice.lock();

      ((Customer) agent).delivery(date, notice);

      // Need to send an interaction event because no delivery
      // notice is sent to anyone.
      SimulationAgent sender = getAgent(notice.getSupplier());
      if (sender != null) {
        getEventWriter().interaction(sender.getIndex(), a.getIndex(),
                         TYPE_DELIVERY);
      }
      return true;
        }
      }
      return false;
    }*/


    private AdvertiserInfo getAdvertiserInfoForAgent(String agentName) {
        return advertiserInfoMap.get(agentName);
    }

    // -------------------------------------------------------------------
    //  API to Bank to allow it to send bank statuses
    // -------------------------------------------------------------------

    final void sendBankStatus(String agentName, BankStatus status) {
        sendMessage(agentName, status);
    }


}
