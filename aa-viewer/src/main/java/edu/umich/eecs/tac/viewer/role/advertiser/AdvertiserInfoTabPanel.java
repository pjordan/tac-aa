package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.publisher.RankingPanel;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

import se.sics.isl.transport.Transportable;

/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 10, 2009
 * Time: 9:33:03 PM
 * To change this template use File | Settings | File Templates.
 */


public class AdvertiserInfoTabPanel extends SimulationTabPanel {

    private int agent;
    private String advertiser;
    private TACAASimulationPanel simulationPanel;
    private JTabbedPane tabbedPane;
    private RetailCatalog catalog;
    private Map<Query, AdvertiserQueryTabPanel> advertiserQueryTabPanels;


    public AdvertiserInfoTabPanel(int agent, String advertiser,
			TACAASimulationPanel simulationPanel){
        super(simulationPanel);
        this.agent = agent;
        this.advertiser = advertiser;
        this.simulationPanel = simulationPanel;

        simulationPanel.addViewListener(new CatalogListener());
        initialize();
    }

    private void initialize(){
       setLayout(new BorderLayout());
       advertiserQueryTabPanels = new HashMap<Query, AdvertiserQueryTabPanel>();
       tabbedPane = new JTabbedPane(JTabbedPane.RIGHT);
       tabbedPane.addTab("Main", new AdvertiserMainTabPanel(simulationPanel));
       add(tabbedPane);
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
                advertiserQueryTabPanels.put(f0, new AdvertiserQueryTabPanel(agent, advertiser, f0,simulationPanel));
            }
            if (!advertiserQueryTabPanels.containsKey(f1_manufacturer)) {
                advertiserQueryTabPanels.put(f1_manufacturer, new AdvertiserQueryTabPanel(agent, advertiser,f1_manufacturer,simulationPanel));
            }
            if (!advertiserQueryTabPanels.containsKey(f1_component)) {
                advertiserQueryTabPanels.put(f1_component, new AdvertiserQueryTabPanel(agent, advertiser,f1_component,simulationPanel));
            }
            if (!advertiserQueryTabPanels.containsKey(f2)) {
                advertiserQueryTabPanels.put(f2, new AdvertiserQueryTabPanel(agent, advertiser,f2,simulationPanel));
            }
        }

        for (Query query : advertiserQueryTabPanels.keySet()) {
            tabbedPane.add("(" + query.getManufacturer() + "," + query.getComponent() + ")",
                         advertiserQueryTabPanels.get(query));
        }
    }




    private class CatalogListener implements ViewListener {

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



}
