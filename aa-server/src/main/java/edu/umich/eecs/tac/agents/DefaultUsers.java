package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.sim.*;
import edu.umich.eecs.tac.user.*;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.util.config.ConfigProxyUtils;
import edu.umich.eecs.tac.props.*;
import se.sics.tasim.aw.Message;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.sim.SimulationAgent;
import se.sics.isl.transport.Transportable;

import java.util.Iterator;
import java.util.Hashtable;
import java.util.Random;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Lee Callender, Patrick Jordan
 */
public class DefaultUsers extends Users implements TACAAConstants {
    private UsersBehavior usersBehavior;

    public DefaultUsers() {
        usersBehavior = new DefaultUsersBehavior(new UsersConfigProxy(), new AgentRepositoryProxy(), new UsersTransactorProxy());
    }

    public void nextTimeUnit(int date) {
        usersBehavior.nextTimeUnit(date);
    }

    protected void setup() {
        super.setup();

        this.log = Logger.getLogger(DefaultUsers.class.getName());

        usersBehavior.setup();
    }

    protected void stopped() {
        usersBehavior.stopped();
    }

    protected void shutdown() {
        usersBehavior.shutdown();
    }

    protected void messageReceived(Message message) {
        usersBehavior.messageReceived(message);
    }

    public boolean addUserEventListener(UserEventListener listener) {
        return usersBehavior.addUserEventListener(listener);
    }

    public boolean containsUserEventListener(UserEventListener listener) {
        return usersBehavior.containsUserEventListener(listener);
    }

    public boolean removeUserEventListener(UserEventListener listener) {
        return usersBehavior.removeUserEventListener(listener);
    }

    public void broadcastUserDistribution() {
        usersBehavior.broadcastUserDistribution(getIndex(), getEventWriter());
    }

    protected class UsersConfigProxy implements ConfigProxy {
        public String getProperty(String name) {
            return DefaultUsers.this.getProperty(name);
        }

        public String getProperty(String name, String defaultValue) {
            return DefaultUsers.this.getProperty(name, defaultValue);
        }

        public String[] getPropertyAsArray(String name) {
            return DefaultUsers.this.getPropertyAsArray(name);
        }

        public String[] getPropertyAsArray(String name, String defaultValue) {
            return DefaultUsers.this.getPropertyAsArray(name, defaultValue);
        }

        public int getPropertyAsInt(String name, int defaultValue) {
            return DefaultUsers.this.getPropertyAsInt(name, defaultValue);
        }

        public int[] getPropertyAsIntArray(String name) {
            return DefaultUsers.this.getPropertyAsIntArray(name);
        }

        public int[] getPropertyAsIntArray(String name, String defaultValue) {
            return DefaultUsers.this.getPropertyAsIntArray(name, defaultValue);
        }

        public long getPropertyAsLong(String name, long defaultValue) {
            return DefaultUsers.this.getPropertyAsLong(name, defaultValue);
        }

        public float getPropertyAsFloat(String name, float defaultValue) {
            return DefaultUsers.this.getPropertyAsFloat(name, defaultValue);
        }

        public double getPropertyAsDouble(String name, double defaultValue) {
            return DefaultUsers.this.getPropertyAsDouble(name, defaultValue);
        }
    }

    protected class UsersTransactorProxy implements UsersTransactor {
        public void transact(String address, double amount) {
            DefaultUsers.this.transact(address, amount);
        }
    }

    protected class AgentRepositoryProxy implements AgentRepository {
        public RetailCatalog getRetailCatalog() {
            return getSimulation().getRetailCatalog();
        }

        public AuctionInfo getAuctionInfo() {
            return getSimulation().getAuctionInfo();
        }

        public Map<String, AdvertiserInfo> getAdvertiserInfo() {
            return getSimulation().getAdvertiserInfo();
        }

        public SimulationAgent[] getPublishers() {
            return getSimulation().getPublishers();
        }

        public SimulationAgent[] getUsers() {
            return getSimulation().getUsers();
        }

        public SalesAnalyst getSalesAnalyst() {
            return getSimulation().getSalesAnalyst();
        }

        public int getNumberOfAdvertisers() {
            return getSimulation().getNumberOfAdvertisers();
        }


        public String[] getAdvertiserAddresses() {
            return getSimulation().getAdvertiserAddresses();
        }
    }
}
