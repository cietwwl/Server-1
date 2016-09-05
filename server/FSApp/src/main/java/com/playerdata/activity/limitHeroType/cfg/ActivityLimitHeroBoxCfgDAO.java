package com.playerdata.activity.limitHeroType.cfg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ActivityLimitHeroBoxCfgDAO extends CfgCsvDao<ActivityLimitHeroBoxCfg>{
	public static ActivityLimitHeroBoxCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityLimitHeroBoxCfgDAO.class);
	}

	private HashMap<String, List<ActivityLimitHeroBoxCfg>> boxCfgListMap ;
	@Override
	public Map<String, ActivityLimitHeroBoxCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityLimitHeroBoxCfg.csv", ActivityLimitHeroBoxCfg.class);		
		HashMap<String, List<ActivityLimitHeroBoxCfg>> boxCfgListMapTmp = new HashMap<String, List<ActivityLimitHeroBoxCfg>>();
		for(ActivityLimitHeroBoxCfg boxCfg : cfgCacheMap.values()){
			ActivityTypeHelper.add(boxCfg, boxCfg.getParentid(), boxCfgListMapTmp);
		}
		this.boxCfgListMap = boxCfgListMapTmp;
		
		return cfgCacheMap;
	}
	
	public List<ActivityLimitHeroBoxCfg> getCfgListByParentID(String parentID){
		return boxCfgListMap.get(parentID);
	}
	
}
