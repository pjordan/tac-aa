package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.TACAAConstants;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
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
public class UserTabPanel extends SimulationTabPanel implements TACAAConstants {
    private XYSeries nsTimeSeries;
    private XYSeries isTimeSeries;
    private XYSeries f0TimeSeries;
    private XYSeries f1TimeSeries;
    private XYSeries f2TimeSeries;
    private XYSeries tTimeSeries;

    private XYSeriesCollection seriescollection;
    private int currentDay;

    public UserTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);


        currentDay = 0;

        initializeView();
        
        simulationPanel.addTickListener(new DayListener());
        simulationPanel.addViewListener(new UserSearchStateListener());
    }

    protected void initializeView() {
        createDataset();
        setLayout(new BorderLayout());
        JFreeChart jfreechart = createChart(seriescollection);
        ChartPanel chartpanel = new ChartPanel(jfreechart, false);
        chartpanel.setPreferredSize(new Dimension(500, 270));
        chartpanel.setMouseZoomable(true, false);

        add(chartpanel, BorderLayout.CENTER);
    }

    protected void nextTimeUnit(long serverTime, int timeUnit) {
        currentDay = timeUnit;
    }

    private class UserSearchStateListener implements ViewListener {

        public void dataUpdated(int agent, int type, int value) {
            switch (type) {
                case DU_NON_SEARCHING:
                    nsTimeSeries.addOrUpdate(currentDay, value);
                    break;
                case DU_INFORMATIONAL_SEARCH:
                    isTimeSeries.addOrUpdate(currentDay, value);
                    break;
                case DU_FOCUS_LEVEL_ZERO:
                    f0TimeSeries.addOrUpdate(currentDay, value);
                    break;
                case DU_FOCUS_LEVEL_ONE:
                    f1TimeSeries.addOrUpdate(currentDay, value);
                    break;
                case DU_FOCUS_LEVEL_TWO:
                    f2TimeSeries.addOrUpdate(currentDay, value);
                    break;
                case DU_TRANSACTED:
                    tTimeSeries.addOrUpdate(currentDay, value);
                    break;
                default:
                    break;
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


    private JFreeChart createChart(XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart("User state distribution", "Day", "Users per state", xydataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.setBackgroundPaint(Color.white);

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();

        xyplot.setBackgroundPaint(Color.lightGray);

        xyplot.setDomainGridlinePaint(Color.white);

        xyplot.setRangeGridlinePaint(Color.white);

        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

        xyplot.setDomainCrosshairVisible(true);

        xyplot.setRangeCrosshairVisible(true);
        
        org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot.getRenderer();

        xyitemrenderer.setBaseStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        
        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
            xylineandshaperenderer.setBaseShapesVisible(false);
        }

        return jfreechart;
    }

    private void createDataset() {
        nsTimeSeries = new XYSeries("NS");
        isTimeSeries = new XYSeries("IS");
        f0TimeSeries = new XYSeries("F0");
        f1TimeSeries = new XYSeries("F1");
        f2TimeSeries = new XYSeries("F2");
        tTimeSeries = new XYSeries("T");

        seriescollection = new XYSeriesCollection();
        //seriescollection.addSeries(nsTimeSeries);
        seriescollection.addSeries(isTimeSeries);
        seriescollection.addSeries(f0TimeSeries);
        seriescollection.addSeries(f1TimeSeries);
        seriescollection.addSeries(f2TimeSeries);
        seriescollection.addSeries(tTimeSeries);
    }

    protected class DayListener implements TickListener {
        public void tick(long serverTime) {
        }

        public void simulationTick(long serverTime, int simulationDate) {
            UserTabPanel.this.nextTimeUnit(serverTime, simulationDate);
        }
    }
}
