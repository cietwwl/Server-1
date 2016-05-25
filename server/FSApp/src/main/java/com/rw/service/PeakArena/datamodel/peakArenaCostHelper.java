package com.rw.service.PeakArena.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.peakArenaCostHelper"  init-method="init" />

public class peakArenaCostHelper extends CfgCsvDao<peakArenaCost> {
	public static peakArenaCostHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaCostHelper.class);
	}

	@Override
	public Map<String, peakArenaCost> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaCost.csv",peakArenaCost.class);
		Collection<peakArenaCost> vals = cfgCacheMap.values();
		for (peakArenaCost cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		if (cfgCacheMap.size() <= 0) throw new RuntimeException("巅峰竞技场的扣费方案至少要有一个");
		return cfgCacheMap;
	}
	
	public peakArenaCost getCfgByResetCount(int resetCount){
		peakArenaCost result = null;
		int bestMatchCount = -1;
		Collection<peakArenaCost> vals = cfgCacheMap.values();
		for (peakArenaCost cfg : vals) {
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
