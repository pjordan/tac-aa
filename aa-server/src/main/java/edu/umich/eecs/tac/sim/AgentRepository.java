package edu.umich.eecs.tac.sim;

import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.AuctionInfo;

import java.util.Map;

import se.sics.tasim.sim.SimulationAgent;

/**
 * The agent repository holds references to all agents in the TAC/AA simulation.
 *
 * @author Patrick Jordan
 */
public interface AgentRepository {
    /**
     * Get the retail catalog used for the simulation.
     *
     * @return the retail catalog
     */
    RetailCatalog getRetailCatalog();

    /**
     * Get the auction information
     *
     * @return the auction info
     */
    AuctionInfo getAuctionInfo();

    /**
     * Get the advertiser information mapping.
     *
     * @return the advertiser information mapping.
     */
    Map<String, AdvertiserInfo> getAdvertiserInfo();

    /**
     * Get the list of publisher agents.
     *
     * @return the list of publisher agents.
     */
    SimulationAgent[] getPublishers();

    /**
     * Get the list of user agents.
     *
     * @return the list of user agents.
     */
    SimulationAgent[] getUsers();

    /**
     * Get the sales analyst.
     *
     * @return the sales analyst.
     */
    SalesAnalyst getSalesAnalyst();
}
