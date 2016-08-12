package com.playerdata.teambattle.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class TeamCfgDAO extends CfgCsvDao<TeamCfg> {
	public static TeamCfgDAO getInstance() {
		return SpringContextUtil.getBean(TeamCfgDAO.class);
	}

	@Override
	public Map<String, TeamCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("teamBattle/team.csv",TeamCfg.class);
		Collection<TeamCfg> vals = cfgCacheMap.values();
		for (TeamCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
