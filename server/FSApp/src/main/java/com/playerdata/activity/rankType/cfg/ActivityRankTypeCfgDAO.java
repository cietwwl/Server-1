package com.playerdata.activity.rankType.cfg;

import java.util.Map;

import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author Aken
 * @Description 竞技之王和战力大比拼配置表Dao
 */
public final class ActivityRankTypeCfgDAO extends CfgCsvDao<ActivityRankTypeCfg> {
	
	public static ActivityRankTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRankTypeCfgDAO.class);
	}
	
	@Override
	public Map<String, ActivityRankTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRankTypeCfg.csv", ActivityRankTypeCfg.class);
		for(ActivityCfgIF cfg : cfgCacheMap.values()){
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}