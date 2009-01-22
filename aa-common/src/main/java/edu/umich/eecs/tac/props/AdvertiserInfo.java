package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;
import java.util.Arrays;

/**
 * AdvertiserInfo contains necessary game information unique to each individual advertiser.
 * AdvertiserInfo contains:
 * <ul>
 *  <li> Manufacturer Specialty - The manufacturer specialization for the advertiser</li>
 *  <li> Component Specialty - The component specialization for the advertiser</li>
 *  <li> Manufacturer Specialist Bonus - The bonus given for a product containing the advertiser's
 * manufacturer specialty</li>
 *  <li> Component Specialist Bonus - The bonus given for a product containing the advertiser's
 * component specialty</li>
 *  <li> Distribution Capacity - The advertiser's daily distribution capacity threshold.</li  
 *
 * @author Patrick Jordan, Lee Callender
 */
public class AdvertiserInfo extends AbstractTransportable {
	private String manufacturerSpecialty;
	private String componentSpecialty;
	private double manufacturerBonus;
	private double componentBonus;
	private double distributionCapacityDiscounter;
	private String publisherId;
	private int distributionCapacity;
	private String advertiserId;
	private int distributionWindow;
	private double targetEffect;
	private double[] focusEffects;

	public AdvertiserInfo() {
		focusEffects = new double[QueryType.values().length];
	}

	public double getFocusEffects(QueryType queryType) {
		return focusEffects[queryType.ordinal()];
	}

	public void setFocusEffects(QueryType queryType, double focusEffect) {
		lockCheck();
		this.focusEffects[queryType.ordinal()] = focusEffect;
	}

	public double getTargetEffect() {
		return targetEffect;
	}

	public void setTargetEffect(double targetEffect) {
		lockCheck();
		this.targetEffect = targetEffect;
	}

	public int getDistributionWindow() {
		return distributionWindow;
	}

	public void setDistributionWindow(int distributionWindow) {
		lockCheck();
		this.distributionWindow = distributionWindow;
	}

	public String getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(String advertiserId) {
		lockCheck();
		this.advertiserId = advertiserId;
	}

	public String getManufacturerSpecialty() {
		return manufacturerSpecialty;
	}

	public void setManufacturerSpecialty(String manufacturerSpecialty) {
		lockCheck();
		this.manufacturerSpecialty = manufacturerSpecialty;
	}

	public String getComponentSpecialty() {
		return componentSpecialty;
	}

	public void setComponentSpecialty(String componentSpecialty) {
		lockCheck();
		this.componentSpecialty = componentSpecialty;
	}

	public double getManufacturerBonus() {
		return manufacturerBonus;
	}

	public void setManufacturerBonus(double manufacturerBonus) {
		lockCheck();
		this.manufacturerBonus = manufacturerBonus;
	}

	public double getComponentBonus() {
		return componentBonus;
	}

	public void setComponentBonus(double componentBonus) {
		lockCheck();
		this.componentBonus = componentBonus;
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		lockCheck();
		this.publisherId = publisherId;
	}

	public int getDistributionCapacity() {
		return distributionCapacity;
	}

	public void setDistributionCapacity(int distributionCapacity) {
		lockCheck();
		this.distributionCapacity = distributionCapacity;
	}

	public double getDistributionCapacityDiscounter() {
		return distributionCapacityDiscounter;
	}

	public void setDistributionCapacityDiscounter(double distributionCapacityDiscounter) {
		lockCheck();
		this.distributionCapacityDiscounter = distributionCapacityDiscounter;
	}

