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
 * TAC05SimulationInfo
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Feb 27 15:30:52 2003
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.props.SCMInfo;
import se.sics.tasim.props.ServerConfig;
import se.sics.tasim.props.StartInfo;

public class TACSCMSimulationInfo extends Parser
{
  
  private static final Logger log = Logger.getLogger(TACSCMSimulationInfo.class.getName());
  
  private int simID;
  private int uniqueID;
  private String simType;
  private String simParams;
  private long startTime;
  private int simLength;
  
  private String serverName;
  private String serverVersion;
  
  private ServerConfig serverConfig;
  private int bankDebtInterestRate = -1;
  private int bankDepositInterestRate = -1;
  private int storageCost = -1;
  
  private BOMBundle bomBundle;
  private ComponentCatalog componentCatalog;
  
  private Participant[] participants;
  private Hashtable participantTable;
  
  private int[] agentRoles;
  private Participant[][] agentsPerRole;
  private int agentRoleNumber;
  
  private int currentDate = 0;
  
  private boolean isParsingExtended = false;
  
  // Note: the context must have been set in the log reader before
  // this object is created!
  public TACSCMSimulationInfo(LogReader logReader) throws IOException, ParseException
  {
    super(logReader);
    
    ParticipantInfo[] infos = logReader.getParticipants();
    participants = new Participant[infos == null ? 0 : infos.length];
    participantTable = new Hashtable();
    for (int i = 0, n = participants.length; i < n; i++)
    {
      ParticipantInfo info = infos[i];
      if (info != null)
      {
        participants[i] = new Participant(info);
        participantTable.put(info.getAddress(), participants[i]);
      }
    }
    simID = logReader.getSimulationID();
    uniqueID = logReader.getUniqueID();
    simType = logReader.getSimulationType();
    simParams = logReader.getSimulationParams();
    startTime = logReader.getStartTime();
    simLength = logReader.getSimulationLength();
    serverName = logReader.getServerName();
    serverVersion = logReader.getServerVersion();
    
    start();
    
    // Extract the rest of the information
    Participant[] manus = getParticipantsByRole(MANUFACTURER);
    if (manus != null)
    {
      Participant m = manus[0];
      StartInfo si = m.getStartInfo();
      if (si != null)
      {
        bankDebtInterestRate = si.getAttributeAsInt("bank.interestRate", bankDebtInterestRate);
        bankDepositInterestRate = si.getAttributeAsInt("bank.depositInterestRate",
            bankDepositInterestRate);
        storageCost = si.getAttributeAsInt("factory.storageCost", storageCost);
      }
    }
  }
  
  public String getServerName()
  {
    return serverName;
  }
  
  public String getServerVersion()
  {
    return serverVersion;
  }
  
  public int getUniqueID()
  {
    return uniqueID;
  }
  
  public int getSimulationID()
  {
    return simID;
  }
  
  public String getSimulationType()
  {
    return simType;
  }
  
  public long getStartTime()
  {
    return startTime;
  }
  
  public int getSimulationLength()
  {
    return simLength;
  }
  
  public int getBankDebtInterestRate()
  {
    return bankDebtInterestRate;
  }
  
  public int getBankDepositInterestRate()
  {
    return bankDepositInterestRate;
  }
  
  public int getStorageCost()
  {
    return storageCost;
  }
  
  public ServerConfig getServerConfig()
  {
    return serverConfig;
  }
  
  public Participant getParticipant(int agentIndex)
  {
    Participant p;
    if (agentIndex >= participants.length || ((p = participants[agentIndex]) == null))
    {
      throw new IllegalArgumentException("no participant " + agentIndex);
    }
    return p;
  }
  
  public Participant getParticipant(String address)
  {
    Participant p = (Participant) participantTable.get(address);
    if (p == null)
    {
      throw new IllegalArgumentException("no participant " + address);
    }
    return p;
  }
  
  public int getParticipantCount()
  {
    return participants.length;
  }
  
  public Participant[] getParticipants()
  {
    return participants;
  }
  
  public Participant[] getParticipantsByRole(int role)
  {
    int index = ArrayUtils.indexOf(agentRoles, 0, agentRoleNumber, role);
    if (index < 0)
    {
      if (agentRoles == null)
      {
        agentRoles = new int[5];
        agentsPerRole = new Participant[5][];
      }
      else if (agentRoleNumber == agentRoles.length)
      {
        agentRoles = ArrayUtils.setSize(agentRoles, agentRoleNumber + 5);
        agentsPerRole = (Participant[][]) ArrayUtils.setSize(agentsPerRole, agentRoleNumber + 5);
      }
      
      ArrayList list = new ArrayList();
      for (int i = 0, n = participants.length; i < n; i++)
      {
        Participant a = participants[i];
        if ((a != null) && (a.getInfo().getRole() == role))
        {
          list.add(a);
        }
      }
      
      index = agentRoleNumber;
      agentsPerRole[agentRoleNumber] = list.size() > 0 ? (Participant[]) list
          .toArray(new Participant[list.size()]) : null;
      agentRoles[agentRoleNumber++] = role;
    }
    return agentsPerRole[index];
  }
  
