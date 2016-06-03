package com.playerdata.activity.dailyCountType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.playerdata.Player;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityDailyTypeCfgDAO extends CfgCsvDao<ActivityDailyTypeCfg> {


	


	public static ActivityDailyTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyTypeCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityDailyTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyCountTypeCfg.csv", ActivityDailyTypeCfg.class);
		for (ActivityDailyTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	



	private void parseTime(ActivityDailyTypeCfg cfgItem){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);		
	}
		
	
	public ActivityDailyTypeCfg getConfig(String id){
		ActivityDailyTypeCfg cfg = getCfgById(id);
		return cfg;
	}
	
	/**
	 * 
	 * @param player
	 * @param countTypeEnum
	 * @param subdaysNum  每日重置类型的活动,第几天
	 * @return
	 */
	public ActivityDailyTypeItem newItem(Player player){
		ActivityDailyTypeCfg cfgById = getConfig(ActivityDailyTypeEnum.Daily.getCfgId());
		if(cfgById!=null){			
			ActivityDailyTypeItem item = new ActivityDailyTypeItem();
			
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


	public List<ActivityDailyTypeSubItem> newItemList(ActivityDailyTypeCfg cfgById) {
		List<ActivityDailyTypeSubItem> subItemList = new ArrayList<ActivityDailyTypeSubItem>();
		List<ActivityDailyTypeSubCfg> allsubCfgList = ActivityDailyTypeSubCfgDAO.getInstance().getAllCfg();	
		for(ActivityDailyTypeSubCfg activityDailyCountTypeSubCfg : allsubCfgList){
			if(!ActivityDailyTypeMgr.getInstance().isOpen(activityDailyCountTypeSubCfg)){
				//该子类型活动当天没开启
				continue;					
			}
			ActivityDailyTypeSubItem subitem = new ActivityDailyTypeSubItem();
			subitem.setCfgId(activityDailyCountTypeSubCfg.getId());
			subitem.setCount(0);
			subitem.setTaken(false);
			subitem.setGiftId(activityDailyCountTypeSubCfg.getGiftId());
			subItemList.add(subitem);
		}		
		return subItemList;
	}
	
	
	

	


}