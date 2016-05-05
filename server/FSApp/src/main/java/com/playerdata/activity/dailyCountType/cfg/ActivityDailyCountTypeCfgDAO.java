package com.playerdata.activity.dailyCountType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeHelper;
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
	



	public void parseTime(ActivityDailyCountTypeCfg cfgItem){
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
	public ActivityDailyCountTypeItem newItem(Player player, ActivityDailyCountTypeEnum countTypeEnum){
		
		String cfgId = countTypeEnum.getCfgId();
		ActivityDailyCountTypeCfg cfgById = getCfgById(cfgId );
		if(cfgById!=null){			
			ActivityDailyCountTypeItem item = new ActivityDailyCountTypeItem();
			String itemId = ActivityDailyCountTypeHelper.getItemId(player.getUserId(), countTypeEnum);
			item.setId(itemId);
			item.setCfgId(cfgId);
			item.setUserId(player.getUserId());
			item.setVersion(cfgById.getVersion());
			return item;
		}else{
			return null;
		}		
		
	}
	
	
	

	


}