package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import static edu.umich.eecs.tac.viewer.ViewerChartFactory.createDaySeriesChartWithColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

import javax.swing.*;
import java.awt.*;

/**
 * @author Patrick Jordan
 */
public class AdvertiserQueryCountPanel extends JPanel {
    private int agent;
    private String advertiser;
    private Query query;

    private int currentDay;
    private XYSeries impressions;
    private XYSeries clicks;
    private XYSeries conversions;
    private Color legendColor;

    public AdvertiserQueryCountPanel(int agent, String advertiser, Query query,
                                     TACAASimulationPanel simulationPanel, Color legendColor) {
        this.agent = agent;
        this.advertiser = advertiser;
        this.query = query;
        this.legendColor = legendColor;

        initialize();

        currentDay = 0;
        simulationPanel.addViewListener(new DataUpdateListener());
        simulationPanel.addTickListener(new DayListener());
    }

    private void initialize() {
        setLayout(new GridLayout(3, 1));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

        add(createChartPanel(createImpressionsChart()));
        add(createChartPanel(createClicksChart()));
        add(createChartPanel(createConversionsChart()));

        setBorder(BorderFactory.createTitledBorder("Raw Metrics"));
    }

    private ChartPanel createChartPanel(JFreeChart jFreeChart) {
        return new ChartPanel(jFreeChart);
    }

    private JFreeChart createConversionsChart() {
        conversions = new XYSeries("Convs");
        return createDaySeriesChartWithColor("Convs", new XYSeriesCollection(conversions), legendColor);
    }

    private JFreeChart createClicksChart() {
        clicks = new XYSeries("Clicks");
        return createDaySeriesChartWithColor("Clicks", new XYSeriesCollection(clicks), legendColor);
    }

    private JFreeChart createImpressionsChart() {
        impressions = new XYSeries("Imprs");
        return createDaySeriesChartWithColor("Imprs", new XYSeriesCollection(impressions), legendColor);
    }

    public int getAgent() {
        return agent;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    protected void addImpressions(int impressions) {
        this.impressions.addOrUpdate(currentDay, impressions);
    }

    protected void addClicks(int clicks) {
        this.clicks.addOrUpdate(currentDay, clicks);
    }

    protected void addConversions(int conversions) {
        this.conversions.addOrUpdate(currentDay, conversions);
    }

    private class DataUpdateListener extends ViewAdaptor {

        public void dataUpdated(final int agent, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (agent == AdvertiserQueryCountPanel.this.agent) {
                        switch (type) {
                            case TACAAConstants.DU_QUERY_REPORT:
                                handleQueryReport((QueryReport) value);
                                break;
                            case TACAAConstants.DU_SALES_REPORT:
                                handleSalesReport((SalesReport) value);
                                break;
                        }
                    }
                }
            });

        }

        private void handleQueryReport(QueryReport queryReport) {
            addImpressions(queryReport.getImpressions(query));
            addClicks(queryReport.getClicks(query));
        }

        private void handleSalesReport(SalesReport salesReport) {
            addConversions(salesReport.getConversions(query));
        }
    }

    protected class DayListener implements TickListener {

        public void tick(long serverTime) {
            AdvertiserQueryCountPanel.this.tick(serverTime);
        }

        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserQueryCountPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
    }
}
