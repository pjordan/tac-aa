package edu.umich.eecs.tac.is;

/**
 * @author Patrick Jordan, Lee Callender
 */

import se.sics.tasim.is.common.ViewerCache;
import se.sics.tasim.is.EventWriter;
import se.sics.isl.transport.Transportable;

import java.util.logging.Logger;
import java.util.Hashtable;

import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.TACAAConstants;

public class TACAAViewerCache extends ViewerCache {
	private static final Logger log = Logger.getLogger(TACAAViewerCache.class
			.getName());

	private RetailCatalog catalog;
	private int timeUnit;

	private static final int DU_AGENT = 0;
	private static final int DU_TYPE = 1;
	private static final int DU_VALUE = 2;
	private static final int DU_PARTS = 3;

	private int[] dataUpdatedConstants;
	private int dataUpdatedCount = 0;
	private int noCachedData = 0;

	private Hashtable cache = new Hashtable();

	public static final int MAX_CACHE = 60;

	public TACAAViewerCache() {
	}

	private void addToCache(int agent, int type, int value) {
		String key = "" + agent + "_" + type;
		CacheEntry ce = (CacheEntry) cache.get(key);
		if (ce == null) {
			ce = new CacheEntry();
			ce.agent = agent;
			ce.type = type;
			ce.cachedData = new int[MAX_CACHE];
			cache.put(key, ce);
		}
		ce.addCachedData(value);
	}

	public void writeCache(EventWriter eventWriter) {
		super.writeCache(eventWriter);

		if (catalog != null) {
			eventWriter.dataUpdated(TACAAConstants.TYPE_NONE, catalog);
		}
		if (timeUnit > 0) {
			eventWriter.nextTimeUnit(timeUnit);
		}
		if (dataUpdatedCount > 0) {
			for (int i = 0, n = dataUpdatedCount * DU_PARTS; i < n; i += DU_PARTS) {
				eventWriter.dataUpdated(dataUpdatedConstants[i + DU_AGENT],
						dataUpdatedConstants[i + DU_TYPE],
						dataUpdatedConstants[i + DU_VALUE]);
			}
		}

		Object[] keys = cache.keySet().toArray();
		if (keys != null) {
			for (int i = 0, n = keys.length; i < n; i++) {
				CacheEntry ce = (CacheEntry) cache.get(keys[i]);
				if (ce != null) {
					eventWriter.intCache(ce.agent, ce.type, ce.getCache());
				}
			}
		}
	}

	public void nextTimeUnit(int timeUnit) {
		this.timeUnit = timeUnit;
	}

	public void dataUpdated(int agent, int type, int value) {
		if (type == TACAAConstants.DU_NON_SEARCHING
				|| type == TACAAConstants.DU_NON_SEARCHING
				|| type == TACAAConstants.DU_INFORMATIONAL_SEARCH
				|| type == TACAAConstants.DU_FOCUS_LEVEL_ZERO
				|| type == TACAAConstants.DU_FOCUS_LEVEL_ONE
				|| type == TACAAConstants.DU_FOCUS_LEVEL_TWO) {
			addToCache(agent, type, value);
		}
	}

	public void dataUpdated(int agent, int type, long value) {
		if ((type & TACAAConstants.DU_BANK_ACCOUNT) != 0) {
			addToCache(agent, type, (int) value);
		}
	}

	public void dataUpdated(int type, Transportable value) {
		Class valueType = value.getClass();
		if (valueType == RetailCatalog.class) {
			this.catalog = (RetailCatalog) value;
		}
	}

	private static class CacheEntry {
		int agent;
		int type;
		int[] cachedData;
		int pos;
		int len;

		public void addCachedData(int value) {
			// System.out.println("**** CacheEntity: adding cache[" + pos + "]="
			// +
			// value);
			cachedData[pos] = value;
			pos = (pos + 1) % MAX_CACHE;
			if (len < MAX_CACHE) {
				len++;
			}
		}

		public int[] getCache() {
			int[] tmp = new int[len];
			int start = ((pos - len) + MAX_CACHE) % MAX_CACHE;
			for (int i = 0, n = len; i < n; i++) {
				tmp[i] = cachedData[(start + i) % MAX_CACHE];
			}
			return tmp;
		}
	}
}
