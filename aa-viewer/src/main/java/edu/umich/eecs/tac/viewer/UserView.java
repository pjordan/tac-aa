package edu.umich.eecs.tac.viewer;

import se.sics.isl.transport.Transportable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.Day;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;
import edu.umich.eecs.tac.TACAAConstants;

/**
 * @author Patrick Jordan
 */
public class UserView extends TACAAAgentView implements TACAAConstants {
    private TimeSeries nsTimeSeries;
    private TimeSeries isTimeSeries;
    private TimeSeries f0TimeSeries;
    private TimeSeries f1TimeSeries;
    private TimeSeries f2TimeSeries;
    private TimeSeries tTimeSeries;

    private TimeSeriesCollection timeseriescollection;
    private Day currentDay;


    public UserView() {
        currentDay = new Day();
    }

    protected void initializeView() {
        createDataset();
        setLayout(new BorderLayout());
        add(new JButton(new ViewAction()), BorderLayout.CENTER);
    }

    protected void nextTimeUnit(long serverTime, int timeUnit) {
        super.nextTimeUnit(serverTime,timeUnit);

        currentDay = (Day)currentDay.next();
    }

    public void dataUpdated(int type, int value) {

        switch(type) {
            case DU_NON_SEARCHING:
                nsTimeSeries.add(currentDay, value);
                break;
            case DU_INFORMATIONAL_SEARCH:
                isTimeSeries.add(currentDay, value);
                break;
            case DU_FOCUS_LEVEL_ZERO:
                f0TimeSeries.add(currentDay, value);
                break;
            case DU_FOCUS_LEVEL_ONE:
                f1TimeSeries.add(currentDay, value);
                break;
            case DU_FOCUS_LEVEL_TWO:
                f2TimeSeries.add(currentDay, value);
                break;
            case DU_TRANSACTED:
                tTimeSeries.add(currentDay, value);
                break;
            default:
                break;
        }
    }

    public void dataUpdated(int agent, int type, int value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dataUpdated(int agent, int type, long value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dataUpdated(int agent, int type, float value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dataUpdated(int agent, int type, double value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dataUpdated(int agent, int type, String value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dataUpdated(int agent, int type, Transportable value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void participant(int agent, int role, String name, int participantID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dataUpdated(int type, Transportable value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected class ViewAction extends AbstractAction {

        public ViewAction() {
            super("View users");
        }

        public void actionPerformed(ActionEvent e) {
            JDialog dialog = new JDialog();
            dialog.setTitle("Viewing the user");

            JFreeChart jfreechart = createChart(timeseriescollection);
            ChartPanel chartpanel = new ChartPanel(jfreechart, false);
            chartpanel.setPreferredSize(new Dimension(500, 270));
            chartpanel.setMouseZoomable(true, false);
            dialog.setContentPane(chartpanel);

            dialog.pack();
            dialog.setVisible(true);
        }
    }

    private static JFreeChart createChart(XYDataset xydataset)
        {
            JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("User state distribution", "Day", "Users per state", xydataset, true, true, false);
            jfreechart.setBackgroundPaint(Color.white);
            XYPlot xyplot = (XYPlot)jfreechart.getPlot();
            xyplot.setBackgroundPaint(Color.lightGray);
            xyplot.setDomainGridlinePaint(Color.white);
            xyplot.setRangeGridlinePaint(Color.white);
            xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
            xyplot.setDomainCrosshairVisible(true);
            xyplot.setRangeCrosshairVisible(true);
            org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot.getRenderer();
            if(xyitemrenderer instanceof XYLineAndShapeRenderer)
            {
                XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyitemrenderer;
                xylineandshaperenderer.setBaseShapesVisible(false);
            }
            //DateAxis dateaxis = (DateAxis)xyplot.getDomainAxis();
            //dateaxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
            return jfreechart;
        }

        private void createDataset()
        {
            nsTimeSeries = new TimeSeries("NS", org.jfree.data.time.Day.class);
            isTimeSeries = new TimeSeries("IS", org.jfree.data.time.Day.class);
            f0TimeSeries = new TimeSeries("F0", org.jfree.data.time.Day.class);
            f1TimeSeries = new TimeSeries("F1", org.jfree.data.time.Day.class);
            f2TimeSeries = new TimeSeries("F2", org.jfree.data.time.Day.class);
            tTimeSeries = new TimeSeries("T", org.jfree.data.time.Day.class);

            timeseriescollection = new TimeSeriesCollection();
            timeseriescollection.addSeries(nsTimeSeries);
            timeseriescollection.addSeries(isTimeSeries);
            timeseriescollection.addSeries(f0TimeSeries);
            timeseriescollection.addSeries(f1TimeSeries);
            timeseriescollection.addSeries(f2TimeSeries);
            timeseriescollection.addSeries(tTimeSeries);
        }

}
