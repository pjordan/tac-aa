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
 * SupplierViewer
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Tue Feb 22 14:32:57 2005
 * Updated : $Date: 2008-03-07 09:59:58 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3824 $
 */
package se.sics.tasim.tacscm.atp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 */
public class SupplierViewer extends JComponent implements ActionListener, KeyListener, Runnable
{
  private static final boolean VERBOSE_DEBUG = false;
  
  private static final int STEP = 0;
  private static final int RUN = 1;
  private static final int CLEAR = 2;
  private static final int RESET = 3;
  
  private static final double PI2 = 3.141592 / 2d;
  
  private static final double DELTA = 0.001;
  
  private static final int Y_MIN = 22;
  private static final int Y_SPACING = 12;
  private static final int DAYS_LINE = 5;
  private static final int PDY = 10;
  private static final int BR_HEIGHT = 5;
  
  private static final Color TRANSP_BLUE = new Color(0x80, 0x80, 0xff, 0x60);
  private static final Color TRANSP_RED = new Color(0xff, 0x80, 0x80, 0x60);
  private static final Color LIGHT_BLUE = new Color(0x80, 0x80, 0xff);
  
  private static final Color COMPLETE_FULL_COLOR = new Color(0x00, 0xd0, 0x00);
  private static final Color COMPLETE_REDUCED_COLOR = new Color(0xa0, 0xff, 0xa0);
  
  /** The color of row separators. Set to NULL for no row separators. */
  private static final Color ROW_COLOR = new Color(0xf3, 0xf3, 0xf3);
  private static final Color SELECTED_ROW_COLOR = new Color(0xc5, 0xc5, 0xc5);
  
  /** SCM info */
  private static final int MAX_RFQ = 30;
  private static final int INVENTORY = 0;
  private static final int NOMINAL_CAPACITY = 550;
  private static final int CAPACITY = NOMINAL_CAPACITY;
  private static final int SHORT_HORIZON = 20;
  private static final double CAPACITY_REDUCTION = 0.005;
  private static final double REPUTATION_EXPO = 2.0;
  private static final double DISCOUNT = 0.5;
  private static final int MAX_DUEDATE = 220;
  private static final int BASE_PRICE = 100;
  
  /** Settings for randomly created test RFQs */
  private static final int MAX_QUANTITY = 10000;
  private static final int REPUTATION_GROUPS = 3;
  
  private int inventory = INVENTORY;
  private int capacity = CAPACITY;
  private int nominalCapacity = NOMINAL_CAPACITY;
  private int shortHorizon = SHORT_HORIZON;
  private int basePrice = BASE_PRICE;
  private double capacityReduction = CAPACITY_REDUCTION;
  private double reputationExpo = REPUTATION_EXPO;
  private double discount = DISCOUNT;
  private int noDays = MAX_DUEDATE;
  private int maxRFQ = 0;
  private int currentDay = 0;
  
  private Random random;
  private SupplierATP supplier;
  
  private int[] cexpected = new int[noDays];
  private int[] ccommited = new int[noDays];
  private int lastCommited = -1;
  
  /** copy of all information for separate price calculation */
  private int[] pcfr = new int[noDays];
  private int[] pOverAlloc = new int[noDays];
  private int[] pAlloc = new int[noDays];
  private double[] pPrice = new double[noDays];
  
  private String currentTitle = null;
  
  private ATPRFQ[] origRfqs = new ATPRFQ[MAX_RFQ];
  private double[] repGroups;
  
  private boolean waitForStep = true;
  private boolean isSupplierRunning = false;
  private int mode = STEP;
  private boolean isRunning = false;
  
  private int testCount = 0;
  private Promise[] lastPromises;
  
  private JButton nextButton;
  private JButton runButton;
  private JButton clearButton;
  private JButton newButton;
  private JButton configButton;
  private JButton promiseButton;
  private JPanel buttonPanel;
  
  /** Viewer information */
  private int maxDigitWidth = 0;
  private int columnWidth = 0;
  
  public SupplierViewer()
  {
    setOpaque(true);
    setBackground(Color.white);
    setToolTipText(" ");
    
    buttonPanel = new JPanel();
    buttonPanel.setBackground(getBackground());
    nextButton = createButton(buttonPanel, "Step", 'S');
    runButton = createButton(buttonPanel, "Run", 'R');
    clearButton = createButton(buttonPanel, "Clear and Restart", 'C');
    newButton = createButton(buttonPanel, "New set of RFQs", 'N');
    configButton = createButton(buttonPanel, "Set Config", 'o');
    promiseButton = createButton(buttonPanel, "Show Offers", 'f');
    setFocusable(true);
    addKeyListener(this);
    
    createSupplier();
  }
  
