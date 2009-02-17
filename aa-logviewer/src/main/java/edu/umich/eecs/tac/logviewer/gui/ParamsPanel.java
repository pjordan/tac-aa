package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.GameInfo;

import javax.swing.*;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 2, 2009
 * Time: 2:12:52 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Displays the main game parameters for a given simulation
 *
 * @author - Lee Callender
 */

//TODO- Add squashing parameter, etc.
public class ParamsPanel {
    private JPanel mainPane;
    JLabel simulationID, secondsPerDay, numberOfDays, squash, server;
    JLabel storageCostLabel;
    JLabel suppNomCap, suppMaxRFQs, suppDiscountFactor;

    public ParamsPanel(GameInfo gameInfo) {
	    mainPane = new JPanel();
	    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
	    mainPane.setBorder(BorderFactory.createTitledBorder
		                    (BorderFactory.createEtchedBorder()," Simulation Parameters "));

	    simulationID = new JLabel("Simulation: " + gameInfo.getSimulationID()
				                        + " (" + gameInfo.getSimulationType() + ')');
	    server = new JLabel("Server: " + gameInfo.getServer());
	    secondsPerDay = new JLabel("Seconds per day: " + gameInfo.getSecondsPerDay());
      numberOfDays = new JLabel("Number of days: " + gameInfo.getNumberOfDays());
      DecimalFormat squashFormat = new DecimalFormat("#.###");
      squash = new JLabel("Squashing Parameter: " + squashFormat.format(gameInfo.getSquashingParameter()));


      mainPane.add(server);
	    mainPane.add(simulationID);
	    mainPane.add(secondsPerDay);
      mainPane.add(numberOfDays);
      mainPane.add(squash);  //Format the number of digits shown.
    }

    public JPanel getMainPane() {
	    return mainPane;
    }
} // ParamsPanel
