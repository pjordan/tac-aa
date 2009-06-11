package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 8, 2009
 * Time: 10:57:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainTabPanel extends SimulationTabPanel {

     public MainTabPanel(TACAASimulationPanel simulationPanel){
         super(simulationPanel);
         setLayout(new GridBagLayout());
         setBackground(TACAAViewerConstants.CHART_BACKGROUND);

         GridBagConstraints c = new GridBagConstraints();
         c.gridx = 0;
         c.gridy = 0;
         c.weightx = 1;
         c.weighty = 1;
         c.fill = GridBagConstraints.BOTH;

         add(new OverviewPanel(simulationPanel), c);

         c.gridx = 1;
         add(new UserPanel(simulationPanel), c);

         c.gridx = 0;
         c.gridy = 1;
         c.gridwidth = 2;
         add(new RevCostPanel(simulationPanel), c);
    }



}
