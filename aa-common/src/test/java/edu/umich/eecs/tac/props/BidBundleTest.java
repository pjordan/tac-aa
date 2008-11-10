package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.BinaryTransportReader;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class BidBundleTest {

    @Test
    public void testEmptyBidBundle() {
        BidBundle bundle = new BidBundle();

        assertNotNull(bundle);
    }

    @Test
    public void testUnitBidBundle() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        BidBundle bundle = new BidBundle();

        assertNotNull(bundle);

        bundle.addQuery(new Query());

        assertEquals(bundle.size(), 1);

        byte[] buffer = getBytesForTransportable(writer,bundle);
        BidBundle received = readFromBytes(reader,buffer,"BidBundle");

        assertNotNull(bundle);
        assertNotNull(received);
        assertEquals(received.size(),1);
    }
}
