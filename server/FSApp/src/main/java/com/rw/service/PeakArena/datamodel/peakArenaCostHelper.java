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
		return cfgCacheMap;
	}
}
