package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.util.config.ConfigProxyUtils;
import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.sim.Auctioneer;
import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.sim.Publisher;
import java.util.Random;

import se.sics.tasim.aw.Message;
import se.sics.tasim.is.EventWriter;

/**
 * @author Patrick Jordan, Lee Callender
 */
public class DefaultUsersBehavior implements UsersBehavior {

    private UserManager userManager;

    private DistributionBroadcaster distributionBroadcaster;

    private int virtualDays;

    private ConfigProxy config;

    private AgentRepository agentRepository;

    private UsersTransactor usersTransactor;

    public DefaultUsersBehavior(ConfigProxy config, AgentRepository agentRepository, UsersTransactor usersTransactor) {

        if(config==null) {
            throw new NullPointerException("config cannot be null");
        }

        this.config = config;

        if(agentRepository==null) {
            throw new NullPointerException("agent repository cannot be null");
        }

        this.agentRepository = agentRepository;

        if(usersTransactor==null) {
            throw new NullPointerException("users transactor cannot be null");
        }

        this.usersTransactor = usersTransactor;
    }

    public void nextTimeUnit(int date) {

        if (date == 0) {
            userManager.initialize(virtualDays);
        }

        userManager.nextTimeUnit(date);
        
        userManager.triggerBehavior((Publisher) agentRepository.getPublishers()[0].getAgent());
    }


    public void setup() {
        virtualDays = config.getPropertyAsInt("virtual_days", 0);

        try {
            // Create the user manager
            UserBehaviorBuilder<UserManager> managerBuilder = createBuilder();

            userManager = managerBuilder.build(config, agentRepository, new Random());

            addUserEventListener(new ConversionMonitor());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected class ConversionMonitor implements UserEventListener {

        public void queryIssued(Query query) {
        }

        public void viewed(Query query, Ad ad, int slot, String advertiser, boolean isPromoted) {
        }

        public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {
        }

        public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
            usersTransactor.transact(advertiser, salesProfit);
        }
    }

    protected UserBehaviorBuilder<UserManager> createBuilder() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return ConfigProxyUtils.createObjectFromProperty(config, "usermanger.builder", "edu.umich.eecs.tac.user.DefaultUserManagerBuilder");
    }


    public void stopped() {
    }

    public void shutdown() {
    }


    public Ranking getRanking(Query query, Auctioneer auctioneer) {
        return auctioneer.runAuction(query).getRanking();
    }

    public void messageReceived(Message message) {
        userManager.messageReceived(message); 
    }

    public boolean addUserEventListener(UserEventListener listener) {
        return userManager.addUserEventListener(listener);
    }

    public boolean containsUserEventListener(UserEventListener listener) {
        return userManager.containsUserEventListener(listener);
    }

    public boolean removeUserEventListener(UserEventListener listener) {
        return userManager.removeUserEventListener(listener);
    }

    public void broadcastUserDistribution(int usersIndex, EventWriter eventWriter) {

        if(distributionBroadcaster==null) {
            distributionBroadcaster = new DefaultDistributionBroadcaster(userManager);
        }

        distributionBroadcaster.broadcastUserDistribution(usersIndex, eventWriter);
    }
}
