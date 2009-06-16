package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Guha Balakrishnan
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

    private class ParticipantListener extends ViewAdaptor {

        public void participant(int agent, int role, String name, int participantID) {
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
