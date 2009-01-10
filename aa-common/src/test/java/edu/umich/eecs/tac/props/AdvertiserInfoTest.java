package edu.umich.eecs.tac.props;

import java.text.ParseException;
import java.util.Arrays;
import org.junit.Test;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import static org.junit.Assert.*;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

/**
 * @author Patrick Jordan
 */
public class AdvertiserInfoTest {
    @Test
    public void testEmptyInfo() {
        AdvertiserInfo info = new AdvertiserInfo();
        assertNotNull(info);
    }

    @Test
    public void testAdvertiserInfo() {
        AdvertiserInfo info = new AdvertiserInfo();
        assertNotNull(info);

        info.setAdvertiserId("a");
        info.setComponentBonus(1.0);
        info.setComponentSpecialty("b");
        info.setDistributionCapacity(10);
        info.setManufacturerBonus(2.0);
        info.setManufacturerSpecialty("c");
        info.setPublisherId("d");
        info.setDecayRate(3.0);
        info.setDistributionWindow(4);
        info.setTargetEffect(3.3);

        info.setFocusEffects(QueryType.FOCUS_LEVEL_ZERO, 0.0);
        info.setFocusEffects(QueryType.FOCUS_LEVEL_ONE, 2.2);
        info.setFocusEffects(QueryType.FOCUS_LEVEL_TWO, 4.4);

        assertEquals(info.getAdvertiserId(), "a");
        assertEquals(info.getComponentBonus(),1.0);
        assertEquals(info.getComponentSpecialty(),"b");
        assertEquals(info.getDistributionCapacity(),10);
        assertEquals(info.getManufacturerBonus(),2.0);
        assertEquals(info.getManufacturerSpecialty(),"c");
        assertEquals(info.getPublisherId(),"d");
        assertEquals(info.getDecayRate(), 3.0);
        assertEquals(info.getDistributionWindow(), 4);
        assertEquals(info.getTargetEffect(), 3.3);

        assertEquals(info.getFocusEffects(QueryType.FOCUS_LEVEL_ZERO), 0.0);
        assertEquals(info.getFocusEffects(QueryType.FOCUS_LEVEL_ONE), 2.2);
        assertEquals(info.getFocusEffects(QueryType.FOCUS_LEVEL_TWO), 4.4);

        info.lock();
        int thrown = 0;
        try {
            info.setAdvertiserId("a");
        } catch (IllegalStateException e) {
            thrown++;
        }
        try {
            info.setComponentBonus(1.0);
        } catch (IllegalStateException e) {
            thrown++;
        }
        try {
            info.setComponentSpecialty("b");
        } catch (IllegalStateException e) {
            thrown++;
        }
        try {
            info.setDistributionCapacity(10);
        } catch (IllegalStateException e) {
            thrown++;
        }
        try {
            info.setManufacturerBonus(2.0);
        } catch (IllegalStateException e) {
            thrown++;
        }
        try {
            info.setManufacturerSpecialty("c");
        } catch (IllegalStateException e) {
            thrown++;
        }
        try {
            info.setPublisherId("d");
        } catch (IllegalStateException e) {
            thrown++;
        }
        try {
            info.setDecayRate(3.0);
        } catch (IllegalStateException e) {
            thrown++;
        }
        try {
            info.setDistributionWindow(4);
        } catch (IllegalStateException e) {
            thrown++;
        }
        try {
            info.setTargetEffect(3.3);
        } catch (IllegalStateException e) {
            thrown++;
        }

        if (thrown != 10) {
            fail("Managed to call set on a locked instance of AdvertiserInfo");
        }
    }

    @Test
    public void testValidTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        AdvertiserInfo instance = new AdvertiserInfo();
        instance.setComponentBonus(100.5);
        instance.setManufacturerSpecialty("c1");
        instance.setComponentSpecialty("cs");
        instance.setPublisherId("pub");
        instance.setAdvertiserId("advertiser");

