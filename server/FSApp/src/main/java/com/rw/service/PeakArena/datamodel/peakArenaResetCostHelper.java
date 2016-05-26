package com.rw.service.PeakArena.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.peakArenaResetCostHelper"  init-method="init" />

public class peakArenaResetCostHelper extends CfgCsvDao<peakArenaResetCost> {
	public static peakArenaResetCostHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaResetCostHelper.class);
	}

	@Override
	public Map<String, peakArenaResetCost> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaResetCost.csv",peakArenaResetCost.class);
		Collection<peakArenaResetCost> vals = cfgCacheMap.values();
		for (peakArenaResetCost cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		if (cfgCacheMap.size() <= 0) throw new RuntimeException("巅峰竞技场的重置扣费方案至少要有一个");
		return cfgCacheMap;
	}
	
	public peakArenaResetCost getCfgByResetCount(int resetCount){
		peakArenaResetCost result = null;
		int bestMatchCount = -1;
		Collection<peakArenaResetCost> vals = cfgCacheMap.values();
		for (peakArenaResetCost cfg : vals) {
			//保底方案
			if (result == null){
				result = cfg;
			}
			if (cfg.getMinCount()<= resetCount && resetCount<= cfg.getMaxCount()){
				return cfg;
			}
			//最佳匹配
			if (cfg.getMaxCount() < resetCount && bestMatchCount < cfg.getMaxCount()){
				result = cfg;
				bestMatchCount = cfg.getMaxCount();
			}
		}
		return result;
	}
}
