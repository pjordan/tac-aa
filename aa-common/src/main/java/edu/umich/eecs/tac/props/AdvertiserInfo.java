package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class AdvertiserInfo extends AbstractTransportable {
    private String manufacturerSpecialty;
    private String componentSpecialty;
    private double manufacturerBonus;
    private double componentBonus;
    private double decayRate;
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

    public double getDecayRate() {
        return decayRate;
    }

    public void setDecayRate(double decayRate) {
        lockCheck();
        this.decayRate = decayRate;
    }

    protected void readWithLock(TransportReader reader) throws ParseException {
        manufacturerSpecialty = reader.getAttribute("manufacturerSpecialty");
        manufacturerBonus = reader.getAttributeAsDouble("manufacturerBonus", 0.0);
        componentSpecialty = reader.getAttribute("componentSpecialty");
        componentBonus = reader.getAttributeAsDouble("componentBonus", 0.0);
        decayRate = reader.getAttributeAsDouble("decayRate", 1.0);
        publisherId = reader.getAttribute("publisherId");
        distributionCapacity = reader.getAttributeAsInt("distributionCapacity");
        advertiserId = reader.getAttribute("advertiserId");
        distributionWindow = reader.getAttributeAsInt("distributionWindow");
        targetEffect = reader.getAttributeAsDouble("targetEffect", 0.0);
        for(QueryType type : QueryType.values()) {
            focusEffects[type.ordinal()] = reader.getAttributeAsDouble(String.format("focusEffect[%s]",type.name()), 1.0);
        }
    }

    protected void writeWithLock(TransportWriter writer) {
        writer.attr("manufacturerSpecialty", manufacturerSpecialty);
        writer.attr("manufacturerBonus", manufacturerBonus);
        writer.attr("componentSpecialty", componentSpecialty);
        writer.attr("componentBonus", componentBonus);
        writer.attr("decayRate", decayRate);
        writer.attr("publisherId", publisherId);
        writer.attr("distributionCapacity", distributionCapacity);
        writer.attr("advertiserId", advertiserId);
        writer.attr("distributionWindow", distributionWindow);
        writer.attr("targetEffect", targetEffect);

        for(QueryType type : QueryType.values()) {
            writer.attr(String.format("focusEffect[%s]",type.name()),focusEffects[type.ordinal()]);
        }
    }
}
