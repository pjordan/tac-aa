package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;
import static edu.umich.eecs.tac.TACAAConstants.*;

import javax.swing.*;

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
		JFreeChart jfreechart = createChart(seriescollection);
		ChartPanel chartpanel = new ChartPanel(jfreechart, false);
		chartpanel.setMouseZoomable(true, false);

		add(chartpanel, BorderLayout.CENTER);
	}

	protected void nextTimeUnit(long serverTime, int timeUnit) {
		currentDay = timeUnit;
	}

	private class UserSearchStateListener extends ViewAdaptor {

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
	}

	private JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createXYLineChart(
				null, "Day", "Users per state", xydataset,
				PlotOrientation.VERTICAL, true, true, false);
		jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

		XYPlot xyplot = (XYPlot) jfreechart.getPlot();

		xyplot.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

		xyplot.setDomainGridlinePaint(Color.GRAY);
        
		xyplot.setRangeGridlinePaint(Color.GRAY);

		xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

		//xyplot.setDomainCrosshairVisible(true);

		//xyplot.setRangeCrosshairVisible(true);

        LegendTitle legendTitle = jfreechart.getLegend();
        legendTitle.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        legendTitle.setFrame(BlockBorder.NONE);

        xyplot.setOutlineVisible(false);
        
		org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot
				.getRenderer();

		xyitemrenderer.setBaseStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));

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
		// seriescollection.addSeries(nsTimeSeries);
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