  private void createSupplier()
  {
    if (ccommited == null || ccommited.length < noDays)
    {
      ccommited = new int[noDays];
      lastCommited = -1;
      cexpected = new int[noDays];
      
      pcfr = new int[noDays];
      pOverAlloc = new int[noDays];
      pAlloc = new int[noDays];
      pPrice = new double[noDays];
    }
    
    supplier = new SupplierATP("", shortHorizon, capacityReduction, reputationExpo,
        nominalCapacity, discount, basePrice, noDays, MAX_RFQ) {
      protected void debugWait(String message)
      {
        SupplierViewer.this.debugWait(message);
      }
    };
    supplier.setLogging(false);
  }
  
  private void debugWait(String message)
  {
    currentTitle = message;
    if (mode == STEP)
    {
      checkPrices();
      repaint();
      waitForStep();
    }
  }
  
  private synchronized void waitForStep()
  {
    try
    {
      while (waitForStep && mode == STEP)
      {
        wait();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      waitForStep = true;
    }
  }
  
  public JComponent getButtonPanel()
  {
    return buttonPanel;
  }
  
  private JButton createButton(JPanel panel, String title, char mn)
  {
    JButton b = new JButton(title);
    b.setMnemonic(mn);
    b.addActionListener(this);
    b.addKeyListener(this);
    b.setMargin(new Insets(0, 2, 0, 2));
    b.setRequestFocusEnabled(false);
    b.setFocusable(false);
    panel.add(b);
    return b;
  }
  
  public void start()
  {
    isRunning = true;
    if (maxRFQ == 0)
    {
      reset();
    }
  }
  
  public void stop()
  {
    isRunning = false;
  }
  
  public void showConfigurationDialog()
  {
    JTextArea textArea = new JTextArea(getConfiguration(), 16, 40);
    Object[] array =
    { "Enter Configuration:", new JScrollPane(textArea) };
    int result = JOptionPane.showConfirmDialog(this, array, "Enter Configuration:",
        JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION)
    {
      String text = textArea.getText();
      if (text.length() > 0)
      {
        try
        {
          setConfiguration(text);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          JOptionPane.showMessageDialog(this, "Could not set configuration:\n" + e, "ERROR",
              JOptionPane.ERROR_MESSAGE);
        }
        repaint();
      }
    }
  }
  
  private void showInfoDialog(String title, String information)
  {
    JTextArea textArea = new JTextArea(information, 16, 40);
    textArea.setEditable(false);
    textArea.setTabSize(8);
    Object[] array =
    { title, new JScrollPane(textArea) };
    JOptionPane.showMessageDialog(this, array, title, JOptionPane.INFORMATION_MESSAGE);
  }
  
  public String getConfiguration()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Inventory: ").append(inventory);
    sb.append("\nCCommited: ");
    if (lastCommited >= 0)
    {
      for (int i = 0, n = lastCommited + 1; i < n; i++)
      {
        if (i > 0)
        {
          sb.append(',');
        }
        sb.append(ccommited[i]);
      }
    }
    sb.append("\nCapacity: ").append(capacity).append("\nNominalCapacity: ")
        .append(nominalCapacity).append("\nCurrentDay: ").append(currentDay).append("\nNoDays: ")
        .append(noDays).append("\nShortHorizon: ").append(shortHorizon).append(
            " \t# tShort in specification").append("\nCapacityReduction: ").append(
            capacityReduction).append(" \t# z in specification").append("\nReputationExpo: ")
        .append(reputationExpo).append("\nDiscount: ").append(discount).append("\nBasePrice: ")
        .append(basePrice);
    
    if (maxRFQ > 0)
    {
      sb.append("\n# RFQ: DueDate,Quantity,ReservePrice,[Reputation]");
      for (int i = 0; i < maxRFQ; i++)
      {
        ATPRFQ rfq = origRfqs[i];
        sb.append("\nRFQ ").append(i + 1).append(": ").append(rfq.dueDate).append(',').append(
            rfq.requestedQuantity).append(',').append(rfq.reservePrice).append(',').append(
            rfq.reputation);
      }
    }
    return sb.append('\n').toString();
  }
  
  private void showConfiguration()
  {
    System.out.println("-------------------------------------------------");
    System.out.println("SETUP:");
    System.out.println(getConfiguration());
    System.out.println("-------------------------------------------------");
  }
  