  // -------------------------------------------------------------------
  // Parser callback handling
  // -------------------------------------------------------------------
  
  protected void messageToRole(int sender, int role, Transportable content)
  {
    for (int i = 0, n = participants.length; i < n; i++)
    {
      if (participants[i].getInfo().getRole() == role)
      {
        participants[i].messageReceived(currentDate, sender, content);
      }
    }
    
    if (sender != 0)
    {
      getParticipant(sender).messageSentToRole(currentDate, role, content);
    }
  }
  
  protected void message(int sender, int receiver, Transportable content)
  {
    // Ignore messages to the coordinator
    if (receiver != 0)
    {
      getParticipant(receiver).messageReceived(currentDate, sender, content);
      if (sender != 0)
      {
        getParticipant(sender).messageSent(currentDate, receiver, content);
      }
    }
  }
  
  protected void data(Transportable object)
  {
    if (object instanceof ServerConfig)
    {
      this.serverConfig = (ServerConfig) object;
    }
  }
  
  protected void dataUpdated(int agentIndex, int type, int value)
  {
    if (type == DU_BANK_ACCOUNT)
    {
      Participant p = getParticipant(agentIndex);
      p.setResult(value);
    }
  }
  
  protected void dataUpdated(int agentIndex, int type, long value)
  {
    if (type == DU_BANK_ACCOUNT)
    {
      Participant p = getParticipant(agentIndex);
      p.setResult(value);
    }
  }
  
  protected void dataUpdated(int agent, int type, float value)
  {}
  
  protected void dataUpdated(int agent, int type, String value)
  {}
  
  protected void dataUpdated(int agent, int type, Transportable content)
  {}
  
  protected void dataUpdated(int type, Transportable object)
  {
    if (object instanceof BOMBundle)
    {
      this.bomBundle = (BOMBundle) object;
    }
    else if (object instanceof ComponentCatalog)
    {
      this.componentCatalog = (ComponentCatalog) object;
    }
  }
  
  protected void interest(int agent, long amount)
  {
    Participant agentInfo = getParticipant(agent);
    agentInfo.addInterest(amount);
  }
  
  protected void storageCost(int agent, long amount)
  {
    Participant agentInfo = getParticipant(agent);
    agentInfo.addStorageCost(amount);
  }
  
  protected void transaction(int supplier, int customer, int orderID, long amount)
  {
    Participant sup = getParticipant(supplier);
    Participant cus = getParticipant(customer);
    cus.addCost(orderID, amount);
    sup.addRevenue(orderID, amount);
  }
  
  protected void penalty(int supplier, int customer, int orderID, int amount, boolean orderCancelled)
  {
    Participant sup = getParticipant(supplier);
    // Participant cus = getParticipant(customer);
    sup.addPenalty(orderID, amount, orderCancelled);
    // Penalties only relevant for suppliers since manufacturers
    // can never claim penalties in TAC SCM
    // cus.addRevenue(orderID, amount);
    
  }
  
  protected void nextDay(int date, long serverTime)
  {
    this.currentDate = date;
  }
  
  protected void unhandledNode(String nodeName)
  {
    // Ignore anything else for now
    log.warning("ignoring unhandled node '" + nodeName + '\'');
  }
  
  public static void main(String[] args) throws IOException, ParseException
  {
    if (args.length != 1)
    {
      System.out.println("Usage: " + TACSCMSimulationInfo.class.getName() + " file");
      System.exit(1);
    }
    
    System.gc();
    System.gc();
    System.gc();
    Runtime runtime = Runtime.getRuntime();
    long memory = runtime.totalMemory();
    long free = runtime.freeMemory();
    
    InputStream in = args[0].endsWith(".gz") ? (InputStream) new GZIPInputStream(
        new FileInputStream(args[0])) : (InputStream) new FileInputStream(args[0]);
    LogReader reader = new LogReader(in);
    reader.setContext(SCMInfo.createContext());
    TACSCMSimulationInfo info = new TACSCMSimulationInfo(reader);
    System.out.println("Simulation " + info.getSimulationID());
    for (int i = 0, n = info.getParticipantCount(); i < n; i++)
    {
      System.out.println(info.getParticipant(i));
    }
    
    System.gc();
    System.gc();
    System.gc();
    long newMemory = runtime.totalMemory();
    long newFree = runtime.freeMemory();
    System.out.println("Memory: " + newMemory + " (" + memory + ") Free memory: " + newFree + " ("
        + free + ')');
    System.out.println("Used memory: " + (newMemory - newFree) + " (" + (memory - free) + ") => "
        + ((newMemory - newFree) - (memory - free)) + " bytes taken memory");
  }
  
} // TACSCMSimulationInfo
