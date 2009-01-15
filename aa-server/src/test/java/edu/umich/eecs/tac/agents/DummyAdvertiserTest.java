package edu.umich.eecs.tac.agents;

/**
 * @author Ben Cassell
 */

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.tasim.aw.AgentService;
import se.sics.tasim.aw.Message;
import se.sics.tasim.sim.SimulationAgent;

import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.sim.DummySimulationAgent;
import edu.umich.eecs.tac.sim.DummyTACAASimulation;

public class DummyAdvertiserTest {

	private DummyAdvertiser dummy;
	private Message message;
	private DummySimulationAgent as;
	private RetailCatalog rc;
	
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
	}
	
	@Test
	public void testMessageReceived() {
		QueryReport qr = new QueryReport();
		qr.addQuery(new Query("Lioneer", "TV"), 1, 1, 0.5, 3);
		message = new Message("dummy", qr);
		dummy.messageReceived(message);
	}

	@Test
	public void testSimulationSetup() {
		fail("Not yet implemented");
	}

	@Test
	public void testSimulationFinished() {
		fail("Not yet implemented");
	}

	@Test
	public void testDummyAdvertiser() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendBidAndAds() {
		fail("Not yet implemented");
	}

}
