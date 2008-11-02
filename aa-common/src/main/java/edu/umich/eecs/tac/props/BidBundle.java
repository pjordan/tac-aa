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

	public boolean isLocked(){
		return isLocked;
	}
	
	public void lock(){
		isLocked = true;
	}
	
	public String getTransportName() {
		return "bidbundle";
	}

	public void addBidPair(){
		if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		bundle.add(new BidPair());
	}

	public void addBidPair(String q){
		if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		bundle.add(new BidPair(q));
	}
	
	public void addBidPair(String q, double b){
		if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		bundle.add(new BidPair(q, b));
	}
	
	public int getSize(){
		return bundle.size();
	}
	
	public void setBid(String q, double b){
		if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		int i;
		for(i = 0; i < bundle.size(); i++){
			if(bundle.get(i).query.equals(q)){
				bundle.get(i).setBidAmount(b);
				return;
			}
		}
		BidPair temp = new BidPair(q, b);
		bundle.add(temp);
	}
	
	public void setBid(int i, double b){
		bundle.get(i).setBidAmount(b);
	}
	
	public void read(TransportReader reader) throws ParseException {
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
	    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
	    while(reader.nextNode("bidbundle", false)){
	    	BidPair temp = new BidPair();
	    	temp.setQuery(reader.getAttribute("query"));
	    	temp.setBidAmount(reader.getAttributeAsDouble("bidamount"));
	    	bundle.add(temp);
	    }
	    if(lock){
	        lock();
	    }
	}
	
	public void write(TransportWriter writer) {
		if (isLocked) {
			writer.attr("lock", 1);
	    }
		int i;
		BidPair temp;
		for(i = 0; i < bundle.size(); i++){
			temp = bundle.get(i);
			writer.node("bidbundle");
			writer.attr("query", temp.getQuery());
			writer.attr("bidamount", temp.getBidAmount());
			writer.endNode("bidbundle");
		}
	}

	private static class BidPair{

		private String query = "";
		private double bidAmount = 0;
		
		public BidPair(){};
		
		public BidPair(String q){
			query = q;
		}
		
		public BidPair(String q, double b){
			query = q;
			bidAmount = b;
		}

		public void setQuery(String q){
			query = q;
		}
		
		public void setBidAmount(double b){
			bidAmount = b;
		}
		
		public String getQuery(){
			return query;
		}
		
		public double getBidAmount(){
			return bidAmount;
		}
		
	}
	
}