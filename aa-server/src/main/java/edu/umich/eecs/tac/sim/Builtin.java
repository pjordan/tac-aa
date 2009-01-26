/**
 * TAC Supply Chain Management Simulator
 * http://www.sics.se/tac/    tac-dev@sics.se
 *
 * Copyright (c) 2001-2005 SICS AB. All rights reserved.
 *
 * SICS grants you the right to use, modify, and redistribute this
 * software for noncommercial purposes, on the conditions that you:
 * (1) retain the original headers, including the copyright notice and
 * this text, (2) clearly document the difference between any derived
 * software and the original, and (3) acknowledge your use of this
 * software in pertaining publications and reports.  SICS provides
 * this software "as is", without any warranty of any kind.  IN NO
 * EVENT SHALL SICS BE LIABLE FOR ANY DIRECT, SPECIAL OR INDIRECT,
 * PUNITIVE, INCIDENTAL OR CONSEQUENTIAL LOSSES OR DAMAGES ARISING OUT
 * OF THE USE OF THE SOFTWARE.
 *
 * -----------------------------------------------------------------
 *
 * Builtin
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Thu May 22 22:33:18 2003
 * Updated : $Date: 2005/12/19 17:30:26 $
 *           $Revision: 1.1 $
 */
package edu.umich.eecs.tac.sim;

import java.util.Random;

import se.sics.isl.util.ConfigManager;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.is.EventWriter;
import static edu.umich.eecs.tac.TACAAConstants.*;

/**
 * @author Lee Callender, Patrick Jordan
 */
public abstract class Builtin extends Agent implements TimeListener {

	private final String baseConfigName;
	private ConfigManager config;
	private String configName;

	private TACAASimulation simulation;
	private int index;

	protected Builtin(String baseConfigName) {
		this.baseConfigName = baseConfigName;
	}

	protected TACAASimulation getSimulation() {
		return simulation;
	}

	// Only for debug and testing purposes.
	protected void setConfig(ConfigManager config, String configName) {
		if (this.config != null) {
			throw new IllegalStateException("config already set");
		}
		this.config = config;
		this.configName = configName;
	}

	protected int getIndex() {
		return index;
	}

	protected final void simulationSetup() {
	}

	final void simulationSetup(TACAASimulation simulation, int index) {
		this.index = index;
		this.simulation = simulation;
		this.config = simulation.getConfig();
		this.configName = getName();
		setup();
	}

	protected final void simulationStopped() {
		stopped();
	}

	protected final void simulationFinished() {
		try {
			shutdown();
		} finally {
			simulation = null;
		}
	}

	// Note: for the properties to be stored in the server configuration
	// that is saved in the simulation log all properties must be
	// accessed during simulation setup!
	protected String getProperty(String name) {
		return getProperty(name, null);
	}

	// Note: for the properties to be stored in the server configuration
	// that is saved in the simulation log all properties must be
	// accessed during simulation setup!
	protected String getProperty(String name, String defaultValue) {
		String value = config.getProperty(baseConfigName + configName + '.'
				+ name);
		if (value == null) {
			value = config.getProperty(baseConfigName + name, defaultValue);
		}
		return value;
	}

	protected String[] getPropertyAsArray(String name) {
		return getPropertyAsArray(name, null);
	}

	protected String[] getPropertyAsArray(String name, String defaultValue) {
		String[] value = config.getPropertyAsArray(baseConfigName + configName
				+ '.' + name);
		if (value == null) {
			value = config.getPropertyAsArray(baseConfigName + name,
					defaultValue);
		}
		return value;
	}

	// Note: for the properties to be stored in the server configuration
	// that is saved in the simulation log all properties must be
	// accessed during simulation setup!
	protected int getPropertyAsInt(String name, int defaultValue) {
		String property = baseConfigName + name;
		int value = config.getPropertyAsInt(property, defaultValue);

		property = baseConfigName + configName + '.' + name;
		value = config.getPropertyAsInt(property, value);

		return value;
	}

	protected int[] getPropertyAsIntArray(String name) {
		return getPropertyAsIntArray(name, null);
	}

	protected int[] getPropertyAsIntArray(String name, String defaultValue) {
		String[] value = getPropertyAsArray(name, defaultValue);
		if (value != null) {
			int[] intValue = new int[value.length];
			for (int i = 0, n = value.length; i < n; i++) {
				intValue[i] = Integer.parseInt(value[i]);
			}
			return intValue;
		}
		return null;
	}

	// Note: for the properties to be stored in the server configuration
	// that is saved in the simulation log all properties must be
	// accessed during simulation setup!
	protected long getPropertyAsLong(String name, long defaultValue) {
		String property = baseConfigName + name;
		long value = config.getPropertyAsLong(property, defaultValue);

		property = baseConfigName + configName + '.' + name;
		value = config.getPropertyAsLong(property, value);

		return value;
	}

	// Note: for the properties to be stored in the server configuration
	// that is saved in the simulation log all properties must be
	// accessed during simulation setup!
	protected float getPropertyAsFloat(String name, float defaultValue) {
		String property = baseConfigName + name;
		float value = config.getPropertyAsFloat(property, defaultValue);

		property = baseConfigName + configName + '.' + name;
		value = config.getPropertyAsFloat(property, value);

		return value;
	}

	// Note: for the properties to be stored in the server configuration
	// that is saved in the simulation log all properties must be
	// accessed during simulation setup!
	protected double getPropertyAsDouble(String name, double defaultValue) {
		String property = baseConfigName + name;
		double value = config.getPropertyAsDouble(property, defaultValue);

		property = baseConfigName + configName + '.' + name;
		value = config.getPropertyAsDouble(property, value);

		return value;
	}

	protected int getNumberOfAdvertisers() {
		return simulation.getNumberOfAdvertisers();
	}

	protected Random getRandom() {
		return simulation.getRandom();
	}

	protected String getAgentName(String agentAddress) {
		return simulation.getAgentName(agentAddress);
	}

	protected EventWriter getEventWriter() {
		return simulation.getEventWriter();
	}

	protected void sendEvent(String message) {
		simulation.getEventWriter().dataUpdated(index,
				TYPE_MESSAGE, message);
	}

	protected void sendWarningEvent(String message) {
		simulation.getEventWriter().dataUpdated(index,
				TYPE_WARNING, message);
	}

	protected String[] getAdvertiserAddresses() {
		return simulation.getAdvertiserAddresses();
	}

	final void sendMessage(String sender, String receiver, Transportable content) {

	}

	protected abstract void setup();

	protected abstract void stopped();

	protected abstract void shutdown();

} // Builtin
