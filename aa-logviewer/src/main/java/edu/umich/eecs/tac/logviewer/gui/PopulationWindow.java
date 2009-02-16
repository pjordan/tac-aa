package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.RetailCatalog;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 11, 2009
 * Time: 1:13:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PopulationWindow extends JFrame {
  GameInfo gameInfo;

  public PopulationWindow(GameInfo gameInfo){
    super("Users per product population");

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.gameInfo = gameInfo;

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(createPopulationPane(), BorderLayout.CENTER);
    pack();
  }

 public JPanel createPopulationPane(){
   GridBagLayout gbl = new GridBagLayout();
	 GridBagConstraints gblConstraints = new GridBagConstraints();
	 gblConstraints.fill = GridBagConstraints.BOTH;

	 JPanel pane = new JPanel();
   pane.setLayout(gbl);

   //Add query panels
   ProductPopulationPanel current;
   Product[] products = gameInfo.getRetailCatalog().keys().toArray(new Product[0]);
   gblConstraints.weightx = 1;
   gblConstraints.weighty = 1;
   gblConstraints.gridwidth = 1;
   //TODO-Number of queries should not be hardcoded
   for(int i = 0; i < 3; i++){
     for(int j = 0; j < 3; j++){
       gblConstraints.gridx = i;
       gblConstraints.gridy = j;
       current = new ProductPopulationPanel(gameInfo, products[i*3 + j]);
        //Add queryPanel information
        gbl.setConstraints(current.getMainPane(), gblConstraints);
        pane.add(current.getMainPane());
      }
    }

    return pane;
 }

}
