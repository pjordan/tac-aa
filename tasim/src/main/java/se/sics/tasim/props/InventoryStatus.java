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
 * InventoryStatus
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Oct 30 10:31:22 2002
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
 * <code>InventoryStatus</code> contains information about the quantity of
 * products in an inventory.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class InventoryStatus implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = 1075202107462872293L;
  
  private final static int PRODUCT_ID = 0;
  private final static int QUANTITY = 1;
  
  private final static int PARTS = 2;
  
  private int[] inventory;
  private int count;
  
  private boolean isLocked = false;
  
  public InventoryStatus()
  {}
  
  public InventoryStatus(InventoryStatus is)
  {
    if (is != null && is.count > 0)
    {
      this.count = is.count;
      
      int size = this.count * PARTS;
      this.inventory = new int[size];
      System.arraycopy(is.inventory, 0, this.inventory, 0, size);
    }
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
   * Returns the number of product types in this inventory
   */
  public int getProductCount()
  {
    return count;
  }
  
  /**
   * Returns the id of the product at the specified index
   * 
   * @param index
   *          the index of the product in this inventory
   * @return the id of the product
   */
  public int getProductID(int index)
  {
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    return inventory[index * PARTS + PRODUCT_ID];
  }
  
  /**
   * Returns the quantity of the product at the specified index
   * 
   * @param index
   *          the index of the product in this inventory
   * @return the quantity of the product in this inventory
   */
  public int getQuantity(int index)
  {
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    return inventory[index * PARTS + QUANTITY];
  }
  
  /**
   * Returns the quantity of the specified product type
   * 
   * @param productID
   *          the id of the product
   * @return the quantity of the product in this inventory or 0 if no such
   *         product is in the inventory
   */
  public int getInventoryQuantity(int productID)
  {
    int index = ArrayUtils.keyValuesIndexOf(inventory, PARTS, PRODUCT_ID, count
        * PARTS, productID);
    if (index >= 0)
    {
      return inventory[index - PRODUCT_ID + QUANTITY];
    }
    return 0;
  }
  
  /**
   * Adds a number of products of the specified product type to this inventory.
   * 
   * @param productID
   *          the id of the product whose quantity to change
   * @param quantity
   *          the quantity to add to this inventory
   */
  public void addInventory(int productID, int quantity)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    int index = ArrayUtils.keyValuesIndexOf(inventory, PARTS, PRODUCT_ID, count
        * PARTS, productID);
    if (index >= 0)
    {
      inventory[index - PRODUCT_ID + QUANTITY] += quantity;
    }
    else
    {
      index = count * PARTS;
      if (inventory == null)
      {
        inventory = new int[5 * PARTS];
      }
      else if (index == inventory.length)
      {
        inventory = ArrayUtils.setSize(inventory, index + 5 * PARTS);
      }
      inventory[index + PRODUCT_ID] = productID;
      inventory[index + QUANTITY] = quantity;
      count++;
    }
  }
  
  /**
   * Removes a number of products of the specified product type to this
   * inventory.
   * 
   * Note: the quantity in this inventory status might become negative if there
   * were not enough products in the inventory.
   * 
   * @param productID
   *          the id of the product whose quantity to change
   * @param quantity
   *          the quantity to remove
   */
  public void removeInventory(int productID, int quantity)
  {
    addInventory(productID, -quantity);
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer().append(getTransportName()).append('[');
    for (int i = 0, n = getProductCount() * PARTS; i < n; i += PARTS)
    {
      if (i > 0)
        sb.append(',');
      sb.append(inventory[i + PRODUCT_ID]).append('=').append(
          inventory[i + QUANTITY]);
    }
    sb.append(']');
    return sb.toString();
  }
  
  /*****************************************************************************
   * Transportable (externalization support)
   ****************************************************************************/
  
  /**
   * Returns the transport name used for externalization.
   */
  public String getTransportName()
  {
    return "inventoryStatus";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
    while (reader.nextNode("inventory", false))
    {
      int productID = reader.getAttributeAsInt("product");
      int quantity = reader.getAttributeAsInt("quantity");
      addInventory(productID, quantity);
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
    for (int i = 0, n = count * PARTS; i < n; i += PARTS)
    {
      int quantity = inventory[i + QUANTITY];
      if (quantity != 0)
      {
        writer.node("inventory").attr("product", inventory[i + PRODUCT_ID])
            .attr("quantity", inventory[i + QUANTITY]).endNode("inventory");
      }
    }
  }
  
} // InventoryStatus
