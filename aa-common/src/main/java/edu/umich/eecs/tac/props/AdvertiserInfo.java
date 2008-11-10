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


    public int getDistributionWindow() {
        return distributionWindow;
    }

    public void setDistributionWindow(int distributionWindow) {
        this.distributionWindow = distributionWindow;
    }

    public String getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(String advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getManufacturerSpecialty() {
        return manufacturerSpecialty;
    }

    public void setManufacturerSpecialty(String manufacturerSpecialty) {
        this.manufacturerSpecialty = manufacturerSpecialty;
    }

    public String getComponentSpecialty() {
        return componentSpecialty;
    }

    public void setComponentSpecialty(String componentSpecialty) {
        this.componentSpecialty = componentSpecialty;
    }

    public double getManufacturerBonus() {
        return manufacturerBonus;
    }

    public void setManufacturerBonus(double manufacturerBonus) {
        this.manufacturerBonus = manufacturerBonus;
    }

    public double getComponentBonus() {
        return componentBonus;
    }

    public void setComponentBonus(double componentBonus) {
        this.componentBonus = componentBonus;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public int getDistributionCapacity() {
        return distributionCapacity;
    }

    public void setDistributionCapacity(int distributionCapacity) {
        this.distributionCapacity = distributionCapacity;
    }

    public double getDecayRate() {
        return decayRate;
    }

    public void setDecayRate(double decayRate) {
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
    }
}
