package edu.umich.eecs.tac.sim;


import java.util.logging.Logger;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.sim.SimulationAgent;
import edu.umich.eecs.tac.props.Ranking;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.user.UserEventListener;

/**
 * @author Lee Callender, Patrick Jordan
 */
public abstract class Users extends Builtin {
  private static final String CONF = "users.";

  protected Logger log = Logger.getLogger(Users.class.getName());

  Publisher[] publishers;



  public Users() {
        super(CONF);
    }

    protected void setup(){
        SimulationAgent[] publish = getSimulation().getPublishers();
        publishers = new Publisher[publish.length];
        for(int i = 0, n = publish.length; i < n; i++){
            publishers[i] = (Publisher) publish[i].getAgent();
        }
    }

    public abstract void broadcastUserDistribution();
    

    // DEBUG FINALIZE REMOVE THIS!!! REMOVE THIS!!!
    protected void finalize() throws Throwable {
        Logger.global.info("USER " + getName() + " IS BEING GARBAGED");
        super.finalize();
    }

    public abstract boolean addUserEventListener(UserEventListener listener);

    public abstract boolean containsUserEventListener(UserEventListener listener);

    public abstract boolean removeUserEventListener(UserEventListener listener);

    protected void transact(String advertiser, double amount) {
        getSimulation().transaction(getAddress(), advertiser, amount);
    }
}
