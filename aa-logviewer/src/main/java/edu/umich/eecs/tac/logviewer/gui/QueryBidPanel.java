package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.QueryReport;
import static edu.umich.eecs.tac.logviewer.util.VisualizerUtils.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 5, 2009
 * Time: 4:37:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryBidPanel {
  JPanel mainPane;
  JLabel bidLabel, reserveLabel, adLabel, cpcLabel, posLabel;
  public static final String BID_STRING = "Bid: ";
  public static final String RESERVE_STRING = "Reserve: ";
  public static final String AD_STRING = "Ad: ";
  public static final String CPC_STRING = "Avg. CPC: ";
  public static final String POS_STRING = "Avg. Position: ";
  public static final String AD_NULL = "NULL";
  public static final DecimalFormat dFormat = new DecimalFormat("$#0.000");
  public static final DecimalFormat pFormat = new DecimalFormat("#0.###");

  double[] bid;
  double[] reserve;
  Ad[]     ad;
  double[] cpc;
  double[]    pos; 

  //GameInfo gameInfo;
  Query query;
  Advertiser advertiser;
  PositiveBoundedRangeModel dayModel;

  public QueryBidPanel(Query query, Advertiser advertiser, PositiveBoundedRangeModel dm, int numDays){
    this.query = query;
    this.advertiser = advertiser;
    this.dayModel = dm;
    this.bid = new double[numDays];
    this.reserve = new double[numDays];
    this.ad = new Ad[numDays];
    this.cpc = new double[numDays];
    this.pos = new double[numDays];

    if(dayModel != null) {
	    dayModel.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent ce) {
			    updateMePlz();
		    }
		  });
    }

    applyData();

    mainPane = new JPanel();
    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
    mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder(),query.toString()));

    bidLabel = new JLabel();
    reserveLabel = new JLabel();
    adLabel = new JLabel();
    cpcLabel = new JLabel();
    posLabel = new JLabel();

    mainPane.add(bidLabel);
    mainPane.add(reserveLabel);
    mainPane.add(adLabel);
    mainPane.add(cpcLabel);
    mainPane.add(posLabel);

    updateMePlz();
  }

  private void applyData(){
    bid[0] = Double.NaN;
    reserve[0] = Double.NaN;
    ad[0] = null;
    cpc[0] = Double.NaN;
    pos[0] = Double.NaN;

    for(int i = 0; i < bid.length - 1; i++){
      BidBundle current = advertiser.getBidBundle(i);
      QueryReport report = advertiser.getQueryReport(i+2);
      //TODO-FIX!
      //What if advertiser doesn't send BidBundle today?
      if(current != null){
        bid[i+1] = current.getBid(query);
        reserve[i+1] = current.getDailyLimit(query);
        ad[i+1] = current.getAd(query);
      }else{
        bid[i+1] = current.PERSISTENT_BID;
        reserve[i+1] = current.PERSISTENT_BID;
        ad[i+1] = current.PERSISTENT_AD;
      }

      if(report != null){
        cpc[i+1] = report.getCPC(query);
        pos[i+1] = report.getPosition(query);
      }else{
        cpc[i+1] = Double.NaN;
        pos[i+1] = Double.NaN;
      }

      if(i != 0){//Does this still apply?
        if(bid[i+1] == current.PERSISTENT_BID)
          bid[i+1] = bid[i];
        if(reserve[i+1] == current.PERSISTENT_SPEND_LIMIT)
          reserve[i+1] = reserve[i];
        if(ad[i+1] == current.PERSISTENT_AD)
          ad[i+1] = ad[i];
      }
    }
  }

  private void updateMePlz(){
    //System.out.println("updating!");
    String s = (""+dayModel.getCurrent());
    int day = dayModel.getCurrent();
    if(Double.isNaN(bid[day]))
      bidLabel.setText(BID_STRING+bid[day]);
    else
      bidLabel.setText(BID_STRING+dFormat.format(bid[day]));
    if(Double.isNaN(reserve[day]))
      reserveLabel.setText(RESERVE_STRING+reserve[day]);
    else
      reserveLabel.setText(RESERVE_STRING+dFormat.format(reserve[day]));
    if(ad[day] != null)
      adLabel.setText(AD_STRING+formatToString(ad[day]));
    else
      adLabel.setText(AD_STRING+AD_NULL);
    if(Double.isNaN(cpc[day]))
      cpcLabel.setText(CPC_STRING+cpc[day]);
    else
      cpcLabel.setText(CPC_STRING+dFormat.format(cpc[day]));
    if(Double.isNaN(pos[day]))
      posLabel.setText(POS_STRING+pos[day]);
    else
      posLabel.setText(POS_STRING+pFormat.format(pos[day]));

  }


  public Component getMainPane() {
    return mainPane;
  }
}
