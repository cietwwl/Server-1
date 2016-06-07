package com.rw.service.PeakArena.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.peakArenaBuyCostHelper"  init-method="init" />

public class peakArenaBuyCostHelper extends RangeConfigHelper<peakArenaBuyCost> {
	public static peakArenaBuyCostHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaBuyCostHelper.class);
	}

	@Override
	public Map<String, peakArenaBuyCost> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaBuyCost.csv",peakArenaBuyCost.class);
		this.doExtraLoad();
		return cfgCacheMap;
	}
	
	public peakArenaBuyCost getCfgByCount(int buyCount){
		return this.getBestMatch(buyCount,true);
	}
}
