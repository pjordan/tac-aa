package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.TACAAConstants;

import java.awt.*;

import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import com.botbox.util.ArrayUtils;
import se.sics.tasim.viewer.TickListener;

import javax.swing.*;
import static edu.umich.eecs.tac.viewer.ViewerChartFactory.*;

/**
 * @author Patrick Jordan
 */
public class OverviewPanel extends SimulationTabPanel {
	private XYSeriesCollection seriescollection;

	private int[] agents;
	private int[] roles;
	private int[] participants;
	private XYSeries[] series;
	private String[] names;
	private int agentCount;
	private int currentDay;

	public OverviewPanel(TACAASimulationPanel simulationPanel) {
		super(simulationPanel);
        setBorder(BorderFactory.createTitledBorder("Advertiser Profits"));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        
		agents = new int[0];
		roles = new int[0];
		participants = new int[0];
		series = new XYSeries[0];
		names = new String[0];
		currentDay = 0;
		initialize();

		getSimulationPanel().addTickListener(new DayListener());
		getSimulationPanel().addViewListener(new BankStatusListener());
	}

	protected void initialize() {
		setLayout(new GridLayout(1, 1));
		seriescollection = new XYSeriesCollection();
		JFreeChart chart = createDaySeriesChartWithColors(null, seriescollection, true);
		ChartPanel chartpanel = new ChartPanel(chart, false);
		chartpanel.setMouseZoomable(true, false);

		add(chartpanel);
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
			series = (XYSeries[]) ArrayUtils.setSize(series, newSize);
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

		if (series[index] == null && TACAAConstants.ADVERTISER == roles[index]) {
			series[index] = new XYSeries(name);
			seriescollection.addSeries(series[index]);
		}
	}

	protected void participant(int agent, int role, String name,
			int participantID) {
		setAgent(agent, role, name, participantID);
	}

	protected void dataUpdated(int agent, int type, double value) {
		int index = ArrayUtils.indexOf(agents, 0, agentCount, agent);
		if (index < 0 || series[index] == null
				|| type != TACAAConstants.DU_BANK_ACCOUNT) {
			return;
		}

		series[index].addOrUpdate(currentDay, value);
	}

	protected void tick(long serverTime) {
	}

	protected void simulationTick(long serverTime, int simulationDate) {
		currentDay = simulationDate;
	}

	protected class DayListener implements TickListener {

		public void tick(long serverTime) {
			OverviewPanel.this.tick(serverTime);
		}

		public void simulationTick(long serverTime, int simulationDate) {
			OverviewPanel.this.simulationTick(serverTime, simulationDate);
		}
	}

	protected class BankStatusListener extends ViewAdaptor {

		public void dataUpdated(int agent, int type, double value) {
			OverviewPanel.this.dataUpdated(agent, type, value);
		}

		public void participant(int agent, int role, String name, int participantID) {
			OverviewPanel.this.participant(agent, role, name, participantID);
		}
	}
}