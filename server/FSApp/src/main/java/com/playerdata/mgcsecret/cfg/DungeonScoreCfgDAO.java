package com.playerdata.mgcsecret.cfg;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class DungeonScoreCfgDAO extends CfgCsvDao<DungeonScoreCfg> {
	public static DungeonScoreCfgDAO getInstance(){
		return SpringContextUtil.getBean(DungeonScoreCfgDAO.class);
	}
	
	@Override
	public Map<String, DungeonScoreCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/dungeonScore.csv", DungeonScoreCfg.class);
		Set<Entry<String, DungeonScoreCfg>> entrySet = cfgCacheMap.entrySet();
		for (Entry<String, DungeonScoreCfg> entry : entrySet) {
			if(entry != null){
				DungeonScoreCfg cfg = entry.getValue();
				if (cfg != null) {
					//cfg.ExtraInit();
				}else{
					//GameLog.error("法宝", "CriticalEnhance.csv", "invalid cfg");
				}
			}
		}

		return cfgCacheMap;
	}
}
