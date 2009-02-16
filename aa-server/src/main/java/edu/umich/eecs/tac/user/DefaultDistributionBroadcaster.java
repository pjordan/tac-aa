package edu.umich.eecs.tac.user;

import se.sics.tasim.is.EventWriter;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.UserPopulationState;
import edu.umich.eecs.tac.props.Product;

/**
 * @author Patrick Jordan, Lee Callender
 */
public class DefaultDistributionBroadcaster implements DistributionBroadcaster {
	private UserManager userManager;

	public DefaultDistributionBroadcaster(UserManager userManager) {

		if (userManager == null) {
			throw new NullPointerException("user manager cannot be null");
		}

		this.userManager = userManager;
	}

  /**
   * For the time being, this broadcasts both the total user distribution plus
   * the distribution over all product populations
   * @param usersIndex
   * @param eventWriter
   */
  public void broadcastUserDistribution(int usersIndex,
			EventWriter eventWriter) {
		int[] distribution = userManager.getStateDistribution();

    QueryState[] states = QueryState.values();

		for (int i = 0; i < distribution.length; i++) {
			switch (states[i]) {
			case NON_SEARCHING:
				eventWriter.dataUpdated(usersIndex,
						TACAAConstants.DU_NON_SEARCHING, distribution[i]);
				break;
			case INFORMATIONAL_SEARCH:
				eventWriter
						.dataUpdated(usersIndex,
								TACAAConstants.DU_INFORMATIONAL_SEARCH,
								distribution[i]);
				break;
			case FOCUS_LEVEL_ZERO:
				eventWriter.dataUpdated(usersIndex,
						TACAAConstants.DU_FOCUS_LEVEL_ZERO, distribution[i]);
				break;
			case FOCUS_LEVEL_ONE:
				eventWriter.dataUpdated(usersIndex,
						TACAAConstants.DU_FOCUS_LEVEL_ONE, distribution[i]);
				break;
			case FOCUS_LEVEL_TWO:
				eventWriter.dataUpdated(usersIndex,
						TACAAConstants.DU_FOCUS_LEVEL_TWO, distribution[i]);
				break;
			case TRANSACTED:
				eventWriter.dataUpdated(usersIndex,
						TACAAConstants.DU_TRANSACTED, distribution[i]);
				break;
			default:
				break;
			}
		}

    //This is inefficient implementation, but works for now
    //We can change if we have time/it's an issue
    UserPopulationState ups = new UserPopulationState();
    for(Product product: userManager.getRetailCatalog()){
        ups.setDistribution(product, userManager.getStateDistribution(product));
    }
    ups.lock();

    eventWriter.dataUpdated(usersIndex, TACAAConstants.TYPE_NONE, ups);
  }
}
