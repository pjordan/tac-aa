package edu.umich.eecs.tac.props;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.BinaryTransportReader;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class AuctionInfoTest {
    private AuctionInfo auctionInfo;

    @Before
    public void setup() {
        auctionInfo = new AuctionInfo();
    }

    @Test
    public void testConstructor() {
        assertNotNull(auctionInfo);
    }

    @Test
    public void testPromotedSlots() {
        assertEquals(auctionInfo.getPromotedSlots(),0);
        auctionInfo.setPromotedSlots(1);
        assertEquals(auctionInfo.getPromotedSlots(),1);
    }

    @Test
    public void testRegularSlots() {
        assertEquals(auctionInfo.getRegularSlots(),0);
        auctionInfo.setRegularSlots(1);
        assertEquals(auctionInfo.getRegularSlots(),1);
    }

    @Test
    public void testPromotedReserve() {
        assertEquals(auctionInfo.getPromotedReserve(),0.0);
        auctionInfo.setPromotedReserve(1.0);
        assertEquals(auctionInfo.getPromotedReserve(),1.0);
    }

    @Test
    public void testRegularReserve() {
        assertEquals(auctionInfo.getRegularReserve(),0.0);
        auctionInfo.setRegularReserve(1.0);
        assertEquals(auctionInfo.getRegularReserve(),1.0);
    }

    @Test
    public void testPromotedSlotBonus() {
        assertEquals(auctionInfo.getPromotedSlotBonus(),0.0);
        auctionInfo.setPromotedSlotBonus(1.0);
        assertEquals(auctionInfo.getPromotedSlotBonus(),1.0);
    }

    @Test
    public void testValidTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        byte[] buffer = getBytesForTransportable(writer, auctionInfo);
        AuctionInfo received = readFromBytes(reader, buffer, "AuctionInfo");

        assertNotNull(received);
        assertEquals(received,auctionInfo);
    }

    @Test
    public void testEquals() {
        AuctionInfo other = new AuctionInfo();

        other.setPromotedReserve(1.0);
        assertFalse(auctionInfo.equals(other));

        other = new AuctionInfo();
        other.setRegularReserve(1.0);
        assertFalse(auctionInfo.equals(other));

        other = new AuctionInfo();
        other.setPromotedSlotBonus(1.0);
        assertFalse(auctionInfo.equals(other));
        
        other = new AuctionInfo();
        other.setRegularSlots(1);
        assertFalse(auctionInfo.equals(other));

        other = new AuctionInfo();
        other.setPromotedSlots(1);
        assertFalse(auctionInfo.equals(other));

        assertEquals(auctionInfo,auctionInfo);
        assertFalse(auctionInfo.equals(null));
        assertFalse(auctionInfo.equals(""));

        other = new AuctionInfo();
        assertEquals(auctionInfo.hashCode(), other.hashCode());

        auctionInfo.setPromotedReserve(1.0);
        auctionInfo.setRegularReserve(1.0);
        auctionInfo.setPromotedSlotBonus(1.0);

        other.setPromotedReserve(1.0);
        other.setRegularReserve(1.0);
        other.setPromotedSlotBonus(1.0);

        assertEquals(auctionInfo.hashCode(), other.hashCode());
    }
}
