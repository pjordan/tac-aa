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
	private SlotInfo slotInfo;

	@Before
	public void setup() {
		slotInfo = new SlotInfo();
	}

	@Test
	public void testConstructor() {
		assertNotNull(slotInfo);
	}

	@Test
	public void testPromotedSlots() {
		assertEquals(slotInfo.getPromotedSlots(), 0);
		slotInfo.setPromotedSlots(1);
		assertEquals(slotInfo.getPromotedSlots(), 1);
	}

	@Test
	public void testRegularSlots() {
		assertEquals(slotInfo.getRegularSlots(), 0);
		slotInfo.setRegularSlots(1);
		assertEquals(slotInfo.getRegularSlots(), 1);
	}

	@Test
	public void testPromotedSlotBonus() {
		assertEquals(slotInfo.getPromotedSlotBonus(), 0.0);
		slotInfo.setPromotedSlotBonus(1.0);
		assertEquals(slotInfo.getPromotedSlotBonus(), 1.0);
	}

	@Test
	public void testValidTransport() throws ParseException {
		BinaryTransportWriter writer = new BinaryTransportWriter();
		BinaryTransportReader reader = new BinaryTransportReader();
		reader.setContext(new AAInfo().createContext());

		byte[] buffer = getBytesForTransportable(writer, slotInfo);
		SlotInfo received = readFromBytes(reader, buffer, "SlotInfo");

		assertNotNull(received);
		assertEquals(received, slotInfo);
	}

	@Test
	public void testEquals() {
		SlotInfo other = new SlotInfo();
		other.setPromotedSlotBonus(1.0);
		assertFalse(slotInfo.equals(other));

		other = new SlotInfo();
		other.setRegularSlots(1);
		assertFalse(slotInfo.equals(other));

		other = new SlotInfo();
		other.setPromotedSlots(1);
		assertFalse(slotInfo.equals(other));

		assertEquals(slotInfo, slotInfo);
		assertFalse(slotInfo.equals(null));
		assertFalse(slotInfo.equals(""));

		other = new SlotInfo();
		assertEquals(slotInfo.hashCode(), other.hashCode());

		slotInfo.setPromotedSlotBonus(1.0);

		other.setPromotedSlotBonus(1.0);

		assertEquals(slotInfo.hashCode(), other.hashCode());
	}
}
