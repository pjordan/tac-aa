package edu.umich.eecs.tac.auction;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;

import java.util.Set;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Patrick Jordan
 */
public class BidManagerImpl implements BidManager {
    private Map<String,BidEntry> entryMap;


    public BidManagerImpl() {
        entryMap = new HashMap<String,BidEntry>();
    }

    public double getBid(String advertiser, Query query) {
        return 0.0;
    }

    public double getQualityScore(String advertiser, Query query) {
        return 1.0;
    }

    public Ad getAd(String advertiser, Query query) {
        return null;
    }

    public void updateBids(String advertiser, BidBundle bundle) {

    }

    public Set<String> advertisers() {
        return entryMap.keySet();
    }

    public void nextTimeUnit(int timeUnit) {
        
    }

    public void addAdvertiser(String advertiser) {
        if(!entryMap.containsKey(advertiser)) {
            entryMap.put(advertiser, new BidEntry());
        }
    }

    static class BidEntry {
        //TODO: track the attributes for each query
    }
}
