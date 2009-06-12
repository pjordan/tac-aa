package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.role.advertiser.*;
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

    private AdvertiserOverviewPanel overviewPanel;

    private Map<String, AdvertiserInfoTabPanel> advertiserInfoPanels;
    private int participantNum;
    private TACAASimulationPanel simulationPanel;

    public AdvertiserTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        participantNum = 0;
        advertiserInfoPanels = new HashMap<String, AdvertiserInfoTabPanel>();
        this.simulationPanel = simulationPanel;
        simulationPanel.addViewListener(new ParticipantListener());
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        add(tabbedPane, BorderLayout.CENTER);

        overviewPanel = new AdvertiserOverviewPanel(getSimulationPanel());
        tabbedPane.addTab("Overview", overviewPanel);
    }

    private class ParticipantListener extends ViewAdaptor {

        public void participant(int agent, int role, String name,
                                int participantID) {
            if (!advertiserInfoPanels.containsKey(name)
                    && role == TACAAConstants.ADVERTISER) {
                AdvertiserInfoTabPanel infoPanel = new AdvertiserInfoTabPanel(
                        agent, name, simulationPanel,
                        TACAAViewerConstants.LEGEND_COLORS[participantNum]);

                advertiserInfoPanels.put(name, infoPanel);
                tabbedPane.addTab(name, infoPanel);
                participantNum++;
            }
        }
    }


}
