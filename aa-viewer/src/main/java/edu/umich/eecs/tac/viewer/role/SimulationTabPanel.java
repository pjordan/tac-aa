package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;

import javax.swing.*;

/**
 * @author Patrick Jordan
 */
public class SimulationTabPanel extends JComponent {
	private TACAASimulationPanel simulationPanel;

	public SimulationTabPanel(TACAASimulationPanel simulationPanel) {
		this.simulationPanel = simulationPanel;
	}

	public TACAASimulationPanel getSimulationPanel() {
		return simulationPanel;
	}
}
