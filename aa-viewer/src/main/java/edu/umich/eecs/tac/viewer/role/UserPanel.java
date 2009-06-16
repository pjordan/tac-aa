package edu.umich.eecs.tac.viewer.role;

import static edu.umich.eecs.tac.TACAAConstants.*;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import static edu.umich.eecs.tac.viewer.ViewerChartFactory.createDaySeriesChartWithColors;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.tasim.viewer.TickListener;

import javax.swing.*;
import java.awt.*;

/**
 * @author Patrick Jordan
 */
public class UserPanel extends SimulationTabPanel {
    private XYSeries nsTimeSeries;
    private XYSeries isTimeSeries;
    private XYSeries f0TimeSeries;
    private XYSeries f1TimeSeries;
    private XYSeries f2TimeSeries;
    private XYSeries tTimeSeries;

    private XYSeriesCollection seriescollection;
    private int currentDay;

    public UserPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        setBorder(BorderFactory.createTitledBorder("User State Distribution"));

        currentDay = 0;

        initializeView();

        simulationPanel.addTickListener(new DayListener());
        simulationPanel.addViewListener(new UserSearchStateListener());
    }

    protected void initializeView() {
        createDataset();
        setLayout(new BorderLayout());
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        JFreeChart jfreechart = createDaySeriesChartWithColors(null, "Users per state", seriescollection, true);
        ChartPanel chartpanel = new ChartPanel(jfreechart, false);
        chartpanel.setMouseZoomable(true, false);

        add(chartpanel, BorderLayout.CENTER);
    }

    protected void nextTimeUnit(long serverTime, int timeUnit) {
        currentDay = timeUnit;
    }

    private class UserSearchStateListener extends ViewAdaptor {

        public void dataUpdated(final int agent, final int type, final int value) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
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
            });

        }
    }

    private void createDataset() {
        nsTimeSeries = new XYSeries("NS");
        isTimeSeries = new XYSeries("IS");
        f0TimeSeries = new XYSeries("F0");
        f1TimeSeries = new XYSeries("F1");
        f2TimeSeries = new XYSeries("F2");
        tTimeSeries = new XYSeries("T");

        seriescollection = new XYSeriesCollection();
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
            UserPanel.this.nextTimeUnit(serverTime, simulationDate);
        }
    }
}
