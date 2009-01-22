package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Product;
import se.sics.isl.transport.Transportable;

import java.util.Map;
import java.util.HashMap;
import java.awt.*;

/**
 * @author Patrick Jordan
 */
public class PublisherTabPanel extends SimulationTabPanel {
	private RetailCatalog catalog;
	private Map<Query, AuctionPanel> auctionPanels;
	private AgentSupport agentSupport;

	public PublisherTabPanel(TACAASimulationPanel simulationPanel) {
		super(simulationPanel);

		agentSupport = new AgentSupport();

		simulationPanel.addViewListener(new BidBundleListener());
		simulationPanel.addViewListener(agentSupport);

		initialize();
	}

	private void initialize() {
		auctionPanels = new HashMap<Query, AuctionPanel>();
	}

	private void handleRetailCatalog(RetailCatalog retailCatalog) {
		this.catalog = retailCatalog;

		this.removeAll();
		auctionPanels.clear();

		for (Product product : retailCatalog) {
			// Create f0
			Query f0 = new Query();

			// Create f1's
			Query f1_manufacturer = new Query(product.getManufacturer(), null);
			Query f1_component = new Query(null, product.getComponent());

			// Create f2
			Query f2 = new Query(product.getManufacturer(), product
					.getComponent());

			if (!auctionPanels.containsKey(f0)) {
				auctionPanels.put(f0, new AuctionPanel(f0, this));
			}
			if (!auctionPanels.containsKey(f1_manufacturer)) {
				auctionPanels.put(f1_manufacturer, new AuctionPanel(
						f1_manufacturer, this));
			}
			if (!auctionPanels.containsKey(f1_component)) {
				auctionPanels.put(f1_component, new AuctionPanel(f1_component,
						this));
			}
			if (!auctionPanels.containsKey(f2)) {
				auctionPanels.put(f2, new AuctionPanel(f2, this));
			}
		}

		int panelCount = auctionPanels.size();
		int sideCount = (int) Math.ceil(Math.sqrt(panelCount));

		setLayout(new GridLayout(sideCount, sideCount));

		for (Query query : auctionPanels.keySet()) {
			add(auctionPanels.get(query));
		}
	}

	private class BidBundleListener implements ViewListener {

		public void dataUpdated(int agent, int type, int value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, long value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, float value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, double value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, String value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, Transportable value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int type, Transportable value) {
			Class valueType = value.getClass();
			if (valueType == RetailCatalog.class) {
				handleRetailCatalog((RetailCatalog) value);
			}
		}

		public void participant(int agent, int role, String name,
				int participantID) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}
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
