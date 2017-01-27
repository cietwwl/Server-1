package com.playerdata.activity.countType.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author aken
 * @Description 登录奖励等基础活动
 */
public final class ActivityCountTypeCfgDAO extends CfgCsvDao<ActivityCountTypeCfg> {

	public static ActivityCountTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityCountTypeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityCountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityCountTypeCfg.csv", ActivityCountTypeCfg.class);
		for (ActivityCountTypeCfg cfgTmp : cfgCacheMap.values()) {
			cfgTmp.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}