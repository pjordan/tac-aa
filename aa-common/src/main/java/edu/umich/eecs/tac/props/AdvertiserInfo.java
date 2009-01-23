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
 *  <li> Distribution Capacity - The advertiser's daily distribution capacity threshold.</li>
 * </ul>
 * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
 * @author Patrick Jordan, Lee Callender
 */
public class AdvertiserInfo extends AbstractTransportable {
    /**
     * The manufacturer specialty (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private String manufacturerSpecialty;
    /**
     * The component specialty (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private String componentSpecialty;
    /**
     * The manufacturer bonus (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private double manufacturerBonus;
    /**
     * The component bonus (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private double componentBonus;
    /**
     * The decay rate (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private double distributionCapacityDiscounter;
    /**
     * The address id of the publisher agent.
     */
    private String publisherId;
    /**
     * The distribution capacity (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private int distributionCapacity;
    /**
     * The address id of the advertiser agent.
     */
    private String advertiserId;
    /**
     * The distribution window (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private int distributionWindow;
    /**
     * The target effect (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private double targetEffect;
    /**
     * The focus effects (see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>).
     */
    private double[] focusEffects;

    /**
     * Creates a new advertiser information object.
     */
    public AdvertiserInfo() {
        focusEffects = new double[QueryType.values().length];
    }

    /**
     * Returns the focus effect for the given {@link QueryType query type}.
     * @param queryType the query type.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     * @return the focus effect for the given {@link QueryType query type}.
     */
    public final double getFocusEffects(final QueryType queryType) {
        return focusEffects[queryType.ordinal()];
    }

    /**
     * Sets the focus effect for the given {@link QueryType query type}.
     * @param queryType queryType the query type.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     * @param focusEffect the focus effect.
     */
    public final void setFocusEffects(final QueryType queryType, final double focusEffect) {
        lockCheck();
        this.focusEffects[queryType.ordinal()] = focusEffect;
    }

    /**
     * Returns the target effect.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     * @return the target effect.
     */
    public final double getTargetEffect() {
        return targetEffect;
    }

    /**
     * Sets the target effect.
     * @param targetEffect the target effect.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final void setTargetEffect(final double targetEffect) {
        lockCheck();
        this.targetEffect = targetEffect;
    }

    /**
     * Returns the distribution window.
     * @return the distribution window.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final int getDistributionWindow() {
        return distributionWindow;
    }

    /**
     * Sets the distribution window.
     * @param distributionWindow the distribution window.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final void setDistributionWindow(final int distributionWindow) {
        lockCheck();
        this.distributionWindow = distributionWindow;
    }

    /**
     * Returns the advertiser address.
     * @return the advertiser address.
     */
    public final String getAdvertiserId() {
        return advertiserId;
    }

    /**
     * Sets the advertiser address.
     * @param advertiserId the advertiser address.
     */
    public final void setAdvertiserId(final String advertiserId) {
        lockCheck();
        this.advertiserId = advertiserId;
    }

    /**
     * Returns the manufacturer specialty.
     * @return the manufacturer specialty.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final String getManufacturerSpecialty() {
        return manufacturerSpecialty;
    }

    /**
     * Sets the manufacturer specialty.
     * @param manufacturerSpecialty the manufacturer specialty.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final void setManufacturerSpecialty(final String manufacturerSpecialty) {
        lockCheck();
        this.manufacturerSpecialty = manufacturerSpecialty;
    }

    /**
     * Returns the component specialty.
     * @return the component specialty.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final String getComponentSpecialty() {
        return componentSpecialty;
    }

    /**
     * Sets the component specialty.
     * @param componentSpecialty the component specialty.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final void setComponentSpecialty(final String componentSpecialty) {
        lockCheck();
        this.componentSpecialty = componentSpecialty;
    }

    /**
     * Returns the manufacturer bonus.
     * @return the manufacturer bonus.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final double getManufacturerBonus() {
        return manufacturerBonus;
    }

    /**
     * Sets the manufacturer bonus.
     * @param manufacturerBonus the manufacturer bonus.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final void setManufacturerBonus(final double manufacturerBonus) {
        lockCheck();
        this.manufacturerBonus = manufacturerBonus;
    }

    /**
     * Returns the component bonus.
     * @return the component bonus.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final double getComponentBonus() {
        return componentBonus;
    }

    /**
     * Sets the component bonus.
     * @param componentBonus the component bonus.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final void setComponentBonus(final double componentBonus) {
        lockCheck();
        this.componentBonus = componentBonus;
    }

    /**
     * Returns the publisher address.
     * @return the publisher address.
     */
    public final String getPublisherId() {
        return publisherId;
    }

    /**
     * Sets the publisher address.
     * @param publisherId the publisher address.
     */
    public final void setPublisherId(final String publisherId) {
        lockCheck();
        this.publisherId = publisherId;
    }