  public void setConfiguration(String text)
  {
    String[] lines = text.split("[\\n\\r]");
    boolean supplierChange = false;
    try
    {
      maxRFQ = 0;
      for (int i = 0, n = lines.length; i < n; i++)
      {
        int index = lines[i].indexOf('#');
        if (index >= 0)
        {
          // Remove any comments
          lines[i] = lines[i].substring(0, index).trim();
        }
        index = lines[i].indexOf(':');
        if (index >= 0)
        {
          String command = lines[i].substring(0, index).trim();
          String dataLine = lines[i].substring(index + 1).trim();
          if (startsWith(command, "rfq"))
          {
            String[] data = dataLine.split("[, \\t]");
            if (maxRFQ >= MAX_RFQ)
            {
              throw new IllegalArgumentException("too many RFQs");
              
            }
            else if (data != null && data.length >= 3)
            {
              int id = maxRFQ + 1;
              int dueDate = Integer.parseInt(data[0]);
              int quantity = Integer.parseInt(data[1]);
              double reservePrice = Double.parseDouble(data[2]);
              double reputation = data.length == 3 ? 1.0 : Double.parseDouble(data[3]);
              origRfqs[maxRFQ] = new ATPRFQ();
              origRfqs[maxRFQ++].setRFQ("", reputation, id, quantity, dueDate, reservePrice);
            }
            
          }
          else if (command.equalsIgnoreCase("ccommited"))
          {
            String[] data = dataLine.split("[, \\t]");
            if (data != null && ccommited != null)
            {
              lastCommited = -1;
              for (int ci = 0, cn = ccommited.length, cm = data.length; ci < cn; ci++)
              {
                if (ci < cm && data[ci].length() > 0)
                {
                  ccommited[ci] = parseInt("ccommited", data[ci], 0);
                  if (ccommited[ci] > 0)
                  {
                    lastCommited = ci;
                  }
                }
                else
                {
                  ccommited[ci] = 0;
                }
              }
            }
            
          }
          else if (command.equalsIgnoreCase("inventory"))
          {
            inventory = parseInt("inventory", dataLine, 0);
            
          }
          else if (command.equalsIgnoreCase("capacity"))
          {
            capacity = parseInt("capacity", dataLine, 1);
            
          }
          else if (command.equalsIgnoreCase("nominalCapacity"))
          {
            nominalCapacity = parseInt("nominalCapacity", dataLine, 1);
            supplierChange = true;
            
          }
          else if (command.equalsIgnoreCase("currentDay"))
          {
            currentDay = parseInt("currentDay", dataLine, 0);
            
          }
          else if (command.equalsIgnoreCase("noDays"))
          {
            noDays = parseInt("noDays", dataLine, 4);
            supplierChange = true;
            
          }
          else if (command.equalsIgnoreCase("basePrice"))
          {
            basePrice = parseInt("basePrice", dataLine, 0);
            supplierChange = true;
            
          }
          else if (command.equalsIgnoreCase("shortHorizon"))
          {
            shortHorizon = parseInt("shortHorizon", dataLine, 0);
            supplierChange = true;
            
          }
          else if (command.equalsIgnoreCase("capacityReduction"))
          {
            capacityReduction = parseDouble("capacityReduction", dataLine, 0.0);
            supplierChange = true;
            
          }
          else if (command.equalsIgnoreCase("reputationExpo"))
          {
            reputationExpo = parseDouble("reputationExpo", dataLine, 0.0);
            supplierChange = true;
            
          }
          else if (command.equalsIgnoreCase("discount"))
          {
            discount = parseDouble("discount", dataLine, 0.0);
            supplierChange = true;
          }
          
        }
        else if (lines[i].length() > 0)
        {
          throw new IllegalArgumentException("parse error: '" + lines[i] + '\'');
        }
      }
    }
    finally
    {
      if (supplierChange)
      {
        createSupplier();
      }
      showConfiguration();
      clear();
    }
  }
  
  private int parseInt(String name, String dataLine, int minValue)
  {
    int value = Integer.parseInt(dataLine);
    if (value < minValue)
    {
      throw new IllegalArgumentException(name + " must be at least " + minValue + ": " + dataLine);
    }
    return value;
  }
  
  private double parseDouble(String name, String dataLine, double minValue)
  {
    double value = Double.parseDouble(dataLine);
    if (value < minValue)
    {
      throw new IllegalArgumentException(name + " must be at least " + minValue + ": " + dataLine);
    }
    return value;
  }
  
  private boolean startsWith(String text, String prefix)
  {
    return text.regionMatches(true, 0, prefix, 0, prefix.length());
  }
  
