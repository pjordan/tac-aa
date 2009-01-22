package edu.umich.eecs.tac.agents;

/**
 * @author Ben Cassell
 */

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;
import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.DummySimulationAgent;

public class DummyAdvertiserTest {

	private DummyAdvertiser dummy;
	private Message message;
	private DummySimulationAgent as;
	private RetailCatalog rc;
	private QueryReport qr;

	@Before
	public void setUp() {
		dummy = new DummyAdvertiser();
		rc = new RetailCatalog();
		rc.addProduct(new Product("man", "com"));
		as = new DummySimulationAgent(dummy, "dummy");
		as.setup();
		dummy.simulationSetup();
		message = new Message("dummy", rc);
		dummy.messageReceived(message);
		dummy.messageReceived(message);
	}

	@Test
	public void testMessageReceived() {
		qr = new QueryReport();
		qr.addQuery(new Query("man", "com"));
		message = new Message("dummy", qr);
		SalesReport sr = new SalesReport();
		sr.addQuery(new Query("man", "com"));
		message = new Message("dummy", sr);
		dummy.messageReceived(message);
		SimulationStatus ss = new SimulationStatus();
		message = new Message("dummy", ss);
		dummy.messageReceived(message);
		AdvertiserInfo ai = new AdvertiserInfo();
		message = new Message("dummy", ai);
		dummy.messageReceived(message);
		BankStatus bs = new BankStatus();
		message = new Message("dummy", bs);
		dummy.messageReceived(message);
		dummy.messageReceived(null);
	}

	@Test
	public void testSimulationSetup() {
		QueryReport qr = new QueryReport();
		qr.addQuery(new Query("man", "com"));
		message = new Message("dummy", qr);
		dummy.messageReceived(message);
		dummy.simulationSetup();
	}

	@Test
	public void testSimulationFinished() {
		dummy.simulationFinished();
	}

	@Test
	public void testSendBidAndAds() {
		dummy.sendBidAndAds();
		qr = new QueryReport();
		qr.addQuery(new Query("man", "com"));
		message = new Message("dummy", qr);
		AdvertiserInfo ai = new AdvertiserInfo();
		ai.setPublisherId("dumbpub");
		message = new Message("dummy", ai);
		dummy.messageReceived(message);
		dummy.sendBidAndAds();
	}

}
