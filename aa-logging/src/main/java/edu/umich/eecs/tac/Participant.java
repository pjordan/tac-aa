package edu.umich.eecs.tac;

import edu.umich.eecs.tac.props.BankStatus;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.logtool.ParticipantInfo;

import java.util.Comparator;

/**
 * @author Patrick Jordan
 */
public class Participant {
	private ParticipantInfo info;
	private double totalResult;
	private double totalCost;
	private double totalRevenue;
	private long totalImpressions;
	private long totalClicks;
	private long totalConversions;

	private StartInfo startInfo;

	private int numberOfDays;

	private static Comparator<Participant> resultComparator;

	public Participant(ParticipantInfo info) {
		this.info = info;
	}

	/**
	 * Returns a comparator that puts the highest values first.
	 */
	public static Comparator getResultComparator() {
		if (resultComparator == null) {
			resultComparator = new Comparator<Participant>() {
				public int compare(Participant o1, Participant o2) {
					double r1 = o1.getResult();
					double r2 = o2.getResult();
					return -Double.compare(r1, r2);
				}

				public boolean equals(Object obj) {
					return obj == this;
				}
			};
		}
		return resultComparator;
	}

	public ParticipantInfo getInfo() {
		return info;
	}

	public StartInfo getStartInfo() {
		return startInfo;
	}

	public double getResult() {
		return totalResult;
	}

	public void setResult(double result) {
		this.totalResult = result;
	}

	public double getCost() {
		return totalCost;
	}

	public void setCost(double Cost) {
		this.totalCost = Cost;
	}

	public void addCost(double Cost) {
		this.totalCost += Cost;
	}

	public double getRevenue() {
		return totalRevenue;
	}

	public void setRevenue(double revenue) {
		this.totalRevenue = revenue;
	}

	public void addRevenue(double revenue) {
		this.totalRevenue += revenue;
	}

	public long getImpressions() {
		return totalImpressions;
	}

	public void setImpressions(long impressions) {
		this.totalImpressions = impressions;
	}

	public void addImpressions(long impressions) {
		this.totalImpressions += impressions;
	}

	public long getClicks() {
		return totalClicks;
	}

	public void setClicks(long clicks) {
		this.totalClicks = clicks;
	}

	public void addClicks(long clicks) {
		this.totalClicks += clicks;
	}

	public long getConversions() {
		return totalConversions;
	}

	public void setConversions(long conversions) {
		this.totalConversions = conversions;
	}

	public void addConversions(long conversions) {
		this.totalConversions += conversions;
	}

	public double getCTR() {
		return getClicks() / ((double) getImpressions());
	}

	public double getCPC() {
		return getCost() / ((double) getClicks());
	}

	public double getCPI() {
		return getCost() / ((double) getImpressions());
	}

	public double getROI() {
		return getResult() / getCost();
	}

	public double getConversionRate() {
		return getConversions() / ((double) getClicks());
	}

	public double getValuePerClick() {
		return getResult() / ((double) getClicks());
	}

	public double getValuePerImpression() {
		return getResult() / ((double) getImpressions());
	}

	// -------------------------------------------------------------------
	// Information through messages sent and received
	// -------------------------------------------------------------------

	public void messageReceived(int date, int sender, Transportable content) {
		if (content instanceof BankStatus) {

		} else if (content instanceof StartInfo) {
			this.startInfo = (StartInfo) content;
			this.numberOfDays = this.startInfo.getNumberOfDays();
		}
	}

	public void messageSent(int date, int receiver, Transportable content) {
	}

	public void messageSentToRole(int date, int role, Transportable content) {
	}

	// -------------------------------------------------------------------
	// For debug output
	// -------------------------------------------------------------------

	public String toString() {
		StringBuffer sb = new StringBuffer().append("Participant[").append(
				totalCost).append(',').append(totalRevenue).append(',').append(
				totalResult);
		return sb.append(']').toString();
	}
}
