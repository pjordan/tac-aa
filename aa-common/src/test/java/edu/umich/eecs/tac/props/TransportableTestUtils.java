/**
 * Created by IntelliJ IDEA.
 * User: pjordan
 * Date: Nov 5, 2008
 * Time: 4:24:05 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.BinaryTransportReader;

import java.text.ParseException;

public class TransportableTestUtils {
	private TransportableTestUtils() {
	}

	public static void writeTransportableNode(TransportWriter writer,
			Transportable transportable) {
		String node = transportable.getTransportName();
		writer.node(node);
		transportable.write(writer);
		writer.endNode(node);
	}

	public static byte[] getBytesForTransportable(BinaryTransportWriter writer,
			Transportable transportable) {
		writeTransportableNode(writer, transportable);
		writer.finish();
		byte[] buffer = writer.getBytes();
		writer.clear();
		return buffer;
	}

	public static <T extends Transportable> T readFromBytes(
			BinaryTransportReader reader, byte[] buffer, String nodeName)
			throws ParseException {
		reader.setMessage(buffer);

		if (!reader.nextNode(nodeName, false)) {
			return null;
		} else {
			return (T) reader.readTransportable();
		}
	}
}