        byte[] buffer = getBytesForTransportable(writer, instance);
        AdvertiserInfo received = readFromBytes(reader, buffer, "AdvertiserInfo");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance,received);
        
        instance.lock();
        received = new AdvertiserInfo();

        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "AdvertiserInfo");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance,received);
    }

    @Test
    public void testEmptyTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        AdvertiserInfo instance = new AdvertiserInfo();

        byte[] buffer = getBytesForTransportable(writer, instance);
        AdvertiserInfo received = readFromBytes(reader, buffer, "AdvertiserInfo");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance, received);

        instance.lock();
        received = new AdvertiserInfo();

        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "AdvertiserInfo");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(instance, received);
    }


    @Test(expected = IllegalStateException.class)
    public void testWriteToLocked() {
        Pricing instance = new Pricing();
        instance.lock();

        Product product = new Product("manufacturer_1", "component_1");
        String advertisor = "advertisor_1";
        AdLink ad = new AdLink(product, advertisor);
        double price = 100.00;

        instance.setPrice(ad, price);
    }

    @Test
    public void testHashCode() {
        AdvertiserInfo instance = new AdvertiserInfo();
        instance.setComponentBonus(100.5);
        instance.setManufacturerSpecialty("c1");
        instance.setComponentSpecialty("cs");
        instance.setPublisherId("pub");
        instance.setAdvertiserId("advertiser");
        instance.setDecayRate(10.5);
        instance.setDistributionCapacity(5);
        instance.setDistributionWindow(4);
        instance.setFocusEffects(QueryType.FOCUS_LEVEL_ZERO, 0.4);
        instance.setFocusEffects(QueryType.FOCUS_LEVEL_ONE, 0.3);
        instance.setFocusEffects(QueryType.FOCUS_LEVEL_TWO, 0.3);
        instance.setManufacturerBonus(13.3);
        instance.setTargetEffect(14.4);


        int result;
        long temp;
        result = instance.getManufacturerSpecialty().hashCode();
        result = 31 * result + instance.getComponentSpecialty().hashCode();
        temp = Double.doubleToLongBits(instance.getManufacturerBonus());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(instance.getComponentBonus());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(instance.getDecayRate());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + instance.getPublisherId().hashCode();
        result = 31 * result + instance.getDistributionCapacity();
        result = 31 * result + instance.getAdvertiserId().hashCode();
        result = 31 * result + instance.getDistributionWindow();
        temp = Double.doubleToLongBits(instance.getTargetEffect());
        result = 31 * result + (int) (temp ^ (temp >>> 32));

        double[] focusEffects = new double[3];
        focusEffects[0] = instance.getFocusEffects(QueryType.FOCUS_LEVEL_ZERO);
        focusEffects[1] = instance.getFocusEffects(QueryType.FOCUS_LEVEL_ONE);
        focusEffects[2] = instance.getFocusEffects(QueryType.FOCUS_LEVEL_TWO);

        result = 31 * result + Arrays.hashCode(focusEffects);
        
        assertEquals(instance.hashCode(), result);

        instance = new AdvertiserInfo();
        assertEquals(instance.hashCode(),29791);
    }

    @Test
    public void testEquals() {
        AdvertiserInfo instance = new AdvertiserInfo();
        assertTrue(instance.equals(instance));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(Product.class));

        double componentBonus = 10.0;
        double decayRate = 0.5;
        int distributionCapacity = 5;
        int distributionWindow = 4;
        double manufacturerBonus = 0.3;
        double targetEffect = 0.4;
        String advertiserId = "ad";
        String componentSpecialty = "comp";
        String manufacturerSpecialty = "man";
        String publisherId = "pub";

        AdvertiserInfo that = new AdvertiserInfo();
        
        that.setComponentBonus(componentBonus);
        assertFalse(instance.equals(that));
        instance.setComponentBonus(componentBonus);

        that.setDecayRate(decayRate);
        assertFalse(instance.equals(that));
        instance.setDecayRate(decayRate);

        that.setDistributionCapacity(distributionCapacity);
        assertFalse(instance.equals(that));
        instance.setDistributionCapacity(distributionCapacity);

        that.setDistributionWindow(distributionWindow);
        assertFalse(instance.equals(that));
        instance.setDistributionWindow(distributionWindow);
        

        that.setManufacturerBonus(manufacturerBonus);
        assertFalse(instance.equals(that));
        instance.setManufacturerBonus(manufacturerBonus);

        that.setTargetEffect(targetEffect);
        assertFalse(instance.equals(that));
        instance.setTargetEffect(targetEffect);

        that.setAdvertiserId(advertiserId);
        assertFalse(instance.equals(that));
        assertFalse(that.equals(instance));
        instance.setAdvertiserId(advertiserId + "x");
        assertFalse(instance.equals(that));
        assertFalse(that.equals(instance));
        instance.setAdvertiserId(advertiserId);

        that.setComponentSpecialty(componentSpecialty);
        assertFalse(instance.equals(that));
        assertFalse(that.equals(instance));
        instance.setComponentSpecialty(componentSpecialty + "x");
        assertFalse(instance.equals(that));
        assertFalse(that.equals(instance));
        instance.setComponentSpecialty(componentSpecialty);

        that.setFocusEffects(QueryType.FOCUS_LEVEL_TWO, 0.4);
        assertFalse(instance.equals(that));
        instance.setFocusEffects(QueryType.FOCUS_LEVEL_TWO, 0.4);

        that.setManufacturerSpecialty(manufacturerSpecialty);
        assertFalse(instance.equals(that));
        assertFalse(that.equals(instance));
        instance.setManufacturerSpecialty(manufacturerSpecialty + "x");
        assertFalse(instance.equals(that));
        assertFalse(that.equals(instance));
        instance.setManufacturerSpecialty(manufacturerSpecialty);

        that.setPublisherId(publisherId);
        assertFalse(instance.equals(that));
        assertFalse(that.equals(instance));
        instance.setPublisherId(publisherId + "x");
        assertFalse(instance.equals(that));
        assertFalse(that.equals(instance));
        instance.setPublisherId(publisherId);

        assertTrue(instance.equals(that));
    }
}