  public void showPromises(Promise[] promises)
  {
    StringBuffer sb = new StringBuffer();
    if (promises == null || promises.length == 0)
    {
      sb.append("No offers made yet!");
    }
    else
    {
      for (int i = 0, n = promises.length; i < n; i++)
      {
        Promise p = promises[i];
        int id = p.getRFQID();
        ATPRFQ rfq = (id > 0 && id <= origRfqs.length && origRfqs[id - 1] != null && origRfqs[id - 1].id == id) ? origRfqs[id - 1]
            : null;
        if (i > 0)
        {
          sb.append('\n');
        }
        sb.append("RFQ ");
        if (id < 10)
        {
          sb.append("  ");
        }
        sb.append(id);
        sb.append(": Q=").append(p.getQuantity());
        if (rfq != null && p.getQuantity() < rfq.requestedQuantity)
        {
          sb.append('/').append(rfq.requestedQuantity);
        }
        sb.append(" \tP=").append(p.getUnitPrice());
        if (rfq != null)
        {
          sb.append('(').append((int) rfq.reservePrice).append(')');
        }
        sb.append(" \tDue=").append(p.getDueDate());
        if (rfq != null && rfq.dueDate < p.getDueDate())
        {
          sb.append('(').append(rfq.dueDate).append(')');
        }
      }
    }
    showInfoDialog("Offers", sb.toString());
  }
  
  public void reset()
  {
    if (random == null)
    {
      random = new Random();
    }
    if (REPUTATION_GROUPS > 1 && repGroups == null)
    {
      repGroups = new double[REPUTATION_GROUPS];
      for (int i = 0, n = REPUTATION_GROUPS; i < n; i++)
      {
        repGroups[i] = random.nextDouble();
      }
    }
    
    if (maxRFQ == 0)
    {
      maxRFQ = MAX_RFQ;
    }
    for (int i = 0, n = maxRFQ; i < n; i++)
    {
      if (origRfqs[i] == null)
      {
        origRfqs[i] = new ATPRFQ();
      }
      
      origRfqs[i].setRFQ("", REPUTATION_GROUPS > 1 ? repGroups[random.nextInt(REPUTATION_GROUPS)]
          : 1.0, i + 1, // RFQ ID
          2 + random.nextInt(MAX_QUANTITY - 2),
          // due dates must be at least two days into the future
          currentDay + 2 + random.nextInt(noDays - currentDay - 2), BASE_PRICE / 2
              + random.nextInt(BASE_PRICE));
    }
    showConfiguration();
    clear();
  }
  
  public void clear()
  {
    supplier.clearAll();
    for (int i = 0, n = pAlloc.length; i < n; i++)
    {
      pAlloc[i] = 0;
      pcfr[i] = 0;
      pPrice[i] = 0;
    }
    lastPromises = null;
  }
  
  public String getToolTipText(MouseEvent e)
  {
    int minY = Y_MIN;
    int y = e.getY() - minY;
    if (y >= 0)
    {
      int rfqIndex = y / Y_SPACING;
      ATPRFQ rfq = supplier.getRFQ(rfqIndex);
      if (rfq != null)
      {
        return "<html><b>RFQ " + (rfqIndex + 1) + "</b>: " + rfq.toHtml() + "</html>";
      }
    }
    return null;
  }
  
  // -------------------------------------------------------------------
  // Price calculation check
  // -------------------------------------------------------------------
  
  private void checkPrices()
  {
    int inventoryForPrice = inventory;
    for (int i = 0; i < noDays; i++)
    {
      pcfr[i] = capacity - ccommited[i];
      
      if (inventoryForPrice > 0 && ccommited[i] > 0)
      {
        if (inventoryForPrice > ccommited[i])
        {
          pcfr[i] += ccommited[i];
          inventoryForPrice -= ccommited[i];
        }
        else
        {
          pcfr[i] += inventoryForPrice;
          inventoryForPrice = 0;
        }
      }
      
      pAlloc[i] = 0;
      pOverAlloc[i] = pcfr[i] < 0 ? -pcfr[i] : 0;
    }
    
    for (int i = 0, n = supplier.getRFQCount(); i < n; i++)
    {
      ATPRFQ rfq = supplier.getRFQ(i);
      if (rfq != null)
      {
        int due = rfq.finDate;
        pcfr[due] -= rfq.currentQuantity;
        pAlloc[due] += rfq.currentQuantity;
      }
    }
    
    int currentNeed = 0;
    for (int i = noDays - 1; i >= currentDay; i--)
    {
      int totNeed = currentNeed + pAlloc[i];
      if (totNeed > capacity)
      {
        pOverAlloc[i] = currentNeed;
        currentNeed = totNeed - capacity;
        pAlloc[i] = capacity;
      }
      else if (currentNeed > 0)
      {
        pAlloc[i] += currentNeed;
        currentNeed = 0;
      }
    }
    
    int cFree = 0;
    // Starts on day 1 since we calculate with finDate * capacity as cap
    // in all other places
    int cap = 0;
    for (int i = currentDay + 1; i < noDays; i++)
    {
      cap += capacity;
      cFree += pcfr[i];
      double cavl = cFree - pOverAlloc[i];
      // Prices are in % of base price
      pPrice[i] = (1 - discount * cavl / cap);
      
      if (VERBOSE_DEBUG && i < 20)
      {
        System.out.println("Day " + i + " cavl=" + cavl + " cap=" + cap + " cfr=" + pcfr[i]
            + " cFree=" + cFree + " overAlloc=" + pOverAlloc[i] + " price=" + pPrice[i]);
      }
    }
  }
  
  // -------------------------------------------------------------------
  // Runnable
  // -------------------------------------------------------------------
  
  private synchronized void startSupplier(int mode)
  {
    if (isSupplierRunning)
    {
      this.mode = mode;
      if (mode == STEP)
      {
        waitForStep = false;
      }
      notifyAll();
      
    }
    else
    {
      switch (mode)
      {
        case STEP:
          this.waitForStep = true;
          // Continue into the RUN case
        case RUN:
          this.mode = mode;
          isSupplierRunning = true;
          (new Thread(this)).start();
          break;
        case CLEAR:
          clear();
          repaint();
          break;
        case RESET:
          reset();
          repaint();
          break;
      }
    }
  }
  
  public void run()
  {
    try
    {
      long startTime = System.currentTimeMillis();
      int count = 0;
      do
      {
        clear();
        
        // Add the RFQs
        for (int i = 0; i < maxRFQ; i++)
        {
          ATPRFQ r = origRfqs[i];
          // Only add the RFQ if within the range
          if (r.dueDate < noDays)
          {
            supplier.addRFQ(r.manufacturer, r.reputation, r.id, r.originalQuantity, r.dueDate,
                (int) r.reservePrice);
          }
        }
        
        count++;
        updateExpectedCapacity();
        lastPromises = supplier.processRFQs(currentDay, capacity, inventory, cexpected, ccommited);
        if (testCount > 0)
        {
          testCount--;
          if (testCount % 100 == 0)
          {
            System.out.println("After " + count + ": time elapsed = "
                + (System.currentTimeMillis() - startTime));
          }
          // } else if (mode == STEP) {
          // showPromises(lastPromises);
        }
        
      } while (testCount > 0 && isRunning);
      
    }
    finally
    {
      currentTitle = null;
      isSupplierRunning = false;
      supplier.setDebug(true);
      switch (mode)
      {
        case CLEAR:
          clear();
          break;
        case RESET:
          reset();
          break;
        default:
          checkPrices();
          break;
      }
      mode = STEP;
      repaint();
    }
  }
  
  protected void updateExpectedCapacity()
  {
    for (int i = 0; i < currentDay; i++)
    {
      cexpected[i] = 0;
    }
    
    double expected = capacity;
    for (int i = currentDay; i < noDays; i++)
    {
      cexpected[i] = (int) expected;
      expected = 0.99 * expected + 0.01 * nominalCapacity;
    }
  }
  
  // -------------------------------------------------------------------
  // Draw information
  // -------------------------------------------------------------------
  
