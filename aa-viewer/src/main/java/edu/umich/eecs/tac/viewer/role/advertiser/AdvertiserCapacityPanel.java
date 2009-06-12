package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
import se.sics.tasim.viewer.TickListener;
import se.sics.isl.transport.Transportable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 11, 2009
 * Time: 5:15:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdvertiserCapacityPanel extends SimulationTabPanel {
    private int agent;
    private String advertiser;
    private int currentDay;
	private XYSeries relativeCapacity;
    private int capacity;
    private int window;
    private Map<Integer, Integer> amountsSold;
    private Map<Query, Query> queries;
    private Color legendColor;

    public AdvertiserCapacityPanel(int agent, String advertiser, TACAASimulationPanel simulationPanel,
                                   Color legendColor){
        super(simulationPanel);
        this.agent = agent;
        this.advertiser = advertiser;
        this.legendColor = legendColor;
        currentDay = 0;

        simulationPanel.addViewListener(new DataUpdateListener());
		simulationPanel.addTickListener(new DayListener());
        initialize();
    }
    protected void initialize(){
        setLayout(new GridLayout(1, 1));
        setBorder(BorderFactory.createTitledBorder(" Capacity Used"));

        amountsSold = new HashMap<Integer, Integer>();
        queries = new HashMap<Query, Query >();
        relativeCapacity = new XYSeries("Relative Capacity");
	    XYSeriesCollection seriescollection = new XYSeriesCollection();


        seriescollection.addSeries(relativeCapacity);
		JFreeChart chart = createChart(seriescollection);
		ChartPanel chartpanel = new ChartPanel(chart, false);
		chartpanel.setMouseZoomable(true, false);

		add(chartpanel);
    }

   private JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createXYLineChart(
				null, "Day", "% Capacity Used", xydataset,
				PlotOrientation.VERTICAL, false, true, false);
		jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
		xyplot.setDomainGridlinePaint(Color.GRAY);
		xyplot.setRangeGridlinePaint(Color.GRAY);
		xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

		XYItemRenderer xyitemrenderer = xyplot.getRenderer();

		xyitemrenderer.setBaseStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));

        xyplot.setOutlineVisible(false);

		if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
			xylineandshaperenderer.setBaseShapesVisible(false);
            xylineandshaperenderer.setSeriesPaint(0, legendColor);
		}
		return jfreechart;
	}

    protected class DayListener implements TickListener {

		public void tick(long serverTime) {
			AdvertiserCapacityPanel.this.tick(serverTime);
		}

		public void simulationTick(long serverTime, int simulationDate) {
			AdvertiserCapacityPanel.this
					.simulationTick(serverTime, simulationDate);
		}
	}

	protected void tick(long serverTime) {
	}

	protected void simulationTick(long serverTime, int simulationDate) {
		currentDay = simulationDate;
	}

     private void handleRetailCatalog(RetailCatalog retailCatalog){
		queries.clear();

		for (Product product : retailCatalog) {
			// Create f0
			Query f0 = new Query();

			// Create f1's
			Query f1_manufacturer = new Query(product.getManufacturer(), null);
			Query f1_component = new Query(null, product.getComponent());

			// Create f2
			Query f2 = new Query(product.getManufacturer(), product
					.getComponent());

			if (!queries.containsKey(f0)) {
				queries.put(f0, f0);
			}
			if (!queries.containsKey(f1_manufacturer)) {
				queries.put(f1_manufacturer, f1_manufacturer);
			}
			if (!queries.containsKey(f1_component)) {
			    queries.put(f1_component, f1_component);
			}
			if (!queries.containsKey(f2)) {
				queries.put(f2, f2);
			}
		}
    }

    private int getAmountSold(SalesReport report){
        Iterator iterator = queries.keySet().iterator();
        int result = 0;
        while(iterator.hasNext()){
            result = result  + report.getConversions((Query)iterator.next());
        }
        return result;
    }

    private void updateChart(){
        double soldInWindow = 0;
        for(int i = currentDay - window; i < currentDay; i++){
            if(!(amountsSold.get(i)==null || Double.isNaN(amountsSold.get(i)))) {

            soldInWindow = soldInWindow + amountsSold.get(i);
            }
        }
        relativeCapacity.addOrUpdate(currentDay, (soldInWindow/capacity) * 100);
        //relativeCapacity.addOrUpdate(currentDay, window);
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
            if(AdvertiserCapacityPanel.this.agent == agent
               && type ==  TACAAConstants.DU_ADVERTISER_INFO &&
               value.getClass() == AdvertiserInfo.class){
                capacity = ((AdvertiserInfo)value).getDistributionCapacity();
                window = ((AdvertiserInfo)value).getDistributionWindow();
            }

            if(AdvertiserCapacityPanel.this.agent == agent &&
               type == TACAAConstants.DU_SALES_REPORT &&
               value.getClass() == SalesReport.class){
                int sold = getAmountSold((SalesReport)value);
                amountsSold.put(currentDay - 1, sold);
                updateChart();
            }
		}

		public void dataUpdated(int type, Transportable value) {
            Class valueType = value.getClass();
			if (valueType == RetailCatalog.class) {
				handleRetailCatalog((RetailCatalog) value);
			}
		}

		public void participant(int agent, int role, String name,
				int participantID) {
		}
	}



}
