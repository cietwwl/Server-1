package com.rw.service.PeakArena.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.peakArenaMatchRuleHelper"  init-method="init" />

public class peakArenaMatchRuleHelper extends RangeConfigHelper<peakArenaMatchRule> {
	public static peakArenaMatchRuleHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaMatchRuleHelper.class);
	}

	@Override
	public Map<String, peakArenaMatchRule> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaMatchRule.csv",peakArenaMatchRule.class);
		this.doExtraLoad();
		return cfgCacheMap;
	}
}
