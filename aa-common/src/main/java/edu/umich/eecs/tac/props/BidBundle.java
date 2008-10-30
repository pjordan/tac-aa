package edu.umich.eecs.tac.props;

import java.io.Serializable;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class BidBundle implements Transportable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5057969669832603679L;
	
	private boolean isLocked = false;
	private List<BidPair> bundle;
	
	public BidBundle(){
		bundle = new LinkedList<BidPair>();
	}

	public void addBidPair(String search, double bid){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		search.toLowerCase();
		Integer index = indexOfSearchString(search);
		
		if(index != null){
			bundle.get(index).setBidAmount(bid);
			return;
		}

		bundle.add(new BidPair(search, bid));
	}
	
	public void removeAll(){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		bundle.clear();
	}
	
	public void updateBid(String search, double bid){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		search.toLowerCase();
		Integer index = indexOfSearchString(search);
		if(index == null){
			bundle.add(new BidPair(search, bid));
			return;
		}
		bundle.get(index).setBidAmount(bid);
	}
	
	public void removeBidPair(String search){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		search.toLowerCase();
		Integer index = indexOfSearchString(search);
		if(index != null)
			bundle.remove(index);
	}
	
	private Integer indexOfSearchString(String search) {
		search.toLowerCase();
		int i;
		for(i = 0; i < bundle.size(); i++){
			if(bundle.get(i).getSearchString() == search)
				return i;
		}
		return null;
	}

	public boolean isLocked(){
		return isLocked;
	}
	
	public void lock(){
		isLocked = true;
	}
	
	public String getTransportName() {
		return "bidbundle";
	}

	public void read(TransportReader reader) throws ParseException {
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
	    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
	    while(reader.nextNode("bidbundle", false)){
	    	
	    }
	}

	public void write(TransportWriter writer) {
		// TODO Auto-generated method stub

	}

	private static class BidPair{

		private String searchString = new String();
		private double bidAmount;
		
		public BidPair(String s, double b){
			searchString = s.toLowerCase();
			bidAmount = b;
		}

		public void setBidAmount(double b){
			bidAmount = b;
		}
		
		public void setSearchString(String s){
			searchString = s.toLowerCase();
		}
		
		public String getSearchString(){
			return searchString;
		}
		
		public double getBidAmount(){
			return bidAmount;
		}
		
	}
	
}