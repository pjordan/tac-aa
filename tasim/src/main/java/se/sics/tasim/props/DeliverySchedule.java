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
 * DeliverySchedule
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Tue Oct 29 14:53:08 2002
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
 * <code>DeliverySchedule</code> contains information about deliveries to
 * make.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class DeliverySchedule implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = -1890418217734990962L;
  
  private final static int PRODUCT_ID = 0;
  private final static int ORDER_ID = 1;
  private final static int QUANTITY = 2;
  
  private final static int PARTS = 3;
  
  private int[] data;
  private String[] deliveryReceiver;
  private int count = 0;
  
  /** This is only a cache */
  private long totalQuantity = 0;
  
  public DeliverySchedule()
  {
    this(5);
  }
  
  public DeliverySchedule(int initialSize)
  {
    data = new int[initialSize * PARTS];
    deliveryReceiver = new String[initialSize];
  }
  
  /**
   * Adds a delivery request to this schedule.
   * 
   * @param productID
   *          the id of the product to be delivered
   * @param quantity
   *          the quantity to be delivered
   * @param orderID
   *          the id of the order for which the delivery will be made
   * @param customer
   *          the address of the receiver of the delivery
   */
  public synchronized void addDelivery(int productID, int quantity,
      int orderID, String customer)
  {
    int index = count * PARTS;
    if (index == data.length)
    {
      data = ArrayUtils.setSize(data, index + PARTS * 20);
      deliveryReceiver = (String[]) ArrayUtils.setSize(deliveryReceiver,
          count + 20);
    }
    data[index + PRODUCT_ID] = productID;
    data[index + ORDER_ID] = orderID;
    data[index + QUANTITY] = quantity;
    deliveryReceiver[count] = customer;
    count++;
    
    // Might just as well update the cache instead of marking it as dirty
    totalQuantity += quantity;
  }
  
  public int getOrderID(int index)
  {
    return get(index, ORDER_ID);
  }
  
  public String getCustomer(int index)
  {
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    return deliveryReceiver[index];
  }
  
  public int getProductID(int index)
  {
    return get(index, PRODUCT_ID);
  }
  
  public int getQuantity(int index)
  {
    return get(index, QUANTITY);
  }
  
  public int getTotalQuantity()
  {
    return Integer.MAX_VALUE <= totalQuantity ? Integer.MAX_VALUE
        : (int) totalQuantity;
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
    StringBuffer sb = new StringBuffer().append(getTransportName()).append('[');
    for (int i = 0, n = size(); i < n; i++)
    {
      sb.append('[').append(data[i * PARTS]);
      for (int j = 1; j < PARTS; j++)
      {
        sb.append(',').append(data[i * PARTS + j]);
      }
      sb.append(',').append(deliveryReceiver[i]).append(']');
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
    return "deliverySchedule";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    while (reader.nextNode("delivery", false))
    {
      int orderID = reader.getAttributeAsInt("order");
      int productID = reader.getAttributeAsInt("product");
      int quantity = reader.getAttributeAsInt("quantity");
      String customer = reader.getAttribute("customer");
      addDelivery(productID, quantity, orderID, customer);
    }
  }
  
  public void write(TransportWriter writer)
  {
    for (int i = 0, index = 0; i < count; i++, index += PARTS)
    {
      writer.node("delivery").attr("order", data[index + ORDER_ID]).attr(
          "product", data[index + PRODUCT_ID]).attr("quantity",
          data[index + QUANTITY]).attr("customer", deliveryReceiver[i])
          .endNode("delivery");
    }
  }
  
} // DeliverySchedule
