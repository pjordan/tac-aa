package edu.umich.eecs.tac.agents;

import edu.umich.eecs.tac.sim.Users;
import edu.umich.eecs.tac.sim.Publisher;
import edu.umich.eecs.tac.user.*;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.util.config.ConfigProxyUtils;
import edu.umich.eecs.tac.props.*;
import se.sics.tasim.aw.Message;
import se.sics.tasim.is.EventWriter;
import se.sics.isl.transport.Transportable;

import java.util.Iterator;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Lee Callender
 */
public class DefaultUsers extends Users implements TACAAConstants {
    private ConfigProxy usersConfigProxy;
    private UserManager userManager;
    private UserClickModel userClickModel;

    public DefaultUsers() {
    }

    public void nextTimeUnit(int date) {
        userManager.nextTimeUnit(date);
        userManager.triggerBehavior((Publisher) (getSimulation().getPublishers()[0].getAgent()));
    }

    protected void setup() {
        super.setup();
        this.log = Logger.getLogger(DefaultUsers.class.getName());

        try {
            // Create the user manager
            UserBehaviorBuilder<UserManager> managerBuilder = createBuilder();
            userManager = managerBuilder.build(getUsersConfigProxy(), getSimulation(), new Random());
            if (userClickModel != null)
                userManager.setUserClickModel(userClickModel);

            addUserEventListener(new ConversionMonitor());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        int numberOfAdvertisers = getNumberOfAdvertisers();
        if (numberOfAdvertisers <= 0) {
            throw new IllegalArgumentException("Number of advertisers not specified in config.");
        }
    }

    protected void stopped() {
    }

    protected void shutdown() {
    }

    // -------------------------------------------------------------------
    // Message handling
    // -------------------------------------------------------------------

    protected void messageReceived(Message message) {
        String sender = message.getSender();
        Transportable content = message.getContent();

        if (content instanceof UserClickModel) {
            handleUserClickModel((UserClickModel) content);
        } else if (content instanceof RetailCatalog) {
            handleRetailCatalog((RetailCatalog) content);
        }
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {

    }

    private void handleUserClickModel(UserClickModel userClickModel) {
        this.userClickModel = userClickModel;

        if (userManager != null) {
            userManager.setUserClickModel(userClickModel);
        }
    }

    protected String getAgentName(String agentAddress) {
        return super.getAgentName(agentAddress);
    }

    protected void sendEvent(String message) {
        super.sendEvent(message);
    }

    protected void sendWarningEvent(String message) {
        super.sendWarningEvent(message);
    }

    // ------------------
    //
    // ------------------
    protected final Ranking getRanking(Query query, Publisher publisher) {
        //THIS MAY NOT BE HOW WE WANT TO ACCESS RANKINGS!
        return publisher.runAuction(query).getRanking();
    }

    protected ConfigProxy getUsersConfigProxy() {
        if (usersConfigProxy == null) {
            usersConfigProxy = new ConfigProxy() {

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
            };
        }

        return usersConfigProxy;
    }

    // ------------------
    // Utility Methods
    // ------------------

    /* protected SalesReportManager getSalesReportManager(String agent){
      SalesReportManager rep = (SalesReportManager) salesTable.get(agent);
      if (rep != null) {
        return rep;
      }

      if (salesReportCount < sales.length) {
        rep = sales[salesReportCount++];
        rep.setName(agent);
        salesTable.put(agent, rep);
        return rep;
      } else {
        // More agents than manufacturers. Should NOT be possible!
        log.severe("AGENT " + agent + " CLAIMS TO BE ADVERTISER BUT "
              + "ALREADY HAVE " + sales.length + " ADVERTISERS!!!");
        SalesReportManager r = new SalesReportManager();
        r.setName(agent);
        salesTable.put(agent, r);
        return r;
      }

    }
    */

    public boolean addUserEventListener(UserEventListener listener) {
        return userManager.addUserEventListener(listener);
    }

    public boolean containsUserEventListener(UserEventListener listener) {
        return userManager.containsUserEventListener(listener);
    }

    public boolean removeUserEventListener(UserEventListener listener) {
        return userManager.removeUserEventListener(listener);
    }

    protected UserBehaviorBuilder<UserManager> createBuilder() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return ConfigProxyUtils.createObjectFromProperty(getUsersConfigProxy(), "usermanger.builder", "edu.umich.eecs.tac.user.DefaultUserManagerBuilder");
    }


    public void broadcastUserDistribution() {
        // User information
        EventWriter eventWriter = getEventWriter();
        int usersIndex = getIndex();

        int[] distribution = userManager.getStateDistribution();
        QueryState[] states = QueryState.values();
        for (int i = 0; i < distribution.length; i++) {
            switch (states[i]) {
                case NON_SEARCHING:
                    eventWriter.dataUpdated(usersIndex, DU_NON_SEARCHING, distribution[i]);
                    break;
                case INFORMATIONAL_SEARCH:
                    eventWriter.dataUpdated(usersIndex, DU_INFORMATIONAL_SEARCH, distribution[i]);
                    break;
                case FOCUS_LEVEL_ZERO:
                    eventWriter.dataUpdated(usersIndex, DU_FOCUS_LEVEL_ZERO, distribution[i]);
                    break;
                case FOCUS_LEVEL_ONE:
                    eventWriter.dataUpdated(usersIndex, DU_FOCUS_LEVEL_ONE, distribution[i]);
                    break;
                case FOCUS_LEVEL_TWO:
                    eventWriter.dataUpdated(usersIndex, DU_FOCUS_LEVEL_TWO, distribution[i]);
                    break;
                case TRANSACTED:
                    eventWriter.dataUpdated(usersIndex, DU_TRANSACTED, distribution[i]);
                    break;
                default:
                    break;
            }
        }
    }

    protected class ConversionMonitor implements UserEventListener {

        public void queryIssued(Query query) {

        }

        public void viewed(Query query, Ad ad, int slot, String advertiser) {

        }

        public void clicked(Query query, Ad ad, int slot, double cpc, String advertiser) {

        }

        public void converted(Query query, Ad ad, int slot, double salesProfit, String advertiser) {
            DefaultUsers.this.transact(advertiser, salesProfit);
        }
    }
}
