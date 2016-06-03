package com.playerdata.activity.VitalityType.cfg;


import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.VitalityType.VitalityTypeEnum;
import com.playerdata.activity.VitalityType.data.ActivityVitalityItem;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeEnum;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfg;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityVitalityCfgDAO extends CfgCsvDao<ActivityVitalityCfg> {
	public static ActivityVitalityCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityVitalityCfgDAO.class);
	}
	
	@Override
	public Map<String, ActivityVitalityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityVitalityTypeCfg.csv", ActivityVitalityCfg.class);
		for (ActivityVitalityCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}		
		return cfgCacheMap;
	}


	public void parseTime(ActivityVitalityCfg cfg){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getStartTimeStr());
		cfg.setStartTime(startTime);		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getEndTimeStr());
		cfg.setEndTime(endTime);		
	}		
	
	public ActivityVitalityCfg getConfig(String id){
		ActivityVitalityCfg cfg = getCfgById(id);
		return cfg;
	}
	
	/**
	 * 
	 * @param player
	 * @param countTypeEnum
	 * @param subdaysNum  无数据记录的玩家根据第几天开始参与活跃之王来生成数据
	 * @return
	 */
	public ActivityVitalityItem newItem(Player player){
		ActivityVitalityCfg cfgById = getConfig(VitalityTypeEnum.Vitality.getCfgId());
		if(cfgById!=null){			
			ActivityVitalityItem item = new ActivityVitalityItem();			
			item.setId(player.getUserId());
			item.setUserId(player.getUserId());
			item.setVersion(cfgById.getVersion());
//			item.setSubItemList(newItemList());
			item.setLastTime(System.currentTimeMillis());
			return item;
		}else{
			return null;
		}		
	}
	


}