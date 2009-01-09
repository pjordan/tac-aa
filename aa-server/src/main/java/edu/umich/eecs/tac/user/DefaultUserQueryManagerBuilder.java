package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.props.RetailCatalog;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserQueryManagerBuilder implements UserBehaviorBuilder<UserQueryManager> {
    public UserQueryManager build(ConfigProxy userConfigProxy, AgentRepository repository, Random random) {
        return new DefaultUserQueryManager(repository.getRetailCatalog(),random);
    }
}
