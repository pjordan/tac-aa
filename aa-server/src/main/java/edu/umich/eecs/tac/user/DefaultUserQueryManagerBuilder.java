package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.util.config.ConfigProxyUtils;
import edu.umich.eecs.tac.sim.TACAASimulation;
import edu.umich.eecs.tac.props.RetailCatalog;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserQueryManagerBuilder implements UserBehaviorBuilder<UserQueryManager> {
    public UserQueryManager build(ConfigProxy userConfigProxy, TACAASimulation simulation, Random random) {

        RetailCatalog retailCatalog = simulation.getRetailCatalog();

        return new DefaultUserQueryManager(retailCatalog,random);
    }
}
