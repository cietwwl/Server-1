package com.playerdata.activity.VitalityType.cfg;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubBoxItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
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
	public ActivityVitalityTypeItem newItem(Player player){
		ActivityVitalityCfg cfgById = getConfig(ActivityVitalityTypeEnum.Vitality.getCfgId());
		if(cfgById!=null){
			int day = getday();
			ActivityVitalityTypeItem item = new ActivityVitalityTypeItem();			
			item.setId(player.getUserId());
			item.setCfgId(cfgById.getId());
			item.setUserId(player.getUserId());
			item.setVersion(cfgById.getVersion());
			item.setActiveCount(0);
			item.setSubItemList(newItemList(day));
			item.setSubBoxItemList(newBoxItemList(day));
			item.setLastTime(System.currentTimeMillis());
			item.setIsCanGetReward(cfgById.getIsCanGetReward());
			return item;
		}else{
			return null;
		}		
	}
	
	

	/**根据当前时间返回处于活动之王活动的第几天*/
	public int getday() {
		ActivityVitalityCfg cfgById = getConfig(ActivityVitalityTypeEnum.Vitality.getCfgId());
		if(cfgById == null){
			return 0;
		}
		long startTime = cfgById.getStartTime();
		long currentTime = System.currentTimeMillis();
		int day = DateUtils.getDayDistance(startTime, currentTime);
		day++;		
		return day;
	}

	public List<ActivityVitalityTypeSubItem> newItemList(int day) {
		List<ActivityVitalityTypeSubItem> subItemList = new ArrayList<ActivityVitalityTypeSubItem>();
		List<ActivityVitalitySubCfg> allsubCfgList = ActivityVitalitySubCfgDAO.getInstance().getAllCfg();	
		for(ActivityVitalitySubCfg activityVitalitySubCfg : allsubCfgList){
			if(activityVitalitySubCfg.getDay() != day){
				continue;
			}
			
			ActivityVitalityTypeSubItem subitem = new ActivityVitalityTypeSubItem();
			subitem.setCfgId(activityVitalitySubCfg.getId());
			subitem.setCount(0);
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
			subItemList.add(subitem);
		}		
		return subItemList;
	}
	
	public List<ActivityVitalityTypeSubBoxItem> newBoxItemList(int day) {
		List<ActivityVitalityTypeSubBoxItem> subItemList = new ArrayList<ActivityVitalityTypeSubBoxItem>();
		List< ActivityVitalityRewardCfg> allsubCfgList = ActivityVitalityRewardCfgDAO.getInstance().getAllCfg();	
		for(ActivityVitalityRewardCfg activityVitalitySubCfg : allsubCfgList){
			if(activityVitalitySubCfg.getDay() != day){
				continue;
			}
			
			ActivityVitalityTypeSubBoxItem subitem = new ActivityVitalityTypeSubBoxItem();
			subitem.setCfgId(activityVitalitySubCfg.getId());
			subitem.setCount(activityVitalitySubCfg.getActivecount());
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
			subItemList.add(subitem);
		}		
		return subItemList;
	}
	
	public ActivityVitalityCfg getparentCfg(){
		List<ActivityVitalityCfg> allCfgList = getAllCfg();		
		if(allCfgList == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "不存在每日活动" );
			return null;			
		}		
		if(allCfgList.size() != 1){
			GameLog.error("activityDailyCountTypeMgr", "list", "同时存在多个每日活动" + allCfgList.size());
			return null;
		}		
		ActivityVitalityCfg vitalityCfg = allCfgList.get(0);		
		return vitalityCfg;
	}

}