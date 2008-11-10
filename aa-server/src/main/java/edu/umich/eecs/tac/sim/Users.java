package edu.umich.eecs.tac.sim;


import java.util.logging.Logger;

import se.sics.isl.transport.Transportable;

/**
 * @author Lee Callender, Patrick Jordan
 */
public abstract class Users extends Builtin {
    private static final String CONF = "user.";

    public Users() {
        super(CONF);
    }


    public abstract void sendSalesReportsToAll();

    // DEBUG FINALIZE REMOVE THIS!!! REMOVE THIS!!!
    protected void finalize() throws Throwable {
        Logger.global.info("USER " + getName() + " IS BEING GARBAGED");
        super.finalize();
    }


}
