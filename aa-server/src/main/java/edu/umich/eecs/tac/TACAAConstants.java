package edu.umich.eecs.tac;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Sep 26, 2008
 * Time: 3:59:09 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * <code>TACAAConstants</code> will be used to define any constants used by
 * the TAC AA Simulations.  
 */

public interface TACAAConstants {

  public static final String[] SUPPORTED_TYPES = {
    "tac09aa"
  };

  public static final int TYPE_NONE = 0;

  // --------------------------------------------------------------------
  // Event types (human readable messages)
  // --------------------------------------------------------------------
  public static final int TYPE_MESSAGE = 1;
  public static final int TYPE_WARNING = 2;

  // --------------------------------------------------------------------
  // Interaction types
  // --------------------------------------------------------------------

  // -------------------------------------------------------------------
  // Data update types
  // -------------------------------------------------------------------

  /** The average network response time for a specific agent (int) */
  public static final int DU_NETWORK_AVG_RESPONSE = 64;

  /** The last network response time for a specific agent (int) */
  public static final int DU_NETWORK_LAST_RESPONSE = 65;

  /** The bank account status for a specific agent (int or long) */
  public final static int DU_BANK_ACCOUNT = 100;




  // --------------------------------------------------------------------
  // TAC AA Participant roles
  // --------------------------------------------------------------------

  /** The TAC AA Publisher role */
  public static final int PUBLISHER = 0;

  /** The TAC AA Advertiser role */
  public static final int ADVERTISER = 1;

  /** The TAC AA User role */
  public static final int USERS = 2;

  /** The TAC AA participant roles as human readable names. */
  public static final String[] ROLE_NAME = {
    "Publisher", "Advertiser", "User"
  };


}
