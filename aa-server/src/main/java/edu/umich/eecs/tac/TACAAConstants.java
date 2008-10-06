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

    // --------------------------------------------------------------------
    // TAC AA Participant roles
    // --------------------------------------------------------------------

    /** The TAC AA Auctioneer role */
    public static final int AUCTIONEER = 0;

    /** The TAC AA Advertiser role */
    public static final int ADVERTISER = 1;

    /** The TAC AA User role */
    public static final int USER = 2;

    /** The TAC AA participant roles as human readable names. */
    public static final String[] ROLE_NAME = {
    "Auctioneer", "Advertiser", "User"
    };


}
