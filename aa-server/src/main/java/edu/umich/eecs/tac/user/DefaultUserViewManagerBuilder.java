package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.sim.AgentRepository;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserViewManagerBuilder implements
		UserBehaviorBuilder<UserViewManager> {
	public UserViewManager build(ConfigProxy userConfigProxy,
			AgentRepository repository, Random random) {
		return new DefaultUserViewManager(repository.getRetailCatalog(),
				repository.getSalesAnalyst(), repository.getAdvertiserInfo(),
				repository.getAuctionInfo(), random);
	}
}
