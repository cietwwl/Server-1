package com.playerdata.activity.dailyDiscountType.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ActivityDailyDiscountItemCfgDao extends CfgCsvDao<ActivityDailyDiscountItemCfg>{
	public static ActivityDailyDiscountItemCfgDao getInstance() {
		return SpringContextUtil.getBean(ActivityDailyDiscountItemCfgDao.class);
	}

	
	@Override
	public Map<String, ActivityDailyDiscountItemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyDiscountItemCfg.csv", ActivityDailyDiscountItemCfg.class);	
		for(ActivityDailyDiscountItemCfg itemCfg:cfgCacheMap.values()){
			prase(itemCfg);
		}		
		return cfgCacheMap;
	}


	private void prase(ActivityDailyDiscountItemCfg itemCfg) {
//		String idAndNum = itemCfg.getItemIdAndNum();
//		String[] strs = idAndNum.split("_");
//		itemCfg.setItemId(Integer.parseInt(strs[0]));
//		itemCfg.setItemNum(Integer.parseInt(strs[1]));
	}
}
