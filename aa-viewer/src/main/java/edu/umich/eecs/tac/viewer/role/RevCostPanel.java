package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.TACAAConstants;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

import se.sics.isl.transport.Transportable;

import javax.swing.*;
import javax.swing.border.TitledBorder;


/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 9, 2009
 * Time: 12:31:24 PM
 * To change this template use File | Settings | File Templates.
 */

public class RevCostPanel extends SimulationTabPanel {

    private Map<String, AgentRevCostPanel> agentPanels;


    public RevCostPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);

        agentPanels = new HashMap<String, AgentRevCostPanel>();

        simulationPanel.addViewListener(new ParticipantListener());

        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(2, 4));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                "Daily Revenue and Cost", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));        
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
            if (!agentPanels.containsKey(name)
                && role == TACAAConstants.ADVERTISER) {
                AgentRevCostPanel agentRevCostPanel = new AgentRevCostPanel(
                                                  agent, name, getSimulationPanel(), false);

                agentPanels.put(name, agentRevCostPanel);

                add(agentRevCostPanel);
            }
        }
    }
}
