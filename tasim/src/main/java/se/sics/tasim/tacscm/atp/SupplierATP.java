/**
 * TAC Supply Chain Management Simulator
 * http://www.sics.se/tac/    tac-dev@sics.se
 *
 * Copyright (c) 2001-2005 SICS AB. All rights reserved.
 *
 * SICS grants you the right to use, modify, and redistribute this
 * software for non-commercial purposes, on the conditions that you:
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
 * SupplierATP
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Feb 21 11:19:24 2005
 * Updated : $Date: 2008-03-07 09:59:58 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3824 $
 */
package se.sics.tasim.tacscm.atp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 */
public class SupplierATP
{
  
  private static final boolean DEBUG = true;
  
  private int shortHorizon;
  private double capacityReduction;
  private double reputationExpo;
  private int capacityNominal;
  private double priceDiscount;
  private double basePrice;
  private int noDays;
  
  private ATPRFQ[] rfqs;
  private ATPRFQ[] activeRfqs;
  private int currentRFQ = -1;
  private int rfqCount = 0;
  
  /** Global variables to store current RFQ processing session */
  private int currentInventory;
  private int capacityActual;
  private int currentDay;
  private int[] capacityExpected;
  private int[] capacityCommitted;
  
  // Available capacity (date)
  private int[] cavl;
  // Free capacity (date)
  private int[] cfr;
  // Current over allocation (date)
  private int[] overAlloc;
  // Current price (date)
  private double[] price;
  
  // debt is a forward dept caused by reducing a RFQ to a lower quantity
  // than the desired (and if any later RFQ still influensing this reduce
  // this RFQ will get back some of the reduction).
  private int debt = 0;
  private int lastDebtDay = -1;
  
  // This is the total reduction made so far (from day 0 until the current
  // day in the RFQ processing.
  private int totalReduction = 0;
  private int lastReductionDueDate = -1;
  
  private boolean debug = true;
  private boolean logging = true;
  
  private boolean isProcessingOffers = false;
  
  private final Logger log;
  
  public SupplierATP(String name, int shortHorizon, double capacityReduction,
      double reputationExpo, int capacityNominal, double priceDiscount, int basePrice, int noDays,
      int maxRFQ)
  {
    // Add the postfix name to the logger name for convenient logging.
    // Note: this is usually a bad idea because the logger objects
    // will never be garbaged but since the supplier names always are
    // the same in TAC SCM games, only a few logger objects will be
    // created.
    this.log = name == null || name.length() == 0 ? Logger.getLogger(SupplierATP.class.getName())
        : Logger.getLogger(SupplierATP.class.getName() + '.' + name);
    
    this.shortHorizon = shortHorizon;
    this.capacityReduction = capacityReduction;
    this.reputationExpo = reputationExpo;
    this.capacityNominal = capacityNominal;
    this.priceDiscount = priceDiscount;
    this.basePrice = basePrice;
    this.noDays = noDays;
    
    this.cfr = new int[noDays];
    this.cavl = new int[noDays];
    this.overAlloc = new int[noDays];
    this.price = new double[noDays];
    
    this.rfqs = new ATPRFQ[maxRFQ];
    this.activeRfqs = new ATPRFQ[maxRFQ];
  }
  
  public int getBasePrice()
  {
    return (int) basePrice;
  }
  
  public void setBasePrice(int basePrice)
  {
    this.basePrice = basePrice;
  }
  
  protected Promise createPromise()
  {
    return new Promise();
  }
  
  public void clear()
  {
    isProcessingOffers = false;
    
    rfqCount = 0;
    currentRFQ = -1;
    
    totalReduction = 0;
    lastReductionDueDate = -1;
    debt = 0;
    lastDebtDay = -1;
    
    currentDay = 0;
    capacityActual = 0;
    currentInventory = 0;
    capacityExpected = null;
    capacityCommitted = null;
  }
  
  public void clearAll()
  {
    clear();
    
    for (int i = 0, n = noDays; i < n; i++)
    {
      cfr[i] = 0;
      cavl[i] = 0;
      overAlloc[i] = 0;
      price[i] = 0.0;
    }
  }
  
  public String getConfiguration()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Inventory: ").append(currentInventory).append("\nCapacity: ").append(capacityActual)
        .append("\nNominalCapacity: ").append(capacityNominal).append("\nCurrentDay: ").append(
            currentDay).append("\nNoDays: ").append(noDays).append("\nShortHorizon: ").append(
            shortHorizon).append(" \t# tShort in specification").append("\nCapacityReduction: ")
        .append(capacityReduction).append(" \t# z in specification").append("\nReputationExpo: ")
        .append(reputationExpo).append("\nDiscount: ").append(priceDiscount)
        .append("\nBasePrice: ").append((int) basePrice);
    
