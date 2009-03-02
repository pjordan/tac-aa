package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.logviewer.info.Advertiser;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.text.DecimalFormat;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 28, 2009
 * Time: 9:07:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuerySalesPanel {
  JPanel mainPane;
  JLabel costLabel, revenueLabel, profitLabel;
  public static final String COST_STRING = "Cost: ";
  public static final String REVENUE_STRING = "Revenue: ";
  public static final String PROFIT_STRING = "Profit: ";
  public static final DecimalFormat dFormat = new DecimalFormat("$#0.00");
  //public static final DecimalFormat pFormat = new DecimalFormat("#0.###");


  //GameInfo gameInfo;
  Query query;
  Advertiser advertiser;
  PositiveBoundedRangeModel dayModel;
  public QuerySalesPanel(Query query, Advertiser advertiser, PositiveBoundedRangeModel dm){
    this.query = query;
    this.advertiser = advertiser;
    this.dayModel = dm;

    if(dayModel != null) {
	    dayModel.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent ce) {
			    updateMePlz();
		    }
		  });
    }

    mainPane = new JPanel();
    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
    mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder(),query.toString()));

    costLabel = new JLabel();
    revenueLabel = new JLabel();
    profitLabel = new JLabel();

    mainPane.add(costLabel);
    mainPane.add(revenueLabel);
    mainPane.add(profitLabel);

    updateMePlz();

  }

  private void updateMePlz(){
    int current = dayModel.getCurrent();
    QueryReport q_report = advertiser.getQueryReport(current+1);
    SalesReport s_report = advertiser.getSalesReport(current+1);
    if(q_report == null || s_report == null){//TODO-Don't assume both will be null or both will exist.
      setDefaultText();
    }else{
      double cost = q_report.getCost(query);
      double revenue =  s_report.getRevenue(query);
      double profit = revenue - cost;
      costLabel.setText(COST_STRING+dFormat.format(cost));
      revenueLabel.setText(REVENUE_STRING+dFormat.format(revenue));
      profitLabel.setText(PROFIT_STRING+dFormat.format(profit));
    }
  }

  private void setDefaultText(){
    costLabel.setText(COST_STRING+"$0.00");
    revenueLabel.setText(REVENUE_STRING+"$0.00");
    profitLabel.setText(PROFIT_STRING+"$0.00");
  }



  public Component getMainPane() {
    return mainPane;
  }

}

