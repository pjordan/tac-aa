package edu.umich.eecs.tac.sim;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Oct 31, 2008
 * Time: 1:46:50 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.logging.Logger;

import se.sics.isl.transport.Transportable;

public abstract class Users extends Builtin {
  private static final String CONF = "user.";

  public Users() {
    super(CONF);
  }

  // DEBUG FINALIZE REMOVE THIS!!! REMOVE THIS!!!
  protected void finalize() throws Throwable {
    Logger.global.info("CUSTOMER " + getName() + " IS BEING GARBAGED");
    super.finalize();
  }


}
