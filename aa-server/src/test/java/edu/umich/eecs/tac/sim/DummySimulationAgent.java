package edu.umich.eecs.tac.sim;

import se.sics.tasim.aw.Agent;
import se.sics.tasim.sim.SimulationAgent;

public class DummySimulationAgent extends SimulationAgent {

	public DummySimulationAgent(Agent agent, String name) {
		super(agent, name);
	}
	
	public void setup(){
		initializeAgent();
	}
	
}
