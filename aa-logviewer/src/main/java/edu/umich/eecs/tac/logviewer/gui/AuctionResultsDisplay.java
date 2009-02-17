package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.logviewer.info.GameInfo;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 17, 2009
 * Time: 1:35:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuctionResultsDisplay {
  JPanel mainPane;

  public AuctionResultsDisplay(GameInfo gameInfo, PositiveBoundedRangeModel dayModel){
    mainPane = new JPanel();
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gblConstraints = new GridBagConstraints();
    gblConstraints.fill = GridBagConstraints.BOTH;
    mainPane.setLayout(gbl);

    mainPane.setBorder(BorderFactory.createTitledBorder
		       ("Auction Results by Query"));
    mainPane.setToolTipText("Auctions sorted by average position");

    Query[] querySpace = gameInfo.getQuerySpace().toArray(new Query[0]);

    AuctionResultsPanel current;
    gblConstraints.weightx = 1;
    gblConstraints.weighty = 1;
    gblConstraints.gridwidth = 1;
    //TODO-Number of queries should not be hardcoded
    for(int i = 0; i < 4; i++){
      for(int j = 0; j < 4; j++){
        gblConstraints.gridx = i;
        gblConstraints.gridy = j;
        current = new AuctionResultsPanel(querySpace[4*i+j], gameInfo, dayModel);
        //Add queryPanel information
        gbl.setConstraints(current.getMainPane(), gblConstraints);
        mainPane.add(current.getMainPane());
      }
    }

  }

  public JPanel getMainPane() {
    return mainPane;
  }
}
