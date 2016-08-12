package com.playerdata.activity.dailyCharge.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.activity.dailyCharge.ActivityDetector;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfgDAO"  init-method="init" />

public class ActivityDailyChargeSubCfgDAO extends CfgCsvDao<ActivityDailyChargeSubCfg> {
	public static ActivityDailyChargeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyChargeSubCfgDAO.class);
	}

	@Override
	public Map<String, ActivityDailyChargeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyChargeSubCfg.csv",ActivityDailyChargeSubCfg.class);
		Collection<ActivityDailyChargeSubCfg> vals = cfgCacheMap.values();
		for (ActivityDailyChargeSubCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
	
	public List<String> getTodaySubActivity(String cfgID){
		List<String> todaySubs = new ArrayList<String>();
		ActivityDailyChargeCfg cfg = ActivityDailyChargeCfgDAO.getInstance().getCfgById(cfgID);
		if(null == cfg) return todaySubs; 
		if(!ActivityDetector.getInstance().isActive(cfg)) return todaySubs;
		//还在活跃期内，取当天的数据
		int todayNum = ActivityDetector.getInstance().getCurrentDay(cfg);
		for(ActivityDailyChargeSubCfg subCfg : cfgCacheMap.values()){
			if(StringUtils.equals(subCfg.getDay(), String.valueOf(todayNum)) && 
					StringUtils.equals(String.valueOf(subCfg.getType()), cfgID)){
				todaySubs.add(String.valueOf(subCfg.getId()));
			}
		}
		return todaySubs;
	}
}
