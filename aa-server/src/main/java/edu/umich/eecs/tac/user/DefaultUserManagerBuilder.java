package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.util.config.ConfigProxy;
import edu.umich.eecs.tac.util.config.ConfigProxyUtils;
import edu.umich.eecs.tac.sim.AgentRepository;
import edu.umich.eecs.tac.props.RetailCatalog;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserManagerBuilder implements UserBehaviorBuilder<UserManager> {
    private static final String BASE = "usermanager";
    private static final String POPULATION_SIZE_KEY = "populationsize";
    private static final int POPULATION_SIZE_DEFAULT = 10000;
    private static final String VIEW_MANAGER_KEY = "viewmanager";
    private static final String VIEW_MANAGER_DEFAULT = "edu.umich.eecs.tac.user.DefaultUserViewManagerBuilder";
    private static final String TRANSITION_MANAGER_KEY = "transitionmanager";
    private static final String TRANSITION_MANAGER_DEFAULT = "edu.umich.eecs.tac.user.DefaultUserTransitionManagerBuilder";
    private static final String QUERY_MANAGER_KEY = "querymanager";
    private static final String QUERY_MANAGER_DEFAULT = "edu.umich.eecs.tac.user.DefaultUserQueryManagerBuilder";

    public UserManager build(ConfigProxy userConfigProxy, AgentRepository repository, Random random) {

        RetailCatalog retailCatalog = repository.getRetailCatalog();

        try {
            UserBehaviorBuilder<UserTransitionManager> transitionBuilder = ConfigProxyUtils.createObjectFromProperty(userConfigProxy,BASE+'.'+TRANSITION_MANAGER_KEY,TRANSITION_MANAGER_DEFAULT);
            UserTransitionManager transitionManager = transitionBuilder.build(userConfigProxy, repository,random);

            UserBehaviorBuilder<UserQueryManager> queryBuilder = ConfigProxyUtils.createObjectFromProperty(userConfigProxy,BASE+'.'+QUERY_MANAGER_KEY,QUERY_MANAGER_DEFAULT);
            UserQueryManager queryManager = queryBuilder.build(userConfigProxy, repository,random);

            UserBehaviorBuilder<UserViewManager> viewBuilder = ConfigProxyUtils.createObjectFromProperty(userConfigProxy,BASE+'.'+VIEW_MANAGER_KEY,VIEW_MANAGER_DEFAULT);
            UserViewManager viewManager = viewBuilder.build(userConfigProxy, repository,random);

            int populationSize = userConfigProxy.getPropertyAsInt(BASE+'.'+ POPULATION_SIZE_KEY,POPULATION_SIZE_DEFAULT);

            return new DefaultUserManager(retailCatalog, transitionManager, queryManager, viewManager, populationSize, random);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
