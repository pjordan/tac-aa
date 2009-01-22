package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserRatioTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserCountTabPanel;

import java.awt.*;

import javax.swing.*;

/**
 * @author Patrick Jordan
 */
public class AdvertiserTabPanel extends SimulationTabPanel {
	private JTabbedPane tabbedPane;

	private AdvertiserRatioTabPanel advertiserRatioTabPanel;
	private AdvertiserCountTabPanel advertiserCountTabPanel;

	public AdvertiserTabPanel(TACAASimulationPanel simulationPanel) {
		super(simulationPanel);

		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		add(tabbedPane, BorderLayout.CENTER);

		advertiserRatioTabPanel = new AdvertiserRatioTabPanel(
				getSimulationPanel());
		tabbedPane.addTab("Ratio Metrics", advertiserRatioTabPanel);

		advertiserCountTabPanel = new AdvertiserCountTabPanel(
				getSimulationPanel());
		tabbedPane.addTab("Raw Metrics", advertiserCountTabPanel);
	}
}
