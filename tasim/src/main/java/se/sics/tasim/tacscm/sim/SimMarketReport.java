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
 * SimMarketReport
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Apr 07 13:29:22 2005
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.sim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.botbox.util.ArrayUtils;
import se.sics.tasim.props.MarketReport;

/**
 */
public class SimMarketReport
{
  
  private final static int SUPPLY_PRODUCT_ID = 0;
  private final static int SUPPLY_ORDERED = 1;
  private final static int SUPPLY_PRODUCED = 2;
  private final static int SUPPLY_DELIVERED = 3;
  
  private final static int SUPPLY_PARTS = 4;
  
  private int[] supplyData;
  private long[] supplyPrice;
  private int supplyCount;
  
  private final static int DEMAND_PRODUCT_ID = 0;
  private final static int DEMAND_REQUESTED = 1;
  private final static int DEMAND_ORDERED = 2;
  
  private final static int DEMAND_PARTS = 3;
  
  private int[] demandData;
  private long[] demandPrice;
  private int demandCount;
  
  private HashMap supplierMap;
  
  private final int startDate;
  private final int endDate;
  
  public SimMarketReport(int startDate, int endDate)
  {
    this(startDate, endDate, 10, 16);
  }
  
  public SimMarketReport(int startDate, int endDate, int supplyInitialSize, int demandInitialSize)
  {
    this.startDate = startDate;
    this.endDate = endDate;
    
    this.supplyData = new int[supplyInitialSize * SUPPLY_PARTS];
    this.supplyPrice = new long[supplyInitialSize];
    this.demandData = new int[demandInitialSize * DEMAND_PARTS];
    this.demandPrice = new long[demandInitialSize];
    
    this.supplierMap = new HashMap();
  }
  
  public MarketReport createMarketReport()
  {
    MarketReport m = new MarketReport(startDate, endDate);
    
    for (int i = 0, index = 0, n = supplyCount * SUPPLY_PARTS; index < n; i++, index += SUPPLY_PARTS)
    {
      int quantityOrdered = supplyData[index + SUPPLY_ORDERED];
      int unitPrice = quantityOrdered > 0 ? ((int) (supplyPrice[i] / quantityOrdered)) : 0;
      m.addSupplyForProduct(supplyData[index + SUPPLY_PRODUCT_ID], quantityOrdered,
          supplyData[index + SUPPLY_PRODUCED], supplyData[index + SUPPLY_DELIVERED], unitPrice);
    }
    
    for (int i = 0, index = 0, n = demandCount * DEMAND_PARTS; index < n; i++, index += DEMAND_PARTS)
    {
      int quantityOrdered = demandData[index + DEMAND_ORDERED];
      int unitPrice = quantityOrdered > 0 ? ((int) (demandPrice[i] / quantityOrdered)) : 0;
      m.addDemandForProduct(demandData[index + DEMAND_PRODUCT_ID], demandData[index
          + DEMAND_REQUESTED], quantityOrdered, unitPrice);
    }
    
    Set set = supplierMap.entrySet();
    Iterator it = set.iterator();
    while (it.hasNext())
    {
      Map.Entry entry = (Map.Entry) it.next();
      SupplierData data = (SupplierData) entry.getValue();
      for (int i = 0, n = data.size(); i < n; i++)
      {
        m.addSupplierCapacity((String) entry.getKey(), data.getProductID(i), data
            .getAverageCapacity(i));
      }
    }
    m.lock();
    return m;
  }
  
  // -------------------------------------------------------------------
  // Supplier section
  // -------------------------------------------------------------------
  
  public void addSupplierCapacity(String supplier, int productID, int capacity)
  {
    SupplierData data = (SupplierData) supplierMap.get(supplier);
    if (data == null)
    {
      data = new SupplierData();
      supplierMap.put(supplier, data);
    }
    data.addCapacity(productID, capacity);
  }
  
  // -------------------------------------------------------------------
  // Supply section
  // -------------------------------------------------------------------
  
  // public void addSupplyProduced(int productID, int quantityProduced) {
  // int index = addSupply(productID) * SUPPLY_PARTS;
  // supplyData[index + SUPPLY_PRODUCED] += quantityProduced;
  // }
  
  public void addSupplyOrdered(int productID, int quantityOrdered, int averageUnitPrice)
  {
    int index = addSupply(productID);
    int supplyIndex = index * SUPPLY_PARTS;
    supplyData[supplyIndex + SUPPLY_ORDERED] += quantityOrdered;
    supplyPrice[index] += quantityOrdered * averageUnitPrice;
  }
  
