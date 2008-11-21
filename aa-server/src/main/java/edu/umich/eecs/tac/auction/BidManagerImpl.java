package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.BidBundle.BidEntry;

import java.util.*;

/**
 * @author Patrick Jordan, Lee Callender
 */
public class BidManagerImpl implements BidManager {
    //TODO: Discuss 'security' issues, test
    private Map<String,BidBundle> entryMap;


    public BidManagerImpl() {
        entryMap = new HashMap<String,BidBundle>();
    }

    public double getBid(String advertiser, Query query) {
      if(!entryMap.containsKey(advertiser))
        return 0.0;  //Double.NaN?

      return entryMap.get(advertiser).getBid(query); 
    }

    public double getQualityScore(String advertiser, Query query) {
      if(!entryMap.containsKey(advertiser))
        return 1.0;

      return 1.0;
    }

    public Ad getAd(String advertiser, Query query) {
      if(!entryMap.containsKey(advertiser))
        return null;  //Generic ad?

      return entryMap.get(advertiser).getAd(query);
    }

    
    public void updateBids(String advertiser, BidBundle bundle) {
      if(!entryMap.containsKey(advertiser)){
        addAdvertiser(advertiser);                 //Should we be checking this against the list of possible advertisers?
      }

      BidBundle bids = entryMap.get(advertiser);
      for (Iterator<Query> it=bundle.iterator(); it.hasNext(); ) {
        Ad advertisement = new Ad(null, advertiser);
        Query query = it.next();
        if(!bids.containsQuery(query)){           //Should we be checking this against the list of possible queries  
          bids.addQuery(query, Double.NaN, advertisement);   //These are the current default values, NaN and generic Ad 
        }

        //Update bid for query only if bid was specified in BidBundle
        double bid = bundle.getBid(query);
        if(bid != Double.NaN){                    //Should this check for negative bids?
          bids.setBid(query, bid);
        }

        //Update ad for query only if bid was specified in BidBundle
        Ad ad = bundle.getAd(query);
        if(ad != null){                           //Should this check to make sure the ad is from the advertiser?
          bids.setAd(query, ad);
        }
        
      }

    }

    public Set<String> advertisers() {
        return entryMap.keySet();
    }

    public void nextTimeUnit(int timeUnit) {   
    }

    public void addAdvertiser(String advertiser) {
        if(!entryMap.containsKey(advertiser)) {
            entryMap.put(advertiser, new BidBundle());
        }
    }

    /*static class BidEntry {
        //TODO: track the attributes for each query
    }*/
}
