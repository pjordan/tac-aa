package edu.umich.eecs.tac.is;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Oct 6, 2008
 * Time: 12:46:42 AM
 * To change this template use File | Settings | File Templates.
 */

import se.sics.tasim.is.common.InfoManager;
import se.sics.tasim.is.common.ResultManager;
import se.sics.tasim.is.common.ViewerCache;
import edu.umich.eecs.tac.TACAAConstants;

//TODO-TEST

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
