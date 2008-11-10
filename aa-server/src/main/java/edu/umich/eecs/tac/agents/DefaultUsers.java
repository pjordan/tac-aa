package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.sim.Users;
import edu.umich.eecs.tac.TACAAConstants;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.aw.Message;

/**
 * @author Lee Callender
 */
public class DefaultUsers extends Users implements TACAAConstants, TimeListener {

    public DefaultUsers() {
    }

    public void nextTimeUnit(int date) {
    }

    protected void setup() {
    }

    protected void stopped() {
    }

    protected void shutdown() {
    }

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


    public void sendSalesReportsToAll() {
        
    }
}
