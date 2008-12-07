package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.sim.TACAASimulation;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public interface UserBehaviorBuilder<T> {
    public T build(ConfigProxy userConfigProxy, TACAASimulation simulation, Random random);
}
