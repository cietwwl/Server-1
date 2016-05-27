package com.rw.service.PeakArena.datamodel;

import java.util.Collection;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.IReadOnlyPair;

abstract public class RangeConfigHelper<ConfigClass extends AbsRangeConfig> extends CfgCsvDao<ConfigClass> {
	protected int maxConfigRank;

	protected void doExtraLoad() {
		Collection<ConfigClass> vals = cfgCacheMap.values();
		maxConfigRank = 0;
		for (ConfigClass cfg : vals) {
			cfg.ExtraInitAfterLoad();
			int max = cfg.getRange().getT2();
			if (maxConfigRank < max) {
				maxConfigRank = max;
			}
		}
		if (maxConfigRank == 0) {
			throw new RuntimeException("至少需要一条配置");
		}

	}

	public ConfigClass getBestMatch(int rank) {
		if (rank > maxConfigRank) {
			return null;
		}
		Collection<ConfigClass> vals = cfgCacheMap.values();
		ConfigClass guestCfg = null;
		for (ConfigClass cfg : vals) {
			IReadOnlyPair<Integer, Integer> rankRange = cfg.getRange();
			int min = rankRange.getT1();
			int max = rankRange.getT2();
			if (min <= rank && rank <= max) {
				return cfg;
			}
			if (guestCfg == null) {
				guestCfg = cfg;
			}
			if (max < rank && guestCfg.getRange().getT2() < max) {
				guestCfg = cfg;
			}
		}
		return guestCfg;
	}
}
