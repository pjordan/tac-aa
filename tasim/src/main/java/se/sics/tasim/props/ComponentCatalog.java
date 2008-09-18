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
 * ComponentCatalog
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Nov 21 13:50:26 2002
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
 * <code>ComponentCatalog</code> contains information about which components
 * that are available and which supplier(s) that are producing them. This
 * message is typically sent in the beginning of a simulation/game.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class ComponentCatalog implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = -8955489326061882427L;
  
  private final static int PRODUCT_ID = 0;
  private final static int BASE_PRICE = 1;
  
  private final static int PARTS = 2;
  
  private boolean isLocked = false;
  
  private int[] data;
  private String[] productNames;
  private String[][] suppliers;
  private int count;
  
  private String[] supplierList;
  private int supplierCount;
  
  public ComponentCatalog()
  {
    this(10);
  }
  
  public ComponentCatalog(int initialSize)
  {
    data = new int[initialSize * PARTS];
    productNames = new String[initialSize];
    suppliers = new String[initialSize][];
    supplierList = new String[10];
  }
  
  public synchronized void addSupplier(String supplier, int[] productID)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    
    if (productID != null)
    {
      int supIndex = ArrayUtils.indexOf(supplierList, 0, supplierCount,
          supplier);
      if (supIndex < 0)
      {
        if (supplierCount == supplierList.length)
        {
          supplierList = (String[]) ArrayUtils.setSize(supplierList,
              supplierCount + 10);
        }
        supplierList[supplierCount++] = supplier;
      }
      else
      {
        // make sure the same object is used for faster comparison
        supplier = supplierList[supIndex];
      }
      
      for (int i = 0, n = productID.length; i < n; i++)
      {
        int index = getIndexFor(productID[i]);
        if (index < 0)
        {
          ensureCapacity(count);
          data[count * PARTS] = productID[i];
          productNames[count] = Integer.toString(productID[i]);
          index = count;
          count++;
        }
        suppliers[index] = (String[]) ArrayUtils.add(String.class,
            suppliers[index], supplier);
      }
    }
  }
  
  // Note: MAY ONLY BE CALLED SYNCHRONIZED
  private void ensureCapacity(int size)
  {
    if (productNames.length <= size)
    {
      int newSize = size + 10;
      data = ArrayUtils.setSize(data, newSize * PARTS);
      productNames = (String[]) ArrayUtils.setSize(productNames, newSize);
      suppliers = (String[][]) ArrayUtils.setSize(suppliers, newSize);
    }
  }
  
  /**
   * Returns the index of the specified product/component ID
   */
  public int getIndexFor(int productID)
  {
    for (int i = 0, index = 0, n = count * PARTS; i < n; i += 2, index++)
    {
      if (data[i + PRODUCT_ID] == productID)
      {
        return index;
      }
    }
    return -1;
  }
  
  /**
   * Returns the product/component ID at the position 'index'
   */
  public int getProductID(int index)
  {
    return get(index, PRODUCT_ID);
  }
  
  public int getProductBasePrice(int index)
  {
    return get(index, BASE_PRICE);
  }
  
  public void setProductBasePrice(int index, int price)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    data[index * PARTS + BASE_PRICE] = price;
  }
  
  /**
   * Returns the name of the component/product at position 'index'
   */
  public String getProductName(int index)
  {
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    return productNames[index];
  }
  
  public synchronized void setProductName(int index, String name)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    productNames[index] = name;
  }
  
  /**
   * Returns the suppliers for the product/component at the specified position
   */
  public String[] getSuppliers(int index)
  {
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    return suppliers[index];
  }
  
  /**
   * Returns the suppliers for the product/component with the specified ID Note:
   * returns NULL if no supplier exists
   */
  public String[] getSuppliersForProduct(int productID)
  {
    int index = getIndexFor(productID);
    if (index >= 0)
    {
      return suppliers[index];
    }
    return null;
  }
  
  /**
   * Returns the number of components in this catalog
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
    StringBuffer buf = new StringBuffer().append(getTransportName())
        .append('[');
    for (int i = 0, index = 0; i < count; i++, index += PARTS)
    {
      String[] supps = suppliers[i];
      if (i > 0)
      {
        buf.append(',');
      }
      buf.append('[').append(data[index + PRODUCT_ID]).append('=').append(
          productNames[i]).append(',').append(data[index + BASE_PRICE]);
      if (supps != null)
      {
        for (int j = 0, m = supps.length; j < m; j++)
        {
          buf.append(',').append(supps[j]);
        }
      }
      buf.append(']');
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
    return "componentCatalog";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    // No need to check lock here because addSupplier will handle it
    boolean locked = reader.getAttributeAsInt("lock", 0) > 0;
    
    while (reader.nextNode("supplier", false))
    {
      String supplier = reader.getAttribute("address");
      reader.enterNode();
      int[] products = readSubs(reader, "product", "id");
      addSupplier(supplier, products);
      reader.exitNode();
    }
    
    while (reader.nextNode("product", false))
    {
      int productID = reader.getAttributeAsInt("id");
      int index = getIndexFor(productID);
      if (index >= 0)
      {
        String productName = reader.getAttribute("name");
        int basePrice = reader.getAttributeAsInt("basePrice", 0);
        setProductName(index, productName);
        if (basePrice != 0)
        {
          setProductBasePrice(index, basePrice);
        }
      }
    }
    if (locked)
    {
      lock();
    }
  }
  
  private int[] readSubs(TransportReader reader, String name, String attrName)
      throws ParseException
  {
    if (!reader.hasMoreNodes())
    {
      return null;
    }
    
    int[] buffer = new int[10];
    int index = 0;
    while (reader.nextNode(name, false))
    {
      if (index == buffer.length)
      {
        buffer = ArrayUtils.setSize(buffer, index + 10);
      }
      buffer[index++] = reader.getAttributeAsInt(attrName);
    }
    
    if (index < buffer.length)
    {
      if (index == 0)
      {
        // No nodes found
        return null;
      }
      
      buffer = ArrayUtils.setSize(buffer, index);
    }
    return buffer;
  }
  
  public void write(TransportWriter writer)
  {
    if (isLocked)
    {
      writer.attr("lock", 1);
    }
    
    for (int i = 0; i < supplierCount; i++)
    {
      String sup = supplierList[i];
      writer.node("supplier").attr("address", sup);
      for (int j = 0, index = 0; j < count; j++, index += PARTS)
      {
        String[] supList = suppliers[j];
        if (supList != null)
        {
          for (int k = 0, n = supList.length; k < n; k++)
          {
            if (sup == supList[k])
            {
              // Supplier supports this kind of product
              writer.node("product").attr("id", data[index + PRODUCT_ID])
                  .endNode("product");
              break;
            }
          }
        }
      }
      writer.endNode("supplier");
    }
    
    for (int i = 0, index = 0; i < count; i++, index += PARTS)
    {
      String name = productNames[i];
      if (name != null)
      {
        int price = data[index + BASE_PRICE];
        writer.node("product").attr("id", data[index + PRODUCT_ID]).attr(
            "name", name);
        if (price != 0)
        {
          writer.attr("basePrice", price);
        }
        writer.endNode("product");
      }
    }
  }
  
} // ComponentCatalog
