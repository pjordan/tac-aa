package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.publisher.RankingPanel;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

import se.sics.isl.transport.Transportable;

/**
 * @author Guha Balakrishnan
 */
public class AdvertiserInfoTabPanel extends SimulationTabPanel {

    private int agent;
    private String advertiser;
    private TACAASimulationPanel simulationPanel;
    private JTabbedPane tabbedPane;
    private RetailCatalog catalog;
    private Map<Query, AdvertiserQueryTabPanel> advertiserQueryTabPanels;
    private Color legendColor;

    public AdvertiserInfoTabPanel(int agent, String advertiser,
                                  TACAASimulationPanel simulationPanel, Color legendColor) {
        super(simulationPanel);
        this.agent = agent;
        this.advertiser = advertiser;
        this.simulationPanel = simulationPanel;
        this.legendColor = legendColor;

        simulationPanel.addViewListener(new CatalogListener());
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        advertiserQueryTabPanels = new HashMap<Query, AdvertiserQueryTabPanel>();
        tabbedPane = new JTabbedPane(JTabbedPane.RIGHT);
        tabbedPane.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        tabbedPane.add("Main", new AdvertiserMainTabPanel(simulationPanel, agent, advertiser, legendColor));
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.catalog = retailCatalog;
        advertiserQueryTabPanels.clear();
        for (Product product : retailCatalog) {
            // Create f0
            Query f0 = new Query();

            // Create f1's
            Query f1_manufacturer = new Query(product.getManufacturer(), null);
            Query f1_component = new Query(null, product.getComponent());

            // Create f2
            Query f2 = new Query(product.getManufacturer(), product
                    .getComponent());

            if (!advertiserQueryTabPanels.containsKey(f0)) {
                advertiserQueryTabPanels.put(f0, new AdvertiserQueryTabPanel(agent, advertiser, f0, simulationPanel, legendColor));
            }
            if (!advertiserQueryTabPanels.containsKey(f1_manufacturer)) {
                advertiserQueryTabPanels.put(f1_manufacturer, new AdvertiserQueryTabPanel(agent, advertiser, f1_manufacturer, simulationPanel, legendColor));
            }
            if (!advertiserQueryTabPanels.containsKey(f1_component)) {
                advertiserQueryTabPanels.put(f1_component, new AdvertiserQueryTabPanel(agent, advertiser, f1_component, simulationPanel, legendColor));
            }
            if (!advertiserQueryTabPanels.containsKey(f2)) {
                advertiserQueryTabPanels.put(f2, new AdvertiserQueryTabPanel(agent, advertiser, f2, simulationPanel, legendColor));
            }
        }


        for (Query query : advertiserQueryTabPanels.keySet()) {
            tabbedPane.add("(" + query.getManufacturer() + "," + query.getComponent() + ")",
                    advertiserQueryTabPanels.get(query));
        }
        add(tabbedPane);
    }

    private class CatalogListener extends ViewAdaptor {

        public void dataUpdated(int type, Transportable value) {
            Class valueType = value.getClass();
            if (valueType == RetailCatalog.class) {
                handleRetailCatalog((RetailCatalog) value);
            }
        }
    }
}
