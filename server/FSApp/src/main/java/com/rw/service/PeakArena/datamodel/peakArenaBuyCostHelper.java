package com.rw.service.PeakArena.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.peakArenaBuyCostHelper"  init-method="init" />

public class peakArenaBuyCostHelper extends CfgCsvDao<peakArenaBuyCost> {
	public static peakArenaBuyCostHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaBuyCostHelper.class);
	}

	@Override
	public Map<String, peakArenaBuyCost> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaBuyCost.csv",peakArenaBuyCost.class);
		Collection<peakArenaBuyCost> vals = cfgCacheMap.values();
		for (peakArenaBuyCost cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		if (cfgCacheMap.size() <= 0) throw new RuntimeException("巅峰竞技场的购买挑战次数扣费方案至少要有一个");
		return cfgCacheMap;
	}
	
	public peakArenaBuyCost getCfgByCount(int buyCount){
		peakArenaBuyCost result = null;
		int bestMatchCount = -1;
		Collection<peakArenaBuyCost> vals = cfgCacheMap.values();
		for (peakArenaBuyCost cfg : vals) {
			//保底方案
			if (result == null){
				result = cfg;
			}
			if (cfg.getMinCount()<= buyCount && buyCount<= cfg.getMaxCount()){
				return cfg;
			}
			//最佳匹配
			if (cfg.getMaxCount() < buyCount && bestMatchCount < cfg.getMaxCount()){
				result = cfg;
				bestMatchCount = cfg.getMaxCount();
			}
		}
		return result;
	}
}
