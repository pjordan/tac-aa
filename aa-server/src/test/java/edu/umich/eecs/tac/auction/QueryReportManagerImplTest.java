package edu.umich.eecs.tac.auction;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.umich.eecs.tac.sim.QueryReportSender;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Ad;

/**
 * @author Patrick Jordan
 */
public class QueryReportManagerImplTest {

	@Test
	public void testConstructor() {
		QueryReportManager queryReportManager = new QueryReportManagerImpl(
				new QueryReportSenderImpl(), 0);
		assertNotNull(queryReportManager);
	}

	@Test
	public void testAddAdvertiser() {
		QueryReportManager queryReportManager = new QueryReportManagerImpl(
				new QueryReportSenderImpl(), 0);

		String advertiser = "alice";

		queryReportManager.addAdvertiser(advertiser);

		assertEquals(queryReportManager.size(), 1, 0);

		queryReportManager.addAdvertiser(advertiser);

		assertEquals(queryReportManager.size(), 1, 0);

		for (int i = 0; i < 8; i++) {
			queryReportManager.addAdvertiser("" + i);
			assertEquals(queryReportManager.size(), i + 2, 0);
		}
	}

	@Test
	public void testSendQueryReports() {
		QueryReportManagerImpl queryReportManager = new QueryReportManagerImpl(
				new QueryReportSenderImpl(), 0);

		String alice = "alice";
		String bob = "bob";

		queryReportManager.addAdvertiser(alice);
		queryReportManager.addAdvertiser(bob);
		assertEquals(queryReportManager.size(), 2, 0);

		queryReportManager.sendQueryReportToAll();

		Query query = new Query();
		Ad ad = new Ad();

		queryReportManager.queryIssued(query);
		queryReportManager.viewed(query, ad, 1, alice, false);
		queryReportManager.clicked(query, ad, 1, 1.0, alice);
		queryReportManager.converted(query, ad, 1, 2.0, alice);

		queryReportManager.sendQueryReportToAll();

		queryReportManager.addClicks("c", query, 1, 1.0);
		queryReportManager.addImpressions("d", query, 1, 0, null, 1);
		queryReportManager.addImpressions("c", query, 0, 1, null, 1);
	}

	private static class QueryReportSenderImpl implements QueryReportSender {
		public void sendQueryReport(String advertiser, QueryReport report) {
		}
	}
}
