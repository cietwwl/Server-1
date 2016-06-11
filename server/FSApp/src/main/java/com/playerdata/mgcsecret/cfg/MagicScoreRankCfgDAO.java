package com.playerdata.mgcsecret.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class MagicScoreRankCfgDAO extends CfgCsvDao<MagicScoreRankCfg> {
	public static MagicScoreRankCfgDAO getInstance(){
		return SpringContextUtil.getBean(MagicScoreRankCfgDAO.class);
	}
	
	@Override
	public Map<String, MagicScoreRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/magicScoreRank.csv", MagicScoreRankCfg.class);
		Collection<MagicScoreRankCfg> vals = cfgCacheMap.values();
		for (MagicScoreRankCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
