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
 * DeliveryNotice
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Nov 28 08:53:39 2002
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
 * <code>DeliveryNotice</code> contains information about a set of deliveries
 * made between two parts.
 * 
 * Each delivery contains:
 * <ul>
 * <li> order id - the order for which the delivery was made
 * <li> product id - the delivered product
 * <li> quantity - the delivered quantity
 * </ul>
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class DeliveryNotice implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = 599522166454448370L;
  
  private final static int ORDER_ID = 0;
  private final static int PRODUCT_ID = 1;
  private final static int QUANTITY = 2;
  
  private final static int PARTS = 3;
  
  private String customer;
  private String supplier;
  
  private int[] data;
  private int count;
  
  private boolean isLocked = false;
  
  /** This is only a cache */
  private long totalQuantity = 0;
  
  public DeliveryNotice(String supplier, String customer)
  {
    this(supplier, customer, 5);
  }
  
  public DeliveryNotice(String supplier, String customer, int initialSize)
  {
    if (supplier == null)
    {
      throw new NullPointerException("supplier");
    }
    if (customer == null)
    {
      throw new NullPointerException("customer");
    }
    this.customer = customer;
    this.supplier = supplier;
    data = new int[initialSize * PARTS];
  }
  
  public DeliveryNotice()
  {
    data = new int[5 * PARTS];
  }
  
  /**
   * Returns the address of the receiver of these deliveries
   */
  public String getCustomer()
  {
    return customer;
  }
  
  /**
   * Returns the address of the sender of these deliveries
   */
  public String getSupplier()
  {
    return supplier;
  }
  
  public synchronized void addDelivery(int orderID, int productCode,
      int quantity)
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
    data[index + PRODUCT_ID] = productCode;
    data[index + QUANTITY] = quantity;
    count++;
    
    // Might just as well update the cache instead of marking it as dirty
    totalQuantity += quantity;
  }
  
  /**
   * Returns the order for which the specified delivery was made
   * 
   * @param index
   *          the index of the delivery in this notice
   * @return the order id for which the delivery was made
   */
  public int getOrderID(int index)
  {
    return get(index, ORDER_ID);
  }
  
  /**
   * Returns the product in the specified delivery
   * 
   * @param index
   *          the index of the delivery in this notice
   * @return the id of the delivered product
   */
  public int getProductID(int index)
  {
    return get(index, PRODUCT_ID);
  }
  
  /**
   * Returns the quantity in the specified delivery
   * 
   * @param index
   *          the index of the delivery in this notice
   * @return the quantity of the delivered product
   */
  public int getQuantity(int index)
  {
    return get(index, QUANTITY);
  }
  
  /**
   * Returns the total quantity of all delivered products in this notice.
   * 
   * @return the total delivered quantity
   */
  public int getTotalQuantity()
  {
    return Integer.MAX_VALUE <= totalQuantity ? Integer.MAX_VALUE
        : (int) totalQuantity;
  }
  
  /**
   * Returns the number of deliveries in this notice.
   */
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
        .append(supplier).append(',').append(customer);
    for (int i = 0; i < count; i++)
    {
      int index = i * PARTS;
      sb.append(',').append('[').append(data[index + ORDER_ID]).append(',')
          .append(data[index + PRODUCT_ID]).append(',').append(
              data[index + QUANTITY]).append(']');
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
    return "deliveryNotice";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    if (customer != null)
    {
      throw new IllegalStateException("already initialized");
    }
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
    customer = reader.getAttribute("customer");
    supplier = reader.getAttribute("supplier");
    
    while (reader.nextNode("delivery", false))
    {
      int orderID = reader.getAttributeAsInt("order");
      int productID = reader.getAttributeAsInt("product");
      int quantity = reader.getAttributeAsInt("quantity");
      addDelivery(orderID, productID, quantity);
    }
    isLocked = lock;
  }
  
  public void write(TransportWriter writer)
  {
    writer.attr("customer", customer).attr("supplier", supplier);
    if (isLocked)
    {
      writer.attr("lock", 1);
    }
    
    for (int i = 0, index = 0; i < count; i++, index += PARTS)
    {
      writer.node("delivery").attr("order", data[index + ORDER_ID]).attr(
          "product", data[index + PRODUCT_ID]).attr("quantity",
          data[index + QUANTITY]).endNode("delivery");
    }
  }
  
} // DeliveryNotice
