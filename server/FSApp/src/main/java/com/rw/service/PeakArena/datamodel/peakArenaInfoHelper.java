package com.rw.service.PeakArena.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.peakArenaInfoHelper"  init-method="init" />

public class peakArenaInfoHelper extends CfgCsvDao<PeakArenaInfo> {
	public static peakArenaInfoHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaInfoHelper.class);
	}

	@Override
	public Map<String, PeakArenaInfo> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaInfo.csv",PeakArenaInfo.class);
		Collection<PeakArenaInfo> vals = cfgCacheMap.values();
		for (PeakArenaInfo cfg : vals) {
			cfg.ExtraInitAfterLoad();
			unique = cfg;
		}
		if (cfgCacheMap.size() != 1) throw new RuntimeException("巅峰竞技场的基本配置应该只有一行");
		return cfgCacheMap;
	}
	private PeakArenaInfo unique = null;

	public PeakArenaInfo getUniqueCfg() {
		return unique;
	}
	
	@Override
	public void CheckConfig() {
		if(unique == null) {
			throw new IllegalArgumentException("巅峰竞技场基础配置不存在！");
		}
		if(unique.getWinScore() == 0) {
			throw new IllegalArgumentException("巅峰竞技场胜利积分为0！");
		}
	}
	
}
