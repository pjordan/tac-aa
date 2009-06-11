


package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.viewer.role.PublisherTabPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.ui.RectangleInsets;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

/**
 * @author Patrick Jordan
 */
public class SeriesPanel extends JComponent{
	private Query query;

	private XYSeriesCollection seriescollection;
	private int currentDay;
	private Map<String, XYSeries> bidSeries;
    private SeriesTabPanel seriesTabPanel;
    private JFreeChart chart;
    private Color[] legendColors;

	public SeriesPanel(Query query, SeriesTabPanel seriesTabPanel, Color [] legendColors) {
		this.query = query;
		this.bidSeries = new HashMap<String, XYSeries>();
        this.seriesTabPanel = seriesTabPanel;
		this.currentDay = 0;
        this.legendColors = legendColors;

		initialize();

		seriesTabPanel.getSimulationPanel().addViewListener(new BidBundleListener());
		seriesTabPanel.getSimulationPanel().addTickListener(new DayListener());
	}

	protected void initialize() {
		setLayout(new GridLayout(1, 1));


		seriescollection = new XYSeriesCollection();

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

		chart = createChart(seriescollection);
		ChartPanel chartpanel = new ChartPanel(chart, false);
		chartpanel.setMouseZoomable(true, false);
		add(chartpanel);
	}

	private JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createXYLineChart(String.format(
				"Auction for (%s,%s)", getQuery().getManufacturer(), getQuery()
						.getComponent()), "Day", "Bid [$]", xydataset,
				PlotOrientation.VERTICAL, false, true, false);
		jfreechart.setBackgroundPaint(Color.white);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
		xyplot.setDomainCrosshairVisible(true);
		xyplot.setRangeCrosshairVisible(true);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        renderer.setBaseStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));
        for(int i = 0; i < legendColors.length; i++)
          renderer.setSeriesPaint(i, legendColors[i]);

		return jfreechart;
	}

    public XYLineAndShapeRenderer getRenderer(){
        return (XYLineAndShapeRenderer)((XYPlot)chart.getPlot()).getRenderer();
    }
    public Query getQuery() {
		return query;
	}

	private class BidBundleListener implements ViewListener {

		public void dataUpdated(int agent, int type, int value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, long value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, float value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, double value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, String value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, Transportable value) {
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
							timeSeries.addOrUpdate(currentDay, bid);
						}
					}
				}
			}
		}

		public void dataUpdated(int type, Transportable value) {

		}

		public void participant(int agent, int role, String name,
				int participantID) {

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