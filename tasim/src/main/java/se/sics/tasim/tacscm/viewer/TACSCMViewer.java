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
 * TACSCMViewer
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Jan 27 14:07:29 2003
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.viewer;

import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.tacscm.TACSCMConstants;
import se.sics.tasim.viewer.AgentView;
import se.sics.tasim.viewer.SimulationPanel;
import se.sics.tasim.viewer.SimulationViewer;
import se.sics.tasim.viewer.ViewerPanel;

public class TACSCMViewer extends SimulationViewer implements TACSCMConstants
{
  
  private static final Logger log = Logger.getLogger(TACSCMViewer.class.getName());
  
  /** The maximal number of events to show in the event logs */
  public static final int MAX_EVENT_NO = 40;
  
  private SimulationPanel simulationPanel;
  
  private BOMBundle bomBundle;
  private ComponentCatalog catalog;
  
  private ViewerPanel mainPanel;
  
  public TACSCMViewer()
  {}
  
  public void init(ViewerPanel mainPanel)
  {
    this.mainPanel = mainPanel;
    
    simulationPanel = new SimulationPanel(mainPanel);
    simulationPanel.setPhaseNumber(3); // Phase 0, 1, and 2
    // Double buffer the phases because the phases for the next day
    // might arrive when still displaying previous day
    simulationPanel.setDoublePhase(true);
    
    // Should be specified in configuration. FIX THIS!!!
    ImageIcon icon = mainPanel.getIcon("color_info2.jpg");
    if (icon != null)
    {
      simulationPanel.addIcon(icon, SimulationPanel.RIGHT, SimulationPanel.RIGHT);
    }
    icon = mainPanel.getIcon("supplierText.jpg");
    if (icon != null)
    {
      simulationPanel.addIcon(icon, SimulationPanel.LEFT, SimulationPanel.CENTER);
    }
  }
  
  // -------------------------------------------------------------------
  // SimulationViewer interface
  // -------------------------------------------------------------------
  
  public JComponent getComponent()
  {
    return simulationPanel;
  }
  
  public void nextTimeUnit(int timeUnit)
  {
    simulationPanel.nextTimeUnit(timeUnit);
  }
  
  public synchronized void participant(int index, int role, String name, int participantID)
  {
    AgentView view;
    int container;
    switch (role)
    {
      case MANUFACTURER:
        view = new ManuView(this);
        container = 1;
        break;
      case SUPPLIER:
        view = new SupplierView(this);
        container = 0;
        break;
      case CUSTOMER:
        view = new CustomerView(this);
        container = 2;
        break;
      case FACTORY:
        view = null;
        container = 0;
        break;
      default:
        log.severe("no viewer for " + name + " with role " + role);
        view = null;
        container = 0;
    }
    if (view != null)
    {
      log.finer("Adding participant " + name + " with role " + role + " at " + container);
      view.setConnectionAxis(AgentView.X_AXIS);
      view.setConnectionDistance(10);
      // This must be done with event dispatch thread. FIX THIS!!!
      simulationPanel.addAgentView(view, index, name, role, getRoleName(role), container);
    }
  }
  
  public void dataUpdated(int agent, int type, int value)
  {
    AgentView view = simulationPanel.getAgentView(agent);
    if (view != null)
    {
      view.dataUpdated(type, value);
    }
  }
  
  public void dataUpdated(int agent, int type, long value)
  {
    AgentView view = simulationPanel.getAgentView(agent);
    if (view != null)
    {
      view.dataUpdated(type, value);
    }
  }
  
  public void dataUpdated(int agent, int type, float value)
  {
    AgentView view = simulationPanel.getAgentView(agent);
    if (view != null)
    {
      view.dataUpdated(type, value);
    }
  }
  
  public void dataUpdated(int agent, int type, String value)
  {
    AgentView view = simulationPanel.getAgentView(agent);
    if (view != null)
    {
      view.dataUpdated(type, value);
    }
  }
  
  public void dataUpdated(int agent, int type, Transportable value)
  {
    AgentView view = simulationPanel.getAgentView(agent);
    if (view != null)
    {
      view.dataUpdated(type, value);
    }
  }
  
  public void dataUpdated(int type, Transportable value)
  {
    Class valueType = value.getClass();
    if (valueType == BOMBundle.class)
    {
      this.bomBundle = (BOMBundle) value;
    }
    else if (valueType == ComponentCatalog.class)
    {
      this.catalog = (ComponentCatalog) value;
    }
  }
  
  public void interaction(int fromAgent, int toAgent, int type)
  {
    AgentView fromView = simulationPanel.getAgentView(fromAgent);
    AgentView toView = simulationPanel.getAgentView(toAgent);
    if (fromView != null && toView != null)
    {
      int phase = fromView.getRole() == MANUFACTURER ? 2 : 0;
      int connectionType;
      if (type == TYPE_ORDER)
      {
        connectionType = SimulationPanel.TYPE_YELLOW;
      }
      else if (type == TYPE_DELIVERY)
      {
        phase = 1;
        connectionType = SimulationPanel.TYPE_GREEN;
      }
      else
      {
        connectionType = SimulationPanel.TYPE_BLUE;
      }
      simulationPanel.addConnection(fromView, toView, phase, connectionType);
    }
  }
  
  public void interactionWithRole(int fromAgent, int role, int type)
  {
    for (int i = 0, n = simulationPanel.getHighestAgentIndex(); i < n; i++)
    {
      AgentView view = simulationPanel.getAgentView(i);
      if (view != null && view.getRole() == role && i != fromAgent)
      {
        interaction(fromAgent, i, type);
      }
    }
  }
  
  public void setServerTime(long serverTime)
  {}
  
  public void simulationStarted(int realSimID, String type, long startTime, long endTime,
      String timeUnitName, int timeUnitCount)
  {
    simulationPanel.simulationStarted(startTime, endTime, timeUnitCount);
  }
  
  public void simulationStopped(int realSimID)
  {
    // This must be done with event dispatch thread. FIX THIS!!!
    simulationPanel.simulationStopped();
  }
  
  public void nextSimulation(int realSimID, long startTime)
  {}
  
  public void intCache(int agent, int type, int[] cache)
  {}
  
  // -------------------------------------------------------------------
  // API towards agent views
  // -------------------------------------------------------------------
  
  public String getRoleName(int role)
  {
    return role >= 0 && role < ROLE_NAME.length ? ROLE_NAME[role] : Integer.toString(role);
  }
  
  public String getAgentName(int agentIndex)
  {
    return simulationPanel.getAgentName(agentIndex);
  }
  
  public BOMBundle getBOMBundle()
  {
    return bomBundle;
  }
  
  public ComponentCatalog getComponentCatalog()
  {
    return catalog;
  }
  
} // TACSCMViewer
