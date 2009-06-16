package edu.umich.eecs.tac.viewer;

import edu.umich.eecs.tac.props.Query;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

/**
 * @author Patrick Jordan
 */
public class ViewerChartFactory {

    private ViewerChartFactory() {
    }

    public static JFreeChart createChart(XYDataset xydataset, String title,
                                         String xLabel, String yLabel, Color legendColor) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart(title, xLabel, yLabel, xydataset,
                PlotOrientation.VERTICAL, false, false, false);

        jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

        formatPlotWithColor((XYPlot) jfreechart.getPlot(), legendColor);

        return jfreechart;
    }

    public static JFreeChart createCapacityChart(XYDataset xydataset, String title, Color legendColor) {
        return createChart(xydataset, title, "Day", "% Capacity Used", legendColor);
    }

    public static JFreeChart createCapacityChart(XYDataset xydataset, Color legendColor) {
        return createCapacityChart(xydataset, null, legendColor);
    }

    public static JFreeChart createDaySeriesChartWithColor(String s, XYDataset xydataset, Color legendColor) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart(s, "Day", "", xydataset,
                PlotOrientation.VERTICAL, false, false, false);

        jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

        formatPlotWithColor((XYPlot) jfreechart.getPlot(), legendColor);

        return jfreechart;
    }

    public static JFreeChart createDaySeriesChartWithColors(String s, XYDataset xydataset, boolean legend) {
        return createDaySeriesChartWithColors(s,"",xydataset, legend);
    }

    public static JFreeChart createDifferenceChart(XYDataset xydataset) {
        return createDifferenceChart(null, xydataset);
    }

    public static JFreeChart createDifferenceChart(String title, XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart(title, "Day", "$", xydataset,
                PlotOrientation.VERTICAL, false, false, false);
        jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        formatPlot(xyplot);

        XYDifferenceRenderer renderer = new XYDifferenceRenderer(Color.green, Color.red, false);


        renderer.setSeriesPaint(0, ChartColor.DARK_GREEN);
        renderer.setSeriesPaint(1, ChartColor.DARK_RED);
        renderer.setBaseStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        xyplot.setRenderer(renderer);

        return jfreechart;
    }

    public static JFreeChart createAuctionChart(Query query, XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart(String.format(
                "Auction for (%s,%s)", query.getManufacturer(), query
                        .getComponent()), "Day", "Bid [$]", xydataset,
                PlotOrientation.VERTICAL, false, true, false);
        jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();

        formatPlot(xyplot);
        formatRendererWithColors(xyplot);

        return jfreechart;
    }

    public static JFreeChart createDaySeriesChartWithColors(String s, String yAxisLabel, XYDataset xydataset,
                                                         boolean legend) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart(s, "Day", yAxisLabel, xydataset,
                PlotOrientation.VERTICAL, true, false, false);
        jfreechart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();

        formatPlotWithColors(xyplot);

        if (legend) {
            LegendTitle legendTitle = jfreechart.getLegend();
            legendTitle.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
            legendTitle.setFrame(BlockBorder.NONE);
        }

        return jfreechart;
    }

    private static void formatPlot(XYPlot xyplot) {
        xyplot.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);

        xyplot.setDomainGridlinePaint(Color.GRAY);

        xyplot.setRangeGridlinePaint(Color.GRAY);

        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

        xyplot.setOutlineVisible(false);
    }

    private static void formatPlotWithColor(XYPlot xyplot, Color legendColor) {
        formatPlot(xyplot);

        formatRendererWithColor(xyplot, legendColor);
    }

    private static void formatPlotWithColors(XYPlot xyplot) {
        formatPlot(xyplot);
        formatRendererWithColors(xyplot);
    }


    private static void formatRendererWithColor(XYPlot xyplot, Color legendColor) {
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();

        xyitemrenderer.setBaseStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));


        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;

            xylineandshaperenderer.setBaseShapesVisible(false);

            xylineandshaperenderer.setSeriesPaint(0, legendColor);
        }
    }

    private static void formatRendererWithColors(XYPlot xyplot) {
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();

        xyitemrenderer.setBaseStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));


        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;

            xylineandshaperenderer.setBaseShapesVisible(false);

            for (int i = 0; i < TACAAViewerConstants.LEGEND_COLORS.length; i++) {
                xylineandshaperenderer.setSeriesPaint(i, TACAAViewerConstants.LEGEND_COLORS[i]);
            }
        }
    }
}
