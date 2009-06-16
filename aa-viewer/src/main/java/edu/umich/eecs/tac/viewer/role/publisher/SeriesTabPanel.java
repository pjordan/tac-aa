package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.viewer.role.AgentSupport;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.TACAAConstants;
import se.sics.isl.transport.Transportable;

import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItem;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.*;


/**
 * @author Guha Balakrishnan
 */
public class SeriesTabPanel extends SimulationTabPanel {
    private RetailCatalog catalog;
    private Map<Query, SeriesPanel> seriesPanels;
    private AgentSupport agentSupport;
    private LegendPanel legendPanel;


    public SeriesTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);

        agentSupport = new AgentSupport();

        simulationPanel.addViewListener(new BidBundleListener());
        simulationPanel.addViewListener(agentSupport);

        initialize();
    }

    private void initialize() {
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        seriesPanels = new HashMap<Query, SeriesPanel>();
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.catalog = retailCatalog;

        this.removeAll();
        seriesPanels.clear();

        for (Product product : retailCatalog) {
            // Create f0
            Query f0 = new Query();

            // Create f1's
            Query f1Manufacturer = new Query(product.getManufacturer(), null);
            Query f1Component = new Query(null, product.getComponent());

            // Create f2
            Query f2 = new Query(product.getManufacturer(), product.getComponent());

            addSeriesPanel(f0);
            addSeriesPanel(f1Manufacturer);
            addSeriesPanel(f1Component);
            addSeriesPanel(f2);
        }

        int panelCount = seriesPanels.size();
        int sideCount = (int) Math.ceil(Math.sqrt(panelCount));

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.ipady = 200;

        Iterator iterator = seriesPanels.keySet().iterator();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                SeriesPanel temp = seriesPanels.get(iterator.next());
                c.gridx = j;
                c.gridy = i;
                add(temp, c);
            }
        }

        legendPanel = new LegendPanel(this, TACAAViewerConstants.LEGEND_COLORS);

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 4;
        c.ipadx = 500;
        c.ipady = 0;
        c.gridwidth = 4;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(5, 0, 0, 0);

        legendPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        c.anchor = GridBagConstraints.PAGE_END;
        add(legendPanel, c);
    }

    private void addSeriesPanel(Query query) {
        if (!seriesPanels.containsKey(query)) {
            seriesPanels.put(query, new SeriesPanel(query, this));
        }
    }

    private class BidBundleListener extends ViewAdaptor {
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

