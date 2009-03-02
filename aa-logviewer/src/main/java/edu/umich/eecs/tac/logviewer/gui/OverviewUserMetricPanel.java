package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;

import javax.swing.*;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Mar 2, 2009
 * Time: 3:17:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverviewUserMetricPanel extends UpdatablePanel {
  JLabel impLabel, clicksLabel, convLabel, ctrLabel, convRateLabel;
  public static final String IMPRESSIONS_STRING = "Total Impressions: ";
  public static final String CLICKS_STRING = "Total Clicks: ";
  public static final String CONVERSIONS_STRING = "Total Conversions: ";
  public static final String CTR_STRING = "CTR: ";
  public static final String CONV_RATE_STRING = "Conv. Rate: ";
  public static final DecimalFormat dFormat = new DecimalFormat("##.##%");
  Query[] querySpace;


  Advertiser advertiser;

  public OverviewUserMetricPanel(Advertiser advertiser, PositiveBoundedRangeModel dm, GameInfo gameInfo) {
    super(dm);
    this.advertiser = advertiser;
    this.querySpace = gameInfo.getQuerySpace().toArray(new Query[0]);

    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
    mainPane.setBorder(BorderFactory.createTitledBorder
          			   (BorderFactory.createEtchedBorder(),"User Metrics"));

    impLabel = new JLabel();
    clicksLabel = new JLabel();
    convLabel = new JLabel();
    ctrLabel = new JLabel();
    convRateLabel = new JLabel();

    mainPane.add(impLabel);
    mainPane.add(clicksLabel);
    mainPane.add(convLabel);
    mainPane.add(ctrLabel);
    mainPane.add(convRateLabel);

    updateMePlz();

  }

  protected void updateMePlz(){
      int current = dayModel.getCurrent();
    QueryReport q_report = advertiser.getQueryReport(current+1);
    SalesReport s_report = advertiser.getSalesReport(current+1);
    if(q_report == null || s_report == null){//TODO-Don't assume both will be null or both will exist.
      setDefaultText();
    }else{
      int impressions = 0;
      int clicks = 0;
      int conversions = 0;
      for(int i=0; i < querySpace.length; i++){
        impressions += q_report.getImpressions(querySpace[i]);
        clicks = q_report.getClicks(querySpace[i]);
        conversions = s_report.getConversions(querySpace[i]);
      }
      impLabel.setText(IMPRESSIONS_STRING+impressions);
      clicksLabel.setText(CLICKS_STRING+clicks);
      convLabel.setText(CONVERSIONS_STRING+conversions);
      ctrLabel.setText(CTR_STRING+dFormat.format(calcCTR(impressions, clicks)));
      convRateLabel.setText(CONV_RATE_STRING+dFormat.format(calcConvRate(conversions, clicks)));
    }



  }

  private void setDefaultText(){

    impLabel.setText(IMPRESSIONS_STRING+0);
    clicksLabel.setText(CLICKS_STRING+0);
    convLabel.setText(CONVERSIONS_STRING+0);
    ctrLabel.setText(CTR_STRING+0.0+"%");
    convRateLabel.setText(CONV_RATE_STRING+0.0+"%");
  }

  protected double calcCTR(int impressions, int clicks) {
		if (impressions > 0) {
			return (((double) clicks)
					/ ((double) impressions));
		} else {
			return 0.0D;
		}
	}

	protected double calcConvRate(int conversions, int clicks) {
		if (clicks > 0) {
			return (((double) conversions)
					/ ((double) clicks));
		} else {
			return 0.0D;
		}
	}
  
}
