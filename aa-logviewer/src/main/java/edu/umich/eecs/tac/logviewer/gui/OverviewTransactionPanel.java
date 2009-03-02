package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.props.Query;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Mar 2, 2009
 * Time: 2:00:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverviewTransactionPanel extends UpdatablePanel {
  JLabel costLabel, revenueLabel, profitLabel;
  public static final String COST_STRING = "Total Cost: ";
  public static final String REVENUE_STRING = "Total Revenue: ";
  public static final String PROFIT_STRING = "Total Profit: ";
  public static final DecimalFormat dFormat = new DecimalFormat("$#0.00");
  Query[] querySpace;


  Advertiser advertiser;
  
  public OverviewTransactionPanel(Advertiser advertiser, PositiveBoundedRangeModel dm, GameInfo gameInfo) {
    super(dm);
    this.advertiser = advertiser;
    this.querySpace = gameInfo.getQuerySpace().toArray(new Query[0]);

    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
    mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder(),"Transactions"));

    costLabel = new JLabel();
    revenueLabel = new JLabel();
    profitLabel = new JLabel();

    mainPane.add(costLabel);
    mainPane.add(revenueLabel);
    mainPane.add(profitLabel);

    updateMePlz();

  }

  private void setDefaultText(){
    costLabel.setText(COST_STRING+"$0.00");
    revenueLabel.setText(REVENUE_STRING+"$0.00");
    profitLabel.setText(PROFIT_STRING+"$0.00");
  }


  protected void updateMePlz(){
    int current = dayModel.getCurrent();
    QueryReport q_report = advertiser.getQueryReport(current+1);
    SalesReport s_report = advertiser.getSalesReport(current+1);
    if(q_report == null || s_report == null){//TODO-Don't assume both will be null or both will exist.
      setDefaultText();
    }else{
      double cost = 0.0D;
      double revenue = 0.0D;
      for(int i = 0; i < querySpace.length; i++){
        cost += q_report.getCost(querySpace[i]);
        revenue +=  s_report.getRevenue(querySpace[i]);
      }
      double profit = revenue - cost;
      costLabel.setText(COST_STRING+dFormat.format(cost));
      revenueLabel.setText(REVENUE_STRING+dFormat.format(revenue));
      profitLabel.setText(PROFIT_STRING+dFormat.format(profit));
    }
  }

}
