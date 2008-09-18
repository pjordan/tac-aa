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
 * ReputationManager
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Fri Feb 25 16:00:58 2005
 * Updated : $Date: 2008-03-07 10:01:29 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3825 $
 */
package se.sics.tasim.tacscm.agents;

import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.tacscm.atp.Promise;

/**
 */
public class ReputationManager
{
  
  private final double maxRatio;
  
  private String agent;
  private long totalOffers;
  private long totalOrders;
  private RFQBundle rfqs;
  
  private double lastReputation = -1.0;
  
  public ReputationManager(int initialEndowment, double maxRatio)
  {
    this.maxRatio = maxRatio;
    this.totalOffers = initialEndowment;
    this.totalOrders = initialEndowment;
  }
  
  public void updateReputation(int offered, int ordered)
  {
    totalOffers += offered;
    totalOrders += ordered;
  }
  
  public void updateReputation(Promise promise)
  {
    int reduced = (int) (0.2 * promise.getReducedQuantity());
    int quantity = promise.getQuantity();
    Promise otherPromise = promise.getOtherPromise();
    if (promise.isOrdered())
    {
      totalOrders += quantity;
      totalOffers += reduced > quantity ? reduced : quantity;
    }
    else if (promise.isOnDueDate() && (otherPromise == null || !otherPromise.isOrdered()))
    {
      totalOffers += reduced > quantity ? reduced : quantity;
    }
  }
  
  public String getName()
  {
    return agent;
  }
  
  public void setName(String name)
  {
    agent = name;
  }
  
  RFQBundle getBundle()
  {
    return rfqs;
  }
  
  boolean setRFQBundle(RFQBundle rfq)
  {
    if (rfqs == null)
    {
      rfqs = rfq;
      return true;
    }
    return false;
  }
  
  void clear()
  {
    rfqs = null;
  }
  
  public double getReputation()
  {
    double orderRatio = ((double) totalOrders) / totalOffers;
    if (orderRatio > maxRatio)
    {
      orderRatio = maxRatio;
    }
    return orderRatio / maxRatio;
  }
  
  public boolean checkReputationChange()
  {
    double reputation = getReputation();
    if (reputation != lastReputation)
    {
      lastReputation = reputation;
      return true;
    }
    return false;
  }
  
} // ReputationManager
