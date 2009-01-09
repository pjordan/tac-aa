package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.sim.TACAASimulation;
import edu.umich.eecs.tac.sim.AgentRepository;

import java.util.Random;

/**
 * User behavior builders construct user behaviors from a configuration.
 *
 * @author Patrick Jordan
 */
public interface UserBehaviorBuilder<T> {
    /**
     * Build a user behavior from a configuration.
     *  
     * @param userConfigProxy the configuration proxy
     * @param repository the repository of agents
     * @param random the random number generator
     * @return a built user behavior
     */
    public T build(ConfigProxy userConfigProxy, AgentRepository repository, Random random);
}
