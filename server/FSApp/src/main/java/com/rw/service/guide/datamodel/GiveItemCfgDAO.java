package com.rw.service.guide.datamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class GiveItemCfgDAO extends CfgCsvDao<GiveItemCfg> {
	public static GiveItemCfgDAO getInstance() {
		return SpringContextUtil.getBean(GiveItemCfgDAO.class);
	}

	private HashMap<Integer,GiveItemCfg> autoSentMap;
	
	@Override
	public Map<String, GiveItemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Guidance/GiveItemCfg.csv",GiveItemCfg.class);
		autoSentMap = new HashMap<Integer, GiveItemCfg>();
		Collection<GiveItemCfg> vals = cfgCacheMap.values();
		for (GiveItemCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			int autoSentLevel = cfg.getAutoSentLevel();
			if (autoSentLevel > 0){
				autoSentMap.put(autoSentLevel, cfg);
			}
		}
		return cfgCacheMap;
	}
	
	public GiveItemCfg getAutoSentCfg(int level){
		return autoSentMap.get(level);
	}
}