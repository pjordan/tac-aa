package edu.umich.eecs.tac.props;

import java.io.Serializable;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class SalesReport implements Transportable, Serializable{

	private static final long serialVersionUID = 3473199640271355791L;
	
	private List<QuerySalesReport> sales;
	private boolean isLocked = false;
	
	public boolean isLocked() {
		return isLocked;
	}

	public void lock() {
		isLocked = true;
	}
	
	public String getTransportName() {
		return "salesreport";
	}
	public SalesReport(){
		sales = new LinkedList<QuerySalesReport>();
	}
	
	public void addQuery(){
		QuerySalesReport temp = new QuerySalesReport();
		sales.add(temp);
	}
	
	public void addQuery(String query){
		QuerySalesReport temp = new QuerySalesReport(query);
		sales.add(temp);
	}
	
	public void addQuery(String query, int conversions, double revenue){
		QuerySalesReport temp = new QuerySalesReport(query, conversions, revenue);
		sales.add(temp);
	}
	
	public void setQueryConversions(String query, int conversions){
		int i;
		for(i = 0; i < sales.size(); i++){
			if(sales.get(i).query.equals(query)){
				sales.get(i).setConversions(conversions);
				return;
			}
		}
		QuerySalesReport temp = new QuerySalesReport(query);
		temp.setConversions(conversions);
		sales.add(temp);
	}
	
	public void setQueryConversions(int index, int conversions){
		sales.get(index).setConversions(conversions);
	}
	
	public void setQueryRevenue(String query, double revenue){
		int i;
		for(i = 0; i < sales.size(); i++){
			if(sales.get(i).query.equals(query)){
				sales.get(i).setRevenue(revenue);
				return;
			}
		}
		QuerySalesReport temp = new QuerySalesReport(query);
		temp.setRevenue(revenue);
		sales.add(temp);
	}
	
	public void setQueryRevenue(int index, double revenue){
		sales.get(index).setRevenue(revenue);
	}
	
	public int getSize(){
		return sales.size();
	}
	
	public int getQueryConversions(String query){
		int i;
		for(i = 0; i < sales.size(); i++){
			if(sales.get(i).query.equals(query)){
				return sales.get(i).getConversions();
			}
		}
		return 0;
	}
	
	public int getQueryConversions(int i){
		return sales.get(i).getConversions();
	}
	
	public double getQueryRevenue(int i){
		return sales.get(i).getRevenue();
	}
	
	public String toString(){
		String r = new String();
		QuerySalesReport temp;
		int i;
		for(i = 0; i < sales.size(); i++){
			temp = sales.get(i);
			r += "Query: " + temp.getQuery() + " ";
			r += "Conversions: " + temp.getConversions() + " ";
			r += "Revenue: " + temp.getRevenue() + "\n";
		}
		return r;
	}
	
	@Override
	public void read(TransportReader reader) throws ParseException {
	    if (isLocked) {
	        throw new IllegalStateException("locked");
	    }
		boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
		while(reader.nextNode("salesreport", false)){
			QuerySalesReport temp = new QuerySalesReport();
			temp.setQuery(reader.getAttribute("query"));
			temp.setConversions(reader.getAttributeAsInt("conversions"));
			temp.setRevenue(reader.getAttributeAsDouble("revenue"));
			sales.add(temp);
		}
	    if(lock){
	        lock();
	    }
	}

	@Override
	public void write(TransportWriter writer) {
		if (isLocked) {
			writer.attr("lock", 1);
	    }
		int i;
		QuerySalesReport temp;
		for(i = 0; i < sales.size(); i++){
			temp = sales.get(i);
			writer.node("salesreport");
			writer.attr("query", temp.getQuery());
			writer.attr("conversions", temp.getConversions());
			writer.attr("revenue", temp.getRevenue());
			writer.endNode("salesreport");
		}
	}
		
	private static class QuerySalesReport{
		
		private String query = "";
		private int conversions = 0;
		private double revenue = 0;
		
		public QuerySalesReport(){}
		
		public QuerySalesReport(String q){
			query = q;
		}
		
		public QuerySalesReport(String q, int c, double r){
			query = q;
			conversions = c;
			revenue = r;
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public int getConversions() {
			return conversions;
		}

		public void setConversions(int conversions) {
			this.conversions = conversions;
		}

		public double getRevenue() {
			return revenue;
		}

		public void setRevenue(double revenue) {
			this.revenue = revenue;
		}
		
	}

}
