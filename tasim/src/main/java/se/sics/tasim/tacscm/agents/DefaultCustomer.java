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
 * DefaultCustomer
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu Jan 04 16:12:35 2005
 * Updated : $Date: 2008-03-07 10:01:29 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3825 $
 */
package se.sics.tasim.tacscm.agents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.sics.isl.gui.DotDiagram;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.tacscm.sim.AbstractCustomer;

public class DefaultCustomer extends AbstractCustomer
{
  
  private final static Logger log = Logger.getLogger(DefaultCustomer.class.getName());
  
  private static final boolean DEBUG = true;
  
  private final static boolean BUSINESS_CYCLE = false;
  
  // private final int[][] DEFAULT_SEGMENTS = {
  // {5, 6, 7, 14, 15}, // High perf segment
  // {2, 3, 4, 11, 12, 13}, // Medium segment
  // {0, 1, 8, 9, 10} // Low perf segment
  // };
  
  // private final int[][] DEFAULT_SEGMENTS = {
  // {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7},
  // {8}, {9}, {10}, {11}, {12}, {13}, {14}, {15}
  // };
  
  private int quantityMin = 1, quantityDiff = 20;
  private int dueDateMin = 3, dueDateDiff = 10;
  // The reserve price settings is specified as percentage of the
  // product base price
  private int reservePriceMin = 200, reservePriceDiff = 100;
  // The penalties are specified as percentage of the reserve price
  private int penaltyMin = 5, penaltyDiff = 10;
  
  // Customer demand segmentation
  private String[] segmentNames;
  private CustomerRW[] segmentWalk;
  
  // Business cycle (not used in TAC'04)
  private double businessTrendMin = 0.98;
  private double businessTrendMax = 1 / businessTrendMin;
  private int businessMin = 70;
  private int businessMax = (int) (100.0 * (100.0 / businessMin));
  private double businessCycleFactor = 1.0;
  private CustomerRW businessCycle;
  
  public DefaultCustomer()
  {}
  
