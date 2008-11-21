package edu.umich.eecs.tac.sim;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Sep 26, 2008
 * Time: 6:09:12 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.Hashtable;
import java.util.logging.Logger;


import com.botbox.util.ArrayUtils;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.sim.Simulation;
import se.sics.tasim.sim.SimulationManager;
import edu.umich.eecs.tac.TACAAConstants;

//TODO-TEST

public class TACAAManager extends SimulationManager {

  private final static Logger log = Logger.getLogger(TACAAManager.class.getName());

  /** The default simulation length if not specified in configuration file */
  private final static int DEFAULT_SIM_LENGTH = 10 * 60;  //Set to 10 minutes

  /** Default number of participants in a TACSCM simulation if not
   * specified in configuration file
   */
  final static int NUMBER_OF_ADVERTISERS = 8;

  private static Hashtable<String, Config> configTable = new Hashtable<String, Config>();

  public TACAAManager() {
  }

  /**
   * Initializes this simulation manager.  Recommended actions is to
   * register all supported simulation types.
   */
  protected void init() {
    for (int i = 0, n = TACAAConstants.SUPPORTED_TYPES.length; i < n; i++) {
      init(TACAAConstants.SUPPORTED_TYPES[i]);
    }
  }

  private void init(String type) {
    configTable.put(type, new Config(type));
    registerType(type);
  }

  protected boolean isSupportedSimulationType(String type) {
    return configTable.get(type) != null;
  }

    protected ConfigManager getSimulationConfig(Config simConfig) {
        // Only supports one type of simulation at this time. FIX THIS!!!
        if (simConfig.simulationConfig == null) {
          ConfigManager config = loadSimulationConfig(simConfig.type);
          simConfig.simulationConfig = config;
          simConfig.simulationLength =
            config.getPropertyAsInt("game.length", DEFAULT_SIM_LENGTH) * 1000;
          simConfig.numberOfAdvertisers =
            config.getPropertyAsInt("game.numberOfAdvertisers", NUMBER_OF_ADVERTISERS);
        }
        return simConfig.simulationConfig;
      }

      public SimulationInfo createSimulationInfo(String type, String params) {
        Config simConfig = (Config) configTable.get(type);
        if (simConfig != null) {
          // Initialize the config if not already done.
          getSimulationConfig(simConfig);
          // Should take length from parameters. FIX THIS!!!
          return createSimulationInfo(type, params, simConfig.simulationLength);
        }
        return null;
      }

      public boolean join(int agentID, int role, SimulationInfo info) {
        Config simConfig = (Config) configTable.get(info.getType());
        if (simConfig == null) {
          return false;
        }
        // Initialize the config if not already done.
        getSimulationConfig(simConfig);

        if ((role == TACAAConstants.ADVERTISER)
            && !info.isFull()
            && info.getParticipantCount() < simConfig.numberOfAdvertisers) {
          info.addParticipant(agentID, TACAAConstants.ADVERTISER);
          // The number of participants should be taken from parameters! FIX THIS!!!
          if (info.getParticipantCount() >= simConfig.numberOfAdvertisers) {
        info.setFull();
          }
          return true;
        }
        return false;
      }

      public String getSimulationRoleName(String type, int simRole) {
        return configTable.get(type) != null
          ? TACAASimulation.getSimulationRoleName(simRole)
          : null;
      }

      public int getSimulationRoleID(String type, String simRole) {
        return configTable.get(type) != null
          ? (simRole == null
         ? TACAAConstants.ADVERTISER
         : TACAASimulation.getSimulationRole(simRole)
         )
          : 0;
      }

      public int getSimulationLength(String type, String params) {
        Config simConfig = (Config) configTable.get(type);
        if (simConfig == null) {
          return DEFAULT_SIM_LENGTH;
        }

        // Initialize the config if not already done.
        getSimulationConfig(simConfig);

        return simConfig.simulationLength;
      }

      public Simulation createSimulation(SimulationInfo info) {
        Config simConfig = (Config) configTable.get(info.getType());
        if (simConfig == null) {
          throw new IllegalArgumentException("simulation type " + info.getType()
                         + " not supported");
        }
        // When should the configuration be reloaded? FIX THIS!!! FIX THIS!!!
        ConfigManager config = getSimulationConfig(simConfig);
        return new TACAASimulation(config);
      }

      private static class Config {

        /** Configuration for a specific type of simulation */
        public final String type;
        public ConfigManager simulationConfig;
        public int simulationLength = DEFAULT_SIM_LENGTH * 1000;
        public int numberOfAdvertisers = NUMBER_OF_ADVERTISERS;

        public Config(String type) {
          this.type = type;
        }
      }
    
}
