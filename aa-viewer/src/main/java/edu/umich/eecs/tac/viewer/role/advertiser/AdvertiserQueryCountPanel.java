package edu.umich.eecs.tac.viewer.role.advertiser;

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

import javax.swing.*;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;

import java.awt.*;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

/**
 * @author Patrick Jordan
 */
public class AdvertiserQueryCountPanel extends JPanel {
    private int agent;
	private String advertiser;
    private Query query;

	private int currentDay;
	private XYSeries impressions;
	private XYSeries clicks;
	private XYSeries conversions;
    private Color legendColor;

	public AdvertiserQueryCountPanel(int agent, String advertiser, Query query,
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
		setLayout(new GridLayout(3, 1));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

		add(createChartPanel(createImpressionsChart()));
		add(createChartPanel(createClicksChart()));
		add(createChartPanel(createConversionsChart()));

		setBorder(BorderFactory.createTitledBorder("Raw Metrics"));
	}

    private ChartPanel createChartPanel(JFreeChart jFreeChart) {
        ChartPanel panel = new ChartPanel(jFreeChart);
        return panel;
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
		JFreeChart jfreechart = ChartFactory.createXYLineChart(s, "Day", "", xydataset, PlotOrientation.VERTICAL,
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

	protected void addImpressions(int impressions) {
		this.impressions.addOrUpdate(currentDay, impressions);
	}

	protected void addClicks(int clicks) {
		this.clicks.addOrUpdate(currentDay, clicks);
	}

	protected void addConversions(int conversions) {
		this.conversions.addOrUpdate(currentDay, conversions);
	}

	private class DataUpdateListener implements ViewListener {

		public void dataUpdated(int agent, int type, int value) {

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
            if (agent == AdvertiserQueryCountPanel.this.agent) {
				switch (type) {
				case TACAAConstants.DU_QUERY_REPORT:
                    handleQueryReport((QueryReport)value);
					break;
				case TACAAConstants.DU_SALES_REPORT:
					handleSalesReport((SalesReport)value);
					break;
				}
			}
		}

        private void handleQueryReport(QueryReport queryReport) {
            addImpressions(queryReport.getImpressions(query));
            addClicks(queryReport.getClicks(query));
        }

        private void handleSalesReport(SalesReport salesReport) {
            addConversions(salesReport.getConversions(query));
        }

        public void dataUpdated(int type, Transportable value) {
		}

		public void participant(int agent, int role, String name,
				int participantID) {
		}
	}

	protected class DayListener implements TickListener {

		public void tick(long serverTime) {
			AdvertiserQueryCountPanel.this.tick(serverTime);
		}

		public void simulationTick(long serverTime, int simulationDate) {
			AdvertiserQueryCountPanel.this.simulationTick(serverTime, simulationDate);
		}
	}

	protected void tick(long serverTime) {
	}

	protected void simulationTick(long serverTime, int simulationDate) {
		currentDay = simulationDate;
	}
}
