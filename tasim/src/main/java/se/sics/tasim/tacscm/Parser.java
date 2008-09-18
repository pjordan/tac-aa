/**
 * TAC Supply Chain Management Log Tools
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
 * Parser
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Fri Jun 06 17:05:21 2003
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm;

import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.SCMInfo;
import se.sics.tasim.props.ServerConfig;

/**
 * The abstract class <code>Parser</code> is a base class that helps with the
 * parsing of TACSCM SCM log files.
 * <p>
 * 
 * As a log file is parsed, the <code>Parser</code> will invoke different
 * methods for the various information found in the log file. These methods
 * should be overridden to handle the information.
 * <p>
 * 
 * The order of the method invokations is important and mirrors the order of the
 * information in the log file.
 */
public abstract class Parser implements TACSCMConstants
{
  
  private static final Logger log = Logger.getLogger(Parser.class.getName());
  
  private static final String CONFIG_NAME = new ServerConfig().getTransportName();
  
  private final LogReader logReader;
  
  protected Parser(LogReader logReader)
  {
    this.logReader = logReader;
    this.logReader.setContext(SCMInfo.createContext());
  }
  
  /**
   * Returns the log reader for this log file.
   */
  protected LogReader getReader()
  {
    return logReader;
  }
  
  /**
   * Starts the log parsing procedure.
   * 
   * @exception IOException
   *              if an error occurs
   * @exception ParseException
   *              if an error occurs
   */
  public final void start() throws IOException, ParseException
  {
    try
    {
      parseStarted();
      while (logReader.hasMoreChunks())
      {
        TransportReader reader = logReader.nextChunk();
        handleNodes(reader);
      }
    }
    finally
    {
      stop();
    }
  }
  
  private void handleNodes(TransportReader reader) throws ParseException
  {
    while (reader.nextNode(false))
    {
      
      if (reader.isNode("intUpdated"))
      {
        int type = reader.getAttributeAsInt("type", 0);
        int agentIndex = reader.getAttributeAsInt("agent", -1);
        int value = reader.getAttributeAsInt("value");
        if (agentIndex >= 0)
        {
          dataUpdated(agentIndex, type, value);
        }
        
      }
      else if (reader.isNode("longUpdated"))
      {
        int type = reader.getAttributeAsInt("type", 0);
        int agentIndex = reader.getAttributeAsInt("agent", -1);
        long value = reader.getAttributeAsLong("value");
        if (agentIndex >= 0)
        {
          dataUpdated(agentIndex, type, value);
        }
        
      }
      else if (reader.isNode("floatUpdated"))
      {
        int type = reader.getAttributeAsInt("type", 0);
        int agentIndex = reader.getAttributeAsInt("agent", -1);
        float value = reader.getAttributeAsFloat("value");
        if (agentIndex >= 0)
        {
          dataUpdated(agentIndex, type, value);
        }
        
      }
      else if (reader.isNode("stringUpdated"))
      {
        int type = reader.getAttributeAsInt("type", 0);
        int agentIndex = reader.getAttributeAsInt("agent", -1);
        String value = reader.getAttribute("value");
        if (agentIndex >= 0)
        {
          dataUpdated(agentIndex, type, value);
        }
        
      }
      else if (reader.isNode("messageToRole"))
      {
        int sender = reader.getAttributeAsInt("sender");
        int role = reader.getAttributeAsInt("role");
        reader.enterNode();
        reader.nextNode(true);
        Transportable content = reader.readTransportable();
        reader.exitNode();
        messageToRole(sender, role, content);
        
      }
      else if (reader.isNode("message"))
      {
        int receiver = reader.getAttributeAsInt("receiver");
        // Ignore messages to the coordinator
        if (receiver != 0)
        {
          int sender = reader.getAttributeAsInt("sender");
          reader.enterNode();
          reader.nextNode(true);
          
          Transportable content = reader.readTransportable();
          reader.exitNode();
          
          message(sender, receiver, content);
        }
        
      }
      else if (reader.isNode("objectUpdated"))
      {
        int agentIndex = reader.getAttributeAsInt("agent", -1);
        int type = reader.getAttributeAsInt("type", 0);
        reader.enterNode();
        reader.nextNode(true);
        Transportable content = reader.readTransportable();
        reader.exitNode();
        if (agentIndex >= 0)
        {
          dataUpdated(agentIndex, type, content);
        }
        else
        {
          dataUpdated(type, content);
        }
        
      }
      else if (reader.isNode("penalty"))
      {
        int supplier = reader.getAttributeAsInt("supplier");
        int customer = reader.getAttributeAsInt("customer");
        int orderID = reader.getAttributeAsInt("orderID");
        int amount = reader.getAttributeAsInt("amount");
        boolean orderCancelled = reader.getAttributeAsInt("orderVoid", 0) > 0;
        penalty(supplier, customer, orderID, amount, orderCancelled);
        
      }
      else if (reader.isNode("transaction"))
      {
        int supplier = reader.getAttributeAsInt("supplier");
        int customer = reader.getAttributeAsInt("customer");
        int orderID = reader.getAttributeAsInt("orderID");
        long amount = reader.getAttributeAsLong("amount");
        transaction(supplier, customer, orderID, amount);
        
      }
      else if (reader.isNode("interest"))
      {
        int agent = reader.getAttributeAsInt("agent");
        long amount = reader.getAttributeAsLong("amount");
        interest(agent, amount);
        
      }
      else if (reader.isNode("storageCost"))
      {
        int agent = reader.getAttributeAsInt("agent");
        long amount = reader.getAttributeAsLong("amount");
        storageCost(agent, amount);
        
      }
      else if (reader.isNode("reputation"))
      {
        int supplier = reader.getAttributeAsInt("supplier");
        int customer = reader.getAttributeAsInt("customer");
        double reputation = reader.getAttributeAsFloat("reputation");
        reputation(supplier, customer, reputation);
        
      }
      else if (reader.isNode("nextTimeUnit"))
      {
        int date = reader.getAttributeAsInt("unit");
        long time = reader.getAttributeAsLong("time", 0L);
        nextDay(date, time);
        
      }
      else if (reader.isNode(CONFIG_NAME))
      {
        Transportable content = reader.readTransportable();
        data(content);
        
      }
      else
      {
        unhandledNode(reader.getNodeName());
      }
    }
  }
  
