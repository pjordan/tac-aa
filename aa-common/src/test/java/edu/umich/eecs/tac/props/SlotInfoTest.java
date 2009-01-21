package edu.umich.eecs.tac.props;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.text.ParseException;

import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.BinaryTransportReader;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

/**
 * @author Patrick Jordan
 */
public class SlotInfoTest {
    private ReserveInfo reserveInfo;

    @Before
    public void setup() {
        reserveInfo = new ReserveInfo();
    }

    @Test
    public void testConstructor() {
        assertNotNull(reserveInfo);
    }

    @Test
    public void testPromotedReserve() {
        assertEquals(reserveInfo.getPromotedReserve(),0.0);
        reserveInfo.setPromotedReserve(1.0);
        assertEquals(reserveInfo.getPromotedReserve(),1.0);
    }

    @Test
    public void testRegularReserve() {
        assertEquals(reserveInfo.getRegularReserve(),0.0);
        reserveInfo.setRegularReserve(1.0);
        assertEquals(reserveInfo.getRegularReserve(),1.0);
    }



    @Test
    public void testValidTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        byte[] buffer = getBytesForTransportable(writer, reserveInfo);
        ReserveInfo received = readFromBytes(reader, buffer, "ReserveInfo");

        assertNotNull(received);
        assertEquals(received, reserveInfo);
    }

    @Test
    public void testEquals() {
        ReserveInfo other = new ReserveInfo();

        other.setPromotedReserve(1.0);
        assertFalse(reserveInfo.equals(other));

        other = new ReserveInfo();
        other.setRegularReserve(1.0);
        assertFalse(reserveInfo.equals(other));


        assertEquals(reserveInfo, reserveInfo);
        assertFalse(reserveInfo.equals(null));
        assertFalse(reserveInfo.equals(""));

        other = new ReserveInfo();
        assertEquals(reserveInfo.hashCode(), other.hashCode());

        reserveInfo.setPromotedReserve(1.0);
        reserveInfo.setRegularReserve(1.0);

        other.setPromotedReserve(1.0);
        other.setRegularReserve(1.0);

        assertEquals(reserveInfo.hashCode(), other.hashCode());
    }
}
