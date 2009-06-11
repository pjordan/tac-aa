
package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.role.publisher.RankingTabPanel;
import edu.umich.eecs.tac.viewer.role.publisher.SeriesTabPanel;

import java.awt.*;
import javax.swing.*;

/**
 * @author Patrick Jordan
*/
public class PublisherTabPanel extends SimulationTabPanel {
    private JTabbedPane tabbedPane;
    private SeriesTabPanel seriesTabPanel;
    private RankingTabPanel rankingTabPanel;

	public PublisherTabPanel(TACAASimulationPanel simulationPanel) {
		super(simulationPanel);
		initialize();
	}

	private void initialize() {
        setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		add(tabbedPane, BorderLayout.CENTER);

		seriesTabPanel = new SeriesTabPanel(
				getSimulationPanel());
		tabbedPane.addTab("Time Series", seriesTabPanel);

		rankingTabPanel = new RankingTabPanel(
				getSimulationPanel());
		tabbedPane.addTab("Auction Rankings", rankingTabPanel);
    }
}


















