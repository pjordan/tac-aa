package edu.umich.eecs.tac.props;
import java.io.Serializable;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class KeywordReport implements Serializable, Transportable {

	private static final long serialVersionUID = -7957495904471250085L;

	private List<QueryReport> report;
	
	private boolean isLocked = false;
	
	public KeywordReport(){
		report = new LinkedList<QueryReport>();
	}
	
	public void addQuery(){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		QueryReport temp = new QueryReport();
		report.add(temp);
	}
	
	public void addQuery(String query){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		QueryReport temp = new QueryReport(query);
		report.add(temp);
	}
	
	public void addQuery(String query, int clicknum, float avgcpc, int imp, float pos){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		QueryReport temp = new QueryReport(query, clicknum, avgcpc, imp, pos);
		report.add(temp);
	}

	public int getSize(){
		return report.size();
	}
	
	public void setQueryAvgPosition(String q, float p){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		int i;
		for(i = 0; i < report.size(); i++){
			if(report.get(i).query.equals(q)){
				report.get(i).setAvgPosition(p);
				return;
			}
		}
		QueryReport temp = new QueryReport(q);
		temp.setAvgPosition(p);
		report.add(temp);
	}
	
	public void setQueryAvgPosition(int i, float p){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		report.get(i).setAvgPosition(p);
	}
	
	public void setQueryImpressions(String q, int imp){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		int i;
		for(i = 0; i < report.size(); i++){
			if(report.get(i).query.equals(q)){
				report.get(i).setImpressions(imp);
				return;
			}
		}
		QueryReport temp = new QueryReport(q);
		temp.setImpressions(imp);
		report.add(temp);
	}
	
	public void setQueryImpressions(int i, int imp){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		report.get(i).setAvgPosition(imp);
	}
	
	public void setQueryClicks(String q, int click){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		int i;
		for(i = 0; i < report.size(); i++){
			if(report.get(i).query.equals(q)){
				report.get(i).setClicks(click);
				return;
			}
		}
		QueryReport temp = new QueryReport(q);
		temp.setClicks(click);
		report.add(temp);
	}
	
	public void setQueryClicks(int i, int click){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		report.get(i).setClicks(click);
	}
	
	public void setQueryAvgCPC(String q, float cpc){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		int i;
		for(i = 0; i < report.size(); i++){
			if(report.get(i).query.equals(q)){
				report.get(i).setAvgCPC(cpc);
				return;
			}
		}
		QueryReport temp = new QueryReport(q);
		temp.setAvgCPC(cpc);
		report.add(temp);
	}
	
	public void setQueryAvgCPC(int i, float cpc){
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
		report.get(i).setAvgCPC(cpc);
	}
	
	public float getQueryAvgPosition(String query){
		int i;
		for(i = 0; i < report.size(); i++){
			if(report.get(i).query.equals(query)){
				return report.get(i).getAvgPosition();
			}
		}
		return 0;
	}
	
	public float getQueryAvgPosition(int i){
		return report.get(i).getAvgPosition();
	}
	
	public float getQueryAvgCPC(String query){
		int i;
		for(i = 0; i < report.size(); i++){
			if(report.get(i).query.equals(query)){
				return report.get(i).getAvgCPC();
			}
		}
		return 0;
	}
	
	public float getQueryAvgCPC(int i){
		return report.get(i).getAvgCPC();
	}
	
	public int getQueryImpressions(String query){
		int i;
		for(i = 0; i < report.size(); i++){
			if(report.get(i).query.equals(query)){
				return report.get(i).getImpressions();
			}
		}
		return 0;
	}
	
	public int getQueryImpressions(int i){
		return report.get(i).getImpressions();
	}
	
	public int getQueryClicks(String query){
		int i;
		for(i = 0; i < report.size(); i++){
			if(report.get(i).query.equals(query)){
				return report.get(i).getClicks();
			}
		}
		return 0;
	}
	
	public int getQueryClicks(int i){
		return report.get(i).getClicks();
	}
	
	public boolean isLocked() {
		return isLocked;
	}

	public void lock() {
		isLocked = true;
	}
	
	public String getTransportName() {
		return "keywordreport";
	}

	public String toString(){
		String r = new String();
		int i;
		QueryReport temp = new QueryReport();
		for(i = 0; i < report.size(); i++){
			temp = report.get(i);
			r += "Query: " + temp.getQuery() + " ";
			r += "Average Position: " + temp.getAvgPosition() + " ";
			r += "Impressions: " + temp.getImpressions() + " ";
			r += "Clicks: " + temp.getClicks() + " ";
			r += "Average Cost Per Click: " + temp.getAvgCPC() + "\n";
		}
		return r;
	}
	
	public void read(TransportReader reader) throws ParseException {
	    if (isLocked) {
	        throw new IllegalStateException("locked");
	    }
		boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
		while (reader.nextNode("keywordreport", false)) {
			QueryReport temp = new QueryReport();
			temp.setQuery(reader.getAttribute("query"));
			temp.setImpressions(reader.getAttributeAsInt("impressions"));
			temp.setClicks(reader.getAttributeAsInt("clicks"));
			temp.setAvgPosition(reader.getAttributeAsFloat("avgposition"));
			temp.setAvgCPC(reader.getAttributeAsFloat("costperclick"));
			report.add(temp);
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
		QueryReport temp;
		for(i = 0; i < report.size(); i++){
			temp = report.get(i);
			writer.node("keywordreport");
			writer.attr("query", temp.getQuery());
			writer.attr("impressions", temp.getImpressions());
			writer.attr("clicks", temp.getClicks());
			writer.attr("avgposition", temp.getAvgPosition());
			writer.attr("costperclick", temp.getAvgCPC());
			writer.endNode("keywordreport");
		}
	}

	private static class QueryReport {

		private String query = new String();
		private int impressions = 0;
		private int clicks = 0;
		private float avgPosition = 0;
		private float avgCPC = 0;
		
		public QueryReport(){}
		
		public QueryReport(String q){
			query = q;
		}
		
		public QueryReport(String q, int clicknum, float cost, int imp, float pos){
			query = q;
			clicks = clicknum;
			avgCPC = cost;
			impressions = imp;
			avgPosition = pos;
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public int getImpressions() {
			return impressions;
		}

		public void setImpressions(int impressions) {
			this.impressions = impressions;
		}

		public int getClicks() {
			return clicks;
		}

		public void setClicks(int clicks) {
			this.clicks = clicks;
		}

		public float getAvgPosition() {
			return avgPosition;
		}

		public void setAvgPosition(float avgPosition) {
			this.avgPosition = avgPosition;
		}

		public float getAvgCPC() {
			return avgCPC;
		}

		public void setAvgCPC(float avgCPC) {
			this.avgCPC = avgCPC;
		}

	}
	
}