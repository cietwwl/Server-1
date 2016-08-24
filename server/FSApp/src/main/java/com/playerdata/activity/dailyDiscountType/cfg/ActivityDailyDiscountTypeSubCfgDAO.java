package com.playerdata.activity.dailyDiscountType.cfg;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;








import org.apache.commons.lang3.StringUtils;

import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeEnum;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityDailyDiscountTypeSubCfgDAO extends CfgCsvDao<ActivityDailyDiscountTypeSubCfg> {
	public static ActivityDailyDiscountTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyDiscountTypeSubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityDailyDiscountTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyDiscountTypeSubCfg.csv", ActivityDailyDiscountTypeSubCfg.class);	
		for (ActivityDailyDiscountTypeSubCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}		
		return cfgCacheMap;
	}
	
	private void parseTime(ActivityDailyDiscountTypeSubCfg cfgTmp){
		List<Integer> itemList = cfgTmp.getItemList();
		String tmp = cfgTmp.getItemIdList();
		String[] tmps = tmp.split("_");
		for(String itemId : tmps){
			int tmpInt = Integer.parseInt(itemId);
			itemList.add(tmpInt);
		}
	}


	public List<ActivityDailyDiscountTypeSubCfg> getCfgListByParentId(String parantId) {
		List<ActivityDailyDiscountTypeSubCfg> subCfgList = getAllCfg();
		List<ActivityDailyDiscountTypeSubCfg> subCfgListByEnumID = new ArrayList<ActivityDailyDiscountTypeSubCfg>();
		for(ActivityDailyDiscountTypeSubCfg subCfg :subCfgList){
			if(StringUtils.equals(subCfg.getParentId(), parantId)){
				subCfgListByEnumID.add(subCfg);
			}			
		}		
		return subCfgListByEnumID;
	}
}