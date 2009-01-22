package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class PublisherInfoTest {
	@Test
	public void testConstructor() {
		PublisherInfo p = new PublisherInfo();
		assertNotNull(p);
	}

	@Test
	public void testAccessors() {
		PublisherInfo p = new PublisherInfo();
		assertEquals(p.getSquashingParameter(), 0.0);
		p.setSquashingParameter(1.0);
		assertEquals(p.getSquashingParameter(), 1.0);
	}

	@Test
	public void testValidTransport() throws ParseException {
		BinaryTransportWriter writer = new BinaryTransportWriter();
		BinaryTransportReader reader = new BinaryTransportReader();
		reader.setContext(new AAInfo().createContext());

		PublisherInfo info = new PublisherInfo();

		byte[] buffer = getBytesForTransportable(writer, info);
		PublisherInfo received = readFromBytes(reader, buffer, "PublisherInfo");
		assertNotNull(received);
		assertEquals(info.getSquashingParameter(), received
				.getSquashingParameter());
	}
}
