package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.TACAAConstants;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;

/**
 * @author Patrick Jordan
 */
public class AdvertiserCountTabPanel extends SimulationTabPanel {
    Map<Integer, String> agents;

    private int currentDay;
    private XYSeriesCollection impressions;
    private XYSeriesCollection clicks;
    private XYSeriesCollection conversions;
    private Map<String, XYSeries> impressionsMap;
    private Map<String, XYSeries> clicksMap;
    private Map<String, XYSeries> conversionsMap;

    public AdvertiserCountTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);

        agents = new HashMap<Integer, String>();
        impressionsMap = new HashMap<String, XYSeries>();
        clicksMap = new HashMap<String, XYSeries>();
        conversionsMap = new HashMap<String, XYSeries>();

        simulationPanel.addViewListener(new DataUpdateListener());
        simulationPanel.addTickListener(new DayListener());
        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(3, 1));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

        add(new ChartPanel(createImpressionsChart()));
        add(new ChartPanel(createClicksChart()));
        add(new ChartPanel(createConversionsChart()));

        setBorder(BorderFactory.createTitledBorder("Counts"));

    }

    private JFreeChart createConversionsChart() {
        conversions = new XYSeriesCollection();
        return createChart("Convs", conversions, true);
    }

    private JFreeChart createClicksChart() {
        clicks = new XYSeriesCollection();
        return createChart("Clicks", clicks, false);
    }

    private JFreeChart createImpressionsChart() {
        impressions = new XYSeriesCollection();
        return createChart("Imprs", impressions, false);
    }

    private JFreeChart createChart(String s, XYDataset xydataset, boolean legend) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart(s, "Day", "",
                xydataset, PlotOrientation.VERTICAL, legend, true, false);
        jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        xyplot.setDomainGridlinePaint(Color.GRAY);
        xyplot.setRangeGridlinePaint(Color.GRAY);
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

        if (legend) {
            LegendTitle legendTitle = jfreechart.getLegend();
            legendTitle.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
            legendTitle.setFrame(BlockBorder.NONE);
        }

        XYItemRenderer xyitemrenderer = xyplot.getRenderer();

        xyitemrenderer.setBaseStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        xyplot.setOutlineVisible(false);

        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
            xylineandshaperenderer.setBaseShapesVisible(false);

            for (int i = 0; i < 8; i++) {
                xylineandshaperenderer.setSeriesPaint(i, TACAAViewerConstants.LEGEND_COLORS[i]);
            }

        }

        return jfreechart;
    }

    protected void addImpressions(String advertiser, int impressions) {

        this.impressionsMap.get(advertiser).addOrUpdate(currentDay, impressions);
    }

    protected void addClicks(String advertiser, int clicks) {
        this.clicksMap.get(advertiser).addOrUpdate(currentDay, clicks);
    }

    protected void addConversions(String advertiser, int conversions) {
        this.conversionsMap.get(advertiser).addOrUpdate(currentDay, conversions);
    }

    private class DataUpdateListener extends ViewAdaptor {

        public void dataUpdated(int agent, int type, int value) {
            String agentAddress = agents.get(agent);

            if (agentAddress != null) {
                switch (type) {
                    case TACAAConstants.DU_IMPRESSIONS:
                        addImpressions(agentAddress, value);
                        break;
                    case TACAAConstants.DU_CLICKS:
                        addClicks(agentAddress, value);
                        break;
                    case TACAAConstants.DU_CONVERSIONS:
                        addConversions(agentAddress, value);
                        break;
                }
            }
        }

        public void participant(int agent, int role, String name, int participantID) {
            handleParticipant(agent, role, name, participantID);
        }
    }

    protected class DayListener implements TickListener {

        public void tick(long serverTime) {
            AdvertiserCountTabPanel.this.tick(serverTime);
        }

        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserCountTabPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
    }

    private void handleParticipant(int agent, int role, String name, int participantID) {
        if (!agents.containsKey(agent) && role == TACAAConstants.ADVERTISER) {
            agents.put(agent, name);
            XYSeries impressionsSeries = new XYSeries(name);
            XYSeries clicksSeries = new XYSeries(name);
            XYSeries conversionsSeries = new XYSeries(name);
            impressionsMap.put(name, impressionsSeries);
            impressions.addSeries(impressionsSeries);
            clicksMap.put(name, clicksSeries);
            clicks.addSeries(clicksSeries);
            conversionsMap.put(name, conversionsSeries);
            conversions.addSeries(conversionsSeries);
        }
    }
}
