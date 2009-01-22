package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.AdLink;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrick Jordan, Lee Callender
 */
public class UserEventSupport {

	private List<UserEventListener> listeners;

	public UserEventSupport() {
		listeners = new ArrayList<UserEventListener>();
	}

	public boolean addUserEventListener(UserEventListener listener) {
		return listeners.add(listener);
	}

	public boolean containsUserEventListener(UserEventListener listener) {
		return listeners.contains(listener);
	}

	public boolean removeUserEventListener(UserEventListener listener) {
		return listeners.remove(listener);
	}

	public void fireQueryIssued(Query query) {
		for (UserEventListener listener : listeners) {
			listener.queryIssued(query);
		}
	}

	public void fireAdViewed(Query query, AdLink ad, int slot,
			boolean isPromoted) {
		for (UserEventListener listener : listeners) {
			listener.viewed(query, ad, slot, ad.getAdvertiser(), isPromoted);
		}
	}

	public void fireAdClicked(Query query, AdLink ad, int slot, double cpc) {
		for (UserEventListener listener : listeners) {
			listener.clicked(query, ad, slot, cpc, ad.getAdvertiser());
		}
	}

	public void fireAdConverted(Query query, AdLink ad, int slot,
			double salesProfit) {
		for (UserEventListener listener : listeners) {
			listener
					.converted(query, ad, slot, salesProfit, ad.getAdvertiser());
		}
	}

}
