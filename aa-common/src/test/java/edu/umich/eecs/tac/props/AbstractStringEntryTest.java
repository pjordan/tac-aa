package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;

import java.text.ParseException;

import se.sics.isl.transport.*;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

/**
 * @author Patrick Jordan
 */
public class AbstractStringEntryTest {
    @Test
    public void testValidTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();


        Context context = new Context("testcontext");
        context.addTransportable(new SimpleAbstractStringEntry());

        reader.setContext(context);

        SimpleAbstractStringEntry entry = new SimpleAbstractStringEntry();

        byte[] buffer = getBytesForTransportable(writer,entry);
        SimpleAbstractStringEntry received = readFromBytes(reader,buffer,"SimpleAbstractStringEntry");

        assertNotNull(received);        
    }

    public static class SimpleAbstractStringEntry extends AbstractStringEntry {
        protected void readEntry(TransportReader reader) throws ParseException {
        }

        protected void writeEntry(TransportWriter writer) {
        }
    }
}
