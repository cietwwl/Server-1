package com.playerdata.activity.VitalityType.cfg;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/**
 * 活跃之王
 * @author Aken
 *
 */
public final class ActivityVitalityCfgDAO extends CfgCsvDao<ActivityVitalityCfg> {
	
	public static ActivityVitalityCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityVitalityCfgDAO.class);
	}
	
	//private HashMap<String, List<ActivityVitalityCfg>> cfgListMap;
	
	@Override
	public Map<String, ActivityVitalityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityVitalityTypeCfg.csv", ActivityVitalityCfg.class);
		HashMap<String, List<ActivityVitalityCfg>> cfgListMapTmp = new HashMap<String, List<ActivityVitalityCfg>>();
		for(ActivityVitalityCfg cfg : cfgCacheMap.values()){
			cfg.ExtraInitAfterLoad();
			ActivityTypeHelper.add(cfg, String.valueOf(cfg.getEnumID()), cfgListMapTmp);			
		}
		//this.cfgListMap = cfgListMapTmp;
		return cfgCacheMap;
	}
}