package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserRatioTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserCountTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserRatioPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserInfoTabPanel;
import edu.umich.eecs.tac.TACAAConstants;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import se.sics.isl.transport.Transportable;

/**
 * @author Patrick Jordan
 */
public class AdvertiserTabPanel extends SimulationTabPanel {
	private JTabbedPane tabbedPane;

	private AdvertiserRatioTabPanel advertiserRatioTabPanel;
	private AdvertiserCountTabPanel advertiserCountTabPanel;
    private Map<String, AdvertiserInfoTabPanel> advertiserInfoPanels;
	public AdvertiserTabPanel(TACAASimulationPanel simulationPanel) {
		super(simulationPanel);

        advertiserInfoPanels = new HashMap<String, AdvertiserInfoTabPanel>();
        simulationPanel.addViewListener(new ParticipantListener());
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

    private class ParticipantListener implements ViewListener {

		public void dataUpdated(int agent, int type, int value) {
		}

		public void dataUpdated(int agent, int type, long value) {
		}

		public void dataUpdated(int agent, int type, float value) {
		}

		public void dataUpdated(int agent, int type, double value) {
		}

		public void dataUpdated(int agent, int type, String value) {
		}

		public void dataUpdated(int agent, int type, Transportable value) {
		}

		public void dataUpdated(int type, Transportable value) {
		}

		public void participant(int agent, int role, String name,
				int participantID) {
			if (!advertiserInfoPanels.containsKey(name)
					&& role == TACAAConstants.ADVERTISER) {
				AdvertiserInfoTabPanel infoPanel = new AdvertiserInfoTabPanel(
						agent, name, getSimulationPanel());

				advertiserInfoPanels.put(name, infoPanel);
                tabbedPane.addTab(name, infoPanel);

			}
		}
	}



}
