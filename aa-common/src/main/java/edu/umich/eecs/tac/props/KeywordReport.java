package edu.umich.eecs.tac.props;
import java.io.Serializable;
import java.text.ParseException;
import java.util.LinkedList;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;


public class KeywordReport implements Serializable, Transportable {

	/**Update when this file is updated.
	 * 
	 */
	private static final long serialVersionUID = -4366560152538990286L;

	LinkedList<QueryReport> yesterdaysReport = new LinkedList<QueryReport>();
	
	public String getTransportName() {
		return "keywordreport";
	}

	public void read(TransportReader reader) throws ParseException {
		// TODO Auto-generated method stub

	}

	public void write(TransportWriter writer) {
		// TODO Auto-generated method stub

	}

}

class QueryReport {

	private String queryString = new String();
	private int impressions;
	private int clicks;
	private int conversions;
	private int position;
	private float cost;
	private float revenue;
	
	public QueryReport(String s){
		queryString = s;
	}
	
	public String getQueryString() {
		return queryString;
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
	public int getConversions() {
		return conversions;
	}
	public void setConversions(int conversions) {
		this.conversions = conversions;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public float getCost() {
		return cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}
	public float getRevenue() {
		return revenue;
	}
	public void setRevenue(float revenue) {
		this.revenue = revenue;
	}
	
}