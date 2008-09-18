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
 * ManuView
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Dec 04 13:12:36 2002
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.viewer;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.botbox.util.ArrayUtils;
import se.sics.isl.gui.BarDiagram;
import se.sics.isl.gui.DotDiagram;
import se.sics.isl.gui.MessageModel;
import se.sics.isl.gui.MessageRenderer;
import se.sics.isl.gui.VUMeter;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.FormatUtils;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.props.FactoryStatus;

public class ManuView extends TACSCMAgentView
{
  
  private static final Logger log = Logger.getLogger(ManuView.class.getName());
  
  private JLabel accountLabel;
  
  private VUMeter assemblyMeter;
  private BarDiagram componentInventoryDiagram;
  private BarDiagram productInventoryDiagram;
  private JPanel diagramPanel;
  private CardLayout diagramLayout;
  
  private JPanel dialog;
  private JList messageList;
  private MessageModel messageModel;
  
  private TitledBorder accountBorder;
  private TitledBorder utilityBorder;
  private DotDiagram bankDiagram;
  private DotDiagram utilDiagram;
  private int[] bankAccount = new int[60];
  private int bankPos;
  private long currentBankAccount = 0L;
  private int[] utilization = new int[60];
  private int utilPos;
  private int currentUtility = 0;
  
  private BarDiagram productDiagram;
  private int[] pcIDs;
  private int[] pcInventory;
  
  private BarDiagram componentDiagram;
  private int[] componentIDs;
  private int[] componentInventory;
  
  private String[] componentNames;
  private String[] pcNames;
  
  public ManuView(TACSCMViewer viewer)
  {
    super(viewer);
  }
  
  protected void initializeView()
  {
    // Create the components
    assemblyMeter = new VUMeter();
    assemblyMeter.setOpaque(false);
    componentInventoryDiagram = new BarDiagram();
    componentInventoryDiagram.setBarColors(Color.yellow, Color.green);
    componentInventoryDiagram.setOpaque(true);
    productInventoryDiagram = new BarDiagram();
    productInventoryDiagram.setBarColors(Color.yellow, Color.green);
    productInventoryDiagram.setOpaque(true);
    
    diagramLayout = new CardLayout();
    diagramPanel = new JPanel(diagramLayout);
    diagramPanel.setOpaque(false);
    diagramPanel.add(assemblyMeter, "Utilization");
    diagramPanel.add(componentInventoryDiagram, "Component Inventory");
    diagramPanel.add(productInventoryDiagram, "Product Inventory");
    
    accountLabel = new JLabel("Bank: $0");
    messageModel = new MessageModel(TACSCMViewer.MAX_EVENT_NO);
    
    if (getIcon() != null)
    {
      // Use an icon with absolute positioning for this component
      if ("true".equalsIgnoreCase(getConfigProperty("nameLabel")))
      {
        JLabel nameLabel = new JLabel(getName());
        layoutComponent("nameLabel", nameLabel);
      }
      
      layoutComponent("diagram", diagramPanel);
      
      layoutComponent("accountLabel", accountLabel);
    }
    else
    {
      GridBagLayout gb = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      
      setBackground(new Color(0xe0e0e0));
      setLayout(gb);
      
      setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
          getName()));
      
      c.fill = GridBagConstraints.BOTH;
      c.ipadx = 4;
      c.ipady = 3;
      c.weightx = 1.0;
      
      Icon assembly = getIcon("assemblyline.jpg");
      if (assembly != null)
      {
        JLabel tmpLabel = new JLabel(assembly);
        gb.setConstraints(tmpLabel, c);
        add(tmpLabel);
      }
      
      c.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(diagramPanel, c);
      add(diagramPanel);
      
      c.ipady = 1;
      
