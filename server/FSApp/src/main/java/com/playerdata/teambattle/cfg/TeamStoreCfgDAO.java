package com.playerdata.teambattle.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class TeamStoreCfgDAO extends CfgCsvDao<TeamStoreCfg> {
	public static TeamStoreCfgDAO getInstance() {
		return SpringContextUtil.getBean(TeamStoreCfgDAO.class);
	}

	@Override
	public Map<String, TeamStoreCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("teamBattle/teamStore.csv",TeamStoreCfg.class);
		Collection<TeamStoreCfg> vals = cfgCacheMap.values();
		for (TeamStoreCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
