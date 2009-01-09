package edu.umich.eecs.tac.props;

import java.text.ParseException;
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

        
        assertEquals(info.getAdvertiserId(),"a");
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
}
