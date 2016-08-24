package com.playerdata.activity.limitHeroType.cfg;

import java.util.Map;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ActivityLimitHeroRankCfgDAO extends CfgCsvDao<ActivityLimitHeroRankCfg>{
	
	public static ActivityLimitHeroRankCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityLimitHeroRankCfgDAO.class);
	}
	
	@Override
	protected Map<String, ActivityLimitHeroRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityLimitHeroRankCfg.csv", ActivityLimitHeroRankCfg.class);
		for (ActivityLimitHeroRankCfg cfgTmp : cfgCacheMap.values()) {
			parSetRankRange(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	
	private void parSetRankRange(ActivityLimitHeroRankCfg cfg){
		String[] strs = cfg.getRankRange().split("_");
		cfg.getRankRanges()[0] = Integer.parseInt(strs[0]);
		if(strs.length == 2){
			cfg.getRankRanges()[1] = Integer.parseInt(strs[1]);
		}else if(strs.length == 1){
			cfg.getRankRanges()[1] = Integer.parseInt(strs[0]);
		}else{
			GameLog.error(LogModule.ComActivityRank, null, "范围rankrange格式错误", null);
		}
	}

}
