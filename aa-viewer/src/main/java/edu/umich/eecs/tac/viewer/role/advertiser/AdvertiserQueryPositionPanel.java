package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
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
        return new ChartPanel(jFreeChart);
    }

    private JFreeChart createPositionChart() {
        position = new XYSeries("Position");
        return createDaySeriesChartWithColor(null, new XYSeriesCollection(position), legendColor);
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

        public void dataUpdated(final int agent, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (agent == AdvertiserQueryPositionPanel.this.agent) {
                        switch (type) {
                            case TACAAConstants.DU_QUERY_REPORT:
                                handleQueryReport((QueryReport) value);
                                break;
                        }
                    }
                }
            });
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
