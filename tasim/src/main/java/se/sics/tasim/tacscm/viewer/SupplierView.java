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
 * SupplierView
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Wed Dec 04 17:47:05 2002
 * Updated : $Date: 2008-03-07 10:04:04 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3826 $
 */
package se.sics.tasim.tacscm.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import se.sics.isl.gui.DotDiagram;
import se.sics.isl.gui.MessageModel;
import se.sics.isl.gui.MessageRenderer;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.props.ComponentCatalog;

public class SupplierView extends TACSCMAgentView
{
  
  // FIX THIS!!! -- Should not be hardcoded to two!!!!!
  private static final Color[] LINE_COLORS =
  { Color.blue, Color.green };
  
  private JPanel dialog;
  private JList messageList;
  private MessageModel messageModel;
  private DotDiagram capacityDiagram;
  private DotDiagram capDiagram;
  private DotDiagram prodDiagram;
  private DotDiagram invDiagram;
  
  // FIX THIS!!! -- Should not be hardcoded to two!!!!!
  private int assemblyLines = 2;
  private int[][] capacity = new int[assemblyLines][30];
  private int[] capPos = new int[assemblyLines];
  private int[][] production = new int[assemblyLines][30];
  private int[] prodPos = new int[assemblyLines];
  private int[][] inventory = new int[assemblyLines][30];
  private int[] inventoryPos = new int[assemblyLines];
  
  private int nominalCapacity = -1;
  
  public SupplierView(TACSCMViewer viewer)
  {
    super(viewer);
  }
  
  protected void initializeView()
  {
    capacityDiagram = new DotDiagram(assemblyLines);
    // FIX THIS!!! should not be hardcoded to two!!!!
    capacityDiagram.setDotColor(0, LINE_COLORS[0]);
    capacityDiagram.setDotColor(1, LINE_COLORS[1]);
    capacityDiagram.setPreferredSize(new Dimension(70, 40));
    messageModel = new MessageModel(TACSCMViewer.MAX_EVENT_NO);
    
    if (getIcon() != null)
    {
      // Use an icon with absolute positioning for this component
      if ("true".equalsIgnoreCase(getConfigProperty("nameLabel")))
      {
        JLabel nameLabel = new JLabel(getName());
        layoutComponent("nameLabel", nameLabel);
      }
      layoutComponent("capacityDiagram", capacityDiagram);
      
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
      
      Icon assembly = getIcon("factory.jpg");
      if (assembly != null)
      {
        JLabel tmpLabel = new JLabel(assembly);
        gb.setConstraints(tmpLabel, c);
        add(tmpLabel);
      }
      
      JPanel panel = new JPanel(new BorderLayout());
      panel.add(capacityDiagram, BorderLayout.CENTER);
      c.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(panel, c);
      add(panel);
    }
    setToolTipText("");
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
    if ((type & DU_CAPACITY_FLAG) > 0)
    {
      int index = type - DU_CAPACITY_FLAG;
      int pos = capPos[index];
      int size = capacity[index].length;
      capacity[index][pos] = value;
      pos = (pos + 1) % size;
      capPos[index] = pos;
      capacityDiagram.setData(index, capacity[index], pos, size);
      if (capDiagram != null)
      {
        capDiagram.setData(index, capacity[index], pos, size);
      }
      
    }
    else if ((type & DU_INVENTORY_FLAG) > 0)
    {
      int index = type - DU_INVENTORY_FLAG;
      int pos = inventoryPos[index];
      int size = inventory[index].length;
      inventory[index][pos] = value;
      pos = (pos + 1) % size;
      inventoryPos[index] = pos;
      if (invDiagram != null)
      {
        invDiagram.setData(index, inventory[index], pos, size);
      }
      
    }
    else if ((type & DU_PRODUCTION_FLAG) > 0)
    {
      int index = type - DU_PRODUCTION_FLAG;
      int pos = prodPos[index];
      int size = production[index].length;
      production[index][pos] = value;
      pos = (pos + 1) % size;
      prodPos[index] = pos;
      if (prodDiagram != null)
      {
        prodDiagram.setData(index, production[index], pos, size);
      }
      
    }
    else if ((type & DU_COMPONENT_PRICE_FLAG) > 0)
    { 

    }
    else if ((type & DU_COMPONENT_ID_FLAG) > 0)
    {
      // int index = type - DU_COMPONENT_ID_FLAG;
      // ComponentCatalog catalog = viewer.getComponentCatalog();
      // String compName = catalog.getProductName(index);
    }
    else if ((type & DU_NOMINAL_CAPACITY_FLAG) > 0)
    {
      // Nominal capacity for this supplier (same for both lines for now)
      if (this.nominalCapacity < 0)
      {
        this.nominalCapacity = value;
        if (capacityDiagram != null)
        {
          capacityDiagram.addConstant(Color.lightGray, this.nominalCapacity);
        }
        if (capDiagram != null)
        {
          capDiagram.addConstant(Color.lightGray, this.nominalCapacity);
        }
        if (prodDiagram != null)
        {
          prodDiagram.addConstant(Color.lightGray, this.nominalCapacity);
        }
        if (invDiagram != null)
        {
          invDiagram.addConstant(Color.lightGray, this.nominalCapacity);
        }
      }
    }
  }
  
  public void dataUpdated(int type, long value)
  {}
  
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
  {}
  
  protected JComponent getDialog()
  {
    if (dialog == null)
    {
      dialog = new JPanel(new BorderLayout());
      dialog.setPreferredSize(new Dimension(500, 450));
      dialog.setBackground(Color.white);
      JPanel left = new JPanel(new GridLayout(0, 1));
      // panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      // panel.setOpaque(false);
      left.setOpaque(false);
      dialog.add(left, BorderLayout.WEST);
      
      Dimension diagramSize = new Dimension(100, 100);
      JLabel tmpLabel = new JLabel();
      Icon agentIcon = getIcon("supplier2.jpg");
      if (agentIcon != null)
      {
        tmpLabel.setIcon(agentIcon);
      }
      tmpLabel.setBorder(BorderFactory.createTitledBorder("Supplier " + getName()));
      left.add(tmpLabel);
      
      // dialog.add(new JLabel("Supplier " + getName()),
      // BorderLayout.NORTH);
      // ImageIcon agentIcon = getIcon("supplier.jpg");
      // if (agentIcon != null) {
      // left.add(new JLabel(agentIcon));
      // }
      
      capDiagram = new DotDiagram(2);
      capDiagram.setBorder(BorderFactory.createTitledBorder("Capacity"));
      capDiagram.setPreferredSize(diagramSize);
      for (int i = 0; i < 2; i++)
      {
        capDiagram.setDotColor(i, LINE_COLORS[i]);
        capDiagram.setData(i, capacity[i], capPos[i], capacity[i].length);
      }
      prodDiagram = new DotDiagram(2);
      prodDiagram.setBorder(BorderFactory.createTitledBorder("Production"));
      prodDiagram.setPreferredSize(diagramSize);
      
      for (int i = 0; i < 2; i++)
      {
        prodDiagram.setDotColor(i, LINE_COLORS[i]);
        prodDiagram.setData(i, production[i], prodPos[i], production[i].length);
      }
      if (this.nominalCapacity > 0)
      {
        capDiagram.addConstant(Color.lightGray, this.nominalCapacity);
        prodDiagram.addConstant(Color.lightGray, this.nominalCapacity);
      }
      left.add(capDiagram);
      left.add(prodDiagram);
      
      invDiagram = new DotDiagram(2);
      invDiagram.setBorder(BorderFactory.createTitledBorder("Inventory"));
      invDiagram.setPreferredSize(diagramSize);
      for (int i = 0; i < 2; i++)
      {
        invDiagram.setDotColor(i, LINE_COLORS[i]);
        invDiagram.setData(i, inventory[i], inventoryPos[i], inventory[i].length);
      }
      if (this.nominalCapacity > 0)
      {
        invDiagram.addConstant(Color.lightGray, this.nominalCapacity);
      }
      left.add(invDiagram);
      
      messageList = new JList(messageModel);
      messageList.setCellRenderer(new MessageRenderer(messageModel));
      dialog.add(createScrollPane(messageList, "Latest Events", true), BorderLayout.CENTER);
    }
    return dialog;
  }
  
  public String getToolTipText(MouseEvent event)
  {
    int x, y, dx, dy;
    if (((x = event.getX()) >= (dx = capacityDiagram.getX()))
        && (x < (dx + capacityDiagram.getWidth()))
        && ((y = event.getY()) >= (dy = capacityDiagram.getY()))
        && (y < (dy + capacityDiagram.getHeight())))
    {
      return "Capacity";
    }
    return null;
  }
  
} // SupplierView
