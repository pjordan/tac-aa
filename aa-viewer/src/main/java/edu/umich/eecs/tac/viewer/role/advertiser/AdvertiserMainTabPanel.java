package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.*;
import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.TACAAConstants;

import javax.swing.*;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.SimulationPanel;

import java.awt.*;

import java.awt.*;

/**
 * @author Guha Balakrishnan
 */
public class AdvertiserMainTabPanel extends SimulationTabPanel {
    private TACAASimulationPanel simulationPanel;
    private JTable table;
    private int agent;
    private String name;
    private Color legendColor;


    private JLabel label;
    public AdvertiserMainTabPanel(TACAASimulationPanel simulationPanel, int agent,
                                  String advertiser, Color legendColor){
         super(simulationPanel);
         this.simulationPanel = simulationPanel;
         this.agent = agent;
         this.name = advertiser;
         this.legendColor = legendColor;

         initialize();
    }

    private void initialize(){
        setLayout(new GridBagLayout());


        ProfitPanel profitPanel = new ProfitPanel(simulationPanel, agent, name, legendColor);
        AdvertiserRateMetricsPanel ratesMetricsPanel = new AdvertiserRateMetricsPanel(
						                      agent, name, simulationPanel, false);
        AdvertiserCountPanel countPanel = new AdvertiserCountPanel(
						                      agent, name, simulationPanel, false, legendColor);
        AgentRevCostPanel agentRevCostPanel = new AgentRevCostPanel(agent, name, simulationPanel, true);

        AdvertiserPropertiesPanel advertiserPropertiesPanel =
                            new AdvertiserPropertiesPanel(agent, name, simulationPanel);

        AdvertiserCapacityPanel advertiserCapacityPanel =
                            new AdvertiserCapacityPanel(agent, name, simulationPanel, legendColor);


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(agentRevCostPanel, c);

        c.gridx = 2;
        c.weightx = 1;
        add(profitPanel, c);

        c.gridx = 3;
        c.weightx = 2;
        add(advertiserCapacityPanel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 2;
        add(ratesMetricsPanel, c);

        c.gridx = 2;
        c.weightx = 1;
        add(advertiserPropertiesPanel,c);

        c.gridx = 3;
        c.weightx = 2;
        add(countPanel,c);
    }
}
