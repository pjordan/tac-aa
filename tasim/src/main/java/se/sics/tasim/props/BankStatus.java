/**
 * TAC Supply Chain Management Simulator
 * http://www.sics.se/tac/    tac-dev@sics.se
 *
 * Copyright (c) 2001-2003 SICS AB. All rights reserved.
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
 * BankStatus
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Fri Feb 14 14:32:48 2003
 * Updated : $Date: 2008-04-04 21:07:49 -0500 (Fri, 04 Apr 2008) $
 *           $Revision: 3982 $
 */
package se.sics.tasim.props;

import java.text.ParseException;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

/**
 * <code>BankStatus</code> contains information about a participants bank
 * account, recent penalties, and recent denied deliveries.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class BankStatus implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = -9089664535771907257L;
  
  private final static int ORDER_ID = 0;
  private final static int AMOUNT = 1;
  
  private final static int PARTS = 2;
  
  private int[] penalties;
  private String[] penaltyReceivers;
  private int penaltyCount;
  
  private int[] deniedDeliveries;
  private String[] deniers;
  private int deniedCount;
  
  private long account = 0;
  
  /** This is only a cache */
  private long totalPenalty;
  
  public BankStatus()
  {}
  
  public long getAccountStatus()
  {
    return account;
  }
  
  public void setAccountStatus(long account)
  {
    this.account = account;
  }
  
  public void addPenalty(String penaltyReceiver, int orderID, int amount)
  {
    if (penaltyReceivers == null)
    {
      penaltyReceivers = new String[10];
      penalties = new int[10 * PARTS];
      
    }
    else if (penaltyCount == penaltyReceivers.length)
    {
      int newSize = penaltyCount + 50;
      penaltyReceivers = (String[]) ArrayUtils.setSize(penaltyReceivers,
          newSize);
      penalties = ArrayUtils.setSize(penalties, newSize * PARTS);
    }
    
    int index = penaltyCount * PARTS;
    penalties[index + ORDER_ID] = orderID;
    penalties[index + AMOUNT] = amount;
    penaltyReceivers[penaltyCount++] = penaltyReceiver;
    
    // Might just as well update the cache instead of marking it as dirty
    totalPenalty += amount;
  }
  
  /**
   * Returns the number of penalties
   */
  public int getPenaltyCount()
  {
    return penaltyCount;
  }
  
  /**
   * Returns the customer that claimed the specified penalty
   * 
   * @param index
   *          the index of the penalty
   * @return the customer that claimed the penalty
   */
  public String getPenaltyReceiver(int index)
  {
    if (index < 0 || index >= penaltyCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + penaltyCount);
    }
    return penaltyReceivers[index];
  }
  
  /**
   * Returns the id of the order for which the specified penalty was claimed
   * 
   * @param index
   *          the index of the penalty
   * @return the id of the order for which the penalty was claimed
   */
  public int getPenaltyOrderID(int index)
  {
    return get(index, ORDER_ID);
  }
  
  /**
   * Returns the claimed amount for the specified penalty
   * 
   * @param index
   *          the index of the penalty
   * @return the claimed amount for the penalty
   */
  public int getPenaltyAmount(int index)
  {
    return get(index, AMOUNT);
  }
  
  /**
   * Returns the total amount claimed for all penalties specified in this bank
   * status.
   */
  public long getTotalPenaltyAmount()
  {
    return totalPenalty;
  }
  
  private int get(int index, int delta)
  {
    if (index < 0 || index >= penaltyCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + penaltyCount);
    }
    return penalties[index * PARTS + delta];
  }
  
  public void addDeniedDelivery(String denier, int orderID)
  {
    if (deniedDeliveries == null)
    {
      deniedDeliveries = new int[10];
      deniers = new String[10];
    }
    else if (deniedCount == deniedDeliveries.length)
    {
      deniedDeliveries = ArrayUtils.setSize(deniedDeliveries, deniedCount + 10);
      deniers = (String[]) ArrayUtils.setSize(deniers, deniedCount + 10);
    }
    deniers[deniedCount] = denier;
    deniedDeliveries[deniedCount++] = orderID;
  }
  
  /**
   * Returns the number of denied deliveries
   */
  public int getDeniedCount()
  {
    return deniedCount;
  }
  
  /**
   * Returns the order id of the specified delivery
   * 
   * @param index
   *          the index of the delivery
   * @return the id of the delivered order
   */
  public int getDeniedOrderID(int index)
  {
    if (index < 0 || index >= deniedCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + deniedCount);
    }
    return deniedDeliveries[index];
  }
  
  /**
   * Returns the receiver that did not accept the specified delivery.
   * 
   * @param index
   *          the index of the delivery
   * @return the receiver of the delivery
   */
  public String getDenier(int index)
  {
    if (index < 0 || index >= deniedCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + deniedCount);
    }
    return deniers[index];
  }
  
  public void clear()
  {
    this.penaltyCount = 0;
    this.account = 0L;
    this.totalPenalty = 0L;
    
    int count = this.deniedCount;
    this.deniedCount = 0;
    while (--count >= 0)
    {
      deniers[count] = null;
    }
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(getTransportName()).append('[').append(account).append(',')
        .append(penaltyCount).append(',').append(deniedCount).append(']');
    return sb.toString();
  }
  
  // -------------------------------------------------------------------
  // Transportable (externalization support)
  // -------------------------------------------------------------------
  
  /**
   * Returns the transport name used for externalization.
   */
  public String getTransportName()
  {
    return "bankStatus";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    String receiver = null;
    String denier = null;
    account = reader.getAttributeAsLong("account", 0L);
    
    while (reader.nextNode(false))
    {
      if (reader.isNode("penalty"))
      {
        // Cache the receiver between calls because often the penalty
        // receiver is the same and it is unnecessary to send it for
        // each penalty
        receiver = (receiver == null) ? reader.getAttribute("receiver")
            : reader.getAttribute("receiver", receiver);
        addPenalty(receiver, reader.getAttributeAsInt("orderID"), reader
            .getAttributeAsInt("amount"));
      }
      else if (reader.isNode("deniedDelivery"))
      {
        denier = (denier == null) ? reader.getAttribute("denier") : reader
            .getAttribute("denier", denier);
        addDeniedDelivery(denier, reader.getAttributeAsInt("orderID"));
      }
    }
  }
  
  public void write(TransportWriter writer)
  {
    if (account != 0L)
    {
      writer.attr("account", account);
    }
    
    String receiver = null;
    for (int i = 0, index = 0; i < penaltyCount; i++, index += PARTS)
    {
      writer.node("penalty");
      
      // Only need to send the penalty receiver if changed since last
      // sent receiver (saves communication because the penalty
      // receiver is often the same). In the first iteration the
      // cached receiver is NULL which never equals to a string value.
      if (!penaltyReceivers[i].equals(receiver))
      {
        receiver = penaltyReceivers[i];
        writer.attr("receiver", receiver);
      }
      writer.attr("orderID", penalties[index + ORDER_ID]).attr("amount",
          penalties[index + AMOUNT]).endNode("penalty");
    }
    
    String denier = null;
    for (int i = 0; i < deniedCount; i++)
    {
      writer.node("deniedDelivery");
      
      // Only need to send the denier if changed since last sent
      // denier (saves communication because the denier is often the
      // same). In the first iteration the cached denier is NULL
      // which never equals to a string value.
      if (!deniers[i].equals(denier))
      {
        denier = deniers[i];
        writer.attr("denier", denier);
      }
      writer.attr("orderID", deniedDeliveries[i]).endNode("deniedDelivery");
    }
  }
  
} // BankStatus
