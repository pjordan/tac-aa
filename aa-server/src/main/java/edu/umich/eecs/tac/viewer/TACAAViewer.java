package edu.umich.eecs.tac.viewer;

import se.sics.tasim.viewer.SimulationViewer;
import se.sics.tasim.viewer.ViewerPanel;
import se.sics.tasim.viewer.SimulationPanel;
import se.sics.tasim.viewer.AgentView;
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
        TACAAAgentView view;
        int container;
        switch (role) {
            case ADVERTISER:
                view = null;
                container = 1;
                break;
            case PUBLISHER:
                view = null;
                container = 0;
                break;
            case USERS:
                view = new UserView();
                container = 2;
                break;
            default:
                log.severe("no viewer for " + name + " with role " + role);
                view = null;
                container = 0;
        }
        if (view != null) {
            log.severe("Adding participant " + name + " with role " + role + " at " + container);
            
            // This must be done with event dispatch thread. FIX THIS!!!
            simulationPanel.addAgentView(view, agent, name, role, getRoleName(role), container);
        }
    }

    public void nextTimeUnit(int timeUnit) {
        simulationPanel.nextTimeUnit(timeUnit);
    }

    public void dataUpdated(int agent, int type, int value) {
        TACAAAgentView view = simulationPanel.getAgentView(agent);
        if (view != null) {
            view.dataUpdated(type, value);
        }
    }

    public void dataUpdated(int agent, int type, long value) {
        TACAAAgentView view = simulationPanel.getAgentView(agent);
        if (view != null) {
            view.dataUpdated(type, value);
        }
    }

    public void dataUpdated(int agent, int type, float value) {
        TACAAAgentView view = simulationPanel.getAgentView(agent);
        if (view != null) {
            view.dataUpdated(type, value);
        }
    }

    public void dataUpdated(int agent, int type, String value) {
        TACAAAgentView view = simulationPanel.getAgentView(agent);
        if (view != null) {
            view.dataUpdated(type, value);
        }
    }

    public void dataUpdated(int agent, int type, Transportable value) {
        TACAAAgentView view = simulationPanel.getAgentView(agent);
        if (view != null) {
            view.dataUpdated(type, value);
        }
    }

    public void dataUpdated(int type, Transportable value) {
        Class valueType = value.getClass();
        if (valueType == RetailCatalog.class) {
            this.catalog = (RetailCatalog) value;
        }
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
