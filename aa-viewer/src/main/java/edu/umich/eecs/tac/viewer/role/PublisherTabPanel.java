
package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.role.publisher.RankingTabPanel;
import edu.umich.eecs.tac.viewer.role.publisher.SeriesTabPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Patrick Jordan
*/
public class PublisherTabPanel extends SimulationTabPanel {

    public PublisherTabPanel(TACAASimulationPanel simulationPanel) {
		super(simulationPanel);
		initialize();
	}

	private void initialize() {
        setLayout(new BorderLayout());
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		add(tabbedPane, BorderLayout.CENTER);

        SeriesTabPanel seriesTabPanel = new SeriesTabPanel(getSimulationPanel());
		tabbedPane.addTab("Bid Series", seriesTabPanel);

        RankingTabPanel rankingTabPanel = new RankingTabPanel(getSimulationPanel());
		tabbedPane.addTab("Auction Rankings", rankingTabPanel);
    }
}


















