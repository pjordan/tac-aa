/**
 * Created by IntelliJ IDEA.
 * User: pjordan
 * Date: Jun 12, 2009
 * Time: 12:40:24 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.umich.eecs.tac.viewer;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

public class ViewerChartFactory {

    private ViewerChartFactory() {
    }

    public static JFreeChart createChart(XYDataset xydataset, String title,
                                         String xLabel, String yLabel, Color legendColor) {
		JFreeChart jfreechart = ChartFactory.createXYLineChart(title, xLabel, yLabel, xydataset,
				                                               PlotOrientation.VERTICAL, false, true, false);

		jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

        formatPlot((XYPlot) jfreechart.getPlot(), legendColor);
        
		return jfreechart;
	}

    public static JFreeChart createCapacityChart(XYDataset xydataset, String title, Color legendColor) {
		return createChart(xydataset, title, "Day", "% Capacity Used", legendColor);
	}

    public static JFreeChart createCapacityChart(XYDataset xydataset, Color legendColor) {
		return createCapacityChart(xydataset, null, legendColor);
	}

    public static JFreeChart createRawMetricsChart(String s, XYDataset xydataset, Color legendColor) {
		JFreeChart jfreechart = ChartFactory.createXYLineChart(s, "Day", "", xydataset,
                                                               PlotOrientation.VERTICAL, false, true, false);

        jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

		formatPlot((XYPlot) jfreechart.getPlot(), legendColor);

		return jfreechart;
	}

    private static void formatPlot(XYPlot xyplot, Color legendColor) {
        xyplot.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

        xyplot.setDomainGridlinePaint(Color.GRAY);

        xyplot.setRangeGridlinePaint(Color.GRAY);

        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

		XYItemRenderer xyitemrenderer = xyplot.getRenderer();

        xyitemrenderer.setBaseStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        xyplot.setOutlineVisible(false);

		if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;

            xylineandshaperenderer.setBaseShapesVisible(false);

            xylineandshaperenderer.setSeriesPaint(0, legendColor);
		}
    }
}
