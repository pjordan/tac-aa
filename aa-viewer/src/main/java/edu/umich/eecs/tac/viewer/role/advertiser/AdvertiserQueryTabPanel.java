package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.auction.AverageRankingPanel;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.props.Query;

import javax.swing.*;
import java.awt.*;

import se.sics.tasim.viewer.SimulationPanel;

/**
 * @author Guha Balakrishnan and Patrick Jordan
 */
public class AdvertiserQueryTabPanel extends JPanel {
    private AverageRankingPanel averageRankingPanel;
    private int agent;
    private String advertiser;
    private Query query;
    private TACAASimulationPanel simulationPanel;
    private AdvertiserQueryRatioPanel queryRatioPanel;
    private AdvertiserQueryCountPanel queryCountPanel;
    private AdvertiserQueryValuePanel queryValuePanel;
    private AdvertiserQueryPositionPanel positionPanel;

    public AdvertiserQueryTabPanel(int agent, String advertiser, Query query, TACAASimulationPanel simulationPanel) {
        this.agent = agent;
        this.advertiser = advertiser;
        this.query = query;
        this.simulationPanel = simulationPanel;

        initialize();

    }

    private void initialize() {
        setLayout(new GridLayout(1, 2));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

        GridBagConstraints c;

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 4;
        c.fill = GridBagConstraints.BOTH;
        this.averageRankingPanel = new AverageRankingPanel(query, simulationPanel);
        leftPanel.add(averageRankingPanel,c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        this.positionPanel = new AdvertiserQueryPositionPanel(agent, advertiser, query, simulationPanel);
        leftPanel.add(positionPanel,c);

        add(leftPanel);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());


        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        queryRatioPanel = new AdvertiserQueryRatioPanel(agent, advertiser, query, simulationPanel);
        rightPanel.add(queryRatioPanel,c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 3;
        c.fill = GridBagConstraints.BOTH;
        queryCountPanel = new AdvertiserQueryCountPanel(agent, advertiser, query, simulationPanel);
        rightPanel.add(queryCountPanel,c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        queryValuePanel = new AdvertiserQueryValuePanel(agent, advertiser, query, simulationPanel);
        rightPanel.add(queryValuePanel,c);
        add(rightPanel);
    }

}
