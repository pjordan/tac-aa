package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.logviewer.info.Advertiser;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.Range;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 17, 2009
 * Time: 3:11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryRatioPanel {
   JPanel mainPane;

  private DefaultValueDataset ctrValue;
	private DefaultValueDataset convValue;

  //GameInfo gameInfo;
  Query query;
  Advertiser advertiser;
  PositiveBoundedRangeModel dayModel;

  public QueryRatioPanel(Query query, Advertiser advertiser, PositiveBoundedRangeModel dayModel){
    this.query = query;
    this.advertiser = advertiser;
    this.dayModel = dayModel;

    if(dayModel != null) {
	    dayModel.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent ce) {
			    updateMePlz();
		    }
		  });
    }

    mainPane = new JPanel();
    mainPane.setLayout(new GridLayout(2, 1));
    mainPane.setBorder(BorderFactory.createTitledBorder(query.toString()));
    //smainPane.setMaximumSize(new Dimension(10,10));

    ChartPanel CTRChart = new ChartPanel(createCTRChart());
    ChartPanel CONVChart = new ChartPanel(createConvChart());
    //CTRChart.setMaximumDrawHeight(100);
    //CTRChart.setMaximumDrawWidth(50);
    //CONVChart.setMaximumDrawHeight(100);
    //CONVChart.setMaximumDrawWidth(50);
    mainPane.add(CTRChart);
		mainPane.add(CONVChart);

}

  private JFreeChart createCTRChart() {
		return createChart("CTR", ctrValue = new DefaultValueDataset(0.0));
	}

	private JFreeChart createConvChart() {
		return createChart("Conv Rate",
				convValue = new DefaultValueDataset(0.0));
	}

  private JFreeChart createChart(String s, ValueDataset dataset) {
		MeterPlot meterplot = new MeterPlot(dataset);
		meterplot.setDialShape(DialShape.CHORD);
		meterplot.setRange(new Range(0.0D, 100D));
		meterplot.addInterval(new MeterInterval("", new Range(0, 100.0D),
				Color.lightGray, new BasicStroke(2.0F),
				new Color(0, 255, 0, 64)));
		meterplot.setNeedlePaint(Color.darkGray);
		meterplot.setDialBackgroundPaint(Color.white);
		meterplot.setDialOutlinePaint(Color.gray);
		meterplot.setMeterAngle(260);
		meterplot.setTickLabelsVisible(true);
		meterplot.setTickLabelFont(new Font("Dialog", 1, 10));
		meterplot.setTickLabelPaint(Color.darkGray);
		meterplot.setTickSize(5D);
		meterplot.setTickPaint(Color.lightGray);
		meterplot.setValuePaint(Color.black);
		meterplot.setValueFont(new Font("Dialog", 1, 14));
		meterplot.setUnits("%");
		return new JFreeChart(s, JFreeChart.DEFAULT_TITLE_FONT, meterplot,
				false);
	}

  protected void updateCTR(int impressions, int clicks) {
		if (impressions > 0) {
			ctrValue.setValue(100.0 * ((double) clicks)
					/ ((double) impressions));
		} else {
			ctrValue.setValue(0.0D);
		}
	}

	protected void updateConvRate(int conversions, int clicks) {
		if (clicks > 0) {
			convValue.setValue(100.0 * ((double) conversions)
					/ ((double) clicks));
		} else {
			convValue.setValue(0.0D);
		}
	}

  private void updateMePlz(){
    /*int impressions, clicks, conversions;
    int currentDay = dayModel.getCurrent();
    QueryReport q_report = advertiser.getQueryReport(dayModel.getCurrent()+1);
    SalesReport s_report = advertiser.getSalesReport(dayModel.getCurrent()+1);
    if(s_report != null && q_report != null){
      updateCTR(q_report.getImpressions(query), q_report.getClicks(query));
      updateConvRate(s_report.getConversions(query), q_report.getClicks(query));
    }else{
      updateCTR(0,0);
      updateConvRate(0,0);
    } */

  }

  public Component getMainPane() {
    return mainPane;
  }
}
