package com.playerdata.activity.dailyCountType.cfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class ActivityDailyTypeSubCfgDAO extends
		CfgCsvDao<ActivityDailyTypeSubCfg> {
	public static ActivityDailyTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyTypeSubCfgDAO.class);
	}

	@Override
	public Map<String, ActivityDailyTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyCountTypeSubCfg.csv",
				ActivityDailyTypeSubCfg.class);
		for (ActivityDailyTypeSubCfg cfgTmp : cfgCacheMap.values()) {
			cfgTmp.ExtraInitAfterLoad();
		}
		HashMap<String, List<ActivityDailyTypeSubCfg>> mapParentidTmp = new HashMap<String, List<ActivityDailyTypeSubCfg>>();
		HashMap<String, List<ActivityDailyTypeSubCfg>> mapEnumidTmp = new HashMap<String, List<ActivityDailyTypeSubCfg>>();
		for (ActivityDailyTypeSubCfg subCfg : cfgCacheMap.values()) {
			ActivityTypeHelper.add(subCfg, subCfg.getParentId(), mapParentidTmp);
			ActivityTypeHelper.add(subCfg, subCfg.getEnumId(), mapEnumidTmp);
		}
//		this.cfgMapByParentid = mapParentidTmp;
//		this.cfgMapByEnumid = mapEnumidTmp;
		return cfgCacheMap;
	}
}