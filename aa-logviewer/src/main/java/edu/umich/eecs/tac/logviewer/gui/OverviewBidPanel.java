package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import static edu.umich.eecs.tac.logviewer.util.VisualizerUtils.formatToString;
import edu.umich.eecs.tac.props.*;

import javax.swing.*;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Mar 2, 2009
 * Time: 2:37:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverviewBidPanel extends UpdatablePanel {
  JLabel bidLabel, reserveLabel, adLabel, cpcLabel, vpcLabel, posLabel;
  public static final String RESERVE_STRING = "Global Spend Limit: ";
  public static final String CPC_STRING = "Total Avg. CPC: ";
  public static final String VPC_STRING = "Total Avg. VPC: ";
  public static final String POS_STRING = "Avg. Placed Position: ";
  public static final String AD_NULL = "NULL";
  public static final DecimalFormat dFormat = new DecimalFormat("$#0.000");
  public static final DecimalFormat pFormat = new DecimalFormat("#0.###");
  Query[] querySpace;
  Advertiser advertiser;

  double[] reserve;
  double[] cpc;
  double[] vpc;
  double[] pos;


  public OverviewBidPanel(Advertiser advertiser, PositiveBoundedRangeModel dm, GameInfo gameInfo, int numDays) {
    super(dm);
    this.advertiser = advertiser;
    this.querySpace = gameInfo.getQuerySpace().toArray(new Query[0]);
    this.reserve = new double[numDays];
    this.cpc = new double[numDays];
    this.vpc = new double[numDays];
    this.pos = new double[numDays];


    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
    mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder(),"Bid Metrics"));


    reserveLabel = new JLabel();
    cpcLabel = new JLabel();
    vpcLabel = new JLabel();
    posLabel = new JLabel();

    mainPane.add(reserveLabel);
    mainPane.add(cpcLabel);
    mainPane.add(vpcLabel);
    mainPane.add(posLabel);

    applyData();
    updateMePlz();
  }

  private void applyData(){
    reserve[0] = Double.NaN;
    cpc[0] = Double.NaN;
    vpc[0] = Double.NaN;
    pos[0] = Double.NaN;

    for(int i = 0; i < reserve.length - 1; i++){
      BidBundle current = advertiser.getBidBundle(i);
      QueryReport report = advertiser.getQueryReport(i+2);
      SalesReport s_report = advertiser.getSalesReport(i+2);
      //TODO-FIX!
      //What if advertiser doesn't send BidBundle today?
      if(current != null){
        reserve[i+1] = current.getCampaignDailySpendLimit();
      }else{
        reserve[i+1] = current.PERSISTENT_BID;
      }
      if(report != null){
        double cost = 0.0D;
        int clicks = 0;
        int count = 0;
        double position = 0.0D;
        double curPosition;
        for(int j = 0; j < querySpace.length; j++){
          cost += report.getCost(querySpace[j]);
          clicks += report.getClicks(querySpace[j]);

          curPosition = report.getPosition(querySpace[j]);
          if(!Double.isNaN(curPosition)){
            position += curPosition;
            count++;
          }
        }
        cpc[i+1] = cost/clicks;
        pos[i+1] = position/count; 
      }else{
        cpc[i+1] = Double.NaN;
        pos[i+1] = Double.NaN;
      }

      if(s_report != null && report != null){
        double revenue = 0.0D;
        double cost = 0.0D;
        int clicks = 0; 
        for(int j = 0; j < querySpace.length; j++){
          revenue += s_report.getRevenue(querySpace[j]);
          cost    += report.getCost(querySpace[j]);
          clicks  += report.getClicks(querySpace[j]);
        }
        vpc[i+1] = (revenue - cost)/clicks;
      }else{
        vpc[i+1] = Double.NaN;
      }

      if(i != 0){//Does this still apply?
        if(reserve[i+1] == current.PERSISTENT_SPEND_LIMIT)
          reserve[i+1] = reserve[i];
      }
    }
  }

  protected void updateMePlz(){
    //System.out.println("updating!");
    String s = (""+dayModel.getCurrent());
    int day = dayModel.getCurrent();
    if(Double.isNaN(reserve[day]))
      reserveLabel.setText(RESERVE_STRING+reserve[day]);
    else
      reserveLabel.setText(RESERVE_STRING+dFormat.format(reserve[day]));
    if(Double.isNaN(cpc[day]))
      cpcLabel.setText(CPC_STRING+cpc[day]);
    else
      cpcLabel.setText(CPC_STRING+dFormat.format(cpc[day]));
    if(Double.isNaN(vpc[day]))
      vpcLabel.setText(VPC_STRING+vpc[day]);
    else
      vpcLabel.setText(VPC_STRING+dFormat.format(vpc[day]));
    if(Double.isNaN(pos[day]))
      posLabel.setText(POS_STRING+pos[day]);
    else
      posLabel.setText(POS_STRING+pFormat.format(pos[day]));
  }

}