    /**
     * Returns the distribution capacity.
     * @return the distribution capacity.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final int getDistributionCapacity() {
        return distributionCapacity;
    }

    /**
     * Sets the distribution capacity.
     * @param distributionCapacity the distribution capacity.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final void setDistributionCapacity(final int distributionCapacity) {
        lockCheck();
        this.distributionCapacity = distributionCapacity;
    }

    /**
     * Returns the decay rate.
     * @return the decay rate.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final double getDistributionCapacityDiscounter() {
        return distributionCapacityDiscounter;
    }

    /**
     * Sets the decay rate.
     * @param distributionCapacityDiscounter the decay rate.
     * @see <a href="http://aa.tradingagents.org/documentation">TAC Documentation</a>
     */
    public final void setDistributionCapacityDiscounter(final double distributionCapacityDiscounter) {
        lockCheck();
        this.distributionCapacityDiscounter = distributionCapacityDiscounter;
    }

    /**
     * Reads the advertiser information parameters from the reader.
     * @param reader the reader to read data from.
     * @throws ParseException if an exception occurs when reading the parameters.
     */
    @Override
    protected final void readWithLock(final TransportReader reader) throws ParseException {
        manufacturerSpecialty = reader.getAttribute("manufacturerSpecialty", null);
        manufacturerBonus = reader.getAttributeAsDouble("manufacturerBonus", 0.0);
        componentSpecialty = reader.getAttribute("componentSpecialty", null);
        componentBonus = reader.getAttributeAsDouble("componentBonus", 0.0);
        distributionCapacityDiscounter = reader.getAttributeAsDouble("distributionCapacityDiscounter", 1.0);
        publisherId = reader.getAttribute("publisherId", null);
        distributionCapacity = reader.getAttributeAsInt("distributionCapacity");
        advertiserId = reader.getAttribute("advertiserId", null);
        distributionWindow = reader.getAttributeAsInt("distributionWindow");
        targetEffect = reader.getAttributeAsDouble("targetEffect", 0.0);

        for (QueryType type : QueryType.values()) {
            focusEffects[type.ordinal()] = reader.getAttributeAsDouble(
                    String.format("focusEffect[%s]", type.name()), 1.0);
        }
    }

    /**
     * Writes the advertiser information parameters to the writer.
     * @param writer the writer to write data to.
     */
    @Override
    protected final void writeWithLock(final TransportWriter writer) {
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

    /**
     * Checks to see if the parameter configuration is the same.
     *
     * @param o the object to check equality
     * @return whether the object has the same parameter configuration.
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AdvertiserInfo that = (AdvertiserInfo) o;

        if (Double.compare(that.componentBonus, componentBonus) != 0) {
            return false;
        }

        if (Double.compare(that.distributionCapacityDiscounter, distributionCapacityDiscounter) != 0) {
            return false;
        }

        if (distributionCapacity != that.distributionCapacity) {
            return false;
        }

        if (distributionWindow != that.distributionWindow) {
            return false;
        }

        if (Double.compare(that.manufacturerBonus, manufacturerBonus) != 0) {
            return false;
        }

        if (Double.compare(that.targetEffect, targetEffect) != 0) {
            return false;
        }

        if (advertiserId != null ? !advertiserId.equals(that.advertiserId) : that.advertiserId != null) {
            return false;
        }

        if (componentSpecialty != null ?
            !componentSpecialty.equals(that.componentSpecialty) : that.componentSpecialty != null) {
            return false;
        }

        if (!Arrays.equals(focusEffects, that.focusEffects)) {
            return false;
        }

        if (manufacturerSpecialty != null ? !manufacturerSpecialty.equals(that.manufacturerSpecialty)
                : that.manufacturerSpecialty != null) {
            return false;
        }

        if (publisherId != null ? !publisherId.equals(that.publisherId) : that.publisherId != null) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code from the configuration parameters.
     * @return the hash code from the configuration parameters.
     */
    @Override
    public final int hashCode() {
        int result;
        long temp;
        result = (manufacturerSpecialty != null ? manufacturerSpecialty.hashCode() : 0);
        result = 31 * result + (componentSpecialty != null ? componentSpecialty.hashCode() : 0);
        temp = manufacturerBonus != +0.0d ? Double.doubleToLongBits(manufacturerBonus) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = componentBonus != +0.0d ? Double.doubleToLongBits(componentBonus) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = distributionCapacityDiscounter != +0.0d ? Double.doubleToLongBits(distributionCapacityDiscounter) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (publisherId != null ? publisherId.hashCode() : 0);
        result = 31 * result + distributionCapacity;
        result = 31 * result + (advertiserId != null ? advertiserId.hashCode() : 0);
        result = 31 * result + distributionWindow;
        temp = targetEffect != +0.0d ? Double.doubleToLongBits(targetEffect) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.hashCode(focusEffects);
        return result;
    }
}
