package com.playerdata.teambattle.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class TeamMatchCfgDAO extends CfgCsvDao<TeamMatchCfg> {
	public static TeamMatchCfgDAO getInstance() {
		return SpringContextUtil.getBean(TeamMatchCfgDAO.class);
	}

	@Override
	public Map<String, TeamMatchCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("teamBattle/teamMatch.csv",TeamMatchCfg.class);
		Collection<TeamMatchCfg> vals = cfgCacheMap.values();
		for (TeamMatchCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
