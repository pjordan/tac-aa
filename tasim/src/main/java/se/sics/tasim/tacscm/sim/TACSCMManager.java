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
 * TACSCMManager
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Oct 14 17:48:08 2002
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.sim;

import java.util.Hashtable;
import java.util.logging.Logger;

import com.botbox.util.ArrayUtils;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.sim.Simulation;
import se.sics.tasim.sim.SimulationManager;
import se.sics.tasim.tacscm.TACSCMConstants;

public class TACSCMManager extends SimulationManager
{
  
  private final static Logger log = Logger.getLogger(TACSCMManager.class.getName());
  
  /** The default simulation length if not specified in configuration file */
  private final static int DEFAULT_SIM_LENGTH = 58 * 60;
  
  /**
   * Default number of participants in a TACSCM simulation if not specified in
   * configuration file
   */
  final static int NUMBER_OF_MANUFACTURERS = 6;
  
  private static Hashtable configTable = new Hashtable();
  
  public TACSCMManager()
  {}
  
  /**
   * Initializes this simulation manager. Recommended actions is to register all
   * supported simulation types.
   */
  protected void init()
  {
    for (int i = 0, n = TACSCMConstants.SUPPORTED_TYPES.length; i < n; i++)
    {
      init(TACSCMConstants.SUPPORTED_TYPES[i]);
    }
  }
  
  private void init(String type)
  {
    configTable.put(type, new Config(type));
    registerType(type);
  }
  
  protected boolean isSupportedSimulationType(String type)
  {
    return configTable.get(type) != null;
  }
  
  protected ConfigManager getSimulationConfig(Config simConfig)
  {
    // Only supports one type of simulation at this time. FIX THIS!!!
    if (simConfig.simulationConfig == null)
    {
      ConfigManager config = loadSimulationConfig(simConfig.type);
      simConfig.simulationConfig = config;
      simConfig.simulationLength = config.getPropertyAsInt("game.length", DEFAULT_SIM_LENGTH) * 1000;
      simConfig.numberOfManufacturers = config.getPropertyAsInt("game.numberOfManufacturers",
          NUMBER_OF_MANUFACTURERS);
    }
    return simConfig.simulationConfig;
  }
  
  public SimulationInfo createSimulationInfo(String type, String params)
  {
    Config simConfig = (Config) configTable.get(type);
    if (simConfig != null)
    {
      // Initialize the config if not already done.
      getSimulationConfig(simConfig);
      // Should take length from parameters. FIX THIS!!!
      return createSimulationInfo(type, params, simConfig.simulationLength);
    }
    return null;
  }
  
  public boolean join(int agentID, int role, SimulationInfo info)
  {
    Config simConfig = (Config) configTable.get(info.getType());
    if (simConfig == null)
    {
      return false;
    }
    // Initialize the config if not already done.
    getSimulationConfig(simConfig);
    
    if ((role == TACSCMConstants.MANUFACTURER) && !info.isFull()
        && info.getParticipantCount() < simConfig.numberOfManufacturers)
    {
      info.addParticipant(agentID, TACSCMConstants.MANUFACTURER);
      // The number of participants should be taken from parameters! FIX THIS!!!
      if (info.getParticipantCount() >= simConfig.numberOfManufacturers)
      {
        info.setFull();
      }
      return true;
    }
    return false;
  }
  
  public String getSimulationRoleName(String type, int simRole)
  {
    return configTable.get(type) != null ? TACSCMSimulation.getSimulationRoleName(simRole) : null;
  }
  
  public int getSimulationRoleID(String type, String simRole)
  {
    return configTable.get(type) != null ? (simRole == null ? TACSCMConstants.MANUFACTURER
        : TACSCMSimulation.getSimulationRole(simRole)) : 0;
  }
  
  public int getSimulationLength(String type, String params)
  {
    Config simConfig = (Config) configTable.get(type);
    if (simConfig == null)
    {
      return DEFAULT_SIM_LENGTH;
    }
    
    // Initialize the config if not already done.
    getSimulationConfig(simConfig);
    
    return simConfig.simulationLength;
  }
  
  public Simulation createSimulation(SimulationInfo info)
  {
    Config simConfig = (Config) configTable.get(info.getType());
    if (simConfig == null)
    {
      throw new IllegalArgumentException("simulation type " + info.getType() + " not supported");
    }
    // When should the configuration be reloaded? FIX THIS!!! FIX THIS!!!
    ConfigManager config = getSimulationConfig(simConfig);
    return new TACSCMSimulation(config);
  }
  
  private static class Config
  {
    
    /** Configuration for a specific type of simulation */
    public final String type;
    public ConfigManager simulationConfig;
    public int simulationLength = DEFAULT_SIM_LENGTH * 1000;
    public int numberOfManufacturers = NUMBER_OF_MANUFACTURERS;
    
    public Config(String type)
    {
      this.type = type;
    }
  }
  
} // TACSCMManager
