package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.*;

import javax.swing.*;

import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartColor;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;
import se.sics.tasim.viewer.TickListener;
import se.sics.isl.transport.Transportable;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 9, 2009
 * Time: 1:06:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentRevCostPanel extends JPanel {
    private int agent;
	private String advertiser;
    private XYSeriesCollection seriescollection;
    private XYSeries revSeries;
    private XYSeries costSeries;
    private int currentDay;
    private Map<Query, Query> queries;
    private Boolean showLegend;

    public AgentRevCostPanel(int agent, String advertiser,
			TACAASimulationPanel simulationPanel, Boolean showLegend){

         setBackground(TACAAViewerConstants.CHART_BACKGROUND);
         revSeries = new XYSeries("Revenue");
         costSeries = new XYSeries("Cost");
         seriescollection = new XYSeriesCollection();

         this.showLegend = showLegend;
         this.agent = agent;
         this.advertiser = advertiser;
         simulationPanel.addTickListener(new DayListener());
		 simulationPanel.addViewListener(new DataUpdateListener());
         initialize();
    }

    private void initialize(){
        setLayout(new GridLayout(1,1));
        queries = new HashMap<Query, Query >();
        seriescollection.addSeries(revSeries);
        seriescollection.addSeries(costSeries);
          
		JFreeChart chart = createChart(seriescollection);
		ChartPanel chartpanel = new ChartPanel(chart, false);
		chartpanel.setMouseZoomable(true, false);
		add(chartpanel);
    }

    private JFreeChart createChart(XYDataset xydataset) {
        JFreeChart jfreechart;
        if(!showLegend){
		  jfreechart = ChartFactory.createXYLineChart(
				  advertiser, "Day", "$", xydataset,
				  PlotOrientation.VERTICAL, false, true, false);
        }
        else{
          jfreechart = ChartFactory.createXYLineChart(
				  null, "Day", "$", xydataset,
				  PlotOrientation.VERTICAL, true, true, false);
          setBorder(BorderFactory.createTitledBorder("Revenue and Cost"));
        }
		jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
		xyplot.setDomainGridlinePaint(Color.GRAY);
		xyplot.setRangeGridlinePaint(Color.GRAY);

        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

	    XYDifferenceRenderer renderer = new XYDifferenceRenderer(
            Color.green, Color.red, false
        );

        xyplot.setOutlineVisible(false);

        renderer.setSeriesPaint(0, ChartColor.DARK_GREEN);
        renderer.setSeriesPaint(1, ChartColor.DARK_RED);
        renderer.setBaseStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT,
				               BasicStroke.JOIN_BEVEL));
        xyplot.setRenderer(renderer);

		return jfreechart;
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

	protected class DayListener implements TickListener {

		public void tick(long serverTime) {
			AgentRevCostPanel.this.tick(serverTime);
		}

		public void simulationTick(long serverTime, int simulationDate) {
			AgentRevCostPanel.this.simulationTick(serverTime, simulationDate);
		}
	}
    
    protected void tick(long serverTime) {
	}

	protected void simulationTick(long serverTime, int simulationDate) {
		currentDay = simulationDate;
	}

    private double getDayCost(QueryReport report){
        Iterator iterator = queries.keySet().iterator();
        double result = 0;
        while(iterator.hasNext()){
            result = result  + report.getCost((Query)iterator.next());

        }
        return result;

    }
    private double getDayRevenue(SalesReport report){
        Iterator iterator = queries.keySet().iterator();
        double result = 0;
        while(iterator.hasNext()){
            result = result  + report.getRevenue((Query)iterator.next());

        }
        return result;
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
             if(type == TACAAConstants.DU_QUERY_REPORT &&
                value.getClass().equals(QueryReport.class) &&
                agent == AgentRevCostPanel.this.agent){

                QueryReport queryReport = (QueryReport) value;

                costSeries.addOrUpdate(currentDay, AgentRevCostPanel.this.getDayCost(queryReport));
             }

             if(type == TACAAConstants.DU_SALES_REPORT &&
                value.getClass().equals(SalesReport.class) &&
                agent == AgentRevCostPanel.this.agent){

                SalesReport salesReport = (SalesReport) value;
       
                revSeries.addOrUpdate(currentDay, AgentRevCostPanel.this.getDayRevenue(salesReport));
             }
 		}

		public void dataUpdated(int type, Transportable value) {
            Class valueType = value.getClass();
			if (valueType == RetailCatalog.class) {
				handleRetailCatalog((RetailCatalog) value);
			}
   		}

		public void participant(int agent, int role, String name, int participantID) {
		}
	}

}
