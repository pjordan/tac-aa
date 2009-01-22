package edu.umich.eecs.tac.util.config;

/**
 * The config proxy gives a reduced view of the
 * {@link se.sics.isl.util.ConfigManager ConfigManager} properties, such as
 * those given by a {@link edu.umich.eecs.tac.sim.Builtin Builtin} agent.
 * 
 * @author Patrick Jordan
 */
public interface ConfigProxy {

	public String getProperty(String name);

	public String getProperty(String name, String defaultValue);

	public String[] getPropertyAsArray(String name);

	public String[] getPropertyAsArray(String name, String defaultValue);

	public int getPropertyAsInt(String name, int defaultValue);

	public int[] getPropertyAsIntArray(String name);

	public int[] getPropertyAsIntArray(String name, String defaultValue);

	public long getPropertyAsLong(String name, long defaultValue);

	public float getPropertyAsFloat(String name, float defaultValue);

	public double getPropertyAsDouble(String name, double defaultValue);
}
