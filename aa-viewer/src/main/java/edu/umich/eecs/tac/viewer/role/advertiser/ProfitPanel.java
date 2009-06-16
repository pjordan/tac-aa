package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import static edu.umich.eecs.tac.viewer.ViewerChartFactory.createDaySeriesChartWithColor;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.tasim.viewer.TickListener;

import javax.swing.*;
import java.awt.*;

/**
 * @author Guha Balakrishnan
 */
public class ProfitPanel extends SimulationTabPanel {

    private int currentDay;
    private String advertiser;
    private int agent;
    private XYSeries series;
    private Color legendColor;

    public ProfitPanel(TACAASimulationPanel simulationPanel, int agent,
                       String advertiser, Color legendColor) {
        super(simulationPanel);

        this.agent = agent;
        this.advertiser = advertiser;
        currentDay = 0;
        this.legendColor = legendColor;


        simulationPanel.addTickListener(new DayListener());
        simulationPanel.addViewListener(new BankStatusListener());
        initialize();
    }

    protected void initialize() {
        setLayout(new GridLayout(1, 1));
        setBorder(BorderFactory.createTitledBorder("Advertiser Profit"));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

        XYSeriesCollection seriescollection = new XYSeriesCollection();
        series = new XYSeries(advertiser);
        seriescollection.addSeries(series);

        JFreeChart chart = createDaySeriesChartWithColor(null, seriescollection, legendColor);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);

        add(chartpanel);
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
    }

    protected class DayListener implements TickListener {

        public void tick(long serverTime) {
            ProfitPanel.this.tick(serverTime);
        }

        public void simulationTick(long serverTime, int simulationDate) {
            ProfitPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

    protected class BankStatusListener extends ViewAdaptor {

        public void dataUpdated(final int agent, final int type, final double value) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (type == TACAAConstants.DU_BANK_ACCOUNT && agent == ProfitPanel.this.agent) {
                        series.addOrUpdate(currentDay, value);
                    }
                }
            });

        }
    }
}