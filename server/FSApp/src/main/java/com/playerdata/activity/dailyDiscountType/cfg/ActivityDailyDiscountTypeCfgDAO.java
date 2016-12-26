package com.playerdata.activity.dailyDiscountType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeEnum;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeHelper;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityDailyDiscountTypeCfgDAO extends
		CfgCsvDao<ActivityDailyDiscountTypeCfg> {

	public static ActivityDailyDiscountTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyDiscountTypeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityDailyDiscountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(
				"Activity/ActivityDailyDiscountTypeCfg.csv",
				ActivityDailyDiscountTypeCfg.class);
		for (ActivityDailyDiscountTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		return cfgCacheMap;
	}

	private void parseTime(ActivityDailyDiscountTypeCfg cfgItem) {
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);	
	}

	public ActivityDailyDiscountTypeItem newItem(Player player, ActivityDailyDiscountTypeCfg cfg) {
		if(cfg!=null){
			ActivityDailyDiscountTypeItem item = new ActivityDailyDiscountTypeItem();
			String itemId = ActivityDailyDiscountTypeHelper.getItemId(player.getUserId(), ActivityDailyDiscountTypeEnum.getById(cfg.getEnumId()));
			item.setId(itemId);
			item.setEnumId(cfg.getEnumId());
			item.setUserId(player.getUserId());
			item.setCfgId(cfg.getId());
			item.setVersion(cfg.getVersion());
			item.setLastTime(System.currentTimeMillis());
			item.setSubItemList(newSubItemList(cfg));
			return item;
		}else{
			return null;
		}	
	}
	
	public List<ActivityDailyDiscountTypeSubItem> newSubItemList(ActivityDailyDiscountTypeCfg cfg) {
		int day ;
		if(cfg == null){
			day = 0;
		}else{
			day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());
		}
		
		
		List<ActivityDailyDiscountTypeSubItem> subItemList = new ArrayList<ActivityDailyDiscountTypeSubItem>();
		List<ActivityDailyDiscountTypeSubCfg> subCfgList = ActivityDailyDiscountTypeSubCfgDAO.getInstance().getCfgListByParentId(cfg.getId());
		for(ActivityDailyDiscountTypeSubCfg activityVitalitySubCfg : subCfgList){
			if(activityVitalitySubCfg.getAfterSomeDays() != day){
				continue;
			}
			
			for(Integer itemId:activityVitalitySubCfg.getItemList()){
				ActivityDailyDiscountItemCfg itemCfg = ActivityDailyDiscountItemCfgDao.getInstance().getCfgById(itemId+"");
				if(itemCfg == null){
					continue;
				}
				ActivityDailyDiscountTypeSubItem subitem = new ActivityDailyDiscountTypeSubItem();
				subitem.setCfgId(itemCfg.getId());
				subitem.setItemId(itemCfg.getItemId());
				subitem.setItemNum(itemCfg.getItemNum());
				subitem.setCount(0);
				subItemList.add(subitem);
			}
			break;//按理只有一个子类
			
		}	
		return subItemList;		
	}
	

	/**
	 *获取和传入数据同类型的，不同id的，处于激活状态的，单一新活动 
	 */
	public ActivityDailyDiscountTypeCfg getCfgByItem(ActivityDailyDiscountTypeItem item) {
		String id = item.getCfgId();
		String enumId = item.getEnumId();
		List<ActivityDailyDiscountTypeCfg> cfglist = getAllCfg();
		List<ActivityDailyDiscountTypeCfg> cfgListByItem = new ArrayList<ActivityDailyDiscountTypeCfg>();
		for(ActivityDailyDiscountTypeCfg cfg : cfglist){
			if(!StringUtils.equals(cfg.getId(),id)&&StringUtils.equals(enumId, cfg.getEnumId())&&ActivityDailyDiscountTypeMgr.getInstance().isOpen(cfg)){
				cfgListByItem.add(cfg);
			}			
		}
		if(cfgListByItem.size() > 1){
			GameLog.error(LogModule.ComActivityDailyDisCount, null, "发现了两个以上开放的活动,活动枚举为="+ enumId, null);
			return null;
		}else if(cfgListByItem.size() == 1){
			return cfgListByItem.get(0);
		}		
		return null;
	}
	
	
	
	
	
	
	
}