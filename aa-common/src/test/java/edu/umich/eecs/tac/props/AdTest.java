package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.BinaryTransportReader;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class AdTest {

    @Test
    public void testGenericAd() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        Ad ad = new Ad();

        assertNotNull(ad);
        assertTrue(ad.isGeneric());
        assertEquals(ad.toString(), "(Ad generic:true product:null)");

        byte[] buffer = getBytesForTransportable(writer, ad);
        Ad received = readFromBytes(reader, buffer, "Ad");

        assertEquals(ad.isGeneric(),received.isGeneric());
        assertEquals(ad.getProduct(),received.getProduct());
        assertEquals(ad,received);
    }

    @Test
    public void testSpecificAd() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        Ad ad = new Ad();
        ad.setProduct(new Product("m", "c"));

        assertNotNull(ad);
        assertFalse(ad.isGeneric());
        assertEquals(ad.toString(), "(Ad generic:false product:(Product (m,c)))");

        byte[] buffer = getBytesForTransportable(writer, ad);
        Ad received = readFromBytes(reader, buffer, "Ad");

        assertEquals(ad.isGeneric(),received.isGeneric());
        assertEquals(ad.getProduct(),received.getProduct());
        assertEquals(ad,received);

    }
}
