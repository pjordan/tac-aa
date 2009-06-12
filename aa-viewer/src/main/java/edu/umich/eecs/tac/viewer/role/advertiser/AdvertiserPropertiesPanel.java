package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.AdvertiserInfo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import se.sics.isl.transport.Transportable;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Guha Balakrishnan and Patrick Jordan
 */
public class AdvertiserPropertiesPanel extends JPanel {
    private static final Map<String,ImageIcon> icons;

    static {
        icons = new HashMap<String, ImageIcon>();
        for(String name : new String[] {"tv","dvd","audio","lioneer","pg","flat"}) {
            icons.put(name, new ImageIcon(AdvertiserPropertiesPanel.class.getResource(String.format("/%s_thumb.gif",name)))); 
        }
    }
    
    private JTable table;
    private int agent;
    private String name;
    private TACAASimulationPanel simulationPanel;
    private JLabel manufacturerLabel;
    private JLabel componentLabel;

    public AdvertiserPropertiesPanel(int agent, String name, TACAASimulationPanel simulationPanel) {
        this.agent = agent;
        this.name = name;
        this.simulationPanel = simulationPanel;
        simulationPanel.addViewListener(new AdvertiserInfoListener());
        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(2,1));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        setBorder(BorderFactory.createTitledBorder("Specialty Information"));

        manufacturerLabel = new JLabel(new ImageIcon());
        manufacturerLabel.setBorder(BorderFactory.createTitledBorder("Manufacturer"));
        add(manufacturerLabel);

        componentLabel = new JLabel(new ImageIcon());
        componentLabel.setBorder(BorderFactory.createTitledBorder("Component"));
        add(componentLabel);
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
            if(agent == AdvertiserPropertiesPanel.this.agent && type == TACAAConstants.DU_ADVERTISER_INFO && value.getClass()== AdvertiserInfo.class) {
                AdvertiserInfo info = (AdvertiserInfo)value;
                String component = info.getComponentSpecialty();
                String manufacturer = info.getManufacturerSpecialty();

                ImageIcon icon = icons.get(component);
                //componentLabel.setText(component);
                if(icon!=null) {
                    componentLabel.setIcon(icon);
                }

                //manufacturerLabel.setText(manufacturer);
                icon = icons.get(manufacturer);
                if(icon!=null) {
                    manufacturerLabel.setIcon(icon);
                }
            }
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