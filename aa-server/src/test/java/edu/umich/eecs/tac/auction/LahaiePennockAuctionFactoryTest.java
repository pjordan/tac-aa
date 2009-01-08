package edu.umich.eecs.tac.auction;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import edu.umich.eecs.tac.props.*;
import com.botbox.util.ArrayUtils;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;


/**
 * @author Lee Callender
 */
public class LahaiePennockAuctionFactoryTest {
  private LahaiePennockAuctionFactory auction; 
  private BidManager bidManager;



  @Before
  public void setUp() {
    String[] advertisers = {"alice","bob","cathy","don","eve"};
    double[] bids = {0.0, 0.25, 0.5, 0.75, 1.0};
    double[] qualityScore = {1.0, 1.0, 1.0, 1.0, 1.0};
    
    bidManager = new SimpleBidManager(advertisers, bids, qualityScore);

    auction = new LahaiePennockAuctionFactory();
    auction.setBidManager(bidManager);
  }

  @Test
  public void testConstructor(){
    assertNotNull(auction);
  }

  @Test
  public void testGetSet(){
    auction.setSlotLimit(5);
    auction.setSquashValue(1.0);
    auction.setBidManager(bidManager);
    assertEquals(auction.getBidManager(), bidManager);
    assertEquals(auction.getSlotLimit(),5);
    assertEquals(auction.getSquashValue(),1.0);
  }

  @Test
  public void testAuctions(){
    //Base auction
    auction.runAuction(new Query("apples","seeds"));
    //assertEquals here.

    //Fewer participants than slots available

    //bid = double.NaN

    //
  }

  private class SimpleBidManager implements BidManager {
    private int size;
    private String[] advertisers;
    private double[] bids;
    private double[] qualityScore;
    private Set<String> setAdv;

    private final static double defaultBid = 0.0;
    private final static double defaultQuality = 1.0;


    public SimpleBidManager(String[] advertisers){
      this.advertisers = advertisers.clone();
      size = this.advertisers.length;
      bids = new double[size];
      qualityScore = new double[size];
      
      for(int i = 0; i < size; i++){
        bids[i] = defaultBid;
        qualityScore[i] = defaultQuality;
      }

      List list = Arrays.asList(advertisers);
      setAdv = new HashSet<String>(list);
    }

    public SimpleBidManager(String[] advertisers, double[] bids, double[] qualityScore){
      this.advertisers = advertisers.clone();
      this.bids = bids.clone();
      this.qualityScore = qualityScore.clone();

      size = this.advertisers.length;

      List<String> list = Arrays.asList(advertisers);
      setAdv = new HashSet<String>(list);
    }

    public void addAdvertiser(String advertiser){}

    public void setBid(String advertiser, double bid){
      int index = ArrayUtils.indexOf(advertisers, 0, size, advertiser);
      bids[index] = bid; 
    }

    public void setQualityScore(String advertiser, double quality){
      int index = ArrayUtils.indexOf(advertisers, 0, size, advertiser);
      qualityScore[index] = quality;
    }

    
    public double getBid(String advertiser, Query query){

      return bids[ArrayUtils.indexOf(advertisers, 0, size, advertiser)];
    }

    public double getQualityScore(String advertiser, Query query){
      return qualityScore[ArrayUtils.indexOf(advertisers, 0, size, advertiser)];
    }

    public AdLink getAdLink(String advertiser, Query query){
      return new AdLink(null, advertiser);
    }

    public void updateBids(String advertiser, BidBundle bundle){}

    public Set<String> advertisers(){
      return setAdv;
    }

    public void nextTimeUnit(int i){}
  }
}
