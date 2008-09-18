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
 * ActiveOrders
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Jun 03 13:28:52 2004
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
 * <code>ActiveOrders</code> contains information about active orders.
 * <p>
 * 
 * Active orders are sent by the server when an agent connects to an already
 * running game/simulation.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class ActiveOrders implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = -8093079538891379712L;
  
  private final static int ORDER_ID = 0;
  private final static int PRODUCT_ID = 1;
  private final static int QUANTITY = 2;
  private final static int UNIT_PRICE = 3;
  private final static int DUEDATE = 4;
  private final static int PENALTY = 5;
  
  private final static int PARTS = 6;
  
  /** Names for the externalization support (Transportable) */
  private static final String[] names =
  { "id", "product", "quantity", "unitprice", "duedate", "penalty" };
  
  private int simDate;
  private int[] customerData;
  private String[] customerAddress;
  private int customerCount;
  
  private int[] supplierData;
  private String[] supplierAddress;
  private int supplierCount;
  
  private boolean isLocked = false;
  
  public ActiveOrders()
  {
    this.customerData = new int[PARTS * 10];
    this.customerAddress = new String[10];
    this.supplierData = new int[PARTS * 10];
    this.supplierAddress = new String[10];
  }
  
  public ActiveOrders(int simDate)
  {
    this();
    this.simDate = simDate;
  }
  
  public boolean isLocked()
  {
    return isLocked;
  }
  
  public void lock()
  {
    isLocked = true;
  }
  
  /**
   * Returns the current simulation date.
   */
  public int getCurrentDate()
  {
    return simDate;
  }
  
  // -------------------------------------------------------------------
  // Customer order handling
  // -------------------------------------------------------------------
  
  public void addCustomerOrder(String customer, int orderID, int productID,
      int quantity, int unitPrice, int dueDate, int penalty)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    if (customer == null)
    {
      throw new NullPointerException("customer");
    }
    int index = customerCount * PARTS;
    if (index == customerData.length)
    {
      customerData = ArrayUtils.setSize(customerData, index + PARTS * 50);
      customerAddress = (String[]) ArrayUtils.setSize(customerAddress,
          customerAddress.length + 50);
    }
    customerData[index + ORDER_ID] = orderID;
    customerData[index + PRODUCT_ID] = productID;
    customerData[index + QUANTITY] = quantity;
    customerData[index + UNIT_PRICE] = unitPrice;
    customerData[index + DUEDATE] = dueDate;
    customerData[index + PENALTY] = penalty;
    customerAddress[customerCount] = customer;
    customerCount++;
  }
  
  /**
   * Returns the customer address of the specified customer order.
   * 
   * @param index
   *          the index of the customer order
   * @return the customer address of the specified customer order
   */
  public String getCustomerAddress(int index)
  {
    if (index < 0 || index >= customerCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + customerCount);
    }
    return customerAddress[index];
  }
  
  /**
   * Returns the order id of the specified customer order.
   * 
   * @param index
   *          the index of the customer order
   * @return the order id of the specified customer order
   */
  public int getCustomerOrderID(int index)
  {
    return getCustomer(index, ORDER_ID);
  }
  
  /**
   * Returns the product id of the specified customer order.
   * 
   * @param index
   *          the index of the customer order
   * @return the product id of the specified customer order
   */
  public int getCustomerProductID(int index)
  {
    return getCustomer(index, PRODUCT_ID);
  }
  
  /**
   * Returns the purchased quantity in the specified customer order.
   * 
   * @param index
   *          the index of the customer order
   * @return the purchased quantity in the specified customer order
   */
  public int getCustomerQuantity(int index)
  {
    return getCustomer(index, QUANTITY);
  }
  
  /**
   * Returns the price per unit in the specified customer order.
   * 
   * @param index
   *          the index of the customer order
   * @return the price per unit in the specified customer order
   */
  public int getCustomerUnitPrice(int index)
  {
    return getCustomer(index, UNIT_PRICE);
  }
  
  /**
   * Returns the due date of the specified customer order.
   * 
   * @param index
   *          the index of the customer order
   * @return the due date of the specified customer order
   */
  public int getCustomerDueDate(int index)
  {
    return getCustomer(index, DUEDATE);
  }
  
  /**
   * Returns the penalty of the specified customer order.
   * 
   * @param index
   *          the index of the customer order
   * @return the penalty of the specified customer order
   */
  public int getCustomerPenalty(int index)
  {
    return getCustomer(index, PENALTY);
  }
  
  /**
   * Returns the number of active customer orders
   * 
   * @return the number of active customer orders
   */
  public int getCustomerOrderCount()
  {
    return customerCount;
  }
  
  private int getCustomer(int index, int delta)
  {
    if (index < 0 || index >= customerCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + customerCount);
    }
    return customerData[index * PARTS + delta];
  }
  
  // -------------------------------------------------------------------
  // Supplier order handling
  // -------------------------------------------------------------------
  
  public void addSupplierOrder(String supplier, int orderID, int productID,
      int quantity, int unitPrice, int dueDate, int penalty)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    if (supplier == null)
    {
      throw new NullPointerException("supplier");
    }
    int index = supplierCount * PARTS;
    if (index == supplierData.length)
    {
      supplierData = ArrayUtils.setSize(supplierData, index + PARTS * 50);
      supplierAddress = (String[]) ArrayUtils.setSize(supplierAddress,
          supplierAddress.length + 50);
    }
    supplierData[index + ORDER_ID] = orderID;
    supplierData[index + PRODUCT_ID] = productID;
    supplierData[index + QUANTITY] = quantity;
    supplierData[index + UNIT_PRICE] = unitPrice;
    supplierData[index + DUEDATE] = dueDate;
    supplierData[index + PENALTY] = penalty;
    supplierAddress[supplierCount] = supplier;
    supplierCount++;
  }
  
  /**
   * Returns the supplier address of the specified supplier order.
   * 
   * @param index
   *          the index of the supplier order
   * @return the supplier address of the specified supplier order
   */
  public String getSupplierAddress(int index)
  {
    if (index < 0 || index >= supplierCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + supplierCount);
    }
    return supplierAddress[index];
  }
  
  /**
   * Returns the order id of the specified supplier order.
   * 
   * @param index
   *          the index of the supplier order
   * @return the order id of the specified supplier order
   */
  public int getSupplierOrderID(int index)
  {
    return getSupplier(index, ORDER_ID);
  }
  
  /**
   * Returns the product id of the specified supplier order.
   * 
   * @param index
   *          the index of the supplier order
   * @return the product id of the specified supplier order
   */
  public int getSupplierProductID(int index)
  {
    return getSupplier(index, PRODUCT_ID);
  }
  
  /**
   * Returns the quantity in the specified supplier order.
   * 
   * @param index
   *          the index of the supplier order
   * @return the quantity in the specified supplier order
   */
  public int getSupplierQuantity(int index)
  {
    return getSupplier(index, QUANTITY);
  }
  
  /**
   * Returns the price per unit in the specified supplier order.
   * 
   * @param index
   *          the index of the supplier order
   * @return the price per unit in the specified supplier order
   */
  public int getSupplierUnitPrice(int index)
  {
    return getSupplier(index, UNIT_PRICE);
  }
  
  /**
   * Returns the due date of the specified supplier order.
   * 
   * @param index
   *          the index of the supplier order
   * @return the due date of the specified supplier order
   */
  public int getSupplierDueDate(int index)
  {
    return getSupplier(index, DUEDATE);
  }
  
  /**
   * Returns the penalty of the specified supplier order.
   * 
   * @param index
   *          the index of the supplier order
   * @return the penalty of the specified supplier order
   */
  public int getSupplierPenalty(int index)
  {
    return getSupplier(index, PENALTY);
  }
  
  public int getSupplierOrderCount()
  {
    return supplierCount;
  }
  
  private int getSupplier(int index, int delta)
  {
    if (index < 0 || index >= supplierCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + supplierCount);
    }
    return supplierData[index * PARTS + delta];
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(getTransportName()).append("[[");
    for (int index = 0, i = 0, n = customerCount * PARTS; index < n; index += PARTS, i++)
    {
      sb.append('[').append(customerAddress[i]);
      for (int j = 0; j < PARTS; j++)
      {
        sb.append(',').append(customerData[index + j]);
      }
      sb.append(']');
    }
    sb.append("][");
    for (int index = 0, i = 0, n = supplierCount * PARTS; index < n; index += PARTS, i++)
    {
      sb.append('[').append(supplierAddress[i]);
      for (int j = 0; j < PARTS; j++)
      {
        sb.append(',').append(supplierData[index + j]);
      }
      sb.append(']');
    }
    sb.append(']');
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
    return "activeOrders";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    // No need to check if islocked because setValidDate will do that
    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
    simDate = reader.getAttributeAsInt("date", 0);
    
    String customer = null;
    String supplier = null;
    while (reader.nextNode(false))
    {
      if (reader.isNode("customerOrder"))
      {
        // Cache the customer between calls because often the customer
        // is the same and it is unnecessary to send it for each
        // order. The code below below ensures that an error occurs if
        // no customer has been specified for first customer order.
        customer = (customer == null) ? reader.getAttribute("customer")
            : reader.getAttribute("customer", customer);
        addCustomerOrder(customer, reader.getAttributeAsInt(names[ORDER_ID]),
            reader.getAttributeAsInt(names[PRODUCT_ID]), reader
                .getAttributeAsInt(names[QUANTITY]), reader
                .getAttributeAsInt(names[UNIT_PRICE]), reader
                .getAttributeAsInt(names[DUEDATE]), reader.getAttributeAsInt(
                names[PENALTY], 0));
        
      }
      else if (reader.isNode("supplierOrder"))
      {
        // Cache the supplier between calls because often the supplier
        // is the same and it is unnecessary to send it for each
        // order. The code below below ensures that an error occurs if
        // no supplier has been specified for first supplier order.
        supplier = (supplier == null) ? reader.getAttribute("supplier")
            : reader.getAttribute("supplier", supplier);
        addSupplierOrder(supplier, reader.getAttributeAsInt(names[ORDER_ID]),
            reader.getAttributeAsInt(names[PRODUCT_ID]), reader
                .getAttributeAsInt(names[QUANTITY]), reader
                .getAttributeAsInt(names[UNIT_PRICE]), reader
                .getAttributeAsInt(names[DUEDATE]), reader.getAttributeAsInt(
                names[PENALTY], 0));
      }
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
    if (simDate > 0)
    {
      writer.attr("date", simDate);
    }
    
    String customer = null;
    for (int index = 0, i = 0, n = customerCount * PARTS; index < n; index += PARTS, i++)
    {
      writer.node("customerOrder");
      // Only need to send the customer if changed since last sent
      // customer order (saves communication because the customer is
      // often the same). In the first iteration the cached customer
      // is NULL which never equals to a string value.
      if (!customerAddress[i].equals(customer))
      {
        customer = customerAddress[i];
        writer.attr("customer", customer);
      }
      for (int j = 0; j < PARTS; j++)
      {
        writer.attr(names[j], customerData[index + j]);
      }
      writer.endNode("customerOrder");
    }
    
    String supplier = null;
    for (int index = 0, i = 0, n = supplierCount * PARTS; index < n; index += PARTS, i++)
    {
      writer.node("supplierOrder");
      // Only need to send the supplier if changed since last sent
      // supplier order (saves communication because the supplier is
      // often the same). In the first iteration the cached supplier
      // is NULL which never equals to a string value.
      if (!supplierAddress[i].equals(supplier))
      {
        supplier = supplierAddress[i];
        writer.attr("supplier", supplier);
      }
      writer.attr(names[ORDER_ID], supplierData[index + ORDER_ID]).attr(
          names[PRODUCT_ID], supplierData[index + PRODUCT_ID]).attr(
          names[QUANTITY], supplierData[index + QUANTITY]).attr(
          names[UNIT_PRICE], supplierData[index + UNIT_PRICE]).attr(
          names[DUEDATE], supplierData[index + DUEDATE]);
      // The penalty is often 0 for supplier orders and does not need
      // to be sent
      int penalty = supplierData[index + PENALTY];
      if (penalty != 0)
      {
        writer.attr(names[PENALTY], penalty);
      }
      writer.endNode("supplierOrder");
    }
  }
  
} // ActiveOrders