  public void addSupplyDelivered(int productID, int quantityDelivered)
  {
    int index = addSupply(productID) * SUPPLY_PARTS;
    supplyData[index + SUPPLY_DELIVERED] += quantityDelivered;
  }
  
  // private void addSupplyForProduct(int productID,
  // int quantityOrdered,
  // int quantityProduced,
  // int quantityDelivered,
  // int averageUnitPrice) {
  // int index = addSupply(productID);
  // int supplyIndex = index * SUPPLY_PARTS;
  // supplyData[supplyIndex + SUPPLY_ORDERED] += quantityOrdered;
  // supplyData[supplyIndex + SUPPLY_PRODUCED] += quantityProduced;
  // supplyData[supplyIndex + SUPPLY_DELIVERED] += quantityDelivered;
  // supplyPrice[index] += quantityOrdered * averageUnitPrice;
  // }
  
  private int addSupply(int productID)
  {
    int index = getSupplyIndexFor(productID);
    if (index < 0)
    {
      index = supplyCount;
      
      int dataIndex = supplyCount * SUPPLY_PARTS;
      if (dataIndex == supplyData.length)
      {
        supplyData = ArrayUtils.setSize(supplyData, dataIndex + SUPPLY_PARTS * 10);
        supplyPrice = ArrayUtils.setSize(supplyPrice, index + 10);
      }
      supplyData[dataIndex + SUPPLY_PRODUCT_ID] = productID;
      supplyCount++;
    }
    return index;
  }
  
  /**
   * Returns the product id of the supply report at the specified index. Supply
   * reports are indexed from 0 to <code>getSupplyCount</code> - 1 where each
   * index represent a different product/component (<code>getSupplyProductID</code>).
   * 
   * @param index
   *          the index of the requested supply report
   * @return the product id
   */
  public int getSupplyProductID(int index)
  {
    return getSupply(index, SUPPLY_PRODUCT_ID);
  }
  
  /**
   * Returns the total quantity of a specific supply ordered from the suppliers
   * during the period. Supply reports are indexed from 0 to
   * <code>getSupplyCount</code> - 1 where each index represent a different
   * product/component (<code>getSupplyProductID</code>).
   * 
   * @param index
   *          the index of the requested supply report
   * @return the total quantity ordered of the specific product/component
   */
  public int getSupplyOrdered(int index)
  {
    return getSupply(index, SUPPLY_ORDERED);
  }
  
  /**
   * Returns the total quantity of a specific supply produced by the suppliers
   * during the period. Supply reports are indexed from 0 to
   * <code>getSupplyCount</code> - 1 where each index represent a different
   * product/component (<code>getSupplyProductID</code>).
   * 
   * @param index
   *          the index of the requested supply report
   * @return the total quantity produced of the specific product/component
   */
  public int getSupplyProduced(int index)
  {
    return getSupply(index, SUPPLY_PRODUCED);
  }
  
  /**
   * Returns the total quantity of a specific supply delivered by the suppliers
   * during the period. Supply reports are indexed from 0 to
   * <code>getSupplyCount</code> - 1 where each index represent a different
   * product/component (<code>getSupplyProductID</code>).
   * 
   * @param index
   *          the index of the requested supply report
   * @return the total quantity delivered of the specific product/component
   */
  public int getSupplyDelivered(int index)
  {
    return getSupply(index, SUPPLY_DELIVERED);
  }
  
  /**
   * Returns the supply report index for the specified product/component or -1
   * if no information about the product/component is known. The index can be
   * used to retrieve further information using <code>getSupplyProduced</code>
   * and <code>getSupplyDelivered</code> methods.
   * 
   * @param productID
   *          the id of the product/component
   * @return the index or -1 if the product/component is not known.
   * @see #getSupplyProduced
   * @see #getSupplyDelivered
   */
  public int getSupplyIndexFor(int productID)
  {
    for (int i = 0, index = 0; index < supplyCount; i += SUPPLY_PARTS, index++)
    {
      if (supplyData[i + SUPPLY_PRODUCT_ID] == productID)
      {
        return index;
      }
    }
    return -1;
  }
  
  /**
   * Returns the number of supply reports available in this market report.
   */
  public int getSupplyCount()
  {
    return supplyCount;
  }
  