  public String getProductSegmentName(int index)
  {
    if (index < 0 || index >= getProductSegmentCount())
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + getProductSegmentCount());
    }
    return segmentNames[index];
  }
  
  public int[] getProductSegment(int index)
  {
    if (index < 0 || index >= getProductSegmentCount())
    {
      throw new IndexOutOfBoundsException("Index: " + index + " Size: " + getProductSegmentCount());
    }
    return segmentWalk[index].getSegments();
  }
  
  public int getProductSegmentCount()
  {
    return segmentNames == null ? 0 : segmentNames.length;
  }
  
  protected void customerSetup()
  {
    Random random = getCustomerSetupRandom();
    customerSetup(random);
  }
  
  private void customerSetup(Random random)
  {
    double trendMin = (double) getPropertyAsFloat("trendMin", 0.95f);
    double trendMax = trendMin > 0 ? 1.0d / trendMin : Double.MAX_VALUE;
    
    int rfqAvgMin = getPropertyAsInt("rfqAvgMin", 80);
    int rfqAvgMax = getPropertyAsInt("rfqAvgMax", 320);
    int rfqAvgDiff = rfqAvgMax - rfqAvgMin + 1;
    check(rfqAvgMin, rfqAvgDiff, "rfq avg");
    
    quantityMin = getPropertyAsInt("quantityMin", quantityMin);
    quantityDiff = getPropertyAsInt("quantityMax", quantityMin + quantityDiff) - quantityMin + 1;
    check(quantityMin, quantityDiff, "quantity");
    
    dueDateMin = getPropertyAsInt("dueDateMin", dueDateMin);
    dueDateDiff = getPropertyAsInt("dueDateMax", dueDateMin + dueDateDiff) - dueDateMin + 1;
    check(dueDateMin, dueDateDiff, "due date");
    
    reservePriceMin = getPropertyAsInt("reservePriceMin", reservePriceMin);
    reservePriceDiff = getPropertyAsInt("reservePriceMax", reservePriceMin + reservePriceDiff)
        - reservePriceMin + 1;
    check(reservePriceMin, reservePriceDiff, "reserve price");
    
    penaltyMin = getPropertyAsInt("penaltyMin", penaltyMin);
    penaltyDiff = getPropertyAsInt("penaltyMax", penaltyMin + penaltyDiff) - penaltyMin + 1;
    check(penaltyMin, penaltyDiff, "penalty");
    
    // Initialize the segments
    String[] segmentNames = getPropertyAsArray("segments");
    if (segmentNames == null)
    {
      throw new IllegalStateException("no customer segments");
    }
    this.segmentNames = segmentNames;
    segmentWalk = new CustomerRW[segmentNames.length];
    for (int i = 0, n = segmentWalk.length; i < n; i++)
    {
      int[] segments = getPropertyAsIntArray("segment." + segmentNames[i]);
      if (segments == null)
      {
        throw new IllegalStateException("no data for segment " + segmentNames[i]);
      }
      int segRFQMin = getPropertyAsInt("segment." + segmentNames[i] + ".rfqAvgMin", -1);
      if (segRFQMin < 0)
      {
        segRFQMin = (int) ((rfqAvgMin * segments.length) / 16.0 + 0.5);
      }
      int segRFQMax = getPropertyAsInt("segment." + segmentNames[i] + ".rfqAvgMax", -1);
      if (segRFQMax <= 0)
      {
        segRFQMax = (int) ((rfqAvgMax * segments.length) / 16.0 + 0.5);
      }
      int segRFQAvgDiff = segRFQMax - segRFQMin + 1;
      check(segRFQMin, segRFQAvgDiff, "segment rfq avg");
      
      int start = segRFQMin + random.nextInt(segRFQAvgDiff);
      if (DEBUG)
        System.out.println("Creating Walk for segment " + (i + 1) + " (" + segmentNames[i]
            + ") RFQ: " + segRFQMin + " - " + segRFQMax + " starting at " + start);
      
      segmentWalk[i] = new CustomerRW(trendMin, trendMax, segRFQMin, segRFQMax, start, segments);
    }
    if (BUSINESS_CYCLE)
    {
      if (DEBUG)
        System.out.println("Business cycle: " + businessMin + " - " + businessMax);
      businessCycle = new CustomerRW(businessTrendMin, businessTrendMax, businessMin, businessMax,
          100, null);
    }
  }
  
  private void check(int min, int diff, String name)
  {
    if (min < 0 || diff <= 0)
    {
      throw new IllegalArgumentException("illegal " + name + " interval");
    }
  }
  
  protected double nextBusinessCycle(Random rnd)
  {
    if (BUSINESS_CYCLE)
    {
      double cval = businessCycle.nextRFQAverage(rnd);
      businessCycleFactor = cval / 100.0;
    }
    return businessCycleFactor;
  }
  
  protected RFQBundle generateRFQs(int date)
  {
    // Random rndSegWalk = getSegRfqsRandom(); //# RFQs (for seg)
    // Random rndProd = getProdRfqsRandom(); //# RFQs (by prod)
    // Random rndRFQ = getRfqInfoRandom(); //RFQ info (qty, due, RP, pen)
    RFQBundle rfq = new RFQBundle(date);
    BOMBundle bomBundle = getBOMBundle();
    for (int s = 0, ns = segmentWalk.length; s < ns; s++)
    {
      Random rndSegWalk = getSegRfqsRandom(s);
      Random rndProd = getProdRfqsRandom(s);
      Random rndRFQ = getRfqInfoRandom(s);
      
      CustomerRW walk = segmentWalk[s];
      int[] segments = walk.getSegments();
      for (int i = 0, n = walk.getRFQNumber(rndSegWalk); i < n; i++)
      {
        int productID = segments[rndProd.nextInt(segments.length)];
        int productIndex = bomBundle.getIndexFor(productID);
        // Random rndRFQ = getRfqInfoRandom(productIndex);
        int quantity = quantityMin + rndRFQ.nextInt(quantityDiff);
        int dueDate = date + dueDateMin + rndRFQ.nextInt(dueDateDiff);
        int reservePricePerUnit = getReservePricePerUnit(rndRFQ, bomBundle, productIndex);
        int penaltyPercentage = penaltyMin + rndRFQ.nextInt(penaltyDiff);
        int penalty = (int) ((double) quantity * reservePricePerUnit * penaltyPercentage / 100.0);
        rfq.addRFQ(getNextRFQID(), productID, quantity, reservePricePerUnit, dueDate, penalty);
      }
    }
    
    // EventWriter eventWriter = getEventWriter();
    // int customerIndex = getIndex();
    // eventWriter.dataUpdated(customerIndex, DU_CUSTOMER_AVERAGE_DEMAND,
    // (int) rfqAverageNumber);
    
    return rfq;
  }
  
  private int getReservePricePerUnit(Random random, BOMBundle bundle, int productIndex)
  {
    int basePrice = bundle.getProductBasePrice(productIndex);
    if (basePrice == 0)
    {
      // What should be done if no base price was be found??? FIX THIS!!!
      basePrice = 4 * 500;
      if (priceWarningCounter++ < 20)
      {
        log.severe("no base price for product " + bundle.getProductID(productIndex) + " found");
      }
    }
    int reservePercentage = reservePriceMin + random.nextInt(reservePriceDiff);
    return (int) ((double) reservePercentage * basePrice / 100.0);
  }
  
  // ONLY FOR DEBUGGING: REMOVE THIS!!! REMOVE THIS!!!
  private int priceWarningCounter = 0;
  
  private static class CustomerRW
  {
    private final double trendMin;
    private final double trendMax;
    private final int rfqAvgMin;
    private final int rfqAvgMax;
    
    private double rfqAverageNumber;
    private double trend;
    // private double dstMax;
    // private double dstMin;
    private int[] segments;
    
    CustomerRW(double trendMin, double trendMax, int avgMin, int avgMax, int startPos,
        int[] segments)
    {
      this.trendMin = trendMin;
      this.trendMax = trendMax;
      this.rfqAvgMin = avgMin;
      this.rfqAvgMax = avgMax;
      this.rfqAverageNumber = startPos;
      this.segments = segments;
      trend = 1.0d;
      
      // dstMax = trendMax - 1.0;
      // dstMin = 1.0 - trendMin;
      
      if (DEBUG)
        System.out.println("Customer RW:" + rfqAvgMin + " - " + rfqAvgMax + " trend: " + trendMin
            + " - " + trendMax);
    }
    
    int[] getSegments()
    {
      return segments;
    }
    
    void reset(Random random)
    {
      rfqAverageNumber = rfqAvgMin + random.nextInt(rfqAvgMax - rfqAvgMin);
    }
    
    double nextRFQAverage(Random random)
    {
      trend += (0.50d - random.nextDouble()) / 50d;
      
      // A tenth of destin to max - destin to min: more symmetric but biased...
      // trend += (random.nextDouble() * dstMax -
      // dstMin * random.nextDouble()) / 10d;
      
      if (trend < trendMin)
      {
        trend = trendMin;
      }
      else if (trend > trendMax)
      {
        trend = trendMax;
      }
      
      rfqAverageNumber *= trend;
      
      if (rfqAverageNumber < rfqAvgMin)
      {
        rfqAverageNumber = rfqAvgMin;
        trend = 1.0d;
      }
      else if (rfqAverageNumber > rfqAvgMax)
      {
        rfqAverageNumber = rfqAvgMax;
        trend = 1.0d;
      }
      return rfqAverageNumber;
    }
    
    int getRFQNumber(Random random)
    {
      int number = nextPoisson(random, nextRFQAverage(random));
      return number;
    }
    
    private int nextPoisson(Random random, double averageNumber)
    {
      double elambda = Math.exp(-averageNumber);
      double product = 1;
      int count = 0;
      if (elambda == 0)
      {
        return (int) averageNumber;
      }
      while (product >= elambda)
      {
        product *= random.nextDouble();
        count++;
      }
      // the result should be one behind
      return count == 0 ? 0 : (count - 1);
    }
    
  }
  
  // -------------------------------------------------------------------
  // Test main with some graphs illustrating the customer demand
  // -------------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    
    // A Fake BOM for "simulation" of the simulation...
    final int FAST_PINTEL = 0;
    final int SLOW_PINTEL = 1;
    final int FAST_IMD = 2;
    final int SLOW_IMD = 3;
    final int MB_PINTEL = 4;
    final int MB_IMD = 5;
    final int BIG_HD = 6;
    final int SMALL_HD = 7;
    final int BIG_MEM = 8;
    final int SMALL_MEM = 9;
    
    final String[] COMP_NAMES = new String[]
    { "FAST_PINTEL", "SLOW_PINTEL", "FAST_IMD", "SLOW_IMD", "MB_PINTEL", "MB_IMD", "BIG_HD",
        "SMALL_HD", "BIG_MEM", "SMALL_MEM" };
    
    final int[][] BOM = new int[][]
    {
    { SLOW_PINTEL, MB_PINTEL, SMALL_MEM, SMALL_HD }, // 0
        { SLOW_PINTEL, MB_PINTEL, SMALL_MEM, BIG_HD }, // 1
        { SLOW_PINTEL, MB_PINTEL, BIG_MEM, SMALL_HD }, // 2
        { SLOW_PINTEL, MB_PINTEL, BIG_MEM, BIG_HD }, // 3
        { FAST_PINTEL, MB_PINTEL, SMALL_MEM, SMALL_HD }, // 4
        { FAST_PINTEL, MB_PINTEL, SMALL_MEM, BIG_HD }, // 5
        { FAST_PINTEL, MB_PINTEL, BIG_MEM, SMALL_HD }, // 6
        { FAST_PINTEL, MB_PINTEL, BIG_MEM, BIG_HD }, // 7
        
        { SLOW_IMD, MB_IMD, SMALL_MEM, SMALL_HD }, // 8
        { SLOW_IMD, MB_IMD, SMALL_MEM, BIG_HD }, // 9
        { SLOW_IMD, MB_IMD, BIG_MEM, SMALL_HD }, // 10
        { SLOW_IMD, MB_IMD, BIG_MEM, BIG_HD }, // 11
        { FAST_IMD, MB_IMD, SMALL_MEM, SMALL_HD }, // 12
        { FAST_IMD, MB_IMD, SMALL_MEM, BIG_HD }, // 13
        { FAST_IMD, MB_IMD, BIG_MEM, SMALL_HD }, // 14
        { FAST_IMD, MB_IMD, BIG_MEM, BIG_HD } // 15
    };
    
    Random rnd = new Random();
    double weight[] = new double[]
    { 2, 1 };
    int noCusts = weight.length;
    ConfigManager config = new ConfigManager();
    String configFile = "config/tac04scm_sim.conf";
    if (!config.loadConfiguration(configFile))
    {
      throw new IllegalStateException("could not load configuration file '" + configFile + '\'');
    }
    
    DefaultCustomer customer = new DefaultCustomer();
    customer.setConfig(config, "customer");
    customer.customerSetup(rnd);
    
    int noDays = 220;
    int noSegments = customer.segmentWalk.length;
    int[][] data = new int[noSegments + 1][noDays];
    int[][] distribution = new int[noSegments + 1][400];
    
    // Left to test:
    // Randomness of each component (based on BOM + segments above)
    int noComp = 10;
    int[][] compData = new int[noComp][noDays];
    double[][] compWeight = new double[noSegments][noComp];
    for (int i = 0, n = noSegments; i < n; i++)
    {
      int[] segments = customer.segmentWalk[i].getSegments();
      for (int j = 0, seglen = segments.length; j < seglen; j++)
      {
        // Convert the default products from product id to index. HACK!!!
        int pc = segments[j] - 1;
        for (int k = 0; k < 4; k++)
        {
          compWeight[i][BOM[pc][k]] += 1.0 / seglen;
        }
      }
    }
    
    for (int i = 0, n = noSegments; i < n; i++)
    {
      System.out.println("Segment " + (1 + i));
      for (int j = 0, m = noComp; j < m; j++)
      {
        System.out.println("Component: " + COMP_NAMES[j] + " -> " + compWeight[i][j]);
      }
    }
    
    JFrame window = new JFrame("Last Customer Random Walk");
    JFrame window2 = new JFrame("Histograms");
    DotDiagram diagram = new DotDiagram(noSegments + 1);
    DotDiagram diagram2 = new DotDiagram(noSegments + 1);
    
    JFrame compRWWindow = new JFrame("Component Random Walks");
    DotDiagram[] compRW = new DotDiagram[noComp / 2];
    JPanel comps = new JPanel(new GridLayout(2, 3));
    for (int i = 0, n = noComp / 2; i < n; i++)
    {
      compRW[i] = new DotDiagram(2);
      compRW[i].setDotColor(0, Color.blue);
      compRW[i].setDotColor(1, Color.green);
      compRW[i].setMinMax(0, 220);
      compRW[i].addConstant(Color.lightGray, 100);
      compRW[i].addConstant(Color.lightGray, 200);
      compRW[i].setData(0, compData[i * 2], 0, compData[i * 2].length);
      compRW[i].setData(1, compData[i * 2 + 1], 0, compData[i * 2 + 1].length);
      JPanel pan = new JPanel(new BorderLayout());
      pan.add(new JLabel(COMP_NAMES[i * 2] + " & " + COMP_NAMES[i * 2 + 1]), BorderLayout.NORTH);
      pan.add(compRW[i], BorderLayout.CENTER);
      comps.add(pan);
    }
    compRWWindow.setBounds(600, 0, 600, 400);
    compRWWindow.getContentPane().add(comps);
    compRWWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    compRWWindow.setVisible(true);
    
    window.setSize(600, 400);
    window.getContentPane().add(diagram);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);
    
    window2.setBounds(0, 400, 600, 400);
    window2.getContentPane().add(diagram2);
    window2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window2.setVisible(true);
    
    diagram.setData(0, data[0], 0, noDays);
    diagram.setData(1, data[1], 0, noDays);
    diagram.setData(2, data[2], 0, noDays);
    diagram.setData(3, data[noSegments], 0, noDays);
    diagram.setDotColor(0, new java.awt.Color(0x2020e0));
    diagram.setDotColor(1, new java.awt.Color(0x208020));
    diagram.setDotColor(2, new java.awt.Color(0xf08080));
    diagram.addConstant(Color.yellow, 320);
    diagram.addConstant(Color.yellow, 80);
    diagram.setMinMax(0, 350);
    
    diagram2.setDotColor(0, new java.awt.Color(0x2020e0));
    diagram2.setDotColor(1, new java.awt.Color(0x208020));
    diagram2.setDotColor(2, new java.awt.Color(0xf08080));
    int dist = 0;
    while (true)
    {
      // Reset walks!!!
      
      for (int i = 0, n = customer.segmentWalk.length; i < n; i++)
      {
        customer.segmentWalk[i].reset(rnd);
      }
      if (BUSINESS_CYCLE)
      {
        customer.businessCycle.reset(rnd);
      }
      
      for (int i = 0, n = 220; i < n; i++)
      {
        for (int j = 0, m = noComp; j < m; j++)
        {
          compData[j][i] = 0;
        }
        if (BUSINESS_CYCLE)
        {
          customer.nextBusinessCycle(rnd);
        }
        
        for (int w = 0, nw = noSegments; w < nw; w++)
        {
          // data[w][i] = customer.segmentWalk[w].getRFQNumber(rnd);
          data[w][i] = (int) (customer.segmentWalk[w].nextRFQAverage(rnd) * customer.businessCycleFactor);
          // data2[i] = (int) customer.segmentWalk[0].rfqAverageNumber;
          // data3[i] = (int) customer.segmentWalk[0].rfqAverageNumber;
          // System.out.println("RFQValue: " + i + " -> " + data2[i]);
          
          for (int j = 0, m = noComp; j < m; j++)
          {
            compData[j][i] += (int) (data[w][i] * compWeight[w][j]);
          }
          
          if (data[w][i] < 400)
          {
            distribution[w][data[w][i]]++;
          }
          else
          {
            distribution[w][399]++;
          }
        }
        data[noSegments][i] = 0;
        for (int w = 0, nw = noSegments; w < nw; w++)
        {
          data[noSegments][i] += data[w][i];
        }
        if (data[noSegments][i] < 400)
        {
          distribution[noSegments][data[noSegments][i]]++;
        }
        else
        {
          distribution[noSegments][399]++;
        }
      }
      
      dist++;
      if (dist > 1000 || (dist % 20 == 0))
      {
        System.out.println("Number of runs: " + dist);
        diagram.repaint();
        for (int i = 0, n = compRW.length; i < n; i++)
        {
          compRW[i].repaint();
        }
        // 	for (int w = 0, n = distribution.length; w < n; w++) {
        // 	  diagram2.setData(w, distribution[w], 0, distribution[w].length);
        // 	}
        diagram2.setData(0, distribution[noSegments], 0, distribution[noSegments].length);
        diagram2.repaint();
      }
      while ((dist > 1000) && System.in.read() != '\n')
        ;
    }
    
  }
  
} // DefaultCustomer