  protected void paintComponent(Graphics g)
  {
    Color oldColor = g.getColor();
    int width = getWidth();
    int height = getHeight();
    if (isOpaque())
    {
      g.setColor(getBackground());
      g.fillRect(0, 0, width, height);
    }
    
    int minY = Y_SPACING + Y_MIN;
    int maxY = minY + Y_SPACING * MAX_RFQ;
    int minX = 40;
    int maxX = width - 177;
    double ddx = (maxX - minX) / (double) noDays;
    int currentRFQ = supplier.getCurrentRFQ();
    int rfqCount = supplier.getRFQCount();
    boolean isProcessingOffers = supplier.isProcessingOffers();
    g.setColor(Color.black);
    
    FontMetrics fm = g.getFontMetrics();
    if (currentTitle != null)
    {
      int titleWidth = fm.stringWidth(currentTitle);
      g.drawString(currentTitle, (width - fm.stringWidth(currentTitle)) / 2, 10);
    }
    
    if (maxDigitWidth == 0)
    {
      // Calculate max digit width
      for (char c = '0'; c <= '9'; c++)
      {
        int w = fm.charWidth(c);
        if (w > maxDigitWidth)
        {
          maxDigitWidth = w;
        }
      }
      columnWidth = 6 * maxDigitWidth;
    }
    
    // Optionally paint every second row to easier connect RFQ # with price
    if (ROW_COLOR != null)
    {
      g.setColor(ROW_COLOR);
      for (int i = 0, y = minY + 4, n = maxRFQ; i < n; i += 2, y += Y_SPACING << 1)
      {
        g.fillRect(minX, y, width - minX, Y_SPACING - 1);
      }
      if (SELECTED_ROW_COLOR != null && currentRFQ >= 0)
      {
        g.setColor(SELECTED_ROW_COLOR);
        g.fillRect(2, minY + (currentRFQ - 1) * Y_SPACING + 4, width - 4, Y_SPACING - 1);
      }
    }
    
    g.setColor(Color.lightGray);
    for (int i = 0; i < noDays; i += DAYS_LINE)
    {
      g.drawLine(minX + (int) (i * ddx), minY, minX + (int) (i * ddx), maxY);
    }
    
    int y = minY - Y_SPACING / 2;
    for (int i = 0, n = rfqCount; i < n; i++)
    {
      ATPRFQ rfq = supplier.getRFQ(i);
      if (rfq != null)
      {
        int dd = (int) (rfq.dueDate * ddx);
        if (isProcessingOffers)
        {
          if (rfq.partialQuantity > 0)
          {
            if (rfq.partialQuantity < rfq.currentQuantity)
            {
              g.setColor(LIGHT_BLUE);
            }
            else
            {
              g.setColor(COMPLETE_FULL_COLOR);
            }
            int len = (int) (ddx * rfq.partialQuantity / capacity);
            g.fillRect(minX + dd - len, y, len, 6);
            
            if (rfq.earliestComplete > 0)
            {
              // Also earliest complete
              g.setColor(Color.red);
              int edd = (int) (rfq.earliestComplete * ddx);
              len = (int) (ddx * (rfq.currentQuantity - rfq.partialQuantity) / capacity);
              if (len < 2)
              {
                len = 2;
              }
              g.fillRect(minX + edd - len, y, len, 7);
            }
          }
          
        }
        else
        {
          if (rfq.finished)
          {
            g.setColor(rfq.currentQuantity < rfq.requestedQuantity ? COMPLETE_REDUCED_COLOR
                : COMPLETE_FULL_COLOR);
          }
          else
          {
            g.setColor(Color.yellow);
          }
          int len = (int) (ddx * rfq.currentQuantity / capacity);
          g.fillRect(minX + dd - len, y, len, 6);
        }
        
        int len = (int) (ddx * rfq.requestedQuantity / capacity + 0.5);
        g.setColor(i == currentRFQ ? Color.red : Color.gray);
        g.drawRect(minX + dd - len, y, len, 6);
        
        y += Y_SPACING;
      }
    }
    
    g.setColor(Color.gray);
    g.drawLine(minX, 0, minX, maxY);
    g.drawLine(minX, maxY, maxX, maxY);
    
    final int colsize = 32;
    final int qsize = 10;
    int colsize2 = colsize / 2;
    width -= 3;
    g.setColor(Color.black);
    g.drawString("RFQ", 0, minY - Y_SPACING);
    g.drawString("rQ", width - colsize * 4 - qsize - colsize2, minY - Y_SPACING);
    g.drawString("cQ", width - colsize * 3 - colsize2, minY - Y_SPACING);
    g.drawString("rP", width - colsize * 2 - colsize2, minY - Y_SPACING);
    g.drawString("cP", width - colsize - colsize2, minY - Y_SPACING);
    if (isProcessingOffers)
    {
      drawString(g, fm, "Due", width, minY - Y_SPACING);
    }
    else
    {
      g.drawString("Pr", width - colsize2, minY - Y_SPACING);
    }
    
    int rfqWidth = fm.stringWidth("RFQ");
    double[] price = supplier.getCurrentPrices();
    double lastRep = -1.0;
    for (int i = 0, n = rfqCount; i < n; i++)
    {
      ATPRFQ rfq = supplier.getRFQ(i);
      if (rfq != null)
      {
        int currentY = minY + Y_SPACING * i + 2;
        drawString(g, fm, rfq.manufacturer + rfq.id, rfqWidth, currentY);
        // Indicate reputation
        if (rfq.reputation != lastRep)
        {
          g.setColor(Color.gray);
          g.drawLine(rfqWidth + 4, minY + Y_SPACING * (i - 1) + 3, maxX - 4, minY + Y_SPACING
              * (i - 1) + 3);
          
          lastRep = rfq.reputation;
          g.setColor(Color.black);
        }
        
        double rfqPrice = rfq.finalPrice > 0 ? rfq.finalPrice : price[rfq.finDate];
        int p1 = (int) Math.round(rfqPrice * 100.0);
        drawString(g, fm, "" + rfq.requestedQuantity, width - colsize * 4 - qsize, currentY);
        drawString(g, fm, "" + rfq.currentQuantity, width - colsize * 3, currentY);
        
        drawString(g, fm, "" + (int) Math.round(100 * rfq.reservePrice), width - colsize * 2,
            currentY);
        
        if (rfq.finalPrice > 0)
        {
          g.setColor(Color.blue);
        }
        else if (i > currentRFQ && currentRFQ > 0)
        {
          g.setColor(Color.lightGray);
        }
        drawString(g, fm, "" + p1, width - colsize, currentY);
        
        if (isProcessingOffers)
        {
          // Show due date and possible earliest complete due date
          StringBuffer sb = new StringBuffer();
          sb.append(rfq.dueDate);
          // if (rfq.earliestComplete > 0) {
          // sb.append('/').append(rfq.earliestComplete);
          // }
          g.setColor(Color.black);
          drawString(g, fm, sb.toString(), width, currentY);
          
        }
        else
        {
          if ((i <= currentRFQ || (currentRFQ < 0))
              && ((rfq.finalPrice > 0 && (rfqPrice > pPrice[rfq.finDate] + DELTA)) || (rfq.finalPrice == 0
                  && Math.abs(rfqPrice - pPrice[rfq.finDate]) >= DELTA && price[rfq.finDate] > 0)))
          {
            g.setColor(Color.red);
            // System.out.println("PRICE DIFF RFQ " + rfq.id + ": " +
            // Math.abs(price[rfq.finDate] - pPrice[rfq.finDate]));
          }
          else
          {
            g.setColor(Color.black);
          }
          int p2 = (int) Math.round(pPrice[rfq.finDate] * 100.0);
          if (VERBOSE_DEBUG)
          {
            System.out.println("pPrice[" + rfq.finDate + "]=" + pPrice[rfq.finDate]);
          }
          drawString(g, fm, "" + p2, width, currentY);
        }
        
        if (i >= currentRFQ)
        {
          // RFQ not yet processed
        }
        else if ((rfqPrice - rfq.reservePrice) >= DELTA)
        {
          if (rfq.currentQuantity > 0)
          {
            g.setColor(Color.red);
            g.drawString("-", width - colsize * 3, currentY);
          }
        }
        else if ((rfq.reservePrice - rfqPrice) >= DELTA)
        {
          if (rfq.currentQuantity <= 0 && rfq.requestedQuantity > 0)
          {
            g.setColor(Color.red);
            g.drawString("+", width - colsize * 3, currentY);
          }
        }
        g.setColor(Color.black);
      }
    }
    if (rfqCount > 0)
    {
      g.setColor(Color.gray);
      g.drawLine(rfqWidth + 4, minY + Y_SPACING * (rfqCount - 1) + 3, maxX - 4, minY + Y_SPACING
          * (rfqCount - 1) + 3);
    }
    
    int startY = maxY + 30;
    
    // Show current accumulated "bookings" for specific day
    g.setColor(TRANSP_BLUE);
    double pos = minX - ddx;
    int[] cfr = supplier.getFreeCapacity();
    for (int i = 0; i < noDays; i++)
    {
      int ydelta = (cfr[i] * BR_HEIGHT) / capacity;
      int len = ((int) (pos + ddx)) - (int) pos;
      if (ydelta >= 0)
      {
        g.fillRect((int) pos, startY - ydelta, len, ydelta);
      }
      else
      {
        g.fillRect((int) pos, startY, len, -ydelta);
      }
      pos += ddx;
    }
    
    g.setColor(TRANSP_RED);
    pos = minX - ddx;
    int[] overAlloc = supplier.getOverAlloc();
    for (int i = 0; i < noDays; i++)
    {
      int ydelta = (overAlloc[i] * BR_HEIGHT) / capacity;
      int len = ((int) (pos + ddx)) - (int) pos;
      g.fillRect((int) pos, startY - ydelta, len, ydelta);
      pos += ddx;
    }
    
    startY += 60;
    
    g.setColor(Color.gray);
    g.drawString("Capacity: " + capacity, minX, startY + 20);
    g.drawString("Inventory: " + inventory, minX + 120, startY + 20);
    // if (lastCommited >= 0) {
    // StringBuffer sb = new StringBuffer();
    // sb.append("Commited: ");
    // for (int i = 0, n = lastCommited + 1; i < n; i++) {
    // if (i > 0) {
    // sb.append(',');
    // }
    // sb.append(ccommited[i]);
    // }
    // g.drawString(sb.toString(), minX + 230, startY + 20);
    // }
    
    if (isProcessingOffers)
    {
      // C available
      g.setColor(Color.lightGray);
      int[] cavl = supplier.getAvailableCapacity();
      g.drawLine(minX, startY, maxX, startY);
      g.drawLine(minX, startY - (int) (0.02 * capacity), maxX, startY - (int) (0.02 * capacity));
      
      g.setColor(TRANSP_BLUE);
      pos = minX + ddx * currentDay;
      int currShortHorizon = shortHorizon + currentDay;
      for (int i = currentDay + 1; i < noDays; i++)
      {
        int expected = cexpected[i];
        int capacityWilling = (int) ((i < currShortHorizon) ? expected : ((1 - capacityReduction
            * (i - currShortHorizon)) * expected));
        if (capacityWilling < 0)
        {
          capacityWilling = 0;
        }
        int next = capacityWilling - ccommited[i];
        int len = ((int) (pos + ddx)) - (int) pos;
        g.fillRect((int) pos, startY - (int) (0.02 * next), len, (int) (0.02 * next));
        pos += ddx;
      }
      
      g.setColor(Color.black);
      for (int i = 1; i < noDays; i++)
      {
        g.drawLine(minX + (int) ((i - 1) * ddx), startY - (int) (0.02 * cavl[i - 1]), minX
            + (int) (i * ddx + 0.5), startY - (int) (0.02 * cavl[i]));
      }
      
    }
    else
    {
      // Price function per due date (price = 1.0 & 0.0 is the reference line)
      g.drawLine(minX, startY, maxX, startY);
      g.drawLine(minX, startY - PDY, maxX, startY - PDY);
      g.drawLine(minX, startY - 2 * PDY, maxX, startY - 2 * PDY);
      for (int i = 1; i < noDays; i++)
      {
        g.drawLine(minX + (int) ((i - 1) * ddx), startY - (int) (PDY * price[i - 1]), minX
            + (int) (i * ddx + 0.5), startY - (int) (PDY * price[i]));
      }
      
      g.drawString("Current Debt: " + supplier.getCurrentDebt(), minX + 120 * 2, startY + 20);
      g.drawString("Total Reduction: " + supplier.getTotalReduction(), minX + 120 * 3, startY + 20);
    }
    
    g.setColor(oldColor);
  }
  
