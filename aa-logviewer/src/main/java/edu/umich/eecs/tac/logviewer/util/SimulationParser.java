package edu.umich.eecs.tac.logviewer.util;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Jan 29, 2009
 * Time: 4:40:15 PM
 * To change this template use File | Settings | File Templates.
 */

import edu.umich.eecs.tac.Parser;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.ServerConfig;
import se.sics.tasim.props.StartInfo;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import com.botbox.util.ArrayUtils;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class SimulationParser extends Parser{
  private LogHandler logHandler;

  private ParserMonitor[] monitors;

  private int numberOfDays;
  private int simulationID;
  private String simulationType;
  private long startTime;
  private int secondsPerDay;
  private double squashingParameter;
  private int gameLengthInDays;
  private int bankInterestMax;
  private int bankInterestMin;
  private int storageCost;
  private String serverName;
  private String serverVersion;

  private ParticipantInfo[] participants;
  private int[] newParticipantIndex;

  private int publisherCount;
  private int advertiserCount;
  private int usersCount;

  private Advertiser[] advertisers;
  private Set<Query> querySpace;
  /*
  private Manufacturer[] manufacturers;
  private Factory[] factories;
  private Supplier[] suppliers;
  private Customer[] customers;

  private PCType[] pcTypes;
  private Component[] components;

  private MarketDevelopment marketDevelopment;
  private int marketReportCount;

  private ArrayList[] compNegotiation;
  private TreeMap compRFQs;
  private TreeMap compOffersFull;
  private TreeMap compOffersPartial;
  private TreeMap compOffersEarliest;
  private TreeMap compOrders;

  private PCNegotiation[][] pcNegotiation;
  private TreeMap pcRFQs;
  private TreeMap pcOffers;
  private TreeMap pcOrders;*/

  private int currentDay;

  /*private BOMBundle bomBundle;
  private ComponentCatalog componentCatalog;*/
  private RetailCatalog retailCatalog;
  private ServerConfig serverConfig;
  private SlotInfo slotInfo; 
  private UserPopulationState[] ups;

  private boolean[] gotLastDayAccount;
  private boolean[] gotLastDayInterest;

  private boolean errorParsing = false;
  private boolean isParserWarningsEnabled = true;

  public SimulationParser(LogHandler logHandler, LogReader lr) {
    super(lr);
    this.logHandler = logHandler;
    simulationID = lr.getSimulationID();
    simulationType = lr.getSimulationType();
    startTime = lr.getStartTime();
    serverName = lr.getServerName();
    serverVersion = lr.getServerVersion();
    isParserWarningsEnabled =
      logHandler.getConfig().getPropertyAsBoolean("visualizer.parserWarnings",
						  true);

    // Set up new indexing for participants [0..] for each type
    participants = lr.getParticipants();
    newParticipantIndex = new int[participants.length];

    for (int i = 0, n = participants.length; i < n; i++) {
      switch(participants[i].getRole()) {
        case TACAAConstants.PUBLISHER : newParticipantIndex[i] = publisherCount++; break;
        case TACAAConstants.ADVERTISER : newParticipantIndex[i] = advertiserCount++; break;
        case TACAAConstants.USERS : newParticipantIndex[i] = usersCount++; break;
      }
    }
  }

  protected void parseStarted() {
	  if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
		    monitors[i].parseStarted();
  }

  protected void parseStopped() {
    System.err.println(); //??

    if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
		    monitors[i].parseStopped();
  }

  /**********************************************************************
   * Message handling dispatching
   *  - a number of methods that gets a chuck, enterperates it and
   *    dispatches it for further processing.
   **********************************************************************/

  protected void message(int sender, int receiver, Transportable content) {
    if (content instanceof BankStatus)
      handleMessage(sender, receiver, (BankStatus) content);
    else if(content instanceof PublisherInfo)
      handleMessage(sender, receiver, (PublisherInfo) content);
    else if(content instanceof AdvertiserInfo)
      handleMessage(sender, receiver, (AdvertiserInfo) content);
    else if(content instanceof BidBundle)
      handleMessage(sender, receiver, (BidBundle) content);
    else if(content instanceof QueryReport)
      handleMessage(sender, receiver, (QueryReport) content);
    else if(content instanceof SalesReport)
      handleMessage(sender, receiver, (SalesReport) content);
    else if(content instanceof SlotInfo)
      handleMessage(sender, receiver, (SlotInfo) content);

    if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
	      monitors[i].message(sender, receiver, content);
  }

  protected void dataUpdated(int type, Transportable content) {

    if (content instanceof StartInfo)
      handleData((StartInfo) content);
    else if(content instanceof RetailCatalog)
      handleData((RetailCatalog) content);
    /*else if(content instanceof UserPopulationState)
      handleData((UserPopulationState) content);*/

    if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
		    monitors[i].dataUpdated(type, content);
  }

  protected void dataUpdated(int sender, int type, Transportable content){
    if(content instanceof UserPopulationState){
      handleData((UserPopulationState) content);
    }
  }

  protected void data(Transportable object) {

    if (object instanceof ServerConfig)
      handleData((ServerConfig) object);

	  if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
		    monitors[i].data(object);
  }

  private void handleMessage(int sender, int receiver, BankStatus content){
    if(participants[receiver].getRole() == TACAAConstants.ADVERTISER){
      int index = newParticipantIndex[receiver];
      advertisers[index].setAccountBalance(currentDay - 1, content.getAccountBalance());
    }
  }

  private void handleMessage(int sender, int receiver, PublisherInfo content){
    this.squashingParameter = content.getSquashingParameter();
  }

  private void handleMessage(int sender, int receiver, AdvertiserInfo content){
    if(participants[receiver].getRole() == TACAAConstants.ADVERTISER){
      int index = newParticipantIndex[receiver];
      advertisers[index].setManufacturerSpecialty(content.getManufacturerSpecialty());
      advertisers[index].setComponentSpecialty(content.getComponentSpecialty());
      advertisers[index].setDistributionCapacity(content.getDistributionCapacity());
      advertisers[index].setDistributionWindow(content.getDistributionWindow());
    }
  }

  private void handleMessage(int sender, int receiver, BidBundle content){
    if(participants[sender].getRole() == TACAAConstants.ADVERTISER){
      int index = newParticipantIndex[sender];
      advertisers[index].setBidBundle(content, currentDay);
    }
  }

  private void handleMessage(int sender, int receiver, QueryReport content){
    if(participants[receiver].getRole() == TACAAConstants.ADVERTISER){
      int index = newParticipantIndex[receiver];
      advertisers[index].setQueryReport(content, currentDay);
    }
  }

  private void handleMessage(int sender, int receiver, SalesReport content){
   if(participants[receiver].getRole() == TACAAConstants.ADVERTISER){
      int index = newParticipantIndex[receiver];
      advertisers[index].setSalesReport(content, currentDay);
    }
  }

  private void handleMessage(int sender, int receiver, SlotInfo content){
    if(this.slotInfo == null)
      this.slotInfo = content;
  }

  private void handleData(RetailCatalog content){
    if(retailCatalog == null)
      this.retailCatalog = content;

    if(querySpace == null){
      generatePossibleQueries(content);
    }

  }

  private void generatePossibleQueries(RetailCatalog retailCatalog) {
		if (retailCatalog != null && querySpace == null) {
			querySpace = new HashSet<Query>();

			for (Product product : retailCatalog) {
				Query f0 = new Query();
				Query f1_manufacturer = new Query(product.getManufacturer(),
						null);
				Query f1_component = new Query(null, product.getComponent());
				Query f2 = new Query(product.getManufacturer(), product
						.getComponent());

				querySpace.add(f0);
				querySpace.add(f1_manufacturer);
				querySpace.add(f1_component);
				querySpace.add(f2);
			}

		}
	}

  private void handleData(ServerConfig content) {
      serverConfig = content;

      secondsPerDay = content.getAttributeAsInt("game.secondsPerDay", -1);

      /*
      Customer.setParams
        (content.getAttributeAsInt("customer.quantityMin", -1),
         content.getAttributeAsInt("customer.quantityMax", -1),
         content.getAttributeAsInt("customer.dueDateMin", -1),
         content.getAttributeAsInt("customer.dueDateMax", -1),
         content.getAttributeAsInt("customer.rfqAvgMin", -1),
         content.getAttributeAsInt("customer.rfqAvgMax", -1),
         content.getAttributeAsInt("customer.daysBeforeVoid", -1),
         Float.parseFloat(content.getAttribute("customer.trendMin", "0")));

      Supplier.setParams
      (content.getAttributeAsInt("supplier.nominalCapacity", -1),
       content.getAttributeAsInt("supplier.maxRFQs", -1),
       content.getAttributeAsFloat("supplier.discountFactor", -1f));

      connectSuppliersWithComponents();*/
  }

  private void handleData(StartInfo startInfo) {
    numberOfDays = startInfo.getNumberOfDays();
    initActors();
    //initCommunication();
    //marketDevelopment = new MarketDevelopment(suppliers, components,
					      //pcTypes, numberOfDays);
  }

  private void handleData(UserPopulationState userPopulationState){
    if(this.ups == null){
      this.ups = new UserPopulationState[numberOfDays];
    }

    ups[currentDay] = userPopulationState;
  }

 /**
	 * Invoked when a new day notification is encountered in the log file.
	 *
	 * @param date
	 *            the new day in the simulation
	 * @param serverTime
	 *            the server time at that point in the simulation
	 */
	protected void nextDay(int date, long serverTime) {
	  currentDay = date;

    int done = (int) (10 * (double) (currentDay+1) / numberOfDays);
    int notDone = 10 - done;

    if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
	      monitors[i].nextDay(date, serverTime);

    System.err.print("Parsing game " + simulationID + ": [");
    for (int i = 0, n = done; i < n; i++)
	    System.err.print("*");
    for (int i = 0, n = notDone; i < n; i++)
	  System.err.print("-");
    System.err.print("]");
    System.err.print((char)13);
  }

  public void unhandledNode(String nodeName) {
    if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
	      monitors[i].unhandledNode(nodeName);
  }

  /**********************************************************************
   *
   * Message handling routines - Communication
   *  - a number of methods called to handle transaction messages
   *
   **********************************************************************/

  /*
  protected void transaction(int supplier, int customer,
			       int orderID, long amount) {
      if (participants[customer].getRole() == CUSTOMER) {
	PCNegotiation pcNeg = (PCNegotiation) pcOrders.get
	  (new CommMessageKey(orderID, newParticipantIndex[supplier], 0));
	if (pcNeg != null) {
	  if (pcNeg.isDelivered()) {
	    warn("PC order " + orderID + " to "
		 + participants[supplier].getName() + " delivered again "
		 + " (first delivered " + pcNeg.getDeliveryDate()
		 + ')');
	  } else {
	    pcNeg.setDeliveryDate(currentDay);
	  }
	} else {
	  warn("no PC order " + orderID + " to "
	       + participants[supplier].getName()
	       + " found for delivery");
	}
      }

      if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	  monitors[i].transaction(supplier, customer, orderID, amount);
    }*/

  /**********************************************************************
   *
   * Access methods - to get data from the parser
   *
   **********************************************************************/

   public void addMonitor(ParserMonitor monitor) {
	    monitors = (ParserMonitor[]) ArrayUtils.add(ParserMonitor.class,
						    monitors, monitor);
   }

   public void removeMonitor(ParserMonitor monitor) {
	    monitors = (ParserMonitor[]) ArrayUtils.remove(monitors, monitor);
   }

   public ParserMonitor[] getMonitors() {
	    return monitors;
   }

  /**
   * Get methods
   */
   public int getCurrentDay() {
	    return currentDay;
   }

   public boolean errorParsing() {
      return errorParsing;
   }

   public int getSecondsPerDay() {
      return secondsPerDay;
   }

   public int getNumberOfDays() {
      return numberOfDays;
   }

   public int getSimulationID() {
      return simulationID;
   }

   public String getSimulationType() {
      return simulationType;
   }

  public long getStartTime() {
      return startTime;
   }

  public String getServer() {
     return serverName + " (version " + serverVersion + ')';
   }

  public double getSquashingParameter(){
     return squashingParameter;
   }

  public RetailCatalog getRetailCatalog() {
    return retailCatalog;
  }

  public SlotInfo getSlotInfo() {
    return slotInfo;
  }

  public UserPopulationState[] getUserPopulationState(){
    return ups;
  }

  public Set<Query> getQuerySpace() {
    return querySpace;
  }

  public Advertiser[] getAdvertisers(){
     return advertisers;
   }
  
  /**********************************************************************
   *
   * Parser log handling (perhaps should be direct calls to LogHandler?)
   *
   **********************************************************************/

   private void warn(String message) {
    if (isParserWarningsEnabled) {
      int timeunit = currentDay;
      logHandler.warn("Parse: [Day " + (timeunit < 10 ? " " : "")
		      + timeunit + "] " + message);
    }
  }

  /**********************************************************************
   *
   *
   *
   **********************************************************************/

  // Keys to hash comMessages with
  protected static class CommMessageKey implements Comparable {
    int id;
    int sender;
    int day;

    CommMessageKey(int id, int sender, int day) {
      this.id = id;
      this.sender = sender;
      this.day = day;
    }

    public int compareTo(Object o) {
      CommMessageKey cm = (CommMessageKey) o;
      if(cm.day < day)
	      return -1;
      else if(cm.day > day)
	      return 1;

      if(cm.sender < sender)
	      return -1;
      else if(cm.sender > sender)
	      return 1;

      if(cm.id < id)
	      return -1;
      else if(cm.id > id)
	      return 1;

      return 0;
    }
  }

  /**********************************************************************
   *
   * Inititalization methods for internal data structures
   *
   **********************************************************************/

  private void initActors() {
    Color[] c_array = {Color.BLUE, Color.CYAN,  Color.GREEN,  new Color(75, 0, 130),
                       Color.RED, Color.MAGENTA, Color.ORANGE, Color.PINK};

    
    advertisers = new Advertiser[advertiserCount];
 
    for (int i = 0, n = participants.length; i < n; i++) {
      switch(participants[i].getRole()) {

        case TACAAConstants.ADVERTISER :
	        advertisers[newParticipantIndex[i]] = new Advertiser(participants[i].getIndex(),
			              participants[i].getAddress(),participants[i].getName(),
                    numberOfDays, c_array[newParticipantIndex[i]]);

	        break;
        case TACAAConstants.PUBLISHER :
	        break;
        case TACAAConstants.USERS :
	        break;

      }
    }
  }//initActors
}
