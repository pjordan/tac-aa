package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import se.sics.isl.transport.Transportable;
import com.botbox.util.ArrayUtils;

/**
 * @author Patrick Jordan
 */
public class AgentSupport extends ViewAdaptor {
	private int[] agents;
	private int[] roles;
	private int[] participants;
	private String[] names;
	private int agentCount;

	public AgentSupport() {
		agents = new int[0];
		roles = new int[0];
		participants = new int[0];
		names = new String[0];
	}

	public int indexOfAgent(int agent) {
		return ArrayUtils.indexOf(agents, 0, agentCount, agent);
	}

	public int size() {
		return agentCount;
	}

	public int agent(int index) {
		return agents[index];
	}

	public int role(int index) {
		return roles[index];
	}

	public int participant(int index) {
		return agents[index];
	}

	public String name(int index) {
		return names[index];
	}	

	public void participant(int agent, int role, String name, int participantID) {
		setAgent(agent, role, name, participantID);
	}

	protected void addAgent(int agent) {
		int index = ArrayUtils.indexOf(agents, 0, agentCount, agent);
		if (index < 0) {
			doAddAgent(agent);
		}
	}

	private int doAddAgent(int agent) {
		if (agentCount == participants.length) {
			int newSize = agentCount + 8;
			agents = ArrayUtils.setSize(agents, newSize);
			roles = ArrayUtils.setSize(roles, newSize);
			participants = ArrayUtils.setSize(participants, newSize);
			names = (String[]) ArrayUtils.setSize(names, newSize);
		}

		agents[agentCount] = agent;

		return agentCount++;
	}

	private void setAgent(int agent, int role, String name, int participantID) {
		addAgent(agent);

		int index = ArrayUtils.indexOf(agents, 0, agentCount, agent);
		roles[index] = role;
		names[index] = name;
		participants[index] = participantID;
	}
}
