package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.viewer.role.AgentSupport;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Product;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;


/**
 * @author Guha Balakrishnan
 */
public class RankingTabPanel extends SimulationTabPanel {
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
        rankingPanels = new HashMap<Query, RankingPanel>();
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.catalog = retailCatalog;

        this.removeAll();
        rankingPanels.clear();

        for (Product product : retailCatalog) {
            // Create f0
            Query f0 = new Query();

            // Create f1's
            Query f1Manufacturer = new Query(product.getManufacturer(), null);
            Query f1Component = new Query(null, product.getComponent());

            // Create f2
            Query f2 = new Query(product.getManufacturer(), product.getComponent());

            addRankingPanel(f0);
            addRankingPanel(f1Manufacturer);
            addRankingPanel(f1Component);
            addRankingPanel(f2);
        }

        int panelCount = rankingPanels.size();
        int sideCount = (int) Math.ceil(Math.sqrt(panelCount));

        setLayout(new GridLayout(sideCount, sideCount));


        for (Query query : rankingPanels.keySet()) {
            add(rankingPanels.get(query));
        }
    }

    private void addRankingPanel(Query query) {
        if (!rankingPanels.containsKey(query)) {
            rankingPanels.put(query, new RankingPanel(query, this));
        }
    }

    private class CatalogListener extends ViewAdaptor {

        public void dataUpdated(final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Class valueType = value.getClass();
                    if (valueType == RetailCatalog.class) {
                        handleRetailCatalog((RetailCatalog) value);
                    }
                }
            });
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

