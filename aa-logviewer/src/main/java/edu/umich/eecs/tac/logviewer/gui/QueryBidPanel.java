package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 5, 2009
 * Time: 4:37:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryBidPanel {
  JPanel mainPane;
  JLabel bidLabel, reserveLabel, adLabel;
  public static final String BID_STRING = "Bid: ";
  public static final String RESERVE_STRING = "Reserve: ";
  public static final String AD_STRING = "Ad: ";
  public static final String AD_NULL = "NULL";

  double[] bid;
  double[] reserve;
  Ad[]     ad;
  
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

    bidLabel = new JLabel(BID_STRING+bid[0]);
    reserveLabel = new JLabel(RESERVE_STRING+reserve[0]);
    if(ad[0] != null)
      adLabel = new JLabel(AD_STRING+ad[0].toString());
    else
      adLabel = new JLabel(AD_STRING+AD_NULL);

    mainPane.add(bidLabel);
    mainPane.add(reserveLabel);
    mainPane.add(adLabel);
  }

  private void applyData(){
    for(int i = 0; i < bid.length; i++){
      BidBundle current = advertiser.getBidBundle(i);
      if(current != null){
        bid[i] = current.getBid(query);
        reserve[i] = current.getDailyLimit(query);
        ad[i] = current.getAd(query);
      }

      if(i != 0){
        if(bid[i] == current.PERSISTENT_BID)
          bid[i] = bid[i-1];
        if(reserve[i] == current.PERSISTENT_SPEND_LIMIT)
          reserve[i] = reserve[i-1];
        if(ad[i] == current.PERSISTENT_AD)
          ad[i] = ad[i-1];
      }
    }
  }

  private void updateMePlz(){
    //System.out.println("updating!");
    String s = (""+dayModel.getCurrent());
    int day = dayModel.getCurrent();
    bidLabel.setText(BID_STRING+bid[day]);
    reserveLabel.setText(RESERVE_STRING+reserve[day]);
    if(ad[day] != null)
      adLabel.setText(AD_STRING+ad[day].toString());
    else
      adLabel.setText(AD_STRING+AD_NULL);
  }


  public Component getMainPane() {
    return mainPane;
  }
}
