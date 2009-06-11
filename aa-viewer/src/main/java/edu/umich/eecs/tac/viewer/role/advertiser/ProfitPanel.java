package edu.umich.eecs.tac.viewer.role.advertiser;

import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.AgentSupport;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.RetailCatalog;

import javax.swing.*;

import com.botbox.util.ArrayUtils;
import se.sics.tasim.viewer.TickListener;
import se.sics.isl.transport.Transportable;

/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 11, 2009
 * Time: 1:04:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProfitPanel extends SimulationTabPanel {

    private XYSeriesCollection seriescollection;
    private TACAASimulationPanel simulationPanel;
	private int currentDay;
    private String advertiser;
    private int agent;
    private XYSeries series;
    private Color legendColor;

	public ProfitPanel(TACAASimulationPanel simulationPanel, int agent,
                       String advertiser, Color legendColor) {
		super(simulationPanel);

        this.simulationPanel = simulationPanel;
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
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
		seriescollection = new XYSeriesCollection();
        series = new XYSeries(advertiser);
        seriescollection.addSeries(series);

		JFreeChart chart = createChart(seriescollection);
		ChartPanel chartpanel = new ChartPanel(chart, false);
		chartpanel.setMouseZoomable(true, false);

        setBorder(BorderFactory.createTitledBorder("Advertiser Profit"));
		add(chartpanel);
	}

	private JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createXYLineChart(
			    null, "Day", "$", xydataset,
				PlotOrientation.VERTICAL, false, true, false);


        jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        xyplot.setDomainGridlinePaint(Color.gray);
        xyplot.setRangeGridlinePaint(Color.gray);
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setRangeCrosshairVisible(true);


		XYItemRenderer xyitemrenderer = xyplot.getRenderer();
		xyitemrenderer.setBaseStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));
        xyplot.setOutlineVisible(false);

		if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
			xylineandshaperenderer.setBaseShapesVisible(false);

            xylineandshaperenderer.setSeriesPaint(0,legendColor);

		}
		return jfreechart;
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

	protected class BankStatusListener implements ViewListener {

		public void dataUpdated(int agent, int type, int value) {

		}

		public void dataUpdated(int agent, int type, long value) {

		}

		public void dataUpdated(int agent, int type, float value) {

		}

		public void dataUpdated(int agent, int type, double value) {
            if (type == TACAAConstants.DU_BANK_ACCOUNT && agent == ProfitPanel.this.agent){
                series.addOrUpdate(currentDay, value);
            }

		}


		public void dataUpdated(int agent, int type, String value) {

		}

		public void dataUpdated(int agent, int type, Transportable value) {

		}

		public void dataUpdated(int type, Transportable value) {

		}

		public void participant(int agent, int role, String name,
				int participantID){

		}
	}
}