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
 * OfferBundle
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Nov 20 15:35:20 2002
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
 * <code>OfferBundle</code> contains an ordered set of offers. Each offer is a
 * response to an RFQ and contains:
 * <ul>
 * <li> Offer id - a, for the sender, unique id for this offer
 * <li> RFQ id - the id for the RFQ to which this offer is a response
 * <li> quantity - the offered quantity
 * <li> unit price - the offered price per item
 * <li> due date - the offered delivery date
 * </ul>
 * Any other information such as product and penalty can be found in the
 * referred RFQ.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class OfferBundle implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = -5172339880543668863L;
  
  private final static int OFFER_ID = 0;
  private final static int RFQ_ID = 1;
  private final static int QUANTITY = 2;
  private final static int UNIT_PRICE = 3;
  private final static int DUEDATE = 4;
  
  private final static int PARTS = 5;
  
  private int validDate;
  private int[] data;
  private int count;
  
  private boolean isLocked = false;
  
  public OfferBundle()
  {
    this(0, 5);
  }
  
  public OfferBundle(int validDate)
  {
    this(validDate, 5);
  }
  
  public OfferBundle(int validDate, int initialSize)
  {
    this.validDate = validDate;
    this.data = new int[initialSize * PARTS];
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
  
  public void addOffer(int offerID, RFQBundle rfqs, int rfqIndex, int unitPrice)
  {
    addOffer(offerID, rfqs, rfqIndex, unitPrice, rfqs.getDueDate(rfqIndex),
        rfqs.getQuantity(rfqIndex));
  }
  
  public void addOffer(int offerID, RFQBundle rfqs, int rfqIndex,
      int unitPrice, int dueDate, int quantity)
  {
    add(offerID, rfqs.getRFQID(rfqIndex), unitPrice, dueDate, quantity);
  }
  
  public void addOffer(int offerID, int rfqID, int unitPrice, int dueDate,
      int quantity)
  {
    // Maybe "remove" the below method and place it here...
    add(offerID, rfqID, unitPrice, dueDate, quantity);
  }
  
  private synchronized void add(int offerID, int rfqID, int unitPrice,
      int dueDate, int quantity)
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
    data[index + RFQ_ID] = rfqID;
    data[index + OFFER_ID] = offerID;
    data[index + QUANTITY] = quantity;
    data[index + UNIT_PRICE] = unitPrice;
    data[index + DUEDATE] = dueDate;
    count++;
  }
  
  public int getIndexFor(int offerID)
  {
    for (int i = 0, index = 0; index < count; i += PARTS, index++)
    {
      if (data[i + OFFER_ID] == offerID)
      {
        return index;
      }
    }
    return -1;
  }
  
  public int getOfferID(int index)
  {
    return get(index, OFFER_ID);
  }
  
  public int getRFQID(int index)
  {
    return get(index, RFQ_ID);
  }
  
  public int getQuantity(int index)
  {
    return get(index, QUANTITY);
  }
  
  public int getUnitPrice(int index)
  {
    return get(index, UNIT_PRICE);
  }
  
  public int getDueDate(int index)
  {
    return get(index, DUEDATE);
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
    StringBuffer sb = new StringBuffer().append(getTransportName()).append('[')
        .append(validDate).append(',');
    for (int index = 0, n = count * PARTS; index < n; index += PARTS)
    {
      sb.append('[').append(data[index]);
      for (int j = 1; j < PARTS; j++)
      {
        sb.append(',').append(data[index + j]);
      }
      sb.append(']');
    }
    return sb.append(']').toString();
  }
  
  /*****************************************************************************
   * Transportable (externalization support)
   ****************************************************************************/
  
  /**
   * Returns the transport name used for externalization.
   */
  public String getTransportName()
  {
    return "offerBundle";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    // No need to check if islocked because setValidDate will do that
    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
    setValidDate(reader.getAttributeAsInt("validDate", 0));
    while (reader.nextNode("offer", false))
    {
      add(reader.getAttributeAsInt("id"), reader.getAttributeAsInt("rfq"),
          reader.getAttributeAsInt("unitprice"), reader
              .getAttributeAsInt("duedate"), reader
              .getAttributeAsInt("quantity"));
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
    if (validDate != 0)
    {
      writer.attr("validDate", validDate);
    }
    for (int index = 0, n = count * PARTS; index < n; index += PARTS)
    {
      writer.node("offer").attr("id", data[index + OFFER_ID]).attr("rfq",
          data[index + RFQ_ID]).attr("unitprice", data[index + UNIT_PRICE])
          .attr("duedate", data[index + DUEDATE]).attr("quantity",
              data[index + QUANTITY]).endNode("offer");
    }
  }
  
} // OfferBundle
