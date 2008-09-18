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
 * MarketReport
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
 * <code>MarketReport</code> contains information about the supply and
 * customer demand in the market during a period of time.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class MarketReport implements Transportable, java.io.Serializable
{
  
  private static final long serialVersionUID = 5878853164404337169L;
  
  private final static int SUPPLY_PRODUCT_ID = 0;
  private final static int SUPPLY_ORDERED = 1;
  private final static int SUPPLY_PRODUCED = 2;
  private final static int SUPPLY_DELIVERED = 3;
  
  private final static int SUPPLY_PARTS = 4;
  
  /** Names for the externalization support (Transportable) */
  private static final String[] supplyNames =
  { "product", "ordered", "produced", "delivered" };
  
  private final static int DEMAND_PRODUCT_ID = 0;
  private final static int DEMAND_REQUESTED = 1;
  private final static int DEMAND_ORDERED = 2;
  
  private final static int DEMAND_PARTS = 3;
  
  /** Names for the externalization support (Transportable) */
  private static final String[] demandNames =
  { "product", "requested", "ordered" };
  
  private SupplierData[] supplierData;
  private int supplierCount;
  
  private int startDate, endDate;
  
  private int[] supplyData;
  private long[] supplyPrice;
  private int supplyCount;
  
  private int[] demandData;
  private long[] demandPrice;
  private int demandCount;
  
  private boolean isLocked = false;
  
  public MarketReport()
  {
    this(0, 0, 10, 16);
  }
  
  public MarketReport(int startDate, int endDate)
  {
    this(startDate, endDate, 10, 16);
  }
  
  public MarketReport(int startDate, int endDate, int supplyInitialSize,
      int demandInitialSize)
  {
    this.startDate = startDate;
    this.endDate = endDate;
    this.supplyData = new int[supplyInitialSize * SUPPLY_PARTS];
    this.supplyPrice = new long[supplyInitialSize];
    this.demandData = new int[demandInitialSize * DEMAND_PARTS];
    this.demandPrice = new long[demandInitialSize];
    
    this.supplierData = new SupplierData[16];
  }
  
  /**
   * Returns the first date of the time period for which this report was
   * generated.
   */
  public int getStartDate()
  {
    return startDate;
  }
  
  /**
   * Returns the last date of the time period for which this report was
   * generated.
   */
  public int getEndDate()
  {
    return endDate;
  }
  
  public boolean isLocked()
  {
    return isLocked;
  }
  
  public void lock()
  {
    isLocked = true;
  }
  
  // -------------------------------------------------------------------
  // Supplier section
  // -------------------------------------------------------------------
  
  /**
   * Add supplier product line information about the specified product.
   * 
   * @param supplierAddress
   *          the supplier to add product line information for
   * @param productID
   *          the product to add product line information for
   * @param capacity
   *          the product line capacity
   * @since TAC SCM AW 0.9.5
   */
  public void addSupplierCapacity(String supplierAddress, int productID,
      int capacity)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    int index = getSupplierIndexFor(supplierAddress, productID);
    SupplierData data;
    if (index < 0)
    {
      if (supplierCount == supplierData.length)
      {
        supplierData = (SupplierData[]) ArrayUtils.setSize(supplierData,
            supplierCount + 5);
      }
      data = new SupplierData(supplierAddress, productID);
      supplierData[supplierCount++] = data;
    }
    else
    {
      data = supplierData[index];
    }
    data.addCapacity(capacity);
  }
  
  /**
   * Returns the address of the supplier at the specified index. Supplier
   * reports are indexed from 0 to <code>getSupplierCount</code> - 1 where
   * each index represent a different supplier product line (<code>getSupplierProductID</code>).
   * 
   * @param index
   *          the index of the requested supplier product line report
   * @return the product id
   * @since 0.9.5
   */
  public String getSupplierAddress(int index)
  {
    SupplierData data = getSupplierData(index);
    return data.supplier;
  }
  
  /**
   * Returns the product id of the supplier product line report at the specified
   * index. Supplier reports are indexed from 0 to <code>getSupplierCount</code> -
   * 1 where each index represent a different supplier product line.
   * 
   * @param index
   *          the index of the requested supplier product line report
   * @return the product id
   * @since TAC SCM AW 0.9.5
   */
  public int getSupplierProductID(int index)
  {
    SupplierData data = getSupplierData(index);
    return data.productID;
  }
  
  /**
   * Returns the average capacity of the supplier product line during the
   * period. Supplier product line reports are indexed from 0 to
   * <code>getSupplierCount</code> - 1 where each index represent a different
   * supplier product line.
   * 
   * @param index
   *          the index of the requested supplier product line report
   * @return the average product line capacity
   * @see #getSupplierCount
   * @since TAC SCM AW 0.9.5
   */
  public int getAverageSupplierCapacity(int index)
  {
    SupplierData data = getSupplierData(index);
    return data.getAverageCapacity();
  }
  
  /**
   * Returns the supplier product line report index for the specified supplier
   * product line or -1 if no information about the line is known. The index can
   * be used to retrieve further information using
   * <code>getAverageSupplierCapacity</code> method.
   * 
   * @param supplierAddress
   *          the supplier address
   * @param productID
   *          the id of the product for the product line
   * @return the report index or -1 if the product is not known
   * @see #getAverageSupplierCapacity
   * @since TAC SCM AW 0.9.5
   */
  public int getSupplierIndexFor(String supplierAddress, int productID)
  {
    for (int i = 0; i < supplierCount; i++)
    {
      if (supplierData[i].productID == productID
          && supplierData[i].supplier.equals(supplierAddress))
      {
        return i;
      }
    }
    return -1;
  }
  
  /**
   * Returns the number of supplier product line reports available in this
   * market report.
   * 
   * @since TAC SCM AW 0.9.5
   */
  public int getSupplierCount()
  {
    return supplierCount;
  }
  
  private SupplierData getSupplierData(int index)
  {
    if (index < 0 || index >= supplierCount)
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + supplierCount);
    }
    return supplierData[index];
  }
  
  // -------------------------------------------------------------------
  // Supply section
  // -------------------------------------------------------------------
  
  /**
   * Add supply information about the specified product.
   * 
   * @param productID
   *          the product to add supply information for
   * @param quantityProduced
   *          the quantity produced
   * @deprecated information about produced supply not available in TAC'05 SCM
   */
  public void addSupplyProduced(int productID, int quantityProduced)
  {
    int index = addSupply(productID) * SUPPLY_PARTS;
    supplyData[index + SUPPLY_PRODUCED] += quantityProduced;
  }
  
  /**
   * Add supply information about the specified product.
   * 
   * @param productID
   *          the product to add supply information for
   * @param quantityOrdered
   *          the quantity ordered
   * @param averageUnitPrice
   *          the average unit price
   * @since TAC SCM AW 0.9.5
   */
  public void addSupplyOrdered(int productID, int quantityOrdered,
      int averageUnitPrice)
  {
    int index = addSupply(productID);
    int supplyIndex = index * SUPPLY_PARTS;
    supplyData[supplyIndex + SUPPLY_ORDERED] += quantityOrdered;
    supplyPrice[index] += quantityOrdered * averageUnitPrice;
  }
  
  /**
   * Add supply information about the specified product.
   * 
   * @param productID
   *          the product to add supply information for
   * @param quantityDelivered
   *          the quantity delivered
   */
  public void addSupplyDelivered(int productID, int quantityDelivered)
  {
    int index = addSupply(productID) * SUPPLY_PARTS;
    supplyData[index + SUPPLY_DELIVERED] += quantityDelivered;
  }
  
  public void addSupplyForProduct(int productID, int quantityOrdered,
      int quantityProduced, int quantityDelivered, int averageUnitPrice)
  {
    int index = addSupply(productID);
    int supplyIndex = index * SUPPLY_PARTS;
    supplyData[supplyIndex + SUPPLY_ORDERED] += quantityOrdered;
    supplyData[supplyIndex + SUPPLY_PRODUCED] += quantityProduced;
    supplyData[supplyIndex + SUPPLY_DELIVERED] += quantityDelivered;
    supplyPrice[index] += quantityOrdered * averageUnitPrice;
  }
  
  private int addSupply(int productID)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    
    int index = getSupplyIndexFor(productID);
    if (index < 0)
    {
      index = supplyCount;
      
      int dataIndex = supplyCount * SUPPLY_PARTS;
      if (dataIndex == supplyData.length)
      {
        supplyData = ArrayUtils.setSize(supplyData, dataIndex + SUPPLY_PARTS
            * 10);
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
   * @since TAC SCM AW 0.9.5
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
   * @deprecated information about produced supply not available in TAC'05 SCM
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
   * Return the average sales price of a specific product sold by the suppliers
   * customers during the period. Supply reports are indexed from 0 to
   * <code>getSupplyCount</code> - 1 where each index represent a different
   * product/component (<code>getSupplyProductID</code>).
   * 
   * @param index
   *          the index of the requested supply report
   * @return the average unit price for the product sold by the suppliers
   * @see #getSupplyProductID
   * @see #getSupplyCount
   * @since TAC SCM AW 0.9.5
   */
  public int getAverageSupplyPrice(int index)
  {
    int quantity = getSupplyOrdered(index);
    // getSupplyOrdered has already verified the index is correct
    return quantity > 0 ? ((int) (supplyPrice[index] / quantity)) : 0;
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
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + supplyCount);
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
   * @param quantityOrdered
   *          the quantity ordered
   * @param averageUnitPrice
   *          the average unit price for the ordered product
   * @deprecated Replaced by <code>addDemandForProduct(int,int,int,int)</code>
   * @see #addDemandForProduct(int,int,int,int)
   */
  public void addDemandForProduct(int productID, int quantityOrdered,
      int averageUnitPrice)
  {
    addDemandForProduct(productID, 0, quantityOrdered, averageUnitPrice);
  }
  
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
   * @since TAC SCM AW 0.9.5
   */
  public void addDemandForProduct(int productID, int quantityRequested,
      int quantityOrdered, int averageUnitPrice)
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    
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
   * @return the average unit price for the product ordered by the customers
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
      throw new IndexOutOfBoundsException("Index: " + index + " Size: "
          + demandCount);
    }
    return demandData[index * DEMAND_PARTS + delta];
  }
  
  // -------------------------------------------------------------------
  // Generation of better debug output
  // -------------------------------------------------------------------
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(getTransportName()).append('[');
    if (startDate != 0 || endDate != 0)
    {
      sb.append(startDate).append(',').append(endDate).append(',');
    }
    sb.append("demand=[");
    for (int i = 0, index = 0, n = demandCount * DEMAND_PARTS; index < n; i++, index += DEMAND_PARTS)
    {
      sb.append('[');
      for (int j = 0; j < DEMAND_PARTS; j++)
      {
        sb.append(demandData[index + j]).append(',');
      }
      sb.append(getAverageProductPrice(i));
      sb.append(']');
    }
    sb.append(",supply=[");
    for (int i = 0, index = 0, n = supplyCount * SUPPLY_PARTS; index < n; i++, index += SUPPLY_PARTS)
    {
      sb.append('[');
      for (int j = 0; j < SUPPLY_PARTS; j++)
      {
        sb.append(supplyData[index + j]).append(',');
      }
      sb.append(getAverageSupplyPrice(i));
      sb.append(']');
    }
    sb.append("],supplier=[");
    for (int i = 0; i < supplierCount; i++)
    {
      SupplierData data = supplierData[i];
      sb.append('[').append(data.supplier).append(',').append(data.productID)
          .append(',').append(data.getAverageCapacity()).append(']');
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
    return "marketReport";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    if (isLocked)
    {
      throw new IllegalStateException("locked");
    }
    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
    this.startDate = reader.getAttributeAsInt("startDate", 0);
    this.endDate = reader.getAttributeAsInt("endDate", 0);
    
    while (reader.nextNode(false))
    {
      if (reader.isNode("supplyInfo"))
      {
        int productID = reader
            .getAttributeAsInt(supplyNames[SUPPLY_PRODUCT_ID]);
        int quantityOrdered = reader.getAttributeAsInt(
            supplyNames[SUPPLY_ORDERED], 0);
        int quantityProduced = reader.getAttributeAsInt(
            supplyNames[SUPPLY_PRODUCED], 0);
        int quantityDelivered = reader
            .getAttributeAsInt(supplyNames[SUPPLY_DELIVERED]);
        int averageUnitPrice = reader.getAttributeAsInt("price", 0);
        
        addSupplyForProduct(productID, quantityOrdered, quantityProduced,
            quantityDelivered, averageUnitPrice);
        
      }
      else if (reader.isNode("demandInfo"))
      {
        int productID = reader
            .getAttributeAsInt(demandNames[DEMAND_PRODUCT_ID]);
        int quantityRequested = reader.getAttributeAsInt(
            demandNames[DEMAND_REQUESTED], 0);
        int quantityOrdered = reader
            .getAttributeAsInt(demandNames[DEMAND_ORDERED]);
        int averageUnitPrice = reader.getAttributeAsInt("price", 0);
        
        addDemandForProduct(productID, quantityRequested, quantityOrdered,
            averageUnitPrice);
        
      }
      else if (reader.isNode("supplierInfo"))
      {
        String supplier = reader.getAttribute("supplier");
        int productID = reader.getAttributeAsInt("product");
        int capacity = reader.getAttributeAsInt("capacity");
        addSupplierCapacity(supplier, productID, capacity);
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
    if (startDate != 0)
    {
      writer.attr("startDate", startDate);
    }
    if (endDate != 0)
    {
      writer.attr("endDate", endDate);
    }
    
    for (int i = 0, index = 0, n = supplyCount * SUPPLY_PARTS; index < n; i++, index += SUPPLY_PARTS)
    {
      int orderedQuantity = supplyData[index + SUPPLY_ORDERED];
      long totalPrice = supplyPrice[i];
      writer.node("supplyInfo");
      for (int j = 0; j < SUPPLY_PARTS; j++)
      {
        writer.attr(supplyNames[j], supplyData[index + j]);
      }
      if (orderedQuantity > 0 && totalPrice > 0)
      {
        writer.attr("price", (int) (totalPrice / orderedQuantity));
      }
      writer.endNode("supplyInfo");
    }
    
    for (int i = 0, index = 0, n = demandCount * DEMAND_PARTS; index < n; i++, index += DEMAND_PARTS)
    {
      int requestedQuantity = demandData[index + DEMAND_REQUESTED];
      int orderedQuantity = demandData[index + DEMAND_ORDERED];
      long totalPrice = demandPrice[i];
      writer.node("demandInfo").attr(demandNames[DEMAND_PRODUCT_ID],
          demandData[index + DEMAND_PRODUCT_ID]).attr(
          demandNames[DEMAND_REQUESTED], requestedQuantity).attr(
          demandNames[DEMAND_ORDERED], orderedQuantity);
      if (orderedQuantity > 0 && totalPrice > 0)
      {
        writer.attr("price", (int) (totalPrice / orderedQuantity));
      }
      writer.endNode("demandInfo");
    }
    
    for (int i = 0; i < supplierCount; i++)
    {
      SupplierData data = supplierData[i];
      writer.node("supplierInfo").attr("supplier", data.supplier).attr(
          "product", data.productID)
          .attr("capacity", data.getAverageCapacity()).endNode("supplierInfo");
    }
  }
  
  // -------------------------------------------------------------------
  // Supplier Data
  // -------------------------------------------------------------------
  
  private static class SupplierData implements java.io.Serializable
  {
    
    public final String supplier;
    public final int productID;
    private int totalCapacity;
    private int capacityCount;
    
    public SupplierData(String supplier, int productID)
    {
      this.supplier = supplier;
      this.productID = productID;
    }
    
    public void addCapacity(int capacity)
    {
      totalCapacity += capacity;
      capacityCount++;
    }
    
    public int getAverageCapacity()
    {
      return capacityCount > 0 ? (totalCapacity / capacityCount) : 0;
    }
    
  }
  
} // MarketReport
