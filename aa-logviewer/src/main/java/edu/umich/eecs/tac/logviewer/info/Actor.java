package edu.umich.eecs.tac.logviewer.info;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Jan 29, 2009
 * Time: 5:07:08 PM
 * To change this template use File | Settings | File Templates.
 *
 *@author SICS 
 */
public class Actor {

  private int simulationIndex;
  private String address;
  private String name;

  public Actor(int simulationIndex, String address, String name) {
    this.simulationIndex = simulationIndex;
    this.address = address;
    this.name = name == null ? address : name;
  }

  public int getSimulationIndex() {
    return simulationIndex;
  }

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

} // Actor

