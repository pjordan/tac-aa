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
 * RFQBundle
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Nov 20 15:33:24 2002
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
 * <code>RFQBundle</code> contains an ordered set of requests for quotes.
 * 
 * Each request for quote (RFQ) contains:
 * <ul>
 * <li> RFQ id - a, for the sender, unique id for this RFQ
 * <li> Product id - the requested product
 * <li> Quantity - the requested quantity of products
 * <li> Due date - the latest delivery date
 * <li> Penalty - the penalty for late delivery of the order (optional)
 * <li> Reserve price per unit - the reserve price for each unit (optional)
 * </ul>
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class RFQBundle implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = 746403139800839531L;
  
  private final static int RFQ_ID = 0;
  private final static int PRODUCT_ID = 1;
  private final static int QUANTITY = 2;
  private final static int DUEDATE = 3;
  private final static int PENALTY = 4;
  private final static int RESERVE_PRICE = 5;
  
  private final static int PARTS = 6;
  
  /** Names for the externalization support (Transportable) */
  private static final String[] names =
  { "id", "product", "quantity", "duedate", "penalty", "reservePricePerUnit" };
  
  private int validDate;
  private int[] rfqData;
  private int rfqCount;
  private int startID = -1;
  private boolean isLocked = false;
  
  /** This is only a cache */
  private long totalQuantity = 0;
  
  public RFQBundle()
  {
    this(0, 5);
  }
  
  public RFQBundle(int validDate)
  {
    this(validDate, 5);
  }
  
  public RFQBundle(int validDate, int initialSize)
  {
    this.validDate = validDate;
    this.rfqData = new int[initialSize * PARTS];
  }
  
  public int getValidDate()
  {
    return validDate;
  }
  
  public void setValidDate(int validDate)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    this.validDate = validDate;
  }
  
  /**
   * @deprecated Replaced by <code>addRFQ(int,int,int,int,int,int)</code>.
   * @see #addRFQ(int,int,int,int,int,int)
   */
  public void addRFQ(int rfqID, int productID, int quantity, int dueDate)
  {
    addRFQ(rfqID, productID, quantity, 0, dueDate, 0);
  }
  
  /**
   * @deprecated Replaced by <code>addRFQ(int,int,int,int,int,int)</code>.
   * @see #addRFQ(int,int,int,int,int,int)
   */
  public void addRFQ(int rfqID, int productID, int quantity, int dueDate,
      int penalty)
  {
    addRFQ(rfqID, productID, quantity, 0, dueDate, penalty);
  }
  
  /**
   * Adds the specified RFQ to this bundle.
   * 
   * @param rfqID
   *          the id of the RFQ
   * @param productID
   *          the requested component
   * @param quantity
   *          the requested quantity
   * @param reservePricePerUnit
   *          the reservation price or 0 for no price constraints
   * @param dueDate
   *          the requested delivery date
   * @param penalty
   *          the penalty for late deliveries or 0 for no penalty
   * @throws IllegalStateException
   *           if this bundle is locked
   */
  public synchronized void addRFQ(int rfqID, int productID, int quantity,
      int reservePricePerUnit, int dueDate, int penalty)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    int index = rfqCount * PARTS;
    if (index == rfqData.length)
    {
      rfqData = ArrayUtils.setSize(rfqData, index + PARTS * 50);
    }
    rfqData[index + RFQ_ID] = rfqID;
    rfqData[index + PRODUCT_ID] = productID;
    rfqData[index + QUANTITY] = quantity;
    rfqData[index + DUEDATE] = dueDate;
    rfqData[index + PENALTY] = penalty;
    rfqData[index + RESERVE_PRICE] = reservePricePerUnit;
    rfqCount++;
    
    if (startID < 0)
    {
      startID = rfqID;
    }
    
    // Might just as well update the cache instead of marking it as dirty
    totalQuantity += quantity < 0 ? -quantity : quantity;
  }
  
  /**
   * Returns the index for the specified RFQ.
   * 
   * @param rfqID
   *          the id of the RFQ
   * @return the index of the specified RFQ or -1 if the RFQ was not found in
   *         this bundle
   */
  public int getIndexFor(int rfqID)
  {
    if ((rfqID < (startID + rfqCount)) && (rfqID >= startID)
        && (rfqData[(rfqID - startID) * PARTS + RFQ_ID] == rfqID))
    {
      // Optimization to quicker retrieve the index when the RFQs are
      // in ID order (which is usually the case)
      return rfqID - startID;
    }
    
    for (int i = 0, index = 0; index < rfqCount; i += PARTS, index++)
    {
      if (rfqData[i + RFQ_ID] == rfqID)
      {
        return index;
      }
    }
    return -1;
  }
  
  /**
   * Returns the id of the specified RFQ.
   * 
   * @param index
   *          the index of the RFQ in this bundle
   * @return the id of the RFQ
   */
  public int getRFQID(int index)
  {
    return get(index, RFQ_ID);
  }
  
  /**
   * Returns the requested product in the specified RFQ.
   * 
   * @param index
   *          the index of the RFQ in this bundle
   * @return the id of the requested product
   */
  public int getProductID(int index)
  {
    return get(index, PRODUCT_ID);
  }
  
  /**
   * Returns the requested quantity in the specified RFQ.
   * 
   * @param index
   *          the index of the RFQ in this bundle
   * @return the quantity of products requested in the specified RFQ
   */
  public int getQuantity(int index)
  {
    return get(index, QUANTITY);
  }
  
  /**
   * Returns the total quantity of all products requested in all RFQs in this
   * bundle.
   */
  public int getTotalQuantity()
  {
    return Integer.MAX_VALUE < totalQuantity ? Integer.MAX_VALUE
        : (int) totalQuantity;
  }
  
  /**
   * Returns the latest delivery date for the specified RFQ.
   * 
   * @param index
   *          the index of the RFQ in this bundle
   * @return the latest delivery date
   */
  public int getDueDate(int index)
  {
    return get(index, DUEDATE);
  }
  
  /**
   * Returns the penalty for late deliveries of the specified RFQ. The penalty
   * is specified as the total sum that is charged on late deliveries. Usually
   * the penalty is charged day for day until the delivery has been made or the
   * order been voided.
   * 
   * @param index
   *          the index of the RFQ in this bundle
   * @return the penalty for late deliveries or 0 for no penalty
   */
  public int getPenalty(int index)
  {
    return get(index, PENALTY);
  }
  
  /**
   * Returns the reserve price per unit for the specified RFQ. This is the
   * maximal price the sender of the request is willing to pay per unit in the
   * order.
   * <p>
   * 
   * This is optional information that the sender might supply.
   * 
   * @param index
   *          the index of the RFQ in this bundle
   * @return the reserve price per unit or 0 if no reserve price has been
   *         specified by the sender
   */
  public int getReservePricePerUnit(int index)
  {
    return get(index, RESERVE_PRICE);
  }
  
  /**
   * Returns the number of RFQs in this bundle.
   */
  public int size()
  {
    return rfqCount;
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
    if (index < 0 || index >= rfqCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + rfqCount);
    }
    return rfqData[index * PARTS + delta];
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(getTransportName()).append('[');
    if (validDate != 0)
    {
      sb.append(validDate).append(',');
    }
    for (int index = 0, n = rfqCount * PARTS; index < n; index += PARTS)
    {
      sb.append('[').append(rfqData[index]);
      for (int j = 1; j < PARTS; j++)
      {
        sb.append(',').append(rfqData[index + j]);
      }
      sb.append(']');
    }
    sb.append(']');
    return sb.toString();
  }
  
  // -------------------------------------------------------------------
  // Transportable (externalization support)
  // -------------------------------------------------------------------
  
  public String getTransportName()
  {
    return "rfqBundle";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    // No need to check if islocked because setValidDate will do that
    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
    setValidDate(reader.getAttributeAsInt("validDate", 0));
    
    while (reader.nextNode("rfq", false))
    {
      // START OF BACKWARD COMPABILITY: REMOVE THIS!!!
      int quantity = reader.getAttributeAsInt(names[QUANTITY]);
      int reservePricePerUnit = reader.getAttributeAsInt(names[RESERVE_PRICE],
          0);
      if (reservePricePerUnit == 0 && quantity > 0)
      {
        reservePricePerUnit = reader.getAttributeAsInt("reservePrice", 0)
            / quantity;
      }
      // END OF BACKWARD COMPABILITY: REMOVE THIS!!!
      addRFQ(reader.getAttributeAsInt(names[RFQ_ID]), reader
          .getAttributeAsInt(names[PRODUCT_ID]), quantity, reservePricePerUnit,
          reader.getAttributeAsInt(names[DUEDATE]), reader.getAttributeAsInt(
              names[PENALTY], 0));
    }
    isLocked = lock;
  }
  
  public void write(TransportWriter writer)
  {
    if (isLocked)
    {
      writer.attr("lock", 1);
    }
    if (validDate != 0)
    {
      writer.attr("validDate", validDate);
    }
    
    // Reserve price and penalty is not needed to be sent when 0
    // (for example when a manufacturer sends it to a supplier).
    // Might be optimized when everyone has updated their reading code.
    for (int index = 0, n = rfqCount * PARTS; index < n; index += PARTS)
    {
      writer.node("rfq");
      for (int j = 0; j < PARTS; j++)
      {
        writer.attr(names[j], rfqData[index + j]);
      }
      writer.endNode("rfq");
    }
  }
  
} // RFQBundle