      gb.setConstraints(accountLabel, c);
      add(accountLabel);
    }
    setToolTipText("");
  }
  
  private boolean checkInitialized()
  {
    BOMBundle bundle;
    ComponentCatalog catalog;
    if ((pcIDs == null) && ((bundle = viewer.getBOMBundle()) != null)
        && ((catalog = viewer.getComponentCatalog()) != null))
    {
      int size = bundle.size();
      pcIDs = new int[size];
      pcInventory = new int[size];
      pcNames = new String[size];
      for (int i = 0; i < size; i++)
      {
        pcIDs[i] = bundle.getProductID(i);
        pcNames[i] = bundle.getProductName(i) + ": ";
      }
      
      size = catalog.size();
      componentNames = new String[size];
      componentIDs = new int[size];
      componentInventory = new int[size];
      for (int i = 0; i < size; i++)
      {
        componentIDs[i] = catalog.getProductID(i);
        componentNames[i] = catalog.getProductName(i) + ": ";
      }
      
      if (componentDiagram != null)
      {
        productDiagram.setNames(pcNames);
        productDiagram.setToolTipVisible(true);
        componentDiagram.setNames(componentNames);
        componentDiagram.setToolTipVisible(true);
      }
    }
    return pcIDs != null;
  }
  
  protected void addMessage(String message, int flag)
  {
    messageModel.addMessage(message, flag);
    if (dialog != null && dialog.isVisible() && messageList != null)
    {
      messageList.ensureIndexIsVisible(messageModel.getSize() - 1);
    }
  }
  
  public void dataUpdated(int type, int value)
  {
    if (type == DU_BANK_ACCOUNT)
    {
      handleBankAccount(value);
    }
  }
  
  public void dataUpdated(int type, long value)
  {
    if (type == DU_BANK_ACCOUNT)
    {
      handleBankAccount(value);
    }
  }
  
  public void dataUpdated(int type, float value)
  {}
  
  public void dataUpdated(int type, String value)
  {
    if (type == TYPE_MESSAGE)
    {
      addMessage(value, MessageModel.NONE);
    }
    else if (type == TYPE_WARNING)
    {
      addMessage(value, MessageModel.WARNING);
    }
  }
  
  public void dataUpdated(int type, Transportable value)
  {
    if (value instanceof FactoryStatus)
    {
      handleInventory((FactoryStatus) value);
    }
  }
  
  private void handleBankAccount(long bankStatus)
  {
    currentBankAccount = bankStatus;
    
    String bankAccountText = "Bank: $" + FormatUtils.formatAmount(currentBankAccount);
    
    accountLabel.setText(bankAccountText);
    if (currentBankAccount < 0)
    {
      accountLabel.setForeground(Color.red);
    }
    else if (currentBankAccount > 0)
    {
      accountLabel.setForeground(Color.blue);
    }
    else
    {
      accountLabel.setForeground(Color.black);
    }
    bankAccount[bankPos] = currentBankAccount >= Integer.MAX_VALUE ? Integer.MAX_VALUE
        : (currentBankAccount <= Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) currentBankAccount);
    bankPos = (bankPos + 1) % bankAccount.length;
    if (bankDiagram != null)
    {
      accountBorder
          .setTitle("Bank Account ($" + FormatUtils.formatAmount(currentBankAccount) + ')');
      bankDiagram.setData(0, bankAccount, bankPos, bankAccount.length);
    }
  }
  
  private void handleInventory(FactoryStatus inventory)
  {
    // Set the value for this day (the last value will be shown in
    // the diagram at the end of the day)
    currentUtility = (int) (inventory.getUtilization() * 100);
    assemblyMeter.setValue(inventory.getUtilization());
    
    if (pcIDs == null && !checkInitialized())
    {
      // Could not initialize the inventory because of missing
      // information (BOMBundle or ComponentCatalog)
      log.severe("could not initialize inventory (missing information)");
    }
    else
    {
      for (int i = 0, n = pcInventory.length; i < n; i++)
      {
        pcInventory[i] = 0;
      }
      for (int i = 0, n = componentInventory.length; i < n; i++)
      {
        componentInventory[i] = 0;
      }
      
      for (int i = 0, n = inventory.getProductCount(); i < n; i++)
      {
        int id = inventory.getProductID(i);
        int index = ArrayUtils.indexOf(componentIDs, id);
        if (index >= 0)
        {
          componentInventory[index] = inventory.getQuantity(i);
        }
        else if ((index = ArrayUtils.indexOf(pcIDs, id)) >= 0)
        {
          pcInventory[index] = inventory.getQuantity(i);
        }
        else
        {
          // What should be done if the component/product was not found???
          log.severe("unknown product in InventoryStatus: " + id);
        }
      }
      // Always set in the inventoryDiagram!
      componentInventoryDiagram.setData(componentInventory);
      productInventoryDiagram.setData(pcInventory);
      
      if (componentDiagram != null)
      {
        productDiagram.setData(pcInventory);
        componentDiagram.setData(componentInventory);
      }
    }
  }
  
  /**
   * Called when the simulation panel enters a new phase
   * 
   * @param phase
   *          the current simulation phase
   */
  protected void nextPhase(int phase)
  {
    // Should show the diagram based on the phase value (in case the
    // viewer is started in a phase different from the first). FIX THIS!!!
    diagramLayout.next(diagramPanel);
  }
  
  /**
   * Called when a new simulation day starts.
   * 
   * @param serverTime
   *          the current server time
   * @param date
   *          the current simulation date
   */
  protected void nextTimeUnit(long serverTime, int timeUnit)
  {
    // Update the diagrams at the end of each day
    utilization[utilPos] = currentUtility;
    utilPos = (utilPos + 1) % utilization.length;
    if (utilDiagram != null)
    {
      utilityBorder.setTitle("Factory Utilization (" + currentUtility + "%)");
      utilDiagram.setData(0, utilization, utilPos, utilization.length);
    }
  }
  
  protected JComponent getDialog()
  {
    if (dialog == null)
    {
      dialog = new JPanel(new GridLayout(3, 2));
      dialog.setBackground(Color.white);
      dialog.setVisible(false);
      
      JLabel tmpLabel = new JLabel();
      Icon agentIcon = getIcon("manufacturer.jpg");
      if (agentIcon != null)
      {
        tmpLabel.setIcon(agentIcon);
      }
      tmpLabel.setBorder(BorderFactory.createTitledBorder(getName()));
      
      dialog.add(tmpLabel);
      
      messageList = new JList(messageModel);
      messageList.setCellRenderer(new MessageRenderer(messageModel));
      dialog.add(createScrollPane(messageList, "Latest Events", true));
      
      Dimension diagramSize = new Dimension(200, 200);
      accountBorder = BorderFactory.createTitledBorder("Bank Account ($"
          + FormatUtils.formatAmount(currentBankAccount) + ')');
      bankDiagram = new DotDiagram(1);
      bankDiagram.setBorder(accountBorder);
      bankDiagram.setPreferredSize(diagramSize);
      bankDiagram.setDotColor(0, Color.blue);
      dialog.add(bankDiagram);
      
      utilityBorder = BorderFactory.createTitledBorder("Factory Utilization (" + currentUtility
          + "%)");
      utilDiagram = new DotDiagram(1);
      utilDiagram.setMinMax(0, 100);
      utilDiagram.setDotColor(0, Color.blue);
      utilDiagram.setBorder(utilityBorder);
      utilDiagram.setPreferredSize(diagramSize);
      dialog.add(utilDiagram);
      
      bankDiagram.setData(0, bankAccount, bankPos, bankAccount.length);
      utilDiagram.setData(0, utilization, utilPos, utilization.length);
      
      productDiagram = new BarDiagram();
      productDiagram.setShowingValue(true);
      productDiagram.setBorder(BorderFactory.createTitledBorder("Product Inventory"));
      productDiagram.setPreferredSize(diagramSize);
      productDiagram.setBarColors(Color.blue, Color.red);
      dialog.add(productDiagram);
      
      componentDiagram = new BarDiagram();
      componentDiagram.setShowingValue(true);
      componentDiagram.setValueColor(Color.black);
      componentDiagram.setBarColors(Color.yellow, Color.green);
      componentDiagram.setBorder(BorderFactory.createTitledBorder("Component Inventory"));
      componentDiagram.setPreferredSize(diagramSize);
      dialog.add(componentDiagram);
      
      if (pcIDs != null)
      {
        productDiagram.setData(pcInventory);
      }
      if (componentIDs != null)
      {
        componentDiagram.setData(componentInventory);
      }
      if (pcNames != null)
      {
        productDiagram.setNames(pcNames);
        productDiagram.setToolTipVisible(true);
      }
      if (componentNames != null)
      {
        componentDiagram.setNames(componentNames);
        componentDiagram.setToolTipVisible(true);
      }
    }
    return dialog;
  }
  
  public String getToolTipText(MouseEvent event)
  {
    int x, y, dx, dy;
    if (((x = event.getX()) >= (dx = diagramPanel.getX())) && (x < (dx + diagramPanel.getWidth()))
        && ((y = event.getY()) >= (dy = diagramPanel.getY()))
        && (y < (dy + diagramPanel.getHeight())))
    {
      // Should be optimized FIX THIS!!!
      if (assemblyMeter.isVisible())
      {
        return "Utilization";
      }
      if (componentInventoryDiagram.isVisible())
      {
        return "Component Inventory";
      }
      if (productInventoryDiagram.isVisible())
      {
        return "Product Inventory";
      }
    }
    return null;
  }
  
} // ManuView
