package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import static edu.umich.eecs.tac.viewer.ViewerChartFactory.createDaySeriesChartWithColor;
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
public class AdvertiserCountPanel extends JPanel {
    private int agent;
    private String advertiser;

    private int currentDay;
    private XYSeries impressions;
    private XYSeries clicks;
    private XYSeries conversions;
    private boolean advertiserBorder;
    private Color legendColor;


    public AdvertiserCountPanel(int agent, String advertiser,
                                TACAASimulationPanel simulationPanel, boolean advertiserBorder, Color legendColor) {
        this.agent = agent;
        this.advertiser = advertiser;
        this.advertiserBorder = advertiserBorder;
        this.legendColor = legendColor;
        initialize();

        currentDay = 0;
        simulationPanel.addViewListener(new DataUpdateListener());
        simulationPanel.addTickListener(new DayListener());
    }

    private void initialize() {
        setLayout(new GridLayout(3, 1));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

        add(new ChartPanel(createImpressionsChart()));
        add(new ChartPanel(createClicksChart()));
        add(new ChartPanel(createConversionsChart()));

        if (advertiserBorder)
            setBorder(BorderFactory.createTitledBorder(advertiser));
        else
            setBorder(BorderFactory.createTitledBorder("Impressions, Clicks and Conversions"));
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

        public void dataUpdated(final int agent, final int type, final int value) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
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
            });

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
