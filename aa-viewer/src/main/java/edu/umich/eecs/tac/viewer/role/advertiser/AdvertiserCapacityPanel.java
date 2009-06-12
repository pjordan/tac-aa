package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import se.sics.tasim.viewer.TickListener;
import se.sics.isl.transport.Transportable;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static edu.umich.eecs.tac.viewer.ViewerChartFactory.*;
import static edu.umich.eecs.tac.viewer.ViewerUtils.*;

/**
 * @author Guha Balakrishnan
 */
public class AdvertiserCapacityPanel extends SimulationTabPanel {
    private int agent;
    private String advertiser;
    private int currentDay;
    private XYSeries relativeCapacity;
    private int capacity;
    private int window;
    private Map<Integer, Integer> amountsSold;
    private Set<Query> queries;
    private Color legendColor;

    public AdvertiserCapacityPanel(int agent, String advertiser, TACAASimulationPanel simulationPanel,
                                   Color legendColor) {
        super(simulationPanel);
        this.agent = agent;
        this.advertiser = advertiser;
        this.legendColor = legendColor;
        currentDay = 0;

        simulationPanel.addViewListener(new DataUpdateListener());
        simulationPanel.addTickListener(new DayListener());
        initialize();
    }

    protected void initialize() {
        setLayout(new GridLayout(1, 1));
        setBorder(BorderFactory.createTitledBorder(" Capacity Used"));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

        amountsSold = new HashMap<Integer, Integer>();
        queries = new HashSet<Query>();
        relativeCapacity = new XYSeries("Relative Capacity");
        XYSeriesCollection seriescollection = new XYSeriesCollection();


        seriescollection.addSeries(relativeCapacity);
        JFreeChart chart = createCapacityChart(seriescollection, legendColor);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);

        add(chartpanel);
    }

    protected class DayListener implements TickListener {

        public void tick(long serverTime) {
            AdvertiserCapacityPanel.this.tick(serverTime);
        }

        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserCapacityPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
    }



    private int getAmountSold(SalesReport report) {

        int result = 0;

        for (Query query : queries) {
            result = result + report.getConversions(query);
        }

        return result;
    }

    private void updateChart() {

        double soldInWindow = 0;

        for (int i = Math.max(0, currentDay - window); i < currentDay; i++) {

            if (!(amountsSold.get(i) == null || Double.isNaN(amountsSold.get(i)))) {

                soldInWindow = soldInWindow + amountsSold.get(i);

            }

        }

        relativeCapacity.addOrUpdate(currentDay, (soldInWindow / capacity) * 100);
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        buildQuerySpace(queries, retailCatalog);
    }

    private void handleAdvertiserInfo(AdvertiserInfo advertiserInfo) {
        capacity = advertiserInfo.getDistributionCapacity();
        window = advertiserInfo.getDistributionWindow();
    }

    private void handleSalesReport(SalesReport salesReport) {
        int sold = getAmountSold(salesReport);
        amountsSold.put(currentDay - 1, sold);
        updateChart();
    }

    private class DataUpdateListener extends ViewAdaptor {

        public void dataUpdated(int agent, int type, Transportable value) {
            if (AdvertiserCapacityPanel.this.agent == agent
                    && type == TACAAConstants.DU_ADVERTISER_INFO &&
                    value.getClass() == AdvertiserInfo.class) {
                handleAdvertiserInfo((AdvertiserInfo) value);
            }

            if (AdvertiserCapacityPanel.this.agent == agent &&
                    type == TACAAConstants.DU_SALES_REPORT &&
                    value.getClass() == SalesReport.class) {
                handleSalesReport((SalesReport)value);
            }
        }

        public void dataUpdated(int type, Transportable value) {
            Class valueType = value.getClass();
            if (valueType == RetailCatalog.class) {
                handleRetailCatalog((RetailCatalog) value);
            }
        }

    }


}
