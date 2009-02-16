package edu.umich.eecs.tac.logviewer.info;

import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 2, 2009
 * Time: 10:37:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class Advertiser extends Actor {
  private int[] balance; //Agent's account balance per day. 
  private Color color;
  private String manufacturerSpecialty;
  private String componentSpecialty;
  private int distributionCapacity;
  private BidBundle[] bidBundle; 
  private QueryReport[] queryReport;
  private SalesReport[] salesReport;

  public Advertiser(int simulationIndex, String address, String name, int numberOfDays, Color color){
    super(simulationIndex, address, name);
    balance = new int[numberOfDays];
    bidBundle = new BidBundle[numberOfDays];
    queryReport = new QueryReport[numberOfDays+1];
    salesReport = new SalesReport[numberOfDays+1];
    this.color = color;
  }

  public void setManufacturerSpecialty(String specialty){
    manufacturerSpecialty = specialty;
  }

  public void setComponentSpecialty(String specialty){
    componentSpecialty = specialty;
  }

  public void setAccountBalance(int day, double accountBalance) {
    balance[day < balance.length ? day : (balance.length - 1)] = (int) accountBalance;
  }

  public void setDistributionCapacity(int distributionCapacity) {
    this.distributionCapacity = distributionCapacity;
  }

  public void setBidBundle(BidBundle bundle, int day){
    this.bidBundle[day] = bundle; 
  }

  public void setQueryReport(QueryReport report, int day){
    this.queryReport[day] = report;
  }

  public void setSalesReport(SalesReport report, int day){
    this.salesReport[day] = report;
  }

  public String getManufacturerSpecialty() {
    return manufacturerSpecialty;
  }

  public String getComponentSpecialty() {
    return componentSpecialty;
  }

  public int getDistributionCapacity() {
    return distributionCapacity;
  }

  public int getAccountBalance(int day) {
    return balance[day];
  }

  public int[] getAccountBalance() {
    return balance;
  }

  public BidBundle[] getBidBundles() {
    return bidBundle;
  }

  public QueryReport[] getQueryReports(){
    return queryReport;
  }

  public SalesReport[] getSalesReports(){
    return salesReport;
  }

  public BidBundle getBidBundle(int day){
    return bidBundle[day];
  }

  public QueryReport getQueryReport(int day){
    return queryReport[day];
  }

  public SalesReport getSalesReport(int day){
    return salesReport[day];
  }

  public Color getColor(){
    return color;
  }

  public void setColor(Color color){
    this.color = color;
  }
}
