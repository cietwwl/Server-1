package com.rw.service.guide.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class GiveItemCfgDAO extends CfgCsvDao<GiveItemCfg> {
	public static GiveItemCfgDAO getInstance() {
		return SpringContextUtil.getBean(GiveItemCfgDAO.class);
	}

	private HashMap<Integer,List<GiveItemCfg>> autoSentMap;
	
	@Override
	public Map<String, GiveItemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Guidance/GiveItemCfg.csv",GiveItemCfg.class);
		autoSentMap = new HashMap<Integer, List<GiveItemCfg>>();
		Collection<GiveItemCfg> vals = cfgCacheMap.values();
		for (GiveItemCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			int autoSentLevel = cfg.getAutoSentLevel();
			if (autoSentLevel > 0){
				List<GiveItemCfg> old = autoSentMap.get(autoSentLevel);
				if (old == null){
					old = new ArrayList<GiveItemCfg>();
					autoSentMap.put(autoSentLevel, old);
				}
				old.add(cfg);
			}
		}
		return cfgCacheMap;
	}
	
	public List<GiveItemCfg> getAutoSentCfg(int level){
		return autoSentMap.get(level);
	}
}