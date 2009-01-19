package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;

import javax.swing.*;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

/**
 * @author Patrick Jordan
 */
public class AdvertiserCountPanel extends JPanel implements TACAAConstants {
    private int agent;
    private String advertiser;

    private int currentDay;
    private XYSeries impressions;
    private XYSeries clicks;
    private XYSeries conversions;

    public AdvertiserCountPanel(int agent, String advertiser, TACAASimulationPanel simulationPanel) {
        this.agent = agent;
        this.advertiser = advertiser;

        initialize();

        currentDay = 0;
        simulationPanel.addViewListener(new DataUpdateListener());
        simulationPanel.addTickListener(new DayListener());
    }

    private void initialize() {
        setLayout(new GridLayout(3, 1));

        add(new ChartPanel(createImpressionsChart()));
        add(new ChartPanel(createClicksChart()));
        add(new ChartPanel(createConversionsChart()));

        setBorder(BorderFactory.createTitledBorder(advertiser));
    }

    private JFreeChart createConversionsChart() {
        conversions = new XYSeries("Convs");
        return createChart("Convs", new XYSeriesCollection(conversions));
    }

    private JFreeChart createClicksChart() {
        clicks = new XYSeries("Clicks");
        return createChart("Clicks", new XYSeriesCollection(clicks));
    }

    private JFreeChart createImpressionsChart() {
        impressions = new XYSeries("Imprs");
        return createChart("Imprs", new XYSeriesCollection(impressions));
    }


    private JFreeChart createChart(String s, XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart(s, "Day", "", xydataset, PlotOrientation.VERTICAL, false, true, false);
        jfreechart.setBackgroundPaint(Color.white);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setRangeCrosshairVisible(true);

        org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
            xylineandshaperenderer.setBaseShapesVisible(false);
        }
        return jfreechart;
    }

    public int getAgent() {
        return agent;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    protected void addImpressions(int impressions) {
        this.impressions.addOrUpdate(currentDay,impressions);
    }

    protected void addClicks(int clicks) {
        this.clicks.addOrUpdate(currentDay, clicks);
    }

    protected void addConversions(int conversions) {
        this.conversions.addOrUpdate(currentDay, conversions);
    }

    private class DataUpdateListener implements ViewListener {

        public void dataUpdated(int agent, int type, int value) {
            if (agent == AdvertiserCountPanel.this.agent) {
                switch (type) {
                    case TACAAConstants.DU_IMPRESSIONS:
                        addImpressions(value);
                        break;
                    case TACAAConstants.DU_CLICKS:
                        addClicks(value);
                        break;
                    case TACAAConstants.DU_CONVERSIONS:
                        addConversions(value);
                        break;
                }
            }
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
        }

        public void participant(int agent, int role, String name, int participantID) {
        }
    }

    protected class DayListener implements TickListener {

        public void tick(long serverTime) {
            AdvertiserCountPanel.this.tick(serverTime);
        }

        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserCountPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
    }
}


