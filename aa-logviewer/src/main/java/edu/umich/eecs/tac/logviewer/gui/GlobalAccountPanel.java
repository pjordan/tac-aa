package edu.umich.eecs.tac.logviewer.gui;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 1, 2009
 * Time: 3:26:53 PM
 * To change this template use File | Settings | File Templates.
 *
 * Modified from SICS GlobalAccountPanel.java
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.ImageIcon;
import javax.swing.Box;
import java.awt.Component;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import edu.umich.eecs.tac.logviewer.info.GameInfo;

public class GlobalAccountPanel {
    JPanel mainPane;
    PositiveRangeDiagram accountDiagram;

    public GlobalAccountPanel(GameInfo simInfo, PositiveBoundedRangeModel dayModel) {
	    mainPane = new JPanel();
	    mainPane.setLayout(new BorderLayout());
	    mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder()," Account Balance "));
	    mainPane.setMinimumSize(new Dimension(280,200));
	    mainPane.setPreferredSize(new Dimension(280,200));

	    accountDiagram = new PositiveRangeDiagram(simInfo.getAdvertiserCount(), dayModel);

	    accountDiagram.addConstant(Color.black, 0);

      for (int i = 0, n = simInfo.getAdvertiserCount(); i < n; i++) {
	      accountDiagram.setData(i, simInfo.getAdvertiser(i).getAccountBalance(), 1);
	      accountDiagram.setDotColor(i, simInfo.getAdvertiser(i).getColor());
	    }

	    accountDiagram.setToolTipText("Account balance for all agents");
	    mainPane.add(accountDiagram, BorderLayout.CENTER);
    }

    public JPanel getMainPane() {
	    return mainPane;
    }
} // GlobalAccountPanel

