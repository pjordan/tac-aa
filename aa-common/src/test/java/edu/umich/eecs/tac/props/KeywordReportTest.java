package edu.umich.eecs.tac.props;

import java.text.ParseException;

import se.sics.isl.transport.*;
import org.junit.Test;

public class KeywordReportTest {

	@Test
	public void testKeywordReport() throws ParseException {
		KeywordReport temp = new KeywordReport();
		temp.addQuery("pg", 100, .25f, 500, 3.4f);
		temp.addQuery("lioneer", 200, .35f, 500, 2.3f);
		temp.addQuery("flat", 350, .43f, 500, 1.0f);
		System.out.println(temp.toString());
		BinaryTransportWriter writer = new BinaryTransportWriter();
		temp.write(writer);
		byte [] buffer = new byte [1024];
		writer.write(buffer);
		BinaryTransportReader reader = new BinaryTransportReader();
		reader.setMessage(buffer);
		KeywordReport received = new KeywordReport();
		received.read(reader);
		System.out.println(received.toString());
	}
}
