package com.playerdata.activity.dailyCountType.cfg;

import java.util.Map;

import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 *  每日福利
 * @author aken
 * @Description 每日福利的活动
 */
public final class ActivityDailyTypeCfgDAO extends CfgCsvDao<ActivityDailyTypeCfg> {
	
	public static ActivityDailyTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyTypeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityDailyTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyCountTypeCfg.csv", ActivityDailyTypeCfg.class);
		for(ActivityCfgIF cfg : cfgCacheMap.values()){
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}