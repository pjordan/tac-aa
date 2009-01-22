package edu.umich.eecs.tac.is;

import se.sics.tasim.is.common.InfoManager;
import se.sics.tasim.is.common.ResultManager;
import se.sics.tasim.is.common.ViewerCache;
import edu.umich.eecs.tac.TACAAConstants;

/**
 * @author Lee Callender, Patrick Jordan
 */
public class TACAAInfoManager extends InfoManager {
	public TACAAInfoManager() {
	}

	protected void init() {
		for (int i = 0, n = TACAAConstants.SUPPORTED_TYPES.length; i < n; i++) {
			registerType(TACAAConstants.SUPPORTED_TYPES[i]);
		}
	}

	public ViewerCache createViewerCache(String simType) {
		return new TACAAViewerCache();
	}

	public ResultManager createResultManager(String simType) {
		return new TACAAResultManager();
	}
}
