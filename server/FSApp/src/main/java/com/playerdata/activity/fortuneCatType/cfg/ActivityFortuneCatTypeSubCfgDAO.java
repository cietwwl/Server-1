package com.playerdata.activity.fortuneCatType.cfg;


import java.util.ArrayList;
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

	
	@Override
	public Map<String, ActivityFortuneCatTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityFortunCatTypeSubCfg.csv", ActivityFortuneCatTypeSubCfg.class);	
		return cfgCacheMap;
	}


	public List<ActivityFortuneCatTypeSubCfg> getCfgListByParentId(String cfgId) {
		List<ActivityFortuneCatTypeSubCfg> subCfgList = getAllCfg();
		List<ActivityFortuneCatTypeSubCfg> subCfgListByCfgId = new ArrayList<ActivityFortuneCatTypeSubCfg>();
		for(ActivityFortuneCatTypeSubCfg subCfg :subCfgList){
			if(StringUtils.equals(subCfg.getParentid(), cfgId)){
				subCfgListByCfgId.add(subCfg);
			}			
		}		
		return subCfgListByCfgId;
	}
}