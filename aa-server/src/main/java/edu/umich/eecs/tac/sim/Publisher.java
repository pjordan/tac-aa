package edu.umich.eecs.tac.sim;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Oct 31, 2008
 * Time: 1:47:24 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.logging.Logger;

public abstract class Publisher extends Builtin {
  private static final String CONF = "publisher.";

  public Publisher() {
    super(CONF);
  }

  // DEBUG FINALIZE REMOVE THIS!!! REMOVE THIS!!!
  protected void finalize() throws Throwable {
    Logger.global.info("CUSTOMER " + getName() + " IS BEING GARBAGED");
    super.finalize();
  }


}