  /**
   * Stops the parser and closes all open files.
   */
  public final void stop()
  {
    logReader.close();
    parseStopped();
  }
  
  // -------------------------------------------------------------------
  // Parser callbacks
  // -------------------------------------------------------------------
  
  /**
   * Invoked when the parse process starts.
   */
  protected void parseStarted()
  {}
  
  /**
   * Invoked when the parse process ends.
   */
  protected void parseStopped()
  {}
  
  /**
   * Invoked when a message to all participants with a specific role is
   * encountered in the log file. Example of this is the RFQs sent by the
   * customers to all manufacturers each day. The default implementation will
   * invoke the <code>message</code> method for all agents with the specific
   * role.
   * 
   * @param sender
   *          the sender of the message
   * @param role
   *          the role of all receivers
   * @param content
   *          the message content
   */
  protected void messageToRole(int sender, int role, Transportable content)
  {
    ParticipantInfo[] infos = logReader.getParticipants();
    if (infos != null)
    {
      for (int i = 0, n = infos.length; i < n; i++)
      {
        if (infos[i].getRole() == role)
        {
          message(sender, infos[i].getIndex(), content);
        }
      }
    }
  }
  
  /**
   * Invoked when a message to a specific receiver is encountered in the log
   * file. Example of this is the offers sent by the manufacturers to the
   * customers.
   * 
   * @param sender
   *          the sender of the message
   * @param receiver
   *          the receiver of the message
   * @param content
   *          the message content
   */
  protected abstract void message(int sender, int receiver, Transportable content);
  
  /**
   * Invoked when some general data is encountered in the log file. An example
   * of this is the server configuration for the simulation.
   * 
   * @param object
   *          the data container
   * @see se.sics.tasim.props.ServerConfig
   */
  protected void data(Transportable object)
  {}
  
