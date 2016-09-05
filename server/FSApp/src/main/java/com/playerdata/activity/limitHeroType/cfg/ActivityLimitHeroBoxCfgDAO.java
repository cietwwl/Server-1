package com.playerdata.activity.limitHeroType.cfg;


import java.util.Map;

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
			String parentID = boxCfg.getParentid();
			List<ActivityLimitHeroBoxCfg> list = boxCfgListMapTmp.get(parentID);
			if(list == null){
				list = new ArrayList<ActivityLimitHeroBoxCfg>();
				boxCfgListMapTmp.put(parentID, list);
			}			
			list.add(boxCfg);
		}
		this.boxCfgListMap = boxCfgListMapTmp;
		
		return cfgCacheMap;
	}
	
	public List<ActivityLimitHeroBoxCfg> getCfgListByParentID(String parentID){
		return boxCfgListMap.get(parentID);
	}
	
}
