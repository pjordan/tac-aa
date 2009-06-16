package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import static edu.umich.eecs.tac.viewer.ViewerChartFactory.createAuctionChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick Jordan
 */
public class SeriesPanel extends JComponent {
    private Query query;

    private int currentDay;
    private Map<String, XYSeries> bidSeries;
    private SeriesTabPanel seriesTabPanel;
    private JFreeChart chart;

    public SeriesPanel(Query query, SeriesTabPanel seriesTabPanel) {
        this.query = query;
        this.bidSeries = new HashMap<String, XYSeries>();
        this.seriesTabPanel = seriesTabPanel;
        this.currentDay = 0;

        initialize();

        seriesTabPanel.getSimulationPanel().addViewListener(new BidBundleListener());
        seriesTabPanel.getSimulationPanel().addTickListener(new DayListener());
    }

    protected void initialize() {
        setLayout(new GridLayout(1, 1));


        XYSeriesCollection seriescollection = new XYSeriesCollection();

        // Participants will be added to the publisher panel before getting
        // here.
        int count = seriesTabPanel.getAgentCount();

        for (int index = 0; index < count; index++) {
            if (seriesTabPanel.getRole(index) == TACAAConstants.ADVERTISER) {
                XYSeries series = new XYSeries(seriesTabPanel
                        .getAgentName(index));

                bidSeries.put(seriesTabPanel.getAgentName(index), series);
                seriescollection.addSeries(series);
            }
        }

        chart = createAuctionChart(getQuery(), seriescollection);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);
        add(chartpanel);
    }    

    public XYLineAndShapeRenderer getRenderer() {
        return (XYLineAndShapeRenderer) ((XYPlot) chart.getPlot()).getRenderer();
    }

    public Query getQuery() {
        return query;
    }

    private class BidBundleListener extends ViewAdaptor {

        public void dataUpdated(final int agent, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (type == TACAAConstants.DU_BIDS
                            && value.getClass().equals(BidBundle.class)) {
                        int index = seriesTabPanel.indexOfAgent(agent);
                        String name = index < 0 ? null : seriesTabPanel
                                .getAgentName(index);

                        if (name != null) {
                            XYSeries timeSeries = bidSeries.get(name);

                            if (timeSeries != null) {

                                BidBundle bundle = (BidBundle) value;

                                double bid = bundle.getBid(query);
                                if (!Double.isNaN(bid)) {
                                    timeSeries.addOrUpdate(currentDay - 1, bid);
                                }
                            }
                        }
                    }
                }
            });

        }
    }

    protected class DayListener implements TickListener {

        public void tick(long serverTime) {
            SeriesPanel.this.tick(serverTime);
        }

        public void simulationTick(long serverTime, int simulationDate) {
            SeriesPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
    }
}