package edu.umich.eecs.tac.viewer;

import se.sics.tasim.viewer.SimulationViewer;
import se.sics.tasim.viewer.ViewerPanel;
import se.sics.isl.transport.Transportable;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.RetailCatalog;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * @author Patrick Jordan
 */
public class TACAAViewer extends SimulationViewer implements TACAAConstants {

    private static final Logger log = Logger.getLogger(TACAAViewer.class.getName());

    private TACAASimulationPanel simulationPanel;


    private ViewerPanel mainPanel;

    private RetailCatalog catalog;

    public void init(ViewerPanel panel) {
        mainPanel = panel;
        simulationPanel = new TACAASimulationPanel(mainPanel);        
    }

    public JComponent getComponent() {
        return simulationPanel;
    }

    public void setServerTime(long serverTime) {
    }

    public void simulationStarted(int realSimID, String type, long startTime, long endTime,
                                  String timeUnitName, int timeUnitCount) {
        simulationPanel.simulationStarted(startTime, endTime, timeUnitCount);
    }

    public void simulationStopped(int realSimID) {
        // This must be done with event dispatch thread. FIX THIS!!!
        simulationPanel.simulationStopped();
    }

    public void nextSimulation(int publicSimID, long startTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }// A cache with values for agent + type (bank account, etc)

    public void intCache(int agent, int type, int[] cache) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void participant(int agent, int role, String name, int participantID) {
        simulationPanel.participant(agent,role,name,participantID);
    }

    public void nextTimeUnit(int timeUnit) {
        simulationPanel.nextTimeUnit(timeUnit);
    }

    public void dataUpdated(int agent, int type, int value) {
        simulationPanel.dataUpdated(agent,type,value);
    }

    public void dataUpdated(int agent, int type, long value) {
        simulationPanel.dataUpdated(agent,type,value);
    }

    public void dataUpdated(int agent, int type, float value) {
        simulationPanel.dataUpdated(agent,type,value);
    }

    public void dataUpdated(int agent, int type, double value) {
        simulationPanel.dataUpdated(agent,type,value);
    }

    public void dataUpdated(int agent, int type, String value) {
        simulationPanel.dataUpdated(agent,type,value);
    }

    public void dataUpdated(int agent, int type, Transportable value) {
        simulationPanel.dataUpdated(agent,type,value);
    }

    public void dataUpdated(int type, Transportable value) {
        Class valueType = value.getClass();
        if (valueType == RetailCatalog.class) {
            this.catalog = (RetailCatalog) value;
        }
        simulationPanel.dataUpdated(type,value);
    }

    public void interaction(int fromAgent, int toAgent, int type) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void interactionWithRole(int fromAgent, int role, int type) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    // -------------------------------------------------------------------
    // API towards agent views
    // -------------------------------------------------------------------

    public String getRoleName(int role) {
        return role >= 0 && role < ROLE_NAME.length ? ROLE_NAME[role] : Integer.toString(role);
    }

    public String getAgentName(int agentIndex) {
        return simulationPanel.getAgentName(agentIndex);
    }

    public RetailCatalog getCatalog() {
        return catalog;
    }
}
