

package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.viewer.role.AgentSupport;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.SlotInfo;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: May 30, 2009
 * Time: 12:40:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class RankingTabPanel extends SimulationTabPanel{
    private RetailCatalog catalog;
	private Map<Query, RankingPanel> rankingPanels;
	private AgentSupport agentSupport;
    private int currentDay;

    public RankingTabPanel(TACAASimulationPanel simulationPanel) {
		super(simulationPanel);

		agentSupport = new AgentSupport();

		simulationPanel.addViewListener(new CatalogListener());
		simulationPanel.addViewListener(agentSupport);
        simulationPanel.addTickListener(new DayListener());

		initialize();
	}

	private void initialize() {
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
		rankingPanels = new HashMap<Query, RankingPanel >();
	}

   	private void handleRetailCatalog(RetailCatalog retailCatalog) {
		this.catalog = retailCatalog;

		this.removeAll();
		rankingPanels.clear();

		for (Product product : retailCatalog) {
			// Create f0
			Query f0 = new Query();

			// Create f1's
			Query f1_manufacturer = new Query(product.getManufacturer(), null);
			Query f1_component = new Query(null, product.getComponent());

			// Create f2
			Query f2 = new Query(product.getManufacturer(), product
					.getComponent());

			if (!rankingPanels.containsKey(f0)) {
				rankingPanels.put(f0, new RankingPanel(f0, this));
			}
			if (!rankingPanels.containsKey(f1_manufacturer)) {
				rankingPanels.put(f1_manufacturer, new RankingPanel(
						f1_manufacturer, this));
			}
			if (!rankingPanels.containsKey(f1_component)) {
			    rankingPanels.put(f1_component, new RankingPanel(f1_component,
						this));
			}
			if (!rankingPanels.containsKey(f2)) {
				rankingPanels.put(f2, new RankingPanel(f2, this));
			}
		}

		int panelCount = rankingPanels.size();
		int sideCount = (int) Math.ceil(Math.sqrt(panelCount));

		setLayout(new GridLayout(sideCount, sideCount));

        
		for (Query query : rankingPanels.keySet()) {
			add(rankingPanels.get(query));
		}
	}

	private class CatalogListener implements ViewListener {

		public void dataUpdated(int agent, int type, int value) {
		}

		public void dataUpdated(int agent, int type, long value) {
		}

		public void dataUpdated(int agent, int type, float value) {
		}

		public void dataUpdated(int agent, int type, double value) {
		}

		public void dataUpdated(int agent, int type, String value) {
		}

		public void dataUpdated(int agent, int type, Transportable value) {
		}

		public void dataUpdated(int type, Transportable value) {
			Class valueType = value.getClass();
			if (valueType == RetailCatalog.class) {
				handleRetailCatalog((RetailCatalog) value);
			}

		}

		public void participant(int agent, int role, String name,
				int participantID) {
		}
	}

    protected class DayListener implements TickListener {

		public void tick(long serverTime) {
			RankingTabPanel.this.tick(serverTime);
		}

		public void simulationTick(long serverTime, int simulationDate) {
			RankingTabPanel.this.simulationTick(serverTime, simulationDate);
		}
	}

	protected void tick(long serverTime) {
	}

	protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
        setBorder(BorderFactory.createTitledBorder("Auction Results for Day " + (currentDay - 1)));
	}

	public int getAgentCount() {
		return agentSupport.size();
	}

	public int getAgent(int index) {
		return agentSupport.agent(index);
	}

	public int getRole(int index) {
		return agentSupport.role(index);
	}

	public int getParticipant(int index) {
		return agentSupport.participant(index);
	}

	public int indexOfAgent(int agent) {
		return agentSupport.indexOfAgent(agent);
	}

	public String getAgentName(int index) {
		return agentSupport.name(index);
	}

}

