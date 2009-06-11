package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import se.sics.isl.transport.Transportable;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 11, 2009
 * Time: 4:27:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdvertiserPropertiesPanel extends JPanel {

    private JTable table;
    private int agent;
    private String name;
    private TACAASimulationPanel simulationPanel;

    public AdvertiserPropertiesPanel(int agent, String name, TACAASimulationPanel simulationPanel){
        this.agent = agent;
        this.name= name;
        this.simulationPanel = simulationPanel;
        simulationPanel.addViewListener(new AdvertiserInfoListener());
        initialize();
    }
    private void initialize(){

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
                      // To change body of implemented methods use File | Settings | File
                      // Templates.
                  }
    }







}