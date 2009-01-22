package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Patrick Jordan
 */
public class AbstractAdvertiserKeyedReportTransportableTest {

	@Test
	public void testContains() {
		assertFalse(new SimpleAbstractAdvertiserKeyedReportTransportable()
				.containsAdvertiser("a"));
	}

	private static class SimpleAbstractAdvertiserKeyedReportTransportable
			extends AbstractAdvertiserKeyedReportTransportable<AdvertiserEntry> {

		protected AdvertiserEntry createEntry(String key) {
			return null;
		}

		protected Class entryClass() {
			return AdvertiserEntry.class;
		}
	}
}
