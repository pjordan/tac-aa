package edu.umich.eecs.tac.logviewer.info;

import edu.umich.eecs.tac.logviewer.util.SimulationParser;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.UserPopulationState;
import edu.umich.eecs.tac.props.SlotInfo;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 1, 2009
 * Time: 1:39:53 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * To not be confused with SimulationInfo.java
 * @author- Lee Callender 
 */
public class GameInfo {
  private String server;
  private int numberOfDays;
  private int simulationID;
  private String simulationType;
  private int secondsPerDay;
  private double squashingParameter;
  //private int bankInterest;
  //private int bankInterestMax;
  //private int bankInterestMin;
  //private int storageCost;

  private Advertiser[] advertisers;
  private Set<Query> querySpace;
  private RetailCatalog catalog;
  private SlotInfo slotInfo;
  /*private PCType[] bom;
  private Component[] componentCatalog;

  private Manufacturer[] manufacturers;
  private Factory[] factories;
  private Supplier[] suppliers;
  private Customer[] customers;

  private ComponentNegotiation[][] compNegotiation;
  private PCNegotiation[][] pcNegotiation;

  private MarketDevelopment marketDevelopment;*/
  private UserPopulationState[] ups;

  public GameInfo(SimulationParser sp) {
    simulationID = sp.getSimulationID();
    simulationType = sp.getSimulationType();
    numberOfDays = sp.getNumberOfDays();
    secondsPerDay = sp.getSecondsPerDay();
    squashingParameter = sp.getSquashingParameter();
    server = sp.getServer();

    advertisers = sp.getAdvertisers();
    querySpace = sp.getQuerySpace();
    catalog = sp.getRetailCatalog();
    slotInfo = sp.getSlotInfo();
    ups = sp.getUserPopulationState();
    //factories = sp.getFactories();
    //suppliers = sp.getSuppliers();
    //customers = sp.getCustomers();

    //marketDevelopment = sp.getMarketDevelopment();
    //bom = sp.getPCTypes();
    //componentCatalog = sp.getComponents();

    //compNegotiation = sp.getComponentNegotiation();
    //pcNegotiation = sp.getPCNegotiation();
  }

  public String getServer() {
    return server;
  }

  public int getSimulationID() {
    return simulationID;
  }

  public String getSimulationType() {
    return simulationType;
  }

  public int getSecondsPerDay() {
    return secondsPerDay;
  }

  public int getNumberOfDays() {
    return numberOfDays;
  }

  public int getAdvertiserCount() {
    return advertisers.length;
  }

  public double getSquashingParameter(){
    return squashingParameter;
  }

  public Set<Query> getQuerySpace() {
    return querySpace;
  }

  public RetailCatalog getRetailCatalog() {
    return catalog;
  }

  public SlotInfo getSlotInfo() {
    return slotInfo;
  }

  public Advertiser[] getAdvertisers(){
    return advertisers;
  }

  public Advertiser getAdvertiser(int index) {
    return advertisers[index];
  }

  public int getAdvertiserIndex(Advertiser m) {
    for (int i = 0, n = advertisers.length; i < n; i++) {
      if(m == advertisers[i])
	      return i;
    }
    return -1;
  }

  public final UserPopulationState getUserPopulationOnDay(int day){
     try{
       return ups[day];
     }catch(Exception e){
       return null;
     }
  }

  public final UserPopulationState[] getUserPopulationState(){
    return ups;
  }

  private boolean isValidDay(int day) {
    return !(day < 0 || day > numberOfDays);
  }
}
