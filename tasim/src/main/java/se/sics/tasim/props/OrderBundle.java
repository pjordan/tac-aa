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
 * OrderBundle
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Nov 20 15:39:50 2002
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
 * <code>OrderBundle</code> contains an set of orders. Each order contains:
 * <ul>
 * <li> order id - a, for the sender, unique id for this order
 * <li> offer id - the id of the offer that is accepted with this order
 * </ul>
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class OrderBundle implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = 1361992356868562281L;
  
  private final static int ORDER_ID = 0;
  private final static int OFFER_ID = 1;
  
  private final static int PARTS = 2;
  
  private int[] data;
  private int count;
  private boolean isLocked = false;
  
  public OrderBundle()
  {
    this(5);
  }
  
  public OrderBundle(int initialSize)
  {
    data = new int[initialSize * PARTS];
  }
  
  /**
   * @deprecated Use addOrder(int orderID, int offerID) instead
   */
  public void addOrder(int orderID, OfferBundle offers, int offerIndex)
  {
    addOrder(orderID, offers.getOfferID(offerIndex));
  }
  
  public synchronized void addOrder(int orderID, int offerID)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    int index = count * PARTS;
    if (index == data.length)
    {
      data = ArrayUtils.setSize(data, index + PARTS * 50);
    }
    data[index + ORDER_ID] = orderID;
    data[index + OFFER_ID] = offerID;
    count++;
  }
  
  public int getOrderID(int index)
  {
    return get(index, ORDER_ID);
  }
  
  public int getOfferID(int index)
  {
    return get(index, OFFER_ID);
  }
  
  public int size()
  {
    return count;
  }
  
  public boolean isLocked()
  {
    return isLocked;
  }
  
  public void lock()
  {
    isLocked = true;
  }
  
  private int get(int index, int delta)
  {
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    return data[index * PARTS + delta];
  }
  
  public String toString()
  {
    StringBuffer buf = new StringBuffer().append(getTransportName())
        .append('[');
    for (int index = 0, n = count * PARTS; index < n; index += PARTS)
    {
      if (index > 0)
        buf.append(',');
      buf.append('[').append(data[index + ORDER_ID]).append(',').append(
          data[index + OFFER_ID]).append(']');
    }
    return buf.append(']').toString();
  }
  
  /*****************************************************************************
   * Transportable (externalization support)
   ****************************************************************************/
  
  /**
   * Returns the transport name used for externalization.
   */
  public String getTransportName()
  {
    return "orderBundle";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    // No need to check if islocked because addOrder will do that
    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
    
    while (reader.nextNode("order", false))
    {
      addOrder(reader.getAttributeAsInt("id"), reader
          .getAttributeAsInt("offer"));
    }
    if (lock)
    {
      lock();
    }
  }
  
  public void write(TransportWriter writer)
  {
    if (isLocked)
    {
      writer.attr("lock", 1);
    }
    
    for (int i = 0, index = 0; i < count; i++, index += PARTS)
    {
      writer.node("order").attr("id", data[index + ORDER_ID]).attr("offer",
          data[index + OFFER_ID]).endNode("order");
    }
  }
  
} // OrderBundle
