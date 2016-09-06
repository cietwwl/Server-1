package com.playerdata.activity.fortuneCatType.cfg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;









import org.apache.commons.lang3.StringUtils;

import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeEnum;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityFortuneCatTypeSubCfgDAO extends CfgCsvDao<ActivityFortuneCatTypeSubCfg> {
	public static ActivityFortuneCatTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityFortuneCatTypeSubCfgDAO.class);
	}

	private HashMap<String, List<ActivityFortuneCatTypeSubCfg>> subCfgListMap ;
	
	
	@Override
	public Map<String, ActivityFortuneCatTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityFortunCatTypeSubCfg.csv", ActivityFortuneCatTypeSubCfg.class);	
		HashMap<String, List<ActivityFortuneCatTypeSubCfg>> subCfgListMapTmp = new HashMap<String, List<ActivityFortuneCatTypeSubCfg>>();
		for(ActivityFortuneCatTypeSubCfg subCfg: cfgCacheMap.values()){
			String parentID = subCfg.getParentid();
			List<ActivityFortuneCatTypeSubCfg> list = subCfgListMapTmp.get(parentID);
			if(list == null){
				list = new ArrayList<ActivityFortuneCatTypeSubCfg>();
				subCfgListMapTmp.put(parentID, list);
			}			
			list.add(subCfg);
		}
		this.subCfgListMap = subCfgListMapTmp;
		return cfgCacheMap;				
	}


	public List<ActivityFortuneCatTypeSubCfg> getCfgListByParentId(String cfgId) {
		return subCfgListMap.get(cfgId);
	}
}