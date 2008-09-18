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
 * PriceReport
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Mar 26 21:08:00 2003
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
 * <code>PriceReport</code> contains information about the price interval of
 * ordered products.
 * <p>
 * 
 * Price reports are usually generated for all orders placed during a time
 * period and contains the lowest and highest order price for each ordered
 * product.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class PriceReport implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = 553988878211682440L;
  
  private final static int PRODUCT_ID = 0;
  private final static int LOWEST_PRICE = 1;
  private final static int HIGHEST_PRICE = 2;
  
  private final static int PARTS = 3;
  
  /** Names for the externalization support (Transportable) */
  private static final String[] names =
  { "product", "lowestPrice", "highestPrice" };
  
  private int[] data;
  private int count;
  private boolean isLocked = false;
  
  public PriceReport()
  {
    this(16);
  }
  
  public PriceReport(int initialSize)
  {
    this.data = new int[initialSize * PARTS];
  }
  
  public synchronized void addPriceInfo(int productID, int lowestPrice,
      int highestPrice)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    int index = count * PARTS;
    if (index == data.length)
    {
      data = ArrayUtils.setSize(data, index + PARTS * 10);
    }
    data[index + PRODUCT_ID] = productID;
    data[index + LOWEST_PRICE] = lowestPrice;
    data[index + HIGHEST_PRICE] = highestPrice;
    count++;
  }
  
  public synchronized void setPriceForProduct(int productID, int price)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    int index = getIndexFor(productID);
    if (index < 0)
    {
      addPriceInfo(productID, price, price);
    }
    else
    {
      index *= PARTS;
      if (price < data[index + LOWEST_PRICE])
      {
        data[index + LOWEST_PRICE] = price;
      }
      if (price > data[index + HIGHEST_PRICE])
      {
        data[index + HIGHEST_PRICE] = price;
      }
    }
  }
  
  /**
   * Searches for the specified product in this report.
   * 
   * @param productID
   *          the product to search for.
   * @return the index of the product or -1 if no informatin about the product
   *         was found in this report.
   */
  public int getIndexFor(int productID)
  {
    for (int i = 0, index = 0; index < count; i += PARTS, index++)
    {
      if (data[i + PRODUCT_ID] == productID)
      {
        return index;
      }
    }
    return -1;
  }
  
  /**
   * Returns the product id.
   * 
   * @param index
   *          the index of the product information in this report.
   * @return the product id.
   */
  public int getProductID(int index)
  {
    return get(index, PRODUCT_ID);
  }
  
  /**
   * Returns the lowest order price.
   * 
   * @param index
   *          the index of the product information in this report.
   * @return the lowest order price for the product at this index.
   */
  public int getLowestPrice(int index)
  {
    return get(index, LOWEST_PRICE);
  }
  
  /**
   * Returns the highest order price.
   * 
   * @param index
   *          the index of the product information in this report.
   * @return the highest order price for the product at this index.
   */
  public int getHighestPrice(int index)
  {
    return get(index, HIGHEST_PRICE);
  }
  
  /**
   * Returns the number of product information in this report.
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
    StringBuffer sb = new StringBuffer();
    sb.append(getTransportName()).append('[');
    for (int index = 0, n = count * PARTS; index < n; index += PARTS)
    {
      sb.append('[').append(data[index]);
      for (int j = 1; j < PARTS; j++)
      {
        sb.append(',').append(data[index + j]);
      }
      sb.append(']');
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
    return "priceReport";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    // No need to check if islocked because addPriceInfo will do that
    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
    
    while (reader.nextNode("priceInfo", false))
    {
      addPriceInfo(reader.getAttributeAsInt(names[PRODUCT_ID]), reader
          .getAttributeAsInt(names[LOWEST_PRICE]), reader
          .getAttributeAsInt(names[HIGHEST_PRICE]));
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
    for (int index = 0, n = count * PARTS; index < n; index += PARTS)
    {
      writer.node("priceInfo");
      for (int j = 0; j < PARTS; j++)
      {
        writer.attr(names[j], data[index + j]);
      }
      writer.endNode("priceInfo");
    }
  }
  
} // PriceReport
