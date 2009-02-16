package edu.umich.eecs.tac.logviewer;

import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;
import se.sics.isl.util.IllegalConfigurationException;
import edu.umich.eecs.tac.TACAASimulationInfo;
import edu.umich.eecs.tac.logviewer.util.SimulationParser;
import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.gui.MainWindow;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;
import edu.umich.eecs.tac.logviewer.info.GameInfo;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Jan 29, 2009
 * Time: 3:44:51 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * @author SICS, Lee Callender
 */
public class Visualizer extends LogHandler {

   MainWindow mainWindow = null;

  protected void start(LogReader reader)
			throws IllegalConfigurationException, IOException, ParseException{

    SimulationParser sp = new SimulationParser(this, reader);
    PositiveBoundedRangeModel dayModel = new PositiveBoundedRangeModel();
	  createMonitors(sp, dayModel);

	  // Start parsing
	  sp.start();
	  if(sp.errorParsing()) {
	    System.err.println("Error while parsing file");
	    return;
	  }

	  // Create simulation info object and let monitors do post processing
	  GameInfo gameInfo = new GameInfo(sp);

	  // Show the visualizer window
	  if(getConfig().getPropertyAsBoolean("showGUI", true) && gameInfo != null) {
	    dayModel.setLast(gameInfo.getNumberOfDays()-1);
	    mainWindow = new MainWindow(gameInfo, dayModel, sp.getMonitors());
	    mainWindow.setVisible(true);
	  }

    sp = null;
  }


  private void createMonitors(SimulationParser sp, PositiveBoundedRangeModel dayModel)
	      throws IllegalConfigurationException, IOException {
	    String[] names = getConfig().getPropertyAsArray("monitor.names");

      ParserMonitor[] monitors = (ParserMonitor[]) getConfig().createInstances("monitor", ParserMonitor.class, names);

      if(monitors == null){
        //System.out.println("No ParserMonitors added.");
        return;
      }

      for (int i = 0, n = monitors.length; i < n; i++) {
	      monitors[i].init(names[i], this, sp, dayModel);
	      sp.addMonitor(monitors[i]);
	    }
  }//createMonitors
} // Visualizer


