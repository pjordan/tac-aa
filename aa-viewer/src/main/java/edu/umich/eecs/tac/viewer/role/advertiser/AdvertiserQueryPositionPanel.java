package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.TACAAConstants;

import javax.swing.*;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

/**
 * @author Patrick Jordan
 */
public class AdvertiserQueryPositionPanel extends JPanel {
    private int agent;
    private String advertiser;
    private Query query;

    private int currentDay;
    private XYSeries position;
    private Color legendColor;

    public AdvertiserQueryPositionPanel(int agent, String advertiser, Query query,
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
        setLayout(new GridLayout(1, 1));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

        add(createChartPanel(createPositionChart()));


        setBorder(BorderFactory.createTitledBorder("Position"));
    }

    private ChartPanel createChartPanel(JFreeChart jFreeChart) {
        ChartPanel panel = new ChartPanel(jFreeChart);
        return panel;
    }

    private JFreeChart createPositionChart() {
        position = new XYSeries("Position");
        return createChart(new XYSeriesCollection(position));
    }

    private JFreeChart createChart(XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart(null, "Day", "", xydataset, PlotOrientation.VERTICAL,
                false, true, false);
        jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        xyplot.setDomainGridlinePaint(Color.GRAY);
        xyplot.setRangeGridlinePaint(Color.GRAY);
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

        org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot
                .getRenderer();
        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
            xylineandshaperenderer.setBaseShapesVisible(false);
            xylineandshaperenderer.setBaseStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL));
            xylineandshaperenderer.setSeriesPaint(0, legendColor);
        }

        return jfreechart;
    }

    public int getAgent() {
        return agent;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    protected void setPosition(double position) {
        this.position.addOrUpdate(currentDay, position);
    }

    private class DataUpdateListener extends ViewAdaptor {

        public void dataUpdated(int agent, int type, Transportable value) {
            if (agent == AdvertiserQueryPositionPanel.this.agent) {
                switch (type) {
                    case TACAAConstants.DU_QUERY_REPORT:
                        handleQueryReport((QueryReport) value);
                        break;
                }
            }
        }

        private void handleQueryReport(QueryReport queryReport) {
            setPosition(queryReport.getPosition(query));
        }
    }

    protected class DayListener implements TickListener {

        public void tick(long serverTime) {
            AdvertiserQueryPositionPanel.this.tick(serverTime);
        }

        public void simulationTick(long serverTime, int simulationDate) {
            AdvertiserQueryPositionPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
    }
}
