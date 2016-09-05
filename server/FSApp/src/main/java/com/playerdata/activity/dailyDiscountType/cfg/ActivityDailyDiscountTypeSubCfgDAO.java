package com.playerdata.activity.dailyDiscountType.cfg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityDailyDiscountTypeSubCfgDAO extends CfgCsvDao<ActivityDailyDiscountTypeSubCfg> {
	public static ActivityDailyDiscountTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyDiscountTypeSubCfgDAO.class);
	}

	private HashMap<String, List<ActivityDailyDiscountTypeSubCfg>> subCfgListMap ;
	
	@Override
	public Map<String, ActivityDailyDiscountTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyDiscountTypeSubCfg.csv", ActivityDailyDiscountTypeSubCfg.class);	
		for (ActivityDailyDiscountTypeSubCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}		
		HashMap<String, List<ActivityDailyDiscountTypeSubCfg>> subCfgListMapTmp = new HashMap<String, List<ActivityDailyDiscountTypeSubCfg>>();
		for(ActivityDailyDiscountTypeSubCfg subCfg : cfgCacheMap.values()){
			String parentid = subCfg.getParentId();
			List<ActivityDailyDiscountTypeSubCfg> list = subCfgListMapTmp.get(parentid);
			if(list == null){
				list = new ArrayList<ActivityDailyDiscountTypeSubCfg>();
				subCfgListMapTmp.put(parentid, list);				
			}
			list.add(subCfg);
 		}
		this.subCfgListMap = subCfgListMapTmp;
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
		return subCfgListMap.get(parantId);
	}
}