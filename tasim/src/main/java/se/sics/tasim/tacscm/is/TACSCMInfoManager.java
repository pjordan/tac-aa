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
 * TACSCMInfoManager
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Mar 03 11:49:29 2003
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.is;

import se.sics.tasim.is.common.InfoManager;
import se.sics.tasim.is.common.ResultManager;
import se.sics.tasim.is.common.ViewerCache;
import se.sics.tasim.tacscm.TACSCMConstants;

public class TACSCMInfoManager extends InfoManager
{
  
  public TACSCMInfoManager()
  {}
  
  protected void init()
  {
    for (int i = 0, n = TACSCMConstants.SUPPORTED_TYPES.length; i < n; i++)
    {
      registerType(TACSCMConstants.SUPPORTED_TYPES[i]);
    }
  }
  
  public ViewerCache createViewerCache(String simType)
  {
    return new TACSCMViewerCache();
  }
  
  public ResultManager createResultManager(String simType)
  {
    return new TACSCMResultManager();
  }
  
} // TACSCMInfoManager
