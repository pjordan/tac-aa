package edu.umich.eecs.tac.user;

/**
 * @author Ben Cassell
 */

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.sics.tasim.sim.SimulationAgent;

import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.AuctionInfo;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.sim.DummyTACAASimulation;
import edu.umich.eecs.tac.sim.SalesAnalyst;
import edu.umich.eecs.tac.util.config.ConfigProxy;

public class DefaultUsersBehaviorTest {
	
	protected DummyTACAASimulation dts;
	private DefaultUsersBehavior dub;
	
	@Before
	public void setUp() throws Exception {
		dts = new DummyTACAASimulation();
		dts.setup();
		dub = new DefaultUsersBehavior(new DummyUsersConfigProxy(), new AgentRepositoryProxy(), new UsersTransactorProxy());
		dub.setup();
	}

	@Test(expected = NullPointerException.class)
	public void testDefaultUsersBehaviorNPEA() {
		dub = new DefaultUsersBehavior(null, new AgentRepositoryProxy(), new UsersTransactorProxy());
	}

	@Test(expected = NullPointerException.class)
	public void testDefaultUsersBehaviorNPEB() {
		dub = new DefaultUsersBehavior(new DummyUsersConfigProxy(), null, new UsersTransactorProxy());
	}
	
	@Test(expected = NullPointerException.class)
	public void testDefaultUsersBehaviorNPEC() {
		dub = new DefaultUsersBehavior(new DummyUsersConfigProxy(), new AgentRepositoryProxy(), null);
	}	
	
	@Test
	public void testNextTimeUnit() {
		dub.nextTimeUnit(0);
		dub.nextTimeUnit(1);
	}

	@Test
	public void testSetup() {

	}

	@Test
	public void testCreateBuilder() {

	}

	@Test
	public void testStopped() {

	}

	@Test
	public void testShutdown() {

	}

	@Test
	public void testGetRanking() {

	}

	@Test
	public void testMessageReceived() {

	}

	@Test
	public void testAddUserEventListener() {

	}

	@Test
	public void testContainsUserEventListener() {

	}

	@Test
	public void testRemoveUserEventListener() {

	}

	@Test
	public void testBroadcastUserDistribution() {

	}

    protected class DummyUsersConfigProxy implements ConfigProxy {
        public String getProperty(String name) {
            return name;
        }

        public String getProperty(String name, String defaultValue) {
            return defaultValue;
        }

        public String[] getPropertyAsArray(String name) {
            return this.getPropertyAsArray(name, "name");
        }

        public String[] getPropertyAsArray(String name, String defaultValue) {
            char [] k = defaultValue.toCharArray();
            String [] i = new String [k.length];
            int j;
            for(j = 0; j < k.length; j++) {
            	i[j] = String.valueOf(k[j]);
            }
            return i;  
        }

        public int getPropertyAsInt(String name, int defaultValue) {
            return 0;
        }

        public int[] getPropertyAsIntArray(String name) {
            return this.getPropertyAsIntArray(name, "name");
        }

        public int[] getPropertyAsIntArray(String name, String defaultValue) {
            char [] k = defaultValue.toCharArray();
            int [] i = new int [k.length];
            int j;
            for(j = 0; j < k.length; j++) {
            	i[j] = (int)k[j];
            }
            return i;  
        }

        public long getPropertyAsLong(String name, long defaultValue) {
            return defaultValue;
        }

        public float getPropertyAsFloat(String name, float defaultValue) {
            return defaultValue;
        }

        public double getPropertyAsDouble(String name, double defaultValue) {
            return defaultValue;
        }
    }

    protected class UsersTransactorProxy implements UsersTransactor {
        public void transact(String address, double amount) {
            this.transact(address, amount);
        }
    }

    protected class AgentRepositoryProxy implements AgentRepository {
        public RetailCatalog getRetailCatalog() {
            return dts.getRetailCatalog();
        }

        public AuctionInfo getAuctionInfo() {
            return dts.getAuctionInfo();
        }

        public Map<String, AdvertiserInfo> getAdvertiserInfo() {
            return dts.getAdvertiserInfo();
        }

        public SimulationAgent[] getPublishers() {
            return dts.getPublishers();
        }

        public SimulationAgent[] getUsers() {
            return dts.getUsers();
        }

        public SalesAnalyst getSalesAnalyst() {
            return dts.getSalesAnalyst();
        }

        public int getNumberOfAdvertisers() {
            return dts.getNumberOfAdvertisers();
        }


        public String[] getAdvertiserAddresses() {
            return dts.getAdvertiserAddresses();
        }
    }
}
