package edu.umich.eecs.tac.viewer;

import se.sics.isl.util.ConfigManager;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * @author Patrick Jordan
 */
public abstract class TACAAAgentView extends JComponent implements ViewListener {
	private static final Logger log = Logger.getLogger(TACAAAgentView.class
			.getName());

	private TACAASimulationPanel parent;
	private int index;
	private String name;
	private int role;

	private String roleName;

	private Icon agentIcon;

	public TACAAAgentView() {
	}

	protected final void initialized() {
		initializeView();
	}

	protected abstract void initializeView();

	final void init(TACAASimulationPanel parent, int index, String name,
			int role, String roleName) {
		if (this.name != null) {
			throw new IllegalStateException("already initialized");
		}
		this.parent = parent;
		this.index = index;
		this.name = name;
		this.role = role;
		this.roleName = roleName;

		String iconName = getConfigProperty("image");
		Icon icon;
		if ((iconName != null) && ((icon = getIcon(iconName)) != null)) {
			// Set the background icon to use for this component. Setting
			// this means that no layout manager will be used.
			setIcon(icon);
		}

		initialized();
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getRole() {
		return role;
	}

	public String getRoleName() {
		return roleName;
	}

	public Icon getIcon() {
		return agentIcon;
	}

	public void setIcon(Icon agentIcon) {
		this.agentIcon = agentIcon;
	}

	/**
	 * ******************************************************************
	 * Information retrieval and utilities for sub classes
	 * ********************************************************************
	 */

	protected ConfigManager getConfig() {
		return parent.getConfig();
	}

	protected Icon getIcon(String iconName) {
		return parent.getIcon(iconName);
	}

	protected String getConfigProperty(String prop) {
		return getConfigProperty(prop, null);
	}

	protected String getConfigProperty(String prop, String defaultValue) {
		ConfigManager config = parent.getConfig();
		String value = config.getProperty(roleName + '.' + getName() + '.'
				+ prop);
		if (value == null) {
			value = config.getProperty(roleName + '.' + prop);
		}
		return value == null ? defaultValue : value;
	}

	/**
	 * Called when a new simulation day starts (if the simulation supports the
	 * day notion).
	 * 
	 * @param serverTime
	 *            the current server time
	 * @param timeUnit
	 *            the current simulation date
	 */
	protected void nextTimeUnit(long serverTime, int timeUnit) {
	}

}
