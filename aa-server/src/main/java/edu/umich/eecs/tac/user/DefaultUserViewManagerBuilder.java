package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.sim.TACAASimulation;
import edu.umich.eecs.tac.props.RetailCatalog;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserViewManagerBuilder implements UserBehaviorBuilder<UserViewManager> {
    public UserViewManager build(ConfigProxy userConfigProxy, TACAASimulation simulation, Random random) {
        return new DefaultUserViewManager(simulation.getRetailCatalog(),simulation.getAdvertiserInfo(), random);
    }
}
