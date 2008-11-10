package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;

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


        assertEquals(info.getAdvertiserId(),"a");
        assertEquals(info.getComponentBonus(),1.0);
        assertEquals(info.getComponentSpecialty(),"b");
        assertEquals(info.getDistributionCapacity(),10);
        assertEquals(info.getManufacturerBonus(),2.0);
        assertEquals(info.getManufacturerSpecialty(),"c");
        assertEquals(info.getPublisherId(),"d");
    }

}