  private void drawString(Graphics g, FontMetrics fm, String text, int x, int y)
  {
    g.drawString(text, x - fm.stringWidth(text), y);
  }
  
  // -------------------------------------------------------------------
  // ActionListener
  // -------------------------------------------------------------------
  
  public void actionPerformed(ActionEvent event)
  {
    Object source = event.getSource();
    if (source == nextButton)
    {
      startSupplier(STEP);
      
    }
    else if (source == runButton)
    {
      supplier.setDebug(false);
      startSupplier(RUN);
      
    }
    else if (source == clearButton)
    {
      startSupplier(CLEAR);
      
    }
    else if (source == newButton)
    {
      startSupplier(RESET);
      
    }
    else if (source == configButton)
    {
      if (isSupplierRunning)
      {
        startSupplier(CLEAR);
      }
      showConfigurationDialog();
      
    }
    else if (source == promiseButton)
    {
      showPromises(lastPromises);
    }
  }
  
  // -------------------------------------------------------------------
  // KeyListener
  // -------------------------------------------------------------------
  
  public void keyTyped(KeyEvent ke)
  {
    if (ke.getModifiers() != 0)
    {
      return;
    }
    switch (ke.getKeyChar())
    {
      case ' ':
      case 's':
      case 'S':
        startSupplier(STEP);
        break;
      case 'a':
      case 'A':
        testCount = 1000;
        supplier.setDebug(false);
        startSupplier(RUN);
        break;
      case 'R':
      case 'r':
        runButton.doClick(0);
        break;
      case 'c':
      case 'C':
        clearButton.doClick(0);
        break;
      case 'n':
      case 'N':
        newButton.doClick(0);
        break;
    }
  }
  
  public void keyPressed(KeyEvent e)
  {}
  
  public void keyReleased(KeyEvent e)
  {}
  
  // -------------------------------------------------------------------
  // Main
  // -------------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    JFrame jf = new JFrame("Supplier ATP Information");
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    SupplierViewer bd = new SupplierViewer();
    bd.random = new Random(47182732L);
    bd.start();
    
    jf.setSize(800, Y_SPACING * MAX_RFQ + 210);
    jf.getContentPane().add(bd, BorderLayout.CENTER);
    jf.getContentPane().add(bd.getButtonPanel(), BorderLayout.SOUTH);
    jf.setVisible(true);
  }
  
} // SupplierViewer