	protected void readWithLock(TransportReader reader) throws ParseException {
		manufacturerSpecialty = reader.getAttribute("manufacturerSpecialty",
				null);
		manufacturerBonus = reader.getAttributeAsDouble("manufacturerBonus",
				0.0);
		componentSpecialty = reader.getAttribute("componentSpecialty", null);
		componentBonus = reader.getAttributeAsDouble("componentBonus", 0.0);
		distributionCapacityDiscounter = reader.getAttributeAsDouble("distributionCapacityDiscounter", 1.0);
		publisherId = reader.getAttribute("publisherId", null);
		distributionCapacity = reader.getAttributeAsInt("distributionCapacity");
		advertiserId = reader.getAttribute("advertiserId", null);
		distributionWindow = reader.getAttributeAsInt("distributionWindow");
		targetEffect = reader.getAttributeAsDouble("targetEffect", 0.0);

		for (QueryType type : QueryType.values()) {
			focusEffects[type.ordinal()] = reader.getAttributeAsDouble(String
					.format("focusEffect[%s]", type.name()), 1.0);
		}
	}

	protected void writeWithLock(TransportWriter writer) {
		if (manufacturerSpecialty != null) {
			writer.attr("manufacturerSpecialty", manufacturerSpecialty);
		}

		writer.attr("manufacturerBonus", manufacturerBonus);

		if (componentSpecialty != null) {
			writer.attr("componentSpecialty", componentSpecialty);
		}

		writer.attr("componentBonus", componentBonus);
		writer.attr("distributionCapacityDiscounter", distributionCapacityDiscounter);

		if (publisherId != null) {
			writer.attr("publisherId", publisherId);
		}

		writer.attr("distributionCapacity", distributionCapacity);

		if (advertiserId != null) {
			writer.attr("advertiserId", advertiserId);
		}

		writer.attr("distributionWindow", distributionWindow);

		writer.attr("targetEffect", targetEffect);

		for (QueryType type : QueryType.values()) {
			writer.attr(String.format("focusEffect[%s]", type.name()),
					focusEffects[type.ordinal()]);
		}
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		AdvertiserInfo that = (AdvertiserInfo) o;

		if (Double.compare(that.componentBonus, componentBonus) != 0)
			return false;
		if (Double.compare(that.distributionCapacityDiscounter, distributionCapacityDiscounter) != 0)
			return false;
		if (distributionCapacity != that.distributionCapacity)
			return false;
		if (distributionWindow != that.distributionWindow)
			return false;
		if (Double.compare(that.manufacturerBonus, manufacturerBonus) != 0)
			return false;
		if (Double.compare(that.targetEffect, targetEffect) != 0)
			return false;
		if (advertiserId != null ? !advertiserId.equals(that.advertiserId)
				: that.advertiserId != null)
			return false;
		if (componentSpecialty != null ? !componentSpecialty
				.equals(that.componentSpecialty)
				: that.componentSpecialty != null)
			return false;
		if (!Arrays.equals(focusEffects, that.focusEffects))
			return false;
		if (manufacturerSpecialty != null ? !manufacturerSpecialty
				.equals(that.manufacturerSpecialty)
				: that.manufacturerSpecialty != null)
			return false;
		if (publisherId != null ? !publisherId.equals(that.publisherId)
				: that.publisherId != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		long temp;
		result = (manufacturerSpecialty != null ? manufacturerSpecialty
				.hashCode() : 0);
		result = 31
				* result
				+ (componentSpecialty != null ? componentSpecialty.hashCode()
						: 0);
		temp = manufacturerBonus != +0.0d ? Double
				.doubleToLongBits(manufacturerBonus) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = componentBonus != +0.0d ? Double
				.doubleToLongBits(componentBonus) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = distributionCapacityDiscounter != +0.0d ? Double.doubleToLongBits(distributionCapacityDiscounter) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result
				+ (publisherId != null ? publisherId.hashCode() : 0);
		result = 31 * result + distributionCapacity;
		result = 31 * result
				+ (advertiserId != null ? advertiserId.hashCode() : 0);
		result = 31 * result + distributionWindow;
		temp = targetEffect != +0.0d ? Double.doubleToLongBits(targetEffect)
				: 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + Arrays.hashCode(focusEffects);
		return result;
	}
}
