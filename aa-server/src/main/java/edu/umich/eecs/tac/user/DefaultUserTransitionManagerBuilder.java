package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.sim.TACAASimulation;
import edu.umich.eecs.tac.props.RetailCatalog;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserTransitionManagerBuilder implements UserBehaviorBuilder<UserTransitionManager> {
    private static final String STANDARD_KEY = "usermanager.usertransitionmanager.probability.standard";
    private static final String BURST_KEY = "usermanager.usertransitionmanager.probability.burst";
    private static final String BURST_PROBABILITY_KEY = "usermanager.usertransitionmanager.burstprobability";
    private static final double BURST_PROBABILITY_DEFAULT = 0.05;

    public UserTransitionManager build(ConfigProxy userConfigProxy, TACAASimulation simulation, Random random) {
        DefaultUserTransitionManager transitionManager = new DefaultUserTransitionManager(random);

        // Construct standard probabilities
        for(QueryState from : QueryState.values()) {
            for(QueryState to : QueryState.values()) {
                double probability = userConfigProxy.getPropertyAsDouble(String.format("%s.%s.%s",STANDARD_KEY,from.toString(),to.toString()),Double.NaN);

                if(!Double.isNaN(probability) && probability>0) {
                    transitionManager.addStandardTransitionProbability(from,to,probability);
                }
            }
        }

        // Construct burst probabilities
        for(QueryState from : QueryState.values()) {
            for(QueryState to : QueryState.values()) {
                double probability = userConfigProxy.getPropertyAsDouble(String.format("%s.%s.%s",BURST_KEY,from.toString(),to.toString()),Double.NaN);

                if(!Double.isNaN(probability) && probability>0) {
                    transitionManager.addBurstTransitionProbability(from,to,probability);
                }
            }
        }

        transitionManager.setBurstProbability(userConfigProxy.getPropertyAsDouble(BURST_PROBABILITY_KEY,BURST_PROBABILITY_DEFAULT));

        return transitionManager;
    }
}
