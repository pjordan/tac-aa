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
 * TACSCMAgentView
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Fri Jan 31 13:56:13 2003
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.viewer;

import java.awt.Point;

import se.sics.tasim.tacscm.TACSCMConstants;
import se.sics.tasim.viewer.AgentView;
import se.sics.tasim.viewer.SimulationPanel;

public abstract class TACSCMAgentView extends AgentView implements TACSCMConstants
{
  
  protected final TACSCMViewer viewer;
  
  public TACSCMAgentView(TACSCMViewer viewer)
  {
    this.viewer = viewer;
  }
  
  protected final void initialized()
  {
    initializeView();
  }
  
  protected abstract void initializeView();
  
  public Point getConnectionPoint(int type, int toX, int toY, boolean isTarget, Point cache)
  {
    Point p = super.getConnectionPoint(type, toX, toY, isTarget, cache);
    // Modify the coordinates for order interaction (yellow arrows)
    // since orders and negotiation (blue arrows) appear in the same
    // phase).
    if (type == SimulationPanel.TYPE_YELLOW)
    {
      p.y += isTarget ? 6 : 12;
    }
    return p;
  }
  
} // TACSCMAgentView
