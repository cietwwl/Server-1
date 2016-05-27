package com.rw.service.PeakArena.datamodel;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.peakArenaPrizeHelper"  init-method="init" />

public class peakArenaPrizeHelper extends RangeConfigHelper<peakArenaPrize> {
	public static peakArenaPrizeHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaPrizeHelper.class);
	}

	@Override
	public Map<String, peakArenaPrize> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaPrize.csv", peakArenaPrize.class);
		this.doExtraLoad();
		// TODO 检查巅峰竞技场容量对应的分段
		return cfgCacheMap;
	}

	public int getBestMatchPrizeCount(int rank) {
		peakArenaPrize guestCfg = getBestMatch(rank,false);
		if (guestCfg == null){
			return 0;
		}
		return guestCfg.getPrizeCountPerHour();
	}
}
