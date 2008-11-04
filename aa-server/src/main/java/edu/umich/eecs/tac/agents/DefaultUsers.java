package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.sim.Users;
import edu.umich.eecs.tac.TACAAConstants;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.aw.Message;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Oct 31, 2008
 * Time: 2:21:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultUsers extends Users implements TACAAConstants, TimeListener {

  public DefaultUsers(){
  }

  public void nextTimeUnit(int date){
  }

  protected void setup(){}
  protected void stopped() {}
  protected void shutdown() {}

  // -------------------------------------------------------------------
  // Message handling
  // -------------------------------------------------------------------

  protected void messageReceived(Message message) {

  }

  protected String getAgentName(String agentAddress) {
    return super.getAgentName(agentAddress);
  }

  protected void sendEvent(String message) {
    super.sendEvent(message);
  }

  protected void sendWarningEvent(String message) {
    super.sendWarningEvent(message);
  }

}