  private int getSupply(int index, int delta)
  {
    if (index < 0 || index >= supplyCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + supplyCount);
    }
    return supplyData[index * SUPPLY_PARTS + delta];
  }
  
  // -------------------------------------------------------------------
  // Customer demand section
  // -------------------------------------------------------------------
  
  /**
   * Add demand information about the specified product.
   * 
   * @param productID
   *          the product to add demand for
   * @param quantityRequested
   *          the quantity requested
   * @param quantityOrdered
   *          the quantity ordered
   * @param averageUnitPrice
   *          the average unit price for the ordered product
   */
  public void addDemandForProduct(int productID, int quantityRequested, int quantityOrdered,
      int averageUnitPrice)
  {
    int i0 = getDemandIndexFor(productID);
    int index = i0 * DEMAND_PARTS;
    if (i0 < 0)
    {
      i0 = demandCount;
      index = i0 * DEMAND_PARTS;
      if (index == demandData.length)
      {
        demandData = ArrayUtils.setSize(demandData, index + DEMAND_PARTS * 10);
        demandPrice = ArrayUtils.setSize(demandPrice, i0 + 10);
      }
      demandData[index + DEMAND_PRODUCT_ID] = productID;
      demandCount++;
    }
    demandData[index + DEMAND_REQUESTED] += quantityRequested;
    demandData[index + DEMAND_ORDERED] += quantityOrdered;
    demandPrice[i0] += quantityOrdered * averageUnitPrice;
  }
  
  /**
   * Return the product id for the demand report at the specified index.
   * Customer demands are indexed from 0 to <code>getDemandCount</code> - 1
   * where each index represent a different product (<code>getDemandProductID</code>).
   * 
   * @param index
   *          the index of the requested product demand report
   * @return the product id
   * @see #getDemandCount
   */
  public int getDemandProductID(int index)
  {
    return getDemand(index, DEMAND_PRODUCT_ID);
  }
  
  /**
   * Return the total quantity of a specific product requested by the customers
   * during the period. Customer demands are indexed from 0 to
   * <code>getDemandCount</code> - 1 where each index represent a different
   * product (<code>getDemandProductID</code>).
   * 
   * @param index
   *          the index of the requested product demand report
   * @return the total quantity of the specific product requested by the
   *         customers
   * @see #getDemandProductID
   * @see #getDemandCount
   * @since TAC SCM AW 0.9.5
   */
  public int getProductsRequested(int index)
  {
    return getDemand(index, DEMAND_REQUESTED);
  }
  
  /**
   * Return the total quantity of a specific product ordered by the customers
   * during the period. Customer demands are indexed from 0 to
   * <code>getDemandCount</code> - 1 where each index represent a different
   * product (<code>getDemandProductID</code>).
   * 
   * @param index
   *          the index of the requested product demand report
   * @return the total quantity of the specific product ordered by the customers
   * @see #getDemandProductID
   * @see #getDemandCount
   */
  public int getProductsOrdered(int index)
  {
    return getDemand(index, DEMAND_ORDERED);
  }
  
  /**
   * Return the average sales price of a specific product ordered by the
   * customers during the period. Customer demands are indexed from 0 to
   * <code>getDemandCount</code> - 1 where each index represent a different
   * product (<code>getDemandProductID</code>).
   * 
   * @param index
   *          the index of the requested product demand report
   * @return the total quantity of the specific product ordered by the customers
   * @see #getDemandProductID
   * @see #getDemandCount
   */
  public int getAverageProductPrice(int index)
  {
    int quantity = getProductsOrdered(index);
    // getProductsOrdered has already verified the index is correct
    return quantity > 0 ? ((int) (demandPrice[index] / quantity)) : 0;
  }
  
  /**
   * Returns the customer demand report index for the specified product or -1 if
   * no information about the product is known. The index can be used to
   * retrieve further information using <code>getProductsOrdered</code> and
   * <code>getAverageProductPrice</code> methods.
   * 
   * @param productID
   *          the id of the product
   * @return the report index or -1 if the product is not known
   * @see #getProductsOrdered
   * @see #getAverageProductPrice
   */
  public int getDemandIndexFor(int productID)
  {
    for (int i = 0, index = 0; index < demandCount; i += DEMAND_PARTS, index++)
    {
      if (demandData[i + DEMAND_PRODUCT_ID] == productID)
      {
        return index;
      }
    }
    return -1;
  }
  
  /**
   * Returns the number of customer product demand reports available in this
   * market report.
   */
  public int getDemandCount()
  {
    return demandCount;
  }
  
  private int getDemand(int index, int delta)
  {
    if (index < 0 || index >= demandCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + demandCount);
    }
    return demandData[index * DEMAND_PARTS + delta];
  }
  
  // -------------------------------------------------------------------
  // Generation of better debug output
  // -------------------------------------------------------------------
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("SimMarketReport").append('[');
    if (startDate != 0 || endDate != 0)
    {
      sb.append(startDate).append(',').append(endDate).append(',');
    }
    sb.append("supply=[");
    for (int i = 0, index = 0, n = supplyCount * SUPPLY_PARTS; index < n; i++, index += SUPPLY_PARTS)
    {
      sb.append('[');
      for (int j = 0; j < SUPPLY_PARTS; j++)
      {
        sb.append(supplyData[index + j]).append(',');
      }
      sb.append(supplyPrice[i]);
      sb.append(']');
    }
    sb.append("],demand=[");
    for (int i = 0, index = 0, n = demandCount * DEMAND_PARTS; index < n; i++, index += DEMAND_PARTS)
    {
      sb.append('[');
      for (int j = 0; j < DEMAND_PARTS; j++)
      {
        sb.append(demandData[index + j]).append(',');
      }
      sb.append(demandPrice[i]);
      sb.append(']');
    }
    sb.append("],supplier=[");
    
    Set set = supplierMap.entrySet();
    Iterator it = set.iterator();
    boolean first = true;
    while (it.hasNext())
    {
      if (first)
      {
        first = false;
      }
      else
      {
        sb.append(',');
      }
      Map.Entry entry = (Map.Entry) it.next();
      sb.append(entry.getKey()).append('=');
      ((SupplierData) entry.getValue()).toString(sb);
    }
    sb.append("]]");
    return sb.toString();
  }
  
  // -------------------------------------------------------------------
  // Supplier Data
  // -------------------------------------------------------------------
  
  private static class SupplierData
  {
    
    private final static int SUPPLIER_PRODUCT_ID = 0;
    private final static int SUPPLIER_CAPACITY = 1;
    private final static int SUPPLIER_COUNT = 2;
    
    private final static int SUPPLIER_PARTS = 3;
    
    private int[] data = new int[2 * SUPPLIER_PARTS];
    private int dataCount;
    
    public void addCapacity(int productID, int capacity)
    {
      int index = getIndexFor(productID) * SUPPLIER_PARTS;
      if (index < 0)
      {
        index = dataCount * SUPPLIER_PARTS;
        if (index == data.length)
        {
          data = ArrayUtils.setSize(data, index + SUPPLIER_PARTS * 10);
        }
        data[index + SUPPLIER_PRODUCT_ID] = productID;
        dataCount++;
      }
      data[index + SUPPLIER_CAPACITY] += capacity;
      data[index + SUPPLIER_COUNT]++;
    }
    
    public int getIndexFor(int productID)
    {
      for (int i = 0, index = 0; index < dataCount; i += SUPPLIER_PARTS, index++)
      {
        if (data[i + SUPPLIER_PRODUCT_ID] == productID)
        {
          return index;
        }
      }
      return -1;
    }
    
    public int size()
    {
      return dataCount;
    }
    
    public int getProductID(int index)
    {
      return getData(index, SUPPLIER_PRODUCT_ID);
    }
    
    public int getAverageCapacity(int index)
    {
      int count = getData(index, SUPPLIER_COUNT);
      return count > 0 ? (data[index * SUPPLIER_PARTS + SUPPLIER_CAPACITY] / count) : 0;
    }
    
    private int getData(int index, int delta)
    {
      if (index < 0 || index >= dataCount)
      {
        throw new IndexOutOfBoundsException("Index: " + index + " Size: " + dataCount);
      }
      return data[index * SUPPLIER_PARTS + delta];
    }
    
    public StringBuffer toString(StringBuffer sb)
    {
      sb.append('[');
      for (int index = 0, n = dataCount * SUPPLIER_PARTS; index < n; index += SUPPLIER_PARTS)
      {
        sb.append('[').append(data[index]);
        for (int j = 1; j < SUPPLIER_PARTS; j++)
        {
          sb.append(',').append(data[index + j]);
        }
        sb.append(']');
      }
      sb.append(']');
      return sb;
    }
  }
  
} // SimMarketReport
