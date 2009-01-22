package edu.umich.eecs.tac.viewer;

import se.sics.isl.transport.Transportable;

/**
 * @author Patrick Jordan
 */
public interface ViewListener {
	public void dataUpdated(int agent, int type, int value);

	public void dataUpdated(int agent, int type, long value);

	public void dataUpdated(int agent, int type, float value);

	public void dataUpdated(int agent, int type, double value);

	public void dataUpdated(int agent, int type, String value);

	public void dataUpdated(int agent, int type, Transportable value);

	public void dataUpdated(int type, Transportable value);

	public void participant(int agent, int role, String name, int participantID);
}
