package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.TACAAConstants;

import javax.swing.*;

import se.sics.isl.transport.Transportable;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 10, 2009
 * Time: 10:30:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdvertiserMainTabPanel extends JPanel {
    private TACAASimulationPanel simulationPanel;
    
    private JLabel label;

    public AdvertiserMainTabPanel(TACAASimulationPanel simulationPanel) {
        this.simulationPanel = simulationPanel;
        simulationPanel.addViewListener(new AdvertiserInfoListener());


        label = new JLabel("Test");
        this.add(label);
    }

    private class AdvertiserInfoListener implements ViewListener {

        public void dataUpdated(int agent, int type, int value) {
            // To change body of implemented methods use File | Settings | File
            // Templates.
        }

        public void dataUpdated(int agent, int type, long value) {
            // To change body of implemented methods use File | Settings | File
            // Templates.
        }

        public void dataUpdated(int agent, int type, float value) {
            // To change body of implemented methods use File | Settings | File
            // Templates.
        }

        public void dataUpdated(int agent, int type, double value) {
            // To change body of implemented methods use File | Settings | File
            // Templates.
        }

        public void dataUpdated(int agent, int type, String value) {
            // To change body of implemented methods use File | Settings | File
            // Templates.
        }

        public void dataUpdated(int agent, int type, Transportable value) {

        }

        public void dataUpdated(int type, Transportable value) {

        }

        public void participant(int agent, int role, String name,
                                int participantID) {
        }
    }


}
