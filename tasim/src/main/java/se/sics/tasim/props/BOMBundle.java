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
 * BOMBundle
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Nov 21 13:32:00 2002
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
 * <code>BOMBundle</code> contains information about which products that can
 * be produced and which components that are needed for each product. A
 * BOMBundle is typically send in the beginning of a game/ simulation.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class BOMBundle implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = 1765235852671812474L;
  
  private final static int PRODUCTION_ID = 0;
  private final static int CYCLES = 1;
  private final static int BASE_PRICE = 2;
  private final static int INDEX = 3;
  
  private final static int PARTS = 4;
  
  private int[] data;
  private String[] productNames;
  private int count;
  
  private int[] componentData;
  private int componentLength;
  
  private String[] segmentName;
  private int[][] segmentProducts;
  private int segmentCount;
  
  private boolean isLocked = false;
  
  public BOMBundle()
  {
    this(16);
  }
  
  public BOMBundle(int initialSize)
  {
    data = new int[initialSize * PARTS];
    productNames = new String[initialSize];
    componentData = new int[initialSize * 2];
  }
  
  public void addBOM(int productID, int cycles, int[] components)
  {
    addBOM(productID, cycles, components, null, null, 0);
  }
  
  public void addBOM(int productID, int cycles, int[] components,
      int[] substitutes)
  {
    addBOM(productID, cycles, components, substitutes, null, 0);
  }
  
  public synchronized void addBOM(int productID, int cycles, int[] components,
      int[] substitutes, String productName, int basePrice)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    int index = count * PARTS;
    if (index == data.length)
    {
      data = ArrayUtils.setSize(data, index + PARTS * 50);
      productNames = (String[]) ArrayUtils.setSize(productNames, count + 50);
    }
    data[index + PRODUCTION_ID] = productID;
    data[index + CYCLES] = cycles;
    data[index + BASE_PRICE] = basePrice;
    data[index + INDEX] = componentLength;
    
    int compLen = components != null ? components.length : 0;
    int substLen = substitutes != null ? substitutes.length : 0;
    if (componentLength + compLen + substLen + 2 >= componentData.length)
    {
      componentData = ArrayUtils.setSize(componentData, componentLength
          + compLen + substLen + 2 + 20);
    }
    index = componentLength;
    componentData[index++] = compLen;
    for (int i = 0; i < compLen; i++)
    {
      componentData[index++] = components[i];
    }
    componentData[index++] = substLen;
    for (int i = 0; i < substLen; i++)
    {
      componentData[index++] = substitutes[i];
    }
    
    // A product name will be created when needed if it is NULL here.
    productNames[count] = productName;
    
    // The count and indexes are updated last to avoid any problems with
    // simultaneous access to the data.
    componentLength = index;
    count++;
  }
  
  /**
   * Returns the index for the specified product.
   * 
   * @param productID
   *          the id of the product
   * @return the index of the specified product or -1 if the product was not
   *         found in this bundle
   */
  public int getIndexFor(int productID)
  {
    for (int i = 0, index = 0; index < count; i += PARTS, index++)
    {
      if (data[i + PRODUCTION_ID] == productID)
      {
        return index;
      }
    }
    return -1;
  }
  
  /**
   * Returns the productID at the specified index (position).
   */
  public int getProductID(int index)
  {
    return get(index, PRODUCTION_ID);
  }
  
  /**
   * Returns the number of assembly cycles required to produce the product at
   * position <code>index</code>.
   */
  public int getAssemblyCyclesRequired(int index)
  {
    return get(index, CYCLES);
  }
  
  /**
   * Returns the base price of the product at position <code>index</code> or 0
   * if no base price is known.
   */
  public int getProductBasePrice(int index)
  {
    return get(index, BASE_PRICE);
  }
  
  /**
   * Returns the name of the component/product at position <code>index</code>.
   */
  public String getProductName(int index)
  {
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    String name = productNames[index];
    if (name == null)
    {
      // Generate product name
      int[] components = getComponents(index);
      if (components != null)
      {
        StringBuffer sb = new StringBuffer();
        sb.append("PC ").append(index + 1).append(" [");
        for (int i = 0, n = components.length; i < n; i++)
        {
          if (i > 0)
          {
            sb.append(',');
          }
          sb.append(components[i]);
        }
        name = sb.append(']').toString();
      }
      else
      {
        name = "PC " + (index + 1);
      }
      productNames[index] = name;
    }
    return name;
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
   * Returns the components required to produce the specified product.
   * 
   * @param productID
   *          the id of the product
   * @return an array of component ids or <CODE>null</CODE> if the product is
   *         not known
   */
  public int[] getComponentsForProductID(int productID)
  {
    int index = getIndexFor(productID);
    return (index >= 0) ? getComponents(index) : null;
  }
  
  /**
   * Returns the components required to produce the product at position
   * <code>index</code>.
   * 
   * @param index
   *          the index of the product
   * @return an array of component ids or <CODE>null</CODE> if the no such
   *         information is known
   */
  public int[] getComponents(int index)
  {
    int compIndex = getComponentIndex(index);
    int number = componentData[compIndex];
    if (number > 0)
    {
      // Should not create new arrays each time. FIX THIS!!! TODO
      int[] subst = new int[number];
      // Probably faster than System.arraycopy because of small arrays
      for (int i = 0; i < number; i++)
      {
        subst[i] = componentData[compIndex + i + 1];
      }
      return subst;
    }
    return null;
  }
  
  /**
   * Returns an array of productIDs that represent substitutes for the specified
   * productID or <CODE>null</CODE> if no such substitutes exists.
   * 
   * <p>
   * Note: product substitutes are not used in TAC03 SCM.
   */
  public int[] getSubstitutesForProductID(int productID)
  {
    int index = getIndexFor(productID);
    return (index >= 0) ? getSubstitutes(index) : null;
  }
  
  /**
   * Returns an array of productIDs that represent substitutes for the productID
   * at position <code>index</code> or <CODE>null</CODE> if no such
   * substitutes exists.
   * 
   * <p>
   * Note: product substitutes are not used in TAC03 SCM.
   */
  public int[] getSubstitutes(int index)
  {
    int compIndex = getComponentIndex(index);
    // We are interested in the substitutes
    compIndex += componentData[compIndex] + 1;
    int number = componentData[compIndex++];
    if (number > 0)
    {
      int[] subst = new int[number];
      // Probably faster than System.arraycopy because of small arrays
      for (int i = 0; i < number; i++)
      {
        subst[i] = componentData[compIndex + i];
      }
      return subst;
    }
    return null;
  }
  
  /**
   * Returns the number of products in this BOMBundle.
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
  
  private int getComponentIndex(int index)
  {
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    return data[index * PARTS + INDEX];
  }
  
  private int get(int index, int delta)
  {
    if (index < 0 || index >= count)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + count);
    }
    return data[index * PARTS + delta];
  }
  
  // -------------------------------------------------------------------
  // Product segmentation
  // -------------------------------------------------------------------
  
  public synchronized void addSegment(String name, int[] products)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    
    if (segmentName == null)
    {
      segmentName = new String[3];
      segmentProducts = new int[3][];
    }
    else if (segmentCount == segmentName.length)
    {
      segmentName = (String[]) ArrayUtils
          .setSize(segmentName, segmentCount + 5);
      segmentProducts = (int[][]) ArrayUtils.setSize(segmentProducts,
          segmentCount + 5);
    }
    segmentName[segmentCount] = name;
    segmentProducts[segmentCount] = products == null ? new int[0]
        : (int[]) products.clone();
    segmentCount++;
  }
  
  /**
   * Returns the name of the product segment at the specified index (position).
   * 
   * @since 0.9.6
   */
  public String getSegmentName(int index)
  {
    if (index < 0 || index >= segmentCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + segmentCount);
    }
    return segmentName[index];
  }
  
  /**
   * Returns the products in the product segment at the specified index
   * (position).
   * 
   * @since 0.9.6
   */
  public int[] getSegmentProducts(int index)
  {
    if (index < 0 || index >= segmentCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + segmentCount);
    }
    return (int[]) segmentProducts[index].clone();
  }
  
  /**
   * Returns the number of product segments in this bundle
   * 
   * @since 0.9.6
   */
  public int getSegmentCount()
  {
    return segmentCount;
  }
  
  // -------------------------------------------------------------------
  //
  // -------------------------------------------------------------------
  
  public boolean equals(Object other)
  {
    if (this == other)
    {
      return true;
    }
    if (other == null || other.getClass() != getClass())
    {
      return false;
    }
    BOMBundle o = (BOMBundle) other;
    if (count != o.count || componentLength != o.componentLength
        || segmentCount != o.segmentCount || isLocked != o.isLocked)
    {
      return false;
    }
    if (segmentCount > 0)
    {
      for (int i = 0, n = segmentCount; i < n; i++)
      {
        int[] sp1 = segmentProducts[i];
        int[] sp2 = o.segmentProducts[i];
        if (!segmentName[i].equals(o.segmentName[i])
            || ((sp1 != sp2) && !equals(sp1, sp2, sp1.length)))
        {
          return false;
        }
      }
    }
    return equals(data, o.data, count)
        && equals(componentData, o.componentData, componentLength);
  }
  
  private boolean equals(int[] data, int[] data2, int len)
  {
    for (int i = 0; i < len; i++)
    {
      if (data[i] != data2[i])
      {
        return false;
      }
    }
    return true;
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(getTransportName()).append('[');
    for (int i = 0, n = size(); i < n; i++)
    {
      if (i > 0)
      {
        sb.append(',');
      }
      sb.append(getProductName(i)).append('(').append(getProductID(i)).append(
          ")={");
      
      int[] components = getComponents(i);
      if (components != null)
      {
        toString(sb, components);
      }
      
      int[] subst = getSubstitutes(i);
      if (subst != null)
      {
        sb.append('|');
        toString(sb, subst);
      }
      sb.append('}');
    }
    sb.append(']');
    if (segmentCount > 0)
    {
      sb.append('[');
      for (int i = 0, n = segmentCount; i < n; i++)
      {
        if (i > 0)
        {
          sb.append(',');
        }
        sb.append(segmentName[i]).append("=[");
        toString(sb, segmentProducts[i]);
        sb.append(']');
      }
      sb.append(']');
    }
    return sb.toString();
  }
  
  private void toString(StringBuffer sb, int[] list)
  {
    for (int j = 0, m = list.length; j < m; j++)
    {
      if (j > 0)
      {
        sb.append(',');
      }
      sb.append(list[j]);
    }
  }
  
  // -------------------------------------------------------------------
  // Transportable (externalization support)
  // -------------------------------------------------------------------
  
  /**
   * Returns the transport name of BOMBundle (for externalization).
   */
  public String getTransportName()
  {
    return "bomBundle";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    // No need to check lock here because addBOM will handle it
    boolean locked = reader.getAttributeAsInt("lock", 0) > 0;
    
    while (reader.nextNode(false))
    {
      if (reader.isNode("product"))
      {
        int productID = reader.getAttributeAsInt("id");
        int cycles = reader.getAttributeAsInt("cycles");
        int basePrice = reader.getAttributeAsInt("basePrice", 0);
        String name = reader.getAttribute("name", null);
        reader.enterNode();
        int[] components = readSubs(reader, "component", "id");
        int[] substitutes = readSubs(reader, "substitute", "id");
        addBOM(productID, cycles, components, substitutes, name, basePrice);
        
        reader.exitNode();
        
      }
      else if (reader.isNode("segment"))
      {
        String name = reader.getAttribute("name");
        reader.enterNode();
        int[] products = readSubs(reader, "product", "id");
        addSegment(name, products);
        reader.exitNode();
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
    
    for (int i = 0, index = 0; i < count; i++, index += PARTS)
    {
      int compIndex = data[index + INDEX];
      int partNumber = componentData[compIndex++];
      int substIndex = compIndex + partNumber;
      int substNumber = componentData[substIndex++];
      int basePrice = data[index + BASE_PRICE];
      String name = productNames[i];
      writer.node("product").attr("id", data[index + PRODUCTION_ID]).attr(
          "cycles", data[index + CYCLES]);
      if (basePrice != 0)
      {
        writer.attr("basePrice", basePrice);
      }
      if (name != null)
      {
        writer.attr("name", name);
      }
      for (int j = compIndex, m = compIndex + partNumber; j < m; j++)
      {
        writer.node("component").attr("id", componentData[j]).endNode(
            "component");
      }
      for (int j = substIndex, m = substIndex + substNumber; j < m; j++)
      {
        writer.node("substitute").attr("id", componentData[j]).endNode(
            "substitute");
      }
      writer.endNode("product");
    }
    
    for (int i = 0, n = segmentCount; i < n; i++)
    {
      writer.node("segment").attr("name", segmentName[i]);
      for (int j = 0, m = segmentProducts[i].length; j < m; j++)
      {
        writer.node("product").attr("id", segmentProducts[i][j]).endNode(
            "product");
      }
      writer.endNode("segment");
    }
  }
  
} // BOMBundle
