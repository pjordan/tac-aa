package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 1, 2009
 * Time: 11:50:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainWindow extends JFrame {

    ParamsPanel paramsPane;
    GlobalAccountPanel accountPane;
    DayChanger dayChanger;
    AdvertiserDisplay advertiserDisplay;
    PopulationPanel populationPanel;
  
  public MainWindow(final GameInfo gameInfo,
                    final PositiveBoundedRangeModel dayModel,
                    final ParserMonitor[] monitors) {


    super("TAC AA Visualizer - main window");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //ComponentCommunication compComModel = new ComponentCommunication(simInfo);
    //compComModel.openAllChannels(CommunicationModel.COM_ALL);

    //PCCommunication pcComModel = new PCCommunication(simInfo);
    //pcComModel.openAllChannels(CommunicationModel.COM_ALL);


    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gblConstraints = new GridBagConstraints();
    Container pane = getContentPane();
    pane.setLayout(gbl);

    dayChanger = new DayChanger(dayModel);
    accountPane = new GlobalAccountPanel(gameInfo, dayModel);
    paramsPane = new ParamsPanel(gameInfo);
    advertiserDisplay = new AdvertiserDisplay(gameInfo, dayModel, monitors);
    populationPanel = new PopulationPanel(gameInfo);
    /*
    //paramsPane = new ParamsPanel(simInfo);
    //dayChanger = new DayChanger(dayModel);
    //accountPane =
	//new GlobalAccountPanel(simInfo, dayModel);
    //actorDisplay = new AdvertiserDisplay(simInfo, dayModel,
		//		    pcComModel, compComModel,
		//		    monitors);

    //ColorLegendPanel colorPane = new ColorLegendPanel();

    //MonitorSelectorPanel monitorPane =
	//new MonitorSelectorPanel(monitors);
    */
    gblConstraints.fill = GridBagConstraints.HORIZONTAL;
    gblConstraints.anchor = GridBagConstraints.NORTHWEST;
    gblConstraints.weighty = 0;

    gblConstraints.gridx = 0;
    gblConstraints.gridy = 0;
    gbl.setConstraints(paramsPane.getMainPane(), gblConstraints);
    pane.add(paramsPane.getMainPane());

    gblConstraints.gridx = 0;
    gblConstraints.gridy = 1;
    gbl.setConstraints(accountPane.getMainPane(), gblConstraints);
    pane.add(accountPane.getMainPane());

    gblConstraints.gridx = 0;
    gblConstraints.gridy = 2;
    gbl.setConstraints(dayChanger.getMainPane(), gblConstraints);
    pane.add(dayChanger.getMainPane());

    /*

//     gblConstraints.gridx = 0;
//     gblConstraints.gridy = 3;
//     gbl.setConstraints(colorPane.getMainPane(), gblConstraints);
//     pane.add(colorPane.getMainPane());

    gblConstraints.fill = GridBagConstraints.BOTH;
    gblConstraints.gridx = 0;
    gblConstraints.gridy = 4;
    gbl.setConstraints(monitorPane.getMainPane(), gblConstraints);
    pane.add(monitorPane.getMainPane());
    */
    gblConstraints.fill = GridBagConstraints.HORIZONTAL;
    //gblConstraints.weighty = 1;
    //gblConstraints.weightx = 1;
    gblConstraints.gridx = 1;
    gblConstraints.gridy = 0;
    gblConstraints.gridheight = 5;
    gbl.setConstraints(advertiserDisplay.getMainPane(), gblConstraints);
    pane.add(advertiserDisplay.getMainPane());

    gblConstraints.gridx = 2;
    gblConstraints.gridy = 0;
    gblConstraints.weightx = 1.0;
    gblConstraints.weighty = 1.0;
    gbl.setConstraints(populationPanel.getMainPane(), gblConstraints);
    pane.add(populationPanel.getMainPane());
    
    pack();
    setLocationRelativeTo(null);
  }

} // MainWindow

