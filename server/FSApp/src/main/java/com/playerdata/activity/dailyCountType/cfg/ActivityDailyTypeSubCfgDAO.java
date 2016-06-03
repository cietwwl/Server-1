package com.playerdata.activity.dailyCountType.cfg;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityDailyTypeSubCfgDAO extends CfgCsvDao<ActivityDailyTypeSubCfg> {


	public static ActivityDailyTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyTypeSubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityDailyTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyCountTypeSubCfg.csv", ActivityDailyTypeSubCfg.class);	
		for (ActivityDailyTypeSubCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	
	private void parseTime(ActivityDailyTypeSubCfg cfgTmp){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgTmp.getStartTimeStr());
		cfgTmp.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgTmp.getEndTimeStr());
		cfgTmp.setEndTime(endTime);		
	}


	public ActivityDailyTypeSubCfg getById(String subId){
		ActivityDailyTypeSubCfg target = new ActivityDailyTypeSubCfg();
		List<ActivityDailyTypeSubCfg> allCfg = getAllCfg();
		for (ActivityDailyTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getId(), subId)){
				target = tmpItem;
			}
		}
		return target;
		
	}
	
	


}