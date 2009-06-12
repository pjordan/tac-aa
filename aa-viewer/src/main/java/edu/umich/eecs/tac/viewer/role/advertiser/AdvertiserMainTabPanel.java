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
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 10, 2009
 * Time: 10:30:20 PM
 * To change this template use File | Settings | File Templates.
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
        AdvertiserRatioPanel ratioPanel = new AdvertiserRatioPanel(
						                      agent, name, simulationPanel, false);
        AdvertiserCountPanel countPanel = new AdvertiserCountPanel(
						                      agent, name, simulationPanel, false, legendColor);
        AgentRevCostPanel agentRevCostPanel = new AgentRevCostPanel(agent, name, simulationPanel, true);


        AdvertiserCapacityPanel advertiserCapacityPanel =
                            new AdvertiserCapacityPanel(agent, name, simulationPanel, legendColor);


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 3;

        add(profitPanel, c);

        c.gridx = 3;
        c.gridwidth = 1;
        //add(advertiserPropertiesPanel,c);
        add(advertiserCapacityPanel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        add(agentRevCostPanel, c);



        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 1;
        add(ratioPanel, c);



        c.gridx = 3;
        add(countPanel,c);
    }
}
