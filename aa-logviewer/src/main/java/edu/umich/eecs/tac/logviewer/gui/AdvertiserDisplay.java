package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 2, 2009
 * Time: 2:32:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdvertiserDisplay {
  JPanel mainPane;
  AdvertiserPanel[] ap;
  //ArrowMesh componentComunication;
  //ArrowMesh pcComunication;

  public AdvertiserDisplay(GameInfo gameInfo,
		      PositiveBoundedRangeModel dayModel,
		      ParserMonitor[] monitors) {

    mainPane = new JPanel();
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gblConstraints = new GridBagConstraints();
    mainPane.setLayout(gbl);

    mainPane.setBorder(BorderFactory.createTitledBorder
		       (" Game situation "));

    ap = new AdvertiserPanel[gameInfo.getAdvertiserCount()];
    gblConstraints.gridx = 0;
    gblConstraints.weighty = 0;
    gblConstraints.anchor = GridBagConstraints.CENTER;
    for (int i = 0, n = gameInfo.getAdvertiserCount(); i < n; i++) {
      ap[i] = new AdvertiserPanel(gameInfo,gameInfo.getAdvertiser(i),
				                          dayModel,monitors);

      gblConstraints.gridy = i+1;
      gbl.setConstraints(ap[i].getMainPane(), gblConstraints);
      mainPane.add(ap[i].getMainPane(), gblConstraints);
    }



  }

  public JPanel getMainPane() {
    return mainPane;
  }
} // ActorDisplay