  /**
   * Invoked when a data update is encountered in the log file.
   * 
   * @param agent
   *          the agent for which the data was updated
   * @param type
   *          the type of the data
   * @param value
   *          the data value
   * @see se.sics.tasim.tacscm.TACSCMConstants
   */
  protected void dataUpdated(int agent, int type, int value)
  {}
  
  /**
   * Invoked when a data update is encountered in the log file.
   * 
   * @param agent
   *          the agent for which the data was updated
   * @param type
   *          the type of the data
   * @param value
   *          the data value
   * @see se.sics.tasim.tacscm.TACSCMConstants
   */
  protected void dataUpdated(int agent, int type, long value)
  {}
  
  /**
   * Invoked when a data update is encountered in the log file.
   * 
   * @param agent
   *          the agent for which the data was updated
   * @param type
   *          the type of the data
   * @param value
   *          the data value
   * @see se.sics.tasim.tacscm.TACSCMConstants
   */
  protected void dataUpdated(int agent, int type, float value)
  {}
  
  /**
   * Invoked when a data update is encountered in the log file.
   * 
   * @param agent
   *          the agent for which the data was updated
   * @param type
   *          the type of the data
   * @param value
   *          the data value
   * @see se.sics.tasim.tacscm.TACSCMConstants
   */
  protected void dataUpdated(int agent, int type, String value)
  {}
  
  /**
   * Invoked when a data update is encountered in the log file.
   * 
   * @param agent
   *          the agent for which the data was updated
   * @param type
   *          the type of the data
   * @param value
   *          the data value
   * @see se.sics.tasim.tacscm.TACSCMConstants
   */
  protected void dataUpdated(int agent, int type, Transportable content)
  {}
  
  /**
   * Invoked when a data update is encountered in the log file. Examples of this
   * is the BOMBundle and ComponentCatalog in the beginning of a simulation.
   * 
   * @param type
   *          the type of the data
   * @param value
   *          the data value
   * @see se.sics.tasim.tacscm.TACSCMConstants
   * @see se.sics.tasim.props.BOMBundle
   * @see se.sics.tasim.props.ComponentCatalog
   */
  protected void dataUpdated(int type, Transportable content)
  {}
  
  /**
   * Invoked when an interest update for a specific agent is encountered in the
   * log file.
   * 
   * @param agent
   *          the agent for which its bank account has been changed by applying
   *          an interest
   * @param amount
   *          the interest amount
   */
  protected void interest(int agent, long amount)
  {}
  
  /**
   * Invoked when a storage cost claim for a specific agent is encountered in
   * the log file.
   * 
   * @param agent
   *          the agent for which its bank account has been decreased by
   *          applying a storage cost
   * @param amount
   *          the storage cost amount
   */
  protected void storageCost(int agent, long amount)
  {}
  
  /**
   * Invoked when a transaction is encountered in the log file.
   * 
   * @param supplier
   *          the supplier which receives the payment
   * @param customer
   *          the paying customer
   * @param orderID
   *          the order causing the payment
   * @param amount
   *          the transacted amount
   */
  protected void transaction(int supplier, int customer, int orderID, long amount)
  {}
  
  /**
   * Invoked when a penalty claim is encountered in the log file.
   * 
   * @param supplier
   *          the supplier which pays the penalty
   * @param customer
   *          the customer which receives the penalty
   * @param orderID
   *          the order causing the penalty
   * @param amount
   *          the penalty fee
   */
  protected void penalty(int supplier, int customer, int orderID, int amount, boolean orderCancelled)
  {}
  
  /**
   * Invoked when a reputation change notification is encountered in
   * the log file.
   */
  protected void reputation(int supplier, int customer, double reputation)
  {}
  
  /**
   * Invoked when a new day notification is encountered in the log file.
   *
   * @param date the new day in the simulation
   * @param serverTime the server time at that point in the simulation
   */
  protected void nextDay(int date, long serverTime)
  {}
  
  /**
   * Invoked when an unknown (unhandled) node is encountered in the
   * log file. The default implementation simply outputs a warning.
   *
   * @param nodeName the name of the unhandled node.
   */
  protected void unhandledNode(String nodeName)
  {
    // Ignore anything else for now
    log.warning("ignoring unhandled node '" + nodeName + '\'');
  }
  
} // Parser
