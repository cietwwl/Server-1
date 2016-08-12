package com.rw.service.PeakArena.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.PeakArena.datamodel.PeakArenaCloseCfgHelper"  init-method="init" />

public class PeakArenaCloseCfgHelper extends CfgCsvDao<PeakArenaCloseCfg> {
	public static PeakArenaCloseCfgHelper getInstance() {
		return SpringContextUtil.getBean(PeakArenaCloseCfgHelper.class);
	}

	private PeakArenaCloseCfg uniqueCfg;

	@Override
	public Map<String, PeakArenaCloseCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/PeakArenaCloseCfg.csv", PeakArenaCloseCfg.class);
		Collection<PeakArenaCloseCfg> vals = cfgCacheMap.values();
		for (PeakArenaCloseCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			uniqueCfg = cfg;
		}
		if (vals.size() <= 0) {
			throw new RuntimeException("巅峰竞技场没有配置关闭时间!");
		}
		return cfgCacheMap;
	}

	public boolean isCloseTime() {
		return uniqueCfg.isCloseTime();
	}
	
	public String getCloseTimeTip(){
		return uniqueCfg.getCloseTimeTip();
	}
}
