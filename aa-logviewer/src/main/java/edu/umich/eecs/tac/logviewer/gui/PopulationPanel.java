package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.gui.advertiser.AdvertiserWindow;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.UserPopulationState;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 11, 2009
 * Time: 8:44:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class PopulationPanel {
  JPanel mainPane;
  PopulationWindow populationWindow;

  GameInfo gameInfo;

  private static final int numQueryStates = 6;
  private XYSeries nsTimeSeries;
	private XYSeries isTimeSeries;
	private XYSeries f0TimeSeries;
	private XYSeries f1TimeSeries;
	private XYSeries f2TimeSeries;
	private XYSeries tTimeSeries;

	private XYSeriesCollection seriescollection;

  public PopulationPanel(GameInfo gameInfo){
    this.gameInfo = gameInfo;
    mainPane = new JPanel();
    mainPane.setLayout(new BorderLayout());
    mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder()," Total User Population "));
    //mainPane.setMinimumSize(new Dimension(280,200));
	  //mainPane.setPreferredSize(new Dimension(280,200));

    mainPane.addMouseListener(new MouseInputAdapter() {
	      public void mouseClicked(MouseEvent me) {
		      openPopulationWindow();
	      }
	  });

    createDataset();
    applyData();
    JFreeChart jfreechart = createChart(seriescollection);
    ChartPanel chartpanel = new ChartPanel(jfreechart, false);
		chartpanel.setMinimumSize(new Dimension(500, 270));
    chartpanel.setPreferredSize(new Dimension(500, 270));
		chartpanel.setMouseZoomable(true, false);
    mainPane.add(chartpanel, BorderLayout.CENTER);
  }

  private void openPopulationWindow(){
    if(populationWindow == null) {
	      populationWindow = new PopulationWindow(gameInfo);
	      populationWindow.setLocationRelativeTo(mainPane);
	      populationWindow.setVisible(true);
	    } else if (populationWindow.isVisible())
	      populationWindow.toFront();
	    else
	      populationWindow.setVisible(true);

      int state = populationWindow.getExtendedState();
	    if ((state & AdvertiserWindow.ICONIFIED) != 0) {
	      populationWindow.setExtendedState(state & ~AdvertiserWindow.ICONIFIED);
	    }
  }

  private void applyData(){
    UserPopulationState[] ups = gameInfo.getUserPopulationState();
    for(int currentDay = 0, n = ups.length; currentDay < n; currentDay++){//For every day
      int[] population = new int[numQueryStates];

      //Grab total population over all products
      for(Product product: gameInfo.getRetailCatalog()){
        int[] productPop = ups[currentDay].getDistribution(product);
        for(int j = 0; j < population.length; j++){
          //System.out.println(j);
          population[j] += productPop[j];
        }
      }

      for(int k = 0; k < population.length; k++){
        switch(k){
          case 0: //NS
            nsTimeSeries.addOrUpdate(currentDay, population[k]);
            break;
          case 1: //IS
            isTimeSeries.addOrUpdate(currentDay, population[k]);
            break;
          case 2: //F0
            f0TimeSeries.addOrUpdate(currentDay, population[k]);
            break;
          case 3: //F1
            f1TimeSeries.addOrUpdate(currentDay, population[k]);
            break;
          case 4: //F2
            f2TimeSeries.addOrUpdate(currentDay, population[k]);
            break;
          case 5: //F3
            tTimeSeries.addOrUpdate(currentDay, population[k]);
            break;
        }
      }
    }
  }
  
  private void createDataset(){
    nsTimeSeries = new XYSeries("NS");
		isTimeSeries = new XYSeries("IS");
		f0TimeSeries = new XYSeries("F0");
		f1TimeSeries = new XYSeries("F1");
		f2TimeSeries = new XYSeries("F2");
		tTimeSeries = new XYSeries("T");

		seriescollection = new XYSeriesCollection();
		//seriescollection.addSeries(nsTimeSeries);
		seriescollection.addSeries(isTimeSeries);
		seriescollection.addSeries(f0TimeSeries);
		seriescollection.addSeries(f1TimeSeries);
		seriescollection.addSeries(f2TimeSeries);
		seriescollection.addSeries(tTimeSeries);  
  }

  private JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createXYLineChart(
				"User state distribution", "Day", "Users per state", xydataset,
				PlotOrientation.VERTICAL, true, true, false);
		jfreechart.setBackgroundPaint(Color.white);

		XYPlot xyplot = (XYPlot) jfreechart.getPlot();

		xyplot.setBackgroundPaint(Color.lightGray);

		xyplot.setDomainGridlinePaint(Color.white);

		xyplot.setRangeGridlinePaint(Color.white);

		xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

		xyplot.setDomainCrosshairVisible(true);

		xyplot.setRangeCrosshairVisible(true);

		org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot
				.getRenderer();

		xyitemrenderer.setBaseStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));

		if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
			xylineandshaperenderer.setBaseShapesVisible(false);
		}

		return jfreechart;
	}
  
  public JPanel getMainPane() {
    return mainPane;
  }
}
