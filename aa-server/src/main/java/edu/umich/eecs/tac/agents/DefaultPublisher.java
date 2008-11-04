package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.TACAAConstants;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.aw.Message;
import se.sics.isl.transport.Transportable;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Oct 31, 2008
 * Time: 2:08:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPublisher extends Publisher implements TACAAConstants, TimeListener {

  public DefaultPublisher() {
  }

  public void nextTimeUnit(int date) {

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
