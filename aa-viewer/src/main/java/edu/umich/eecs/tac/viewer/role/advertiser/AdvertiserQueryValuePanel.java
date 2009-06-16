package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.TACAAConstants;

import javax.swing.*;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.awt.*;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;
import static edu.umich.eecs.tac.viewer.ViewerChartFactory.*;

/**
 * @author Patrick Jordan
 */
public class AdvertiserQueryValuePanel extends JPanel {
    private int agent;
    private String advertiser;
    private Query query;

    private XYSeriesCollection seriescollection;
    private XYSeries revSeries;
    private XYSeries costSeries;
    private int currentDay;

    public AdvertiserQueryValuePanel(int agent, String advertiser, Query query,
                                     TACAASimulationPanel simulationPanel) {

        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        revSeries = new XYSeries("Revenue");
        costSeries = new XYSeries("Cost");
        seriescollection = new XYSeriesCollection();

        this.agent = agent;
        this.advertiser = advertiser;
        this.query = query;

        simulationPanel.addTickListener(new DayListener());
        simulationPanel.addViewListener(new DataUpdateListener());
        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(1, 1));
        seriescollection.addSeries(revSeries);
        seriescollection.addSeries(costSeries);

        JFreeChart chart = createDifferenceChart(seriescollection);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);
        add(chartpanel);

        setBorder(BorderFactory.createTitledBorder("Revenue and Cost"));
    }

    protected class DayListener implements TickListener {

        public void tick(long serverTime) {
            AdvertiserQueryValuePanel.this.tick(serverTime);
        }

        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserQueryValuePanel.this.simulationTick(serverTime, simulationDate);
        }
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
    }

    private double getDayCost(QueryReport report) {
        return report.getCost(query);
    }

    private double getDayRevenue(SalesReport report) {
        return report.getRevenue(query);
    }

    private class DataUpdateListener extends ViewAdaptor {

        public void dataUpdated(final int agent, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (type == TACAAConstants.DU_QUERY_REPORT &&
                            value.getClass().equals(QueryReport.class) &&
                            agent == AdvertiserQueryValuePanel.this.agent) {

                        QueryReport queryReport = (QueryReport) value;

                        costSeries.addOrUpdate(currentDay, getDayCost(queryReport));
                    }

                    if (type == TACAAConstants.DU_SALES_REPORT &&
                            value.getClass().equals(SalesReport.class) &&
                            agent == AdvertiserQueryValuePanel.this.agent) {

                        SalesReport salesReport = (SalesReport) value;

                        revSeries.addOrUpdate(currentDay, getDayRevenue(salesReport));
                    }
                }
            });


        }
    }

}
