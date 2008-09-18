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
 * SCMInfo
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Feb 13 14:48:12 2003
 * Updated : $Date: 2008-04-04 21:07:49 -0500 (Fri, 04 Apr 2008) $
 *           $Revision: 3982 $
 */
package se.sics.tasim.props;

import se.sics.isl.transport.Context;

/**
 * <code>SCMInfo</code> is used to generate transport contexts for
 * communication with SCM servers.
 */
public class SCMInfo
{
  
  public static final String CONTEXT_NAME = "scmcontext";
  
  /** Cache of the last created context (since contexts should be constants) */
  private static Context lastContext;
  
  public static Context createContext()
  {
    return createContext(null);
  }
  
  public static Context createContext(Context parentContext)
  {
    Context context = lastContext;
    if (context != null && context.getParent() == parentContext)
    {
      return context;
    }
    
    context = new Context(CONTEXT_NAME, parentContext);
    // New in version 0.9.7
    context.addTransportable(new Ping());
    // New in version 0.9.6
    context.addTransportable(new ActiveOrders());
    
    context.addTransportable(new Alert());
    context.addTransportable(new BankStatus());
    context.addTransportable(new BOMBundle());
    context.addTransportable(new ComponentCatalog());
    context.addTransportable(new DeliveryNotice());
    context.addTransportable(new DeliverySchedule());
    context.addTransportable(new FactoryStatus());
    context.addTransportable(new InventoryStatus());
    context.addTransportable(new MarketReport());
    context.addTransportable(new OfferBundle());
    context.addTransportable(new OrderBundle());
    context.addTransportable(new PriceReport());
    context.addTransportable(new ProductionSchedule());
    context.addTransportable(new RFQBundle());
    context.addTransportable(new SimulationStatus());
    context.addTransportable(new StartInfo());
    context.addTransportable(new ServerConfig());
    // Cache the last context
    lastContext = context;
    return context;
  }
  
  // Prevent instances of this class
  private SCMInfo()
  {}
  
} // SCMInfo
