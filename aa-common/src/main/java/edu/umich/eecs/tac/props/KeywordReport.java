package edu.umich.eecs.tac.props;
import java.io.Serializable;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

//STILL IN PROGRESS
public class KeywordReport implements Serializable, Transportable {

	/**Update when this file is updated.
	 * 
	 */
	private static final long serialVersionUID = -4366560152538990286L;

	private List<QueryReport> yesterdaysReport;
	
	private boolean isLocked = false;
	
	public KeywordReport(){
		yesterdaysReport = new LinkedList<QueryReport>();
	}
	
	public void addQueryReport(){
		QueryReport temp = new QueryReport();
		yesterdaysReport.add(temp);
	}
	
	public void addQuery(String query){
		QueryReport temp = new QueryReport(query);
		yesterdaysReport.add(temp);
	}
	
	public void addQuery(String query, int clicknum, double avgcpc, int imp, double pos){
		QueryReport temp = new QueryReport(query, clicknum, avgcpc, imp, pos);
		yesterdaysReport.add(temp);
	}
	
	//TODO: ADD GETTERS AND SETTERS FOR THIS LEVEL OF ACCESS
	
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
		for(i = 0; i < yesterdaysReport.size(); i++){
			temp = yesterdaysReport.get(i);
			r += "Query: " + temp.getQuery() + " ";
			r += "Avergae Position: " + temp.getAvgPosition() + " ";
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
			temp.setAvgPosition(reader.getAttributeAsInt("position"));
			temp.setAvgCPC(reader.getAttributeAsDouble("costperclick"));
			yesterdaysReport.add(temp);
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
		for(i = 0; i < yesterdaysReport.size(); i++){
			temp = yesterdaysReport.get(i);
			writer.node("keywordreport");
			writer.attr("queryString", temp.getQuery());
			writer.attr("impressions", temp.getImpressions());
			writer.attr("clicks", temp.getClicks());
			writer.attr("position", temp.getAvgPosition());
			writer.attr("costperclick", temp.getAvgCPC());
			writer.endNode("keywordreport");
		}
	}

	private static class QueryReport {

		private String query = new String();
		private int impressions;
		private int clicks;
		private double avgPosition;
		private double avgCPC;
		
		public QueryReport(){}
		
		public QueryReport(String q){
			query = q;
		}
		
		public QueryReport(String q, int clicknum, double cost, int imp, double pos){
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

		public double getAvgPosition() {
			return avgPosition;
		}

		public void setAvgPosition(double avgPosition) {
			this.avgPosition = avgPosition;
		}

		public double getAvgCPC() {
			return avgCPC;
		}

		public void setAvgCPC(double avgCPC) {
			this.avgCPC = avgCPC;
		}

	}
	
}