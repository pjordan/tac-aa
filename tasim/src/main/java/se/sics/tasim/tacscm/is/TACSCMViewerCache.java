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
 * TACSCMViewerCache
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Mar 20 16:48:09 2003
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.is;

import java.util.Hashtable;
import java.util.logging.Logger;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.is.common.ViewerCache;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.tacscm.TACSCMConstants;

public class TACSCMViewerCache extends ViewerCache
{
  
  private static final Logger log = Logger.getLogger(TACSCMViewerCache.class.getName());
  
  private BOMBundle bomBundle;
  private ComponentCatalog catalog;
  private int timeUnit;
  
  private static final int DU_AGENT = 0;
  private static final int DU_TYPE = 1;
  private static final int DU_VALUE = 2;
  private static final int DU_PARTS = 3;
  
  private int[] dataUpdatedConstants;
  private int dataUpdatedCount = 0;
  private int noCachedData = 0;
  
  private Hashtable cache = new Hashtable();
  
  public static final int MAX_CACHE = 60;
  
  public TACSCMViewerCache()
  {}
  
  private void addToCache(int agent, int type, int value)
  {
    String key = "" + agent + "_" + type;
    CacheEntry ce = (CacheEntry) cache.get(key);
    if (ce == null)
    {
      ce = new CacheEntry();
      ce.agent = agent;
      ce.type = type;
      ce.cachedData = new int[MAX_CACHE];
      cache.put(key, ce);
    }
    ce.addCachedData(value);
  }
  
  public void writeCache(EventWriter eventWriter)
  {
    super.writeCache(eventWriter);
    
    if (bomBundle != null)
    {
      eventWriter.dataUpdated(TACSCMConstants.TYPE_NONE, bomBundle);
    }
    if (catalog != null)
    {
      eventWriter.dataUpdated(TACSCMConstants.TYPE_NONE, catalog);
    }
    if (timeUnit > 0)
    {
      eventWriter.nextTimeUnit(timeUnit);
    }
    if (dataUpdatedCount > 0)
    {
      for (int i = 0, n = dataUpdatedCount * DU_PARTS; i < n; i += DU_PARTS)
      {
        eventWriter.dataUpdated(dataUpdatedConstants[i + DU_AGENT], dataUpdatedConstants[i
            + DU_TYPE], dataUpdatedConstants[i + DU_VALUE]);
      }
    }
    
    Object[] keys = cache.keySet().toArray();
    if (keys != null)
    {
      for (int i = 0, n = keys.length; i < n; i++)
      {
        CacheEntry ce = (CacheEntry) cache.get(keys[i]);
        if (ce != null)
        {
          eventWriter.intCache(ce.agent, ce.type, ce.getCache());
        }
      }
    }
  }
  
  public void nextTimeUnit(int timeUnit)
  {
    this.timeUnit = timeUnit;
  }
  
  public void dataUpdated(int agent, int type, int value)
  {
    if ((type & (TACSCMConstants.DU_NOMINAL_CAPACITY_FLAG | TACSCMConstants.DU_COMPONENT_ID_FLAG)) != 0)
    {
      synchronized (this)
      {
        int index = dataUpdatedCount * DU_PARTS;
        if (dataUpdatedConstants == null)
        {
          dataUpdatedConstants = new int[8 * DU_PARTS];
        }
        else if (index == dataUpdatedConstants.length)
        {
          dataUpdatedConstants = ArrayUtils.setSize(dataUpdatedConstants, index + 8 * DU_PARTS);
        }
        dataUpdatedConstants[index + DU_AGENT] = agent;
        dataUpdatedConstants[index + DU_TYPE] = type;
        dataUpdatedConstants[index + DU_VALUE] = value;
        dataUpdatedCount++;
      }
    }
    else if (type == TACSCMConstants.DU_CUSTOMER_DEMAND
        || type == TACSCMConstants.DU_CUSTOMER_ORDERED
        || (type & TACSCMConstants.DU_CAPACITY_FLAG) != 0
        || (type & TACSCMConstants.DU_INVENTORY_FLAG) != 0
        || (type & TACSCMConstants.DU_PRODUCTION_FLAG) != 0)
    {
      addToCache(agent, type, value);
    }
  }
  
  public void dataUpdated(int agent, int type, long value)
  {
    if ((type & TACSCMConstants.DU_BANK_ACCOUNT) != 0)
    {
      addToCache(agent, type, (int) value);
    }
  }
  
  public void dataUpdated(int type, Transportable value)
  {
    Class valueType = value.getClass();
    if (valueType == BOMBundle.class)
    {
      this.bomBundle = (BOMBundle) value;
    }
    else if (valueType == ComponentCatalog.class)
    {
      this.catalog = (ComponentCatalog) value;
    }
  }
  
  private static class CacheEntry
  {
    int agent;
    int type;
    int[] cachedData;
    int pos;
    int len;
    
    public void addCachedData(int value)
    {
      // System.out.println("**** CacheEntity: adding cache[" + pos + "]=" +
      // value);
      cachedData[pos] = value;
      pos = (pos + 1) % MAX_CACHE;
      if (len < MAX_CACHE)
      {
        len++;
      }
    }
    
    public int[] getCache()
    {
      int[] tmp = new int[len];
      int start = ((pos - len) + MAX_CACHE) % MAX_CACHE;
      for (int i = 0, n = len; i < n; i++)
      {
        tmp[i] = cachedData[(start + i) % MAX_CACHE];
      }
      return tmp;
    }
  }
  
} // TACSCMViewerCache