    if (rfqCount > 0)
    {
      sb.append("\n# RFQ: DueDate,Quantity,ReservePrice,[Reputation]");
      for (int i = 0; i < rfqCount; i++)
      {
        ATPRFQ rfq = rfqs[i];
        sb.append("\nRFQ ").append(i + 1).append(": ").append(rfq.dueDate).append(',').append(
            rfq.requestedQuantity).append(',').append(rfq.reservePrice).append(',').append(
            rfq.reputation);
      }
    }
    return sb.append('\n').toString();
  }
  
  public int getRFQCount()
  {
    return rfqCount;
  }
  
  // Add the specified RFQ
  public void addRFQ(String manufacturer, double reputation, int rfqID, int quantity, int dueDate,
      int reservePrice)
  {
    if (rfqCount >= rfqs.length)
    {
      // No more RFQs
      throw new IllegalArgumentException("too many rfqs");
    }
    
    if (rfqs[rfqCount] == null)
    {
      rfqs[rfqCount] = new ATPRFQ();
    }
    rfqs[rfqCount++].setRFQ(manufacturer, reputation, rfqID, quantity, dueDate,
        (reservePrice / basePrice));
  }
  
  // Process all RFQs given the current commitment (capacity Committed)
  public Promise[] processRFQs(int currentDay, int capacityActual, int inventory,
      int[] capacityExpected, int[] capacityCommitted)
  {
    if (rfqCount == 0)
    {
      if (debug)
        debugWait("No RFQs!");
      return null;
    }
    
    this.currentDay = currentDay;
    this.capacityActual = capacityActual;
    this.currentInventory = inventory;
    this.capacityExpected = capacityExpected;
    this.capacityCommitted = capacityCommitted;
    
    if (DEBUG && debug)
    {
      debug("----------------------\n" + "CURRENT CONFIGURATION\n" + "----------------------\n"
          + getConfiguration());
    }
    
    setup(capacityCommitted);
    if (debug)
      debugWait("Initial Setup");
    
    // Sort the RFQs on reputation, due date and reserve price
    Arrays.sort(rfqs, 0, rfqCount, rfqs[0]);
    if (debug)
      debugWait("Sorted RFQs");
    
    // Merge equals RFQs
    mergeEqualRFQs();
    
    // Find reputation groups
    int rfqStart = 0;
    int rfqEnd = 1;
    int repCount = 0;
    do
    {
      rfqEnd = findReputationEnd(rfqStart);
      repCount++;
      processRFQs(rfqStart, rfqEnd, repCount);
      rfqStart = rfqEnd;
    } while (rfqStart < rfqCount);
    
    if (debug)
      debugWait("Finished processing RFQs");
    
    // Pricing done and quantities decided. Now allocate capacity for
    // partial and earliest complete offers as needed.
    processOffers();
    
    // Now the offers can be created.
    ArrayList list = new ArrayList();
    for (int i = 0; i < rfqCount; i++)
    {
      ATPRFQ rfq = rfqs[i];
      if (rfq.hasBundle())
      {
        rfq.processBundle();
        for (int j = 0, m = rfq.getBundleCount(); j < m; j++)
        {
          addPromiseFor(list, rfq.getBundledRFQ(j));
        }
        rfq.clearBundle();
        
      }
      else
      {
        addPromiseFor(list, rfq);
      }
    }
    
    if (debug)
      debugWait("Finished processing offers");
    
    return list.size() > 0 ? (Promise[]) list.toArray(new Promise[list.size()]) : null;
  }
  
  private void addPromiseFor(ArrayList list, ATPRFQ rfq)
  {
    if (rfq.partialQuantity < rfq.currentQuantity)
    {
      // Add a partial promise
      Promise promise = createPromise();
      promise.set(rfq, basePrice, Promise.PARTIAL);
      list.add(promise);
      
      if (rfq.earliestComplete > 0)
      {
        // Add an earliest complete promise
        Promise otherPromise = createPromise();
        otherPromise.set(rfq, basePrice, Promise.EARLIEST_COMPLETE);
        list.add(otherPromise);
        
        promise.setOtherPromise(otherPromise);
      }
      
    }
    else
    {
      // Add a full promise (delivery possible at requested due date)
      Promise promise = createPromise();
      promise.set(rfq, basePrice, Promise.FULL);
      list.add(promise);
    }
  }
  
  private void processRFQs(int startRFQ, int endRFQ, int reputationGroup)
  {
    totalReduction = 0;
    lastReductionDueDate = -1;
    debt = 0;
    lastDebtDay = currentDay;
    
    // Remove / scale dows RFQs
    scaleDown(startRFQ, endRFQ);
    // if (debug) debugWait("Reputation Set " + reputationGroup
    // + ": Scaled down RFQs");
    
    calculatePrices(startRFQ, endRFQ);
    if (debug)
      debugWait("Reputation Set " + reputationGroup + ": Calculated allocation and capacity");
    
    for (int i = startRFQ; i < endRFQ; i++)
    {
      ATPRFQ rfq = rfqs[i];
      int finDate = rfq.finDate;
      double rPrice = rfq.reservePrice;
      double cPrice = price[finDate];
      int ctot = capacityActual * (finDate - currentDay);
      if (debug && DEBUG)
      {
        debug("------------------------------------------------\n" + "Processing " + (i + 1) + " ("
            + rfq + ") oldPrice: " + cPrice);
      }
      
      // Update the price for this RFQ based on the total reduction so far
      if (totalReduction > 0 && finDate != lastReductionDueDate)
      {
        cPrice -= totalReduction / (2.0 * ctot);
        lastReductionDueDate = finDate;
        if (DEBUG && debug)
        {
          debug("New price: " + cPrice + " totalReduction: " + totalReduction + " date: " + finDate);
        }
        price[finDate] = cPrice;
      }
      
      if ((cPrice - rPrice) <= 0 || rPrice == 0.0)
      {
        // the price has been updated and the reserve price is
        // now at least as high as current price.
        rfqs[i].finished = true;
      }
      else
      {
        boolean loop;
        int loopNo = 0;
        do
        {
          loop = false;
          if (DEBUG && debug)
          {
            debug("RFQ: " + rfq.id + " Current price: " + price[finDate] + " Date: " + finDate
                + " Debt: " + debt);
          }
          // Find the maximal additional reduction that the debt can
          // cause this RFQ
          if (debt > 0)
          {
            // An RFQ can only absorb up to the smallest over allocation.
            // Last debt addition was at lastDebtDay and we only need to
            // check until that day.
            // Should lastDebtDay be checked or not???
            for (int d = finDate - 1; d >= lastDebtDay; d--)
            {
              if (overAlloc[d] < debt)
              {
                debt = overAlloc[d];
                if (DEBUG && debt == 0 && debug)
                {
                  debug("debt became 0 at " + d);
                }
              }
            }
            if (DEBUG && debug)
            {
              debug("debt was reduced to " + debt + " from " + finDate + " to " + lastDebtDay);
            }
          }
          
          // Calculate the number of components to reduce to get to the
          // correct price level (including debt)
          int desiredReduction = (int) (Math.ceil((1.0 / priceDiscount) * ctot * (cPrice - rPrice)
              + debt));
          int actualReduction = (rfq.currentQuantity <= desiredReduction) ? rfq.currentQuantity
              : desiredReduction;
          int reduction = 0;
          
          // If we have a debt we need to let the RFQs get their components
          if (debt > 0)
          {
            int maxReduction = debt > actualReduction ? actualReduction : debt;
            int prevRFQ = findPreviousRFQ(i, startRFQ);
            int prevDate = prevRFQ >= 0 ? rfqs[prevRFQ].finDate : -1;
            for (int day = finDate; day > currentDay && maxReduction > 0; day--)
            {
              
              while (day == prevDate && maxReduction > 0)
              {
                int compBeforeWakeup = rfqs[prevRFQ].getComponentsBeforeWakeup(price[day],
                    capacityActual, currentInventory, currentDay);
                if (compBeforeWakeup <= 0)
                {
                  // This RFQ can immediately absorb the reduction
                  // (or part of it)
                  int diff = rfqs[prevRFQ].requestedQuantity - rfqs[prevRFQ].currentQuantity;
                  if (diff <= maxReduction)
                  {
                    if (debug && DEBUG)
                    {
                      debug("debt increasing rfq " + rfqs[prevRFQ].id + " with " + diff
                          + " new quantity=" + rfqs[prevRFQ].currentQuantity);
                    }
                    rfqs[prevRFQ].currentQuantity += diff;
                    maxReduction -= diff;
                    cfr[day] -= diff;
                    reduction += diff;
                    if (debug && DEBUG)
                    {
                      debug("debt increasing rfq " + rfqs[prevRFQ].id + " with " + diff
                          + " new quantity=" + rfqs[prevRFQ].currentQuantity);
                    }
                  }
                  else
                  {
                    rfqs[prevRFQ].currentQuantity += maxReduction;
                    cfr[day] -= maxReduction;
                    reduction += maxReduction;
                    maxReduction = 0;
                    if (debug && DEBUG)
                    {
                      debug("debt increasing rfq " + rfqs[prevRFQ].id + " with " + maxReduction
                          + " new quantity=" + rfqs[prevRFQ].currentQuantity);
                    }
                  }
                  
                  if (rfqs[prevRFQ].currentQuantity == rfqs[prevRFQ].requestedQuantity)
                  {
                    rfqs[prevRFQ].finished = true;
                  }
                }
                
                prevRFQ = findPreviousRFQ(prevRFQ, startRFQ);
                prevDate = prevRFQ >= 0 ? rfqs[prevRFQ].finDate : -1;
              }
              
              overAlloc[day - 1] -= maxReduction;
            }
            debt -= reduction;
            if (DEBUG && debug)
            {
              debug("Reduced debt with " + reduction + " new debt=" + debt);
            }
          }
          
          if (reduction >= actualReduction)
          {
            // All components consumed by debt
            rfq.currentQuantity -= reduction;
            cfr[finDate] += reduction;
            
            if (debug && DEBUG)
            {
              debug("" + reduction + " components consumed by debt" + " (rfq " + rfq.id
                  + " has now quantity=" + rfq.currentQuantity + ")  actualReduction="
                  + actualReduction);
            }
            if (rfq.currentQuantity != 0)
            {
              debugWarning("QUANTITY NOT 0 FOR RFQ " + rfq.id);
            }
          }
          else
          {
            if (debt > 0)
            {
              debugWarning("ERROR: debt=" + debt + " should be 0!!! (reduction=" + reduction
                  + "  actual=" + actualReduction + ')');
            }
            
            int bestWakeupRFQ = -1;
            int bestComp = Integer.MAX_VALUE;
            int prevRFQ = findPreviousRFQ(i, startRFQ);
            int prevDate = prevRFQ >= 0 ? rfqs[prevRFQ].finDate : -1;
            int maxReduction = actualReduction - reduction;
            for (int day = finDate; day > currentDay && maxReduction > 0; day--)
            {
              
              while (day == prevDate && maxReduction > 0)
              {
                int compBeforeWakeup = rfqs[prevRFQ].getComponentsBeforeWakeup(price[day],
                    capacityActual, currentInventory, currentDay);
                // Fewest components until wakeup and also possible to wake up
                if (compBeforeWakeup < bestComp && compBeforeWakeup < maxReduction)
                {
                  bestComp = compBeforeWakeup;
                  bestWakeupRFQ = prevRFQ;
                }
                
                prevRFQ = findPreviousRFQ(prevRFQ, startRFQ);
                prevDate = prevRFQ >= 0 ? rfqs[prevRFQ].finDate : -1;
              }
              
              // Calculate the maximal reduction for the previous day
              if (maxReduction > overAlloc[day - 1])
              {
                maxReduction = overAlloc[day - 1];
              }
            }
            
            int doReduce = actualReduction - reduction;
            if (bestWakeupRFQ >= 0)
            {
              // We have found an RFQ that might be possible to wakeup
              if (bestComp < (actualReduction - reduction))
              {
                // Possible to wake up. Need another go in the loop.
                loop = true;
                doReduce = bestComp;
                debt = rfqs[bestWakeupRFQ].requestedQuantity - rfqs[bestWakeupRFQ].currentQuantity;
                lastDebtDay = rfqs[bestWakeupRFQ].finDate;
                if (debug && DEBUG)
                {
                  debug("WAKEUP rfq " + rfqs[bestWakeupRFQ].id + " at "
                      + rfqs[bestWakeupRFQ].finDate + " now=" + finDate + " compBefore=" + bestComp
                      + " new debt=" + debt);
                }
              }
              else
              {
                // Not possible to wakeup the RFQ. Simply reduce the
                // current RFQ. Will reduce 'doReduce' components.
              }
            }
            
            rfq.currentQuantity -= doReduce + reduction;
            price[finDate] -= doReduce / (2.0 * ctot);
            cfr[finDate] += doReduce + reduction;
            totalReduction += doReduce;
            lastReductionDueDate = finDate;
            
            if (DEBUG && debug)
            {
              debug("Reducing " + doReduce + " backwards " + " (rfq " + rfq.id + " with "
                  + (doReduce + reduction) + ")  new total reduction=" + totalReduction);
            }
            // Make the reduction
            for (int day = finDate - 1; day >= currentDay && doReduce > 0; day--)
            {
              if (doReduce < overAlloc[day])
              {
                overAlloc[day] -= doReduce;
              }
              else
              {
                doReduce = overAlloc[day];
                overAlloc[day] = 0;
              }
              price[day] -= doReduce / (2.0 * (capacityActual * (day - currentDay)));
            }
          }
          
          if (loop)
          {
            loopNo++;
            if (loopNo > 5)
            {
              debugWarning("LOOP RFQ: " + rfq);
            }
          }
          else
          {
            loopNo = 0;
          }
          
        } while (loop);
        
        if (rfq.currentQuantity == rfq.requestedQuantity)
        {
          rfq.finished = true;
        }
        else if (rfq.currentQuantity > 0)
        {
          debt += rfq.requestedQuantity - rfq.currentQuantity;
          lastDebtDay = finDate;
        }
      }
      currentRFQ = i;
      if (debug)
        debugWait("Reputation Set " + reputationGroup + " Pricing: processed RFQ " + (i + 1));
    }
    // The RFQs in this reputation set are now finished
    for (int i = startRFQ; i < endRFQ; i++)
    {
      rfqs[i].finalPrice = price[rfqs[i].finDate];
      rfqs[i].finished = true;
      if (DEBUG && debug)
      {
        debug("SET FINAL PRICE: " + rfqs[i]);
      }
    }
    currentRFQ = -1;
  }
  
  private void processOffers()
  {
    isProcessingOffers = true;
    
    // Start on day 1 because no new capacity can be allocated day 0
    cfr[currentDay] = 0;
    int currShortHorizon = currentDay + shortHorizon;
    for (int i = currentDay + 1, n = noDays; i < n; i++)
    {
      int expected = capacityExpected[i];
      int capacityWilling = (int) ((i <= currShortHorizon) ? expected : ((1 - capacityReduction
          * (i - currShortHorizon)) * expected));
      if (capacityWilling < 0)
      {
        if (DEBUG)
          System.out.println("CAPACITY WILLING: " + capacityWilling);
        capacityWilling = 0;
      }
      cfr[i] = capacityWilling - capacityCommitted[i];
    }
    
    for (int i = 0; i < rfqCount; i++)
    {
      cfr[rfqs[i].finDate] -= rfqs[i].currentQuantity;
      rfqs[i].partialQuantity = rfqs[i].currentQuantity;
    }
    
    int lastConflictDay = 0;
    int currentNeed = 0;
    for (int i = noDays - 1; i > currentDay; i--)
    {
      if (currentNeed > cfr[i])
      {
        currentNeed -= cfr[i];
      }
      else
      {
        currentNeed = 0;
      }
      overAlloc[i - 1] = currentNeed;
      if (currentNeed == 0)
      {
        lastConflictDay = i;
      }
    }
    
    // Modify current need based on inventory
    currentNeed -= currentInventory;
    if (currentNeed < 0)
    {
      currentNeed = 0;
    }
    
    if (debug)
      debugWait("Offers:  Excessive Demand: " + currentNeed + "  Last Conflict Day: "
          + lastConflictDay);
    
    // ///////////////////////////////////////////////////////
    // Allocate capacity for partial offers
    // ///////////////////////////////////////////////////////
    
    int totalConflict = currentNeed;
    int totalDemand;
    if (currentNeed > 0)
      do
      {
        double totalWeight = 0.0;
        for (int i = 0; i < rfqCount; i++)
        {
          ATPRFQ rfq = rfqs[i];
          if (rfq.finDate <= lastConflictDay && rfq.partialQuantity > 0)
          {
            totalWeight += rfq.updateWeight(reputationExpo);
          }
        }
        
        totalDemand = 0;
        for (int i = 0; i < rfqCount; i++)
        {
          ATPRFQ rfq = rfqs[i];
          if (rfq.finDate <= lastConflictDay && rfq.partialQuantity > 0)
          {
            int newQ = (int) (rfq.partialQuantity - currentNeed * rfq.reputationWeight
                / totalWeight);
            int reduction;
            if (newQ <= 0)
            {
              reduction = rfq.partialQuantity;
              rfq.partialQuantity = 0;
              if (DEBUG && debug)
              {
                debug("RFQ " + rfq.id + " WILL NOT GET PARTIAL OFFER! " + newQ);
              }
            }
            else
            {
              reduction = rfq.partialQuantity - newQ;
              rfq.partialQuantity = newQ;
              totalDemand += newQ;
            }
            cfr[rfq.finDate] += reduction;
            
            // Update the over allocation
            int d = rfq.finDate - 1;
            while (reduction > 0 && d >= currentDay)
            {
              if (overAlloc[d] > reduction)
              {
                overAlloc[d] -= reduction;
              }
              else
              {
                reduction = overAlloc[d];
                overAlloc[d] = 0;
                if (d < lastConflictDay)
                {
                  lastConflictDay = d;
                }
              }
              d--;
            }
          }
        }
        currentNeed = overAlloc[currentDay] - currentInventory;
        if (currentNeed < 0)
        {
          currentNeed = 0;
        }
        if (debug)
          debugWait("Offers:  Excessive Demand: " + totalConflict + "  Remaining Conflict: "
              + currentNeed + "  Last Conflict Day: " + lastConflictDay + "  Remaining demand: "
              + totalDemand);
      } while (currentNeed > 0 && totalDemand > 0);
    
    // ///////////////////////////////////////////////////////
    // Allocation capacity for earliest complete offers
    // ///////////////////////////////////////////////////////
    
    // Calculate available components. Do not include fri components
    // at day 0 because no components may be produced for these offers.
    int totalAvailable = currentInventory - overAlloc[currentDay];
    if (totalAvailable < 0)
    {
      totalAvailable = 0;
    }
    for (int i = currentDay + 1, n = noDays; i < n; i++)
    {
      int avl = cfr[i] - overAlloc[i];
      if (avl > 0)
      {
        cavl[i] = avl;
        totalAvailable += avl;
      }
      else
      {
        cavl[i] = 0;
      }
    }
    
    if (debug)
      debugWait("Calculated total available capacity: " + totalAvailable);
    
    int nextActiveRFQ = 0;
    int activeCount;
    do
    {
      int day = currentDay;
      int nextDate = 0;
      activeCount = 0;
      
      // find next RFQ to activate
      while (nextActiveRFQ < rfqCount)
      {
        ATPRFQ rfq = rfqs[nextActiveRFQ++];
        if (rfq.partialQuantity < rfq.currentQuantity && rfq.earliestComplete == 0)
        {
          rfq.leftOverQuantity = rfq.currentQuantity - rfq.partialQuantity;
          if (rfq.leftOverQuantity > totalAvailable)
          {
            // This RFQ can not be fully satisfied with an earliest
            // complete offer.
            if (DEBUG && debug)
            {
              debug("no earliest complete for rfq " + rfq.id
                  + " due to lack of free capacity (need " + rfq.leftOverQuantity + " but only "
                  + totalAvailable + " available)");
            }
            
          }
          else
          {
            totalAvailable -= rfq.leftOverQuantity;
            day = rfq.dueDate;
            
            // Utilize any free capacity prior to this date
            for (int i = day; i > currentDay; i--)
            {
              if (cavl[i] > 0)
              {
                if (cavl[i] >= rfq.leftOverQuantity)
                {
                  cavl[i] -= rfq.leftOverQuantity;
                  rfq.leftOverQuantity = 0;
                  rfq.partialQuantity = rfq.currentQuantity;
                  if (DEBUG && debug)
                  {
                    debug("RFQ " + rfq.id + " became unpartialized!");
                  }
                  break;
                }
                else
                {
                  rfq.leftOverQuantity -= cavl[i];
                  cavl[i] = 0;
                }
              }
            }
            
            if (rfq.leftOverQuantity > 0)
            {
              activeRfqs[activeCount++] = rfq;
              // System.out.println("activating at day " + day + ": " + rfq
              // + " (" + (nextActiveRFQ - 1) + ')');
              break;
            }
          }
        }
      }
      
      if (activeCount == 0)
      {
        // No more RFQs to activate
        break;
      }
      
      // All free capacity prior to this date has already been
      // utilized (otherwise the active RFQ would have been completed)
      double currentReputation = activeRfqs[0].reputation;
      while (day < noDays)
      {
        if (day >= nextDate)
        {
          // Activate next RFQ if possible
          nextDate = Integer.MAX_VALUE;
          while (nextActiveRFQ < rfqCount)
          {
            ATPRFQ rfq = rfqs[nextActiveRFQ];
            if (rfq.reputation != currentReputation)
            {
              // No more RFQs in this reputation set
              break;
              
            }
            else if (rfq.partialQuantity < rfq.currentQuantity && rfq.earliestComplete == 0)
            {
              if (rfq.dueDate > day)
              {
                nextDate = rfq.dueDate;
                break;
                
              }
              else
              {
                rfq.leftOverQuantity = rfq.currentQuantity - rfq.partialQuantity;
                if (rfq.leftOverQuantity > totalAvailable)
                {
                  // This RFQ can not be fully satisfied with an earliest
                  // complete offer.
                  if (DEBUG && debug)
                  {
                    debug("no earliest complete for rfq " + rfq.id
                        + " due to lack of free capacity (need " + rfq.leftOverQuantity
                        + " but only " + totalAvailable + " available)");
                  }
                  
                }
                else
                {
                  totalAvailable -= rfq.leftOverQuantity;
                  activeRfqs[activeCount++] = rfq;
                  // System.out.println("activating at day " + day + ": " + rfq
                  // + " (" + (nextActiveRFQ) + ')');
                }
              }
            }
            nextActiveRFQ++;
          }
        }
        
        int available = cavl[day];
        while (available > 0 && activeCount > 0)
        {
          int divided = available / activeCount;
          int spill = available % activeCount;
          // counter for distributing spill components (an extra
          // active count is subtracted to avoid negative modulo later on)
          int da = (day % activeCount) - activeCount;
          boolean finished = false;
          for (int i = 0; i < activeCount; i++)
          {
            ATPRFQ rfq = activeRfqs[i];
            int rest = rfq.leftOverQuantity;
            int rDivided = divided;
            if (((i - da) % activeCount) < spill)
            {
              rDivided++;
              // System.out.println("RFQ " + rfq.id + " got spill (avl="
              // + available + ",div=" + divided
              // + ",spill=" + spill
              // + ",day=" + day
              // + ",count=" + activeCount
              // + ')');
            }
            if (rest > rDivided)
            {
              rfq.leftOverQuantity -= rDivided;
              cavl[day] -= rDivided;
              available -= rDivided;
            }
            else
            {
              // RFQ no longer active
              rfq.leftOverQuantity = 0;
              rfq.earliestComplete = day + 1;
              cavl[day] -= rest;
              available -= rest;
              
              finished = true;
            }
          }
          if (finished)
          {
            for (int i = 0; i < activeCount; i++)
            {
              ATPRFQ rfq = activeRfqs[i];
              if (rfq.leftOverQuantity == 0)
              {
                activeCount--;
                activeRfqs[i] = activeRfqs[activeCount];
                activeRfqs[activeCount] = null;
                // Process this index again due to moved last rfq to here
                i--;
              }
            }
          }
        }
        if (activeCount == 0)
        {
          day = nextDate;
        }
        else
        {
          day++;
        }
      }
      
      if (activeCount > 0)
      {
        // Failed to allocate capacity for all RFQs. Remaining RFQs
        // will not receive any earliest complete offers.
        debug("Failed to generate earliest complete for " + activeCount + " RFQs");
        break;
      }
      
    } while (nextActiveRFQ < rfqCount);
    
    if (debug)
      debugWait("Offers:  Allocated earliest complete: " + activeCount + " RFQs remaining, "
          + (rfqCount - nextActiveRFQ) + " RFQs skipped,  " + "Total Available: " + totalAvailable);
  }
  
  private void mergeEqualRFQs()
  {
    int index = 1;
    ATPRFQ last = rfqs[0];
    for (int i = 1, n = rfqCount; i < n; i++)
    {
      if (last.equals(rfqs[i]))
      {
        if (DEBUG && debug)
        {
          debug("Bundling RFQs " + last.id + " and " + rfqs[i].id);
        }
        // Combine these rfqs
        last.bundleRFQ(rfqs[i]);
        rfqs[i] = null;
        
      }
      else if (index != i)
      {
        last = rfqs[i];
        rfqs[index++] = last;
        
      }
      else
      {
        last = rfqs[index++];
      }
    }
    if (index < rfqCount)
    {
      // Some RFQs have been bundled
      for (int i = index; i < rfqCount; i++)
      {
        rfqs[i] = null;
      }
      rfqCount = index;
    }
  }
  
  private int findReputationEnd(int start)
  {
    double lastRep = rfqs[start].reputation;
    int end = start + 1;
    while (end < rfqCount && rfqs[end].reputation == lastRep)
    {
      end++;
    }
    return end;
  }
  
  private int findPreviousRFQ(int index, int startRFQ)
  {
    while (--index >= startRFQ)
    {
      ATPRFQ prevRFQ = rfqs[index];
      if (!prevRFQ.finished && prevRFQ.requestedQuantity > prevRFQ.currentQuantity)
      {
        return index;
      }
    }
    return -1;
  }
  
  protected void debugWarning(String message)
  {
    if (logging)
    {
      log.warning(message);
    }
    else
    {
      System.err.println(message);
    }
  }
  
  protected void debug(String message)
  {
    if (logging)
    {
      log.finest(message);
    }
    else
    {
      System.out.println(message);
    }
  }
  
  // Called to enable interactive versions of this algorithm
  protected void debugWait(String message)
  {
    if (logging)
    {
      log.finest(message);
    }
    else
    {
      System.out.println(message);
    }
  }
  
  // -------------------------------------------------------------------
  // Utility methods
  // -------------------------------------------------------------------
  
  private void setup(int[] capacityCommitted)
  {
    int inventory = currentInventory;
    for (int i = currentDay; i < noDays; i++)
    {
      price[i] = 0.0;
      cfr[i] = capacityActual - capacityCommitted[i];
      
      if (inventory > 0 && capacityCommitted[i] > 0)
      {
        if (inventory > capacityCommitted[i])
        {
          cfr[i] += capacityCommitted[i];
          inventory -= capacityCommitted[i];
        }
        else
        {
          cfr[i] += inventory;
          inventory = 0;
        }
      }
    }
    
    int currentNeed = 0;
    for (int i = noDays - 1; i > currentDay; i--)
    {
      int totNeed = currentNeed + (capacityActual - cfr[i]);
      if (totNeed > capacityActual)
      {
        currentNeed = totNeed - capacityActual;
      }
      else
      {
        currentNeed = 0;
      }
      overAlloc[i - 1] = currentNeed;
    }
  }
  
  private void calculatePrices(int rfqStart, int rfqEnd)
  {
    for (int i = rfqStart; i < rfqEnd; i++)
    {
      cfr[rfqs[i].finDate] -= rfqs[i].currentQuantity;
    }
    
    int currentNeed = 0;
    for (int i = noDays - 1; i > currentDay; i--)
    {
      //       int totNeed = currentNeed + Math.max(0, capacityActual - cfr[i]);
      int c = capacityActual - cfr[i];
      int totNeed = currentNeed + (c > 0 ? c : 0);
      if (totNeed > capacityActual)
      {
        currentNeed = totNeed - capacityActual;
      }
      else
      {
        currentNeed = 0;
      }
      overAlloc[i - 1] = currentNeed;
    }
    
    updatePrice();
    
    // Lock RFQs with high enough reserve price (0 indicates any price)
    for (int i = rfqStart; i < rfqEnd; i++)
    {
      double p = rfqs[i].reservePrice;
      if (p > price[rfqs[i].finDate] || p == 0.0)
      {
        rfqs[i].finished = true;
      }
    }
  }
  
  private void updatePrice()
  {
    int cFree = 0;
    int cap = 0;
    
    // Starts on day 1 since we calculate with finDate * CAPACITY as cap
    // in all other places
    for (int i = currentDay + 1, n = noDays; i < n; i++)
    {
      cFree += cfr[i];
      cap += capacityActual;
      double cavl = cFree - overAlloc[i];
      //       if (debug) System.out.println("Day " + i + " cavl = "
      // 				    + cavl + " cap = " + cap);
      price[i] = 1 - priceDiscount * cavl / cap;
    }
  }
  
  private void scaleDown(int rfqStart, int rfqEnd)
  {
    for (int i = rfqStart; i < rfqEnd; i++)
    {
      double aCap = capacityActual * (rfqs[i].finDate - currentDay);
      double price = 0.0;
      if (aCap > 0)
      {
        price = 1 - priceDiscount * (aCap - rfqs[i].requestedQuantity) / aCap;
      }
      else
      {
        // Very high price if nothing available!
        price = 100000.0;
      }
      //       if (DEBUG && debug)
      // 	System.out.println("Min price for RFQ " + rfqs[i].id + " " + price);
      
      if (price > rfqs[i].reservePrice && rfqs[i].reservePrice > 0)
      {
        // Scale down the RFQ!!!
        rfqs[i].currentQuantity = (int) (rfqs[i].reservePrice / price * rfqs[i].requestedQuantity);
        // 'Force' it down -> can not be more than this
        rfqs[i].requestedQuantity = rfqs[i].currentQuantity;
      }
      else
      {
        rfqs[i].currentQuantity = rfqs[i].requestedQuantity;
      }
    }
  }
  
  // -------------------------------------------------------------------
  // Information access for debug and test purposes
  // -------------------------------------------------------------------
  
  boolean isDebug()
  {
    return debug;
  }
  
  void setDebug(boolean debug)
  {
    this.debug = debug;
  }
  
  boolean isLogging()
  {
    return logging;
  }
  
  void setLogging(boolean logging)
  {
    this.logging = logging;
  }
  
  boolean isProcessingOffers()
  {
    return isProcessingOffers;
  }
  
  int[] getAvailableCapacity()
  {
    return cavl;
  }
  
  int[] getFreeCapacity()
  {
    return cfr;
  }
  
  int[] getOverAlloc()
  {
    return overAlloc;
  }
  
  double[] getCurrentPrices()
  {
    return price;
  }
  
  int getCurrentDebt()
  {
    return debt;
  }
  
  int getTotalReduction()
  {
    return totalReduction;
  }
  
  int getCurrentRFQ()
  {
    return currentRFQ;
  }
  
  ATPRFQ getRFQ(int index)
  {
    if (index >= rfqCount)
    {
      return null;
    }
    return rfqs[index];
  }
  
} // SupplierATP
