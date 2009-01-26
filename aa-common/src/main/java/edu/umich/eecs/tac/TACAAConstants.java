package edu.umich.eecs.tac;

/**
 * TACAAConstants will be used to define any constants used by the TAC AA Simulations.
 *
 * @author SICS, Patrick Jordan, Lee Callender
 */
public final class TACAAConstants {
    /**
     * Sole constructor (should not be invoked).
     */
    private TACAAConstants() {
    }

    /**
     * The simulation supported types.
     */
    public static final String[] SUPPORTED_TYPES = {"tac09aa"};
    /**
     * Human readable message.
     */
    public static final int TYPE_NONE = 0;
    /**
     * Human readable message.
     */
    public static final int TYPE_MESSAGE = 1;
    /**
     * Human readable warning.
     */
    public static final int TYPE_WARNING = 2;
    /**
     * The average network response time for a specific agent (int).
     */
    public static final int DU_NETWORK_AVG_RESPONSE = 64;
    /**
     * The last network response time for a specific agent (int).
     */
    public static final int DU_NETWORK_LAST_RESPONSE = 65;
    /**
     * The bank account status for a specific agent (int or long or double).
     */
    public static final int DU_BANK_ACCOUNT = 100;
    /**
     * The number of non-searching users.
     */
    public static final int DU_NON_SEARCHING = 200;
    /**
     * The number of informational-search users.
     */
    public static final int DU_INFORMATIONAL_SEARCH = 201;
    /**
     * The number of focus level zero users.
     */
    public static final int DU_FOCUS_LEVEL_ZERO = 202;
    /**
     * The number of focus level one users.
     */
    public static final int DU_FOCUS_LEVEL_ONE = 203;
    /**
     * The number of focus level two users.
     */
    public static final int DU_FOCUS_LEVEL_TWO = 204;
    /**
     * The number of transacted users.
     */
    public static final int DU_TRANSACTED = 205;
    /**
     * The bid of an advertiser.
     */
    public static final int DU_BIDS = 300;
    /**
     * The impressions an advertiser receives.
     */
    public static final int DU_IMPRESSIONS = 301;
    /**
     * The clicks an advertiser receives.
     */
    public static final int DU_CLICKS = 302;
    /**
     * The conversions an advertiser receives.
     */
    public static final int DU_CONVERSIONS = 303;
    /**
     * The TAC AA Publisher role.
     */
    public static final int PUBLISHER = 0;
    /**
     * The TAC AA Advertiser role.
     */
    public static final int ADVERTISER = 1;
    /**
     * The TAC AA User role.
     */
    public static final int USERS = 2;
    /**
     * The TAC AA participant roles as human readable names.
     */
    public static final String[] ROLE_NAME = {"Publisher", "Advertiser", "User"};
}
