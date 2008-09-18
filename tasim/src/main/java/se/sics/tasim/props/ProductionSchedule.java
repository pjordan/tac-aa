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
 * ProductionSchedule
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Tue Oct 29 14:32:38 2002
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
 * <code>ProductionSchedule</code> contains information about production to
 * perform.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class ProductionSchedule implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = -400899896032305684L;
  
  private final static int PRODUCT_ID = 0;
  private final static int QUANTITY = 1;
  
  private final static int PARTS = 2;
  
  private int[] data;
  private int count = 0;
  
  public ProductionSchedule()
  {
    this(5);
  }
  
  public ProductionSchedule(int initialSize)
  {
    data = new int[initialSize * PARTS];
  }
  
  /**
   * Adds a production request to this schedule.
   * 
   * @param productID
   *          the id of the product to be produced
   * @param quantity
   *          the quantity to produce
   */
  public synchronized void addProduction(int productID, int quantity)
  {
    int index = count * PARTS;
    if (index == data.length)
    {
      data = ArrayUtils.setSize(data, index + PARTS * 20);
    }
    data[index + PRODUCT_ID] = productID;
    data[index + QUANTITY] = quantity;
    count++;
  }
  
  public int getProductID(int index)
  {
    return get(index, PRODUCT_ID);
  }
  
  public int getQuantity(int index)
  {
    return get(index, QUANTITY);
  }
  
  public int size()
  {
    return count;
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
    StringBuffer sb = new StringBuffer();
    sb.append(getTransportName()).append('[');
    for (int i = 0, n = size(); i < n; i++)
    {
      sb.append('[').append(getProductID(i)).append(',').append(getQuantity(i))
          .append(']');
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
    return "productionSchedule";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    while (reader.nextNode("produce", false))
    {
      addProduction(reader.getAttributeAsInt("product"), reader
          .getAttributeAsInt("quantity"));
    }
  }
  
  public void write(TransportWriter writer)
  {
    for (int i = 0, index = 0; i < count; i++, index += PARTS)
    {
      writer.node("produce").attr("product", data[index + PRODUCT_ID]).attr(
          "quantity", data[index + QUANTITY]).endNode("produce");
    }
  }
  
} // ProductionSchedule
