package edu.umich.eecs.tac.sim;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Sep 26, 2008
 * Time: 6:55:14 PM
 * To change this template use File | Settings | File Templates.
 */

import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.sim.Simulation;
import se.sics.tasim.sim.SimulationAgent;
import se.sics.tasim.aw.Message;

//TODO-MODIFY THIS CLASS *AND* SIMULATION.JAVA

public class TACAASimulation extends Simulation {

    public TACAASimulation(ConfigManager config){
        super(config);
    }

    public static String getSimulationRoleName(int simRole) {return null;}

    public static int getSimulationRole(String role) {return 0;}
    
    
    protected void setupSimulation()
      throws IllegalConfigurationException{}

    protected void startSimulation(){}

   /**
    * Notification when this simulation is preparing to stop. Called after the
    * agents have been stopped but still can receive messages.
    */
    protected void prepareStopSimulation(){}

   /**
    * Notification when this simulation has been stopped. Called after the agents
    * shutdown.
    */
    protected void completeStopSimulation(){}

   /**
    * Called whenever an external agent has logged in and needs to recover its
    * state. The simulation should respond with the current recover mode (none,
    * immediately, or after next time unit). This method should return
    * <code>RECOVERY_NONE</code> if the simulation not yet have been started.
    * <p>
    *
    * The simulation might recover the agent using this method if recovering the
    * agent can be done using the agent communication thread. In that case
    * <code>RECOVERY_NONE</code> should be returned. If any other recover mode
    * is returned, the simulation will later be asked to recover the agent using
    * the simulation thread by a call to <code>recoverAgent</code>.
    *
    * A common case might be when an agent reestablishing a lost connection to
    * the server.
    *
    * @param agent
    *          the <code>SimulationAgent</code> to be recovered.
    * @return the recovery mode for the agent
    * @see #RECOVERY_NONE
    * @see #RECOVERY_IMMEDIATELY
    * @see #RECOVERY_AFTER_NEXT_TICK
    * @see #recoverAgent(se.sics.tasim.sim.SimulationAgent)
    */
    protected int getAgentRecoverMode(SimulationAgent agent){return 0;}

  /**
   * Called whenever an external agent has logged in and needs to recover its
   * state. The simulation should respond with the setup messages together with
   * any other state information the agent needs to continue playing in the
   * simulation (orders, inventory, etc). This method should not do anything if
   * the simulation not yet have been started.
   * <p>
   *
   * A common case might be when an agent reestablishing a lost connection to
   * the server.
   *
   * @param agent
   *          the <code>SimulationAgent</code> to be recovered.
   */
   protected void recoverAgent(SimulationAgent agent){}

   /**
   * Validates this message to ensure that it may be delivered to the agent.
   * Messages to the coordinator and the administration are never validated.
   *
   * @param receiver
   *          the agent to deliver the message to
   * @param message
   *          the message to validate
   * @return true if the message should be delivered and false otherwise
   */
  protected boolean validateMessage(SimulationAgent receiver,
      Message message){return false;}

  /**
   * Validates this message to ensure that it may be broadcasted to all agents
   * with the specified role.
   *
   * This method can also be used to log messages
   *
   * @param sender
   *          the agent sender the message
   * @param role
   *          the role of all receiving agents
   * @param content
   *          the message content
   * @return true if the message should be delivered and false otherwise
   */
  protected boolean validateMessageToRole(SimulationAgent sender,
      int role, Transportable content){return false;}

  /**
   * Validates this message from the coordinator to ensure that it may be
   * broadcasted to all agents with the specified role.
   *
   * This method can also be used to log messages
   *
   * @param role
   *          the role of all receiving agents
   * @param content
   *          the message content
   * @return true if the message should be delivered and false otherwise
   */
  protected boolean validateMessageToRole(int role,
      Transportable content){return false;}

  /**
   * Delivers a message to the coordinator (the simulation). The coordinator
   * must self validate the message.
   *
   * @param message
   *          the message
   */
  protected void messageReceived(Message message){}
}
