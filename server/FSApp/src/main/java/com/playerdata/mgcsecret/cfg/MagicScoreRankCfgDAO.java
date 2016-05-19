package com.playerdata.mgcsecret.cfg;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
		Set<Entry<String, MagicScoreRankCfg>> entrySet = cfgCacheMap.entrySet();
		for (Entry<String, MagicScoreRankCfg> entry : entrySet) {
			if(entry != null){
				MagicScoreRankCfg cfg = entry.getValue();
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
