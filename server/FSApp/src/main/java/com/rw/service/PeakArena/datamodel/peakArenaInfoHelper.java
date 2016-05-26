package com.rw.service.PeakArena.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.peakArenaInfoHelper"  init-method="init" />

public class peakArenaInfoHelper extends CfgCsvDao<peakArenaInfo> {
	public static peakArenaInfoHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaInfoHelper.class);
	}

	@Override
	public Map<String, peakArenaInfo> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaInfo.csv",peakArenaInfo.class);
		Collection<peakArenaInfo> vals = cfgCacheMap.values();
		for (peakArenaInfo cfg : vals) {
			cfg.ExtraInitAfterLoad();
			unique = cfg;
		}
		if (cfgCacheMap.size() != 1) throw new RuntimeException("巅峰竞技场的基本配置应该只有一行");
		return cfgCacheMap;
	}
	private peakArenaInfo unique = null;

	public peakArenaInfo getUniqueCfg() {
		return unique;
	}
}
