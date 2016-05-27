package com.rw.service.PeakArena.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.peakArenaResetCostHelper"  init-method="init" />

public class peakArenaResetCostHelper extends RangeConfigHelper<peakArenaResetCost> {
	public static peakArenaResetCostHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaResetCostHelper.class);
	}

	@Override
	public Map<String, peakArenaResetCost> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaResetCost.csv",peakArenaResetCost.class);
		this.doExtraLoad();
		return cfgCacheMap;
	}
	
	public peakArenaResetCost getCfgByResetCount(int resetCount){
		peakArenaResetCost result = this.getBestMatch(resetCount);
		return result;
	}
}
