package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;

import javax.swing.*;
import java.awt.*;

/**
 * @author Patrick Jordan
 */
public class AdvertiserOverviewPanel extends JPanel {
    private AdvertiserRatioTabPanel advertiserRatioTabPanel;
	private AdvertiserCountTabPanel advertiserCountTabPanel;
    private TACAASimulationPanel simulationPanel;

    public AdvertiserOverviewPanel(TACAASimulationPanel simulationPanel) {
        this.simulationPanel = simulationPanel;

        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        advertiserCountTabPanel = new AdvertiserCountTabPanel( simulationPanel);
        add(advertiserCountTabPanel,c);
        

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        advertiserRatioTabPanel = new AdvertiserRatioTabPanel( simulationPanel);
        add(advertiserRatioTabPanel,c);
    }
}
