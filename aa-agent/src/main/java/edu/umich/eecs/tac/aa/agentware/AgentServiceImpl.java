package edu.umich.eecs.tac.aa.agentware;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.AgentService;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;

public class AgentServiceImpl extends AgentService {

	private static final Logger log = Logger.getLogger(AgentServiceImpl.class
			.getName());

	private StartInfo startInfo;
	private TimeListener[] timeListeners;
	private SimClient client;

	private int currentTimeUnit = -1;
	private int maxTimeUnits = Integer.MAX_VALUE;

	private int simulationDay = -1;
	// Report next time unit to time listeners when first message is received
	private boolean isAwaitingNewDay = true;

	private int timerTimeUnit;
	private Timer timer;
	private TimerTask timerTask;

	public AgentServiceImpl(SimClient client, String name, Agent agent,
			Message setupMessage) {
		super(agent, name);
		this.client = client;

		this.startInfo = (StartInfo) setupMessage.getContent();
		initializeAgent();

		// The AgentService is not initialized until a simulation is being
		// started
		simulationSetup(setupMessage.getReceiver());

		int millisPerTimeUnit = this.startInfo.getSecondsPerDay() * 1000;
		if (millisPerTimeUnit > 0) {
			// Set a limit on the simulation length in case the server fails
			// to notify about simulation end but also allow for some delays.
			this.maxTimeUnits = this.startInfo.getNumberOfDays() + 1;
			setupTimer(this.startInfo.getStartTime(), millisPerTimeUnit);
		}
	}

	final void stopAgent() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		if (timer != null) {
			timer.cancel();
		}
		timerTask = null;
		timer = null;
		simulationStopped();
		simulationFinished();
	}

	protected void deliverToServer(Message message) {
		client.deliverToServer(message);
	}

	protected void deliverToServer(int role, Transportable message) {
		log.severe("Agent can not deliver to role " + role);
	}

	protected long getServerTime() {
		return client.getServerTime();
	}

	protected void deliverToAgent(Message message) {
		if (isAwaitingNewDay) {
			isAwaitingNewDay = false;
			notifyTimeListeners(++simulationDay);
		}

		try {
			Transportable content = message.getContent();
			if (content instanceof SimulationStatus) {
				// Contains the current day/time unit and indicates that the
				// next message will not arrive until next day/time unit.
				simulationDay = ((SimulationStatus) content).getCurrentDate();
				isAwaitingNewDay = true;
				notifyTimeListeners(simulationDay);
			}

			super.deliverToAgent(message);
		} catch (ThreadDeath e) {
			log.log(Level.SEVERE, "message thread died", e);
			throw e;
		} catch (Throwable e) {
			log.log(Level.SEVERE, "agent could not handle message " + message,
					e);
		}
	}

	// -------------------------------------------------------------------
	// Time Listening
	// -------------------------------------------------------------------

	private void setupTimer(long startServerTime, int millisPerTimeUnit) {
		timer = new Timer();
		timerTask = new TimerTask() {
			public void run() {
				tick();
			}
		};
		// Must handle the difference between the server time and the system
		// time
		long startTime = startServerTime + client.getTimeDiff();
		long currentServerTime = client.getServerTime();
		if (currentServerTime > startServerTime) {
			// Since the game already started we need to calculate the
			// current time unit
			currentTimeUnit = (int) ((currentServerTime - startServerTime) / millisPerTimeUnit);
			startTime += currentTimeUnit * millisPerTimeUnit;
		}
		timer.scheduleAtFixedRate(timerTask, new Date(startTime),
				millisPerTimeUnit);
	}

	private void tick() {
		notifyTimeListeners(timerTimeUnit++);
	}

	private void notifyTimeListeners(int unit) {
		boolean notify = false;
		synchronized (this) {
			if (unit > currentTimeUnit) {
				currentTimeUnit = unit;
				notify = true;
			}
		}

		if (notify) {
			log.fine("*** TIME UNIT " + currentTimeUnit);
			if (unit > maxTimeUnits) {
				// It seems like the server has failed to notify this agent
				// about the simulation end
				client.showWarning("Forced Simulation End",
						"forcing simulation to end at time unit " + unit
								+ " (max " + maxTimeUnits + " time units)");
				client.stopSimulation(this);

			} else {
				TimeListener[] listeners = timeListeners;
				if (listeners != null) {
					for (int i = 0, n = listeners.length; i < n; i++) {
						try {
							listeners[i].nextTimeUnit(currentTimeUnit);
						} catch (ThreadDeath e) {
							throw e;
						} catch (Throwable e) {
							log.log(Level.SEVERE,
									"could not deliver time unit "
											+ currentTimeUnit + " to "
											+ listeners[i], e);
						}
					}
				}
			}
		}
	}

	protected synchronized void addTimeListener(TimeListener listener) {
		timeListeners = (TimeListener[]) ArrayUtils.add(TimeListener.class,
				timeListeners, listener);
	}

	protected synchronized void removeTimeListener(TimeListener listener) {
		timeListeners = (TimeListener[]) ArrayUtils.remove(timeListeners,
				listener);
	}

} // AgentServiceImpl
