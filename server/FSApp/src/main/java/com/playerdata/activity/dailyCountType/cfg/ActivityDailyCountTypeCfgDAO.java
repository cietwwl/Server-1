package com.playerdata.activity.dailyCountType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.playerdata.Player;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeMgr;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityDailyCountTypeCfgDAO extends CfgCsvDao<ActivityDailyCountTypeCfg> {


	


	public static ActivityDailyCountTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyCountTypeCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityDailyCountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyCountTypeCfg.csv", ActivityDailyCountTypeCfg.class);
		for (ActivityDailyCountTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	



	private void parseTime(ActivityDailyCountTypeCfg cfgItem){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);		
	}
		
	
	public ActivityDailyCountTypeCfg getConfig(String id){
		ActivityDailyCountTypeCfg cfg = getCfgById(id);
		return cfg;
	}
	
	/**
	 * 
	 * @param player
	 * @param countTypeEnum
	 * @param subdaysNum  每日重置类型的活动,第几天
	 * @return
	 */
	public ActivityDailyCountTypeItem newItem(Player player){
		ActivityDailyCountTypeCfg cfgById = getConfig(ActivityDailyCountTypeEnum.Daily.getCfgId());
		if(cfgById!=null){			
			ActivityDailyCountTypeItem item = new ActivityDailyCountTypeItem();			
			item.setId(player.getUserId());
			item.setUserId(player.getUserId());
			item.setCfgid(cfgById.getId());
			item.setVersion(cfgById.getVersion());
			item.setSubItemList(newItemList());
			item.setLastTime(System.currentTimeMillis());
			return item;
		}else{
			return null;
		}		
	}


	public List<ActivityDailyCountTypeSubItem> newItemList() {
		List<ActivityDailyCountTypeSubItem> subItemList = new ArrayList<ActivityDailyCountTypeSubItem>();
		List<ActivityDailyCountTypeSubCfg> allsubCfgList = ActivityDailyCountTypeSubCfgDAO.getInstance().getAllCfg();	
		for(ActivityDailyCountTypeSubCfg activityDailyCountTypeSubCfg : allsubCfgList){
			if(!ActivityDailyCountTypeMgr.getInstance().isOpen(activityDailyCountTypeSubCfg)){
				//该子类型活动当天没开启
				continue;					
			}
			ActivityDailyCountTypeSubItem subitem = new ActivityDailyCountTypeSubItem();
			subitem.setCfgId(activityDailyCountTypeSubCfg.getId());
			subitem.setCount(0);
			subitem.setTaken(false);
			subitem.setGiftId(activityDailyCountTypeSubCfg.getGiftId());
			subItemList.add(subitem);
		}
		
		
		
		return subItemList;
	}
	
	
	

	


}