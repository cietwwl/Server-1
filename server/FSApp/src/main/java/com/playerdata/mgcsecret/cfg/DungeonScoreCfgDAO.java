package com.playerdata.mgcsecret.cfg;

import java.util.Collection;
import java.util.Map;

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
		Collection<DungeonScoreCfg> vals = cfgCacheMap.values();
		for (DungeonScoreCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
