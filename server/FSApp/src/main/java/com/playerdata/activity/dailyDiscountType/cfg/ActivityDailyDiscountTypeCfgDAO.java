package com.playerdata.activity.dailyDiscountType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
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
	
	private HashMap<String, List<ActivityDailyDiscountTypeCfg>> enumIdCfgMapping;
	
	
	@Override
	public Map<String, ActivityDailyDiscountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(
				"Activity/ActivityDailyDiscountTypeCfg.csv",
				ActivityDailyDiscountTypeCfg.class);
		for (ActivityDailyDiscountTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		HashMap<String, List<ActivityDailyDiscountTypeCfg>> enumIdCfgMapping_ = new HashMap<String, List<ActivityDailyDiscountTypeCfg>>();
		for (ActivityDailyDiscountTypeCfg typeCfg : cfgCacheMap.values()) {
			ActivityTypeHelper.add(typeCfg, typeCfg.getEnumId(), enumIdCfgMapping_);
		}
		this.enumIdCfgMapping = enumIdCfgMapping_;
		
		
		return cfgCacheMap;
	}

	private void parseTime(ActivityDailyDiscountTypeCfg cfgItem) {
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);	
	}

//	public ActivityDailyDiscountTypeItem newItem(Player player, ActivityDailyDiscountTypeCfg cfg) {
//		if(cfg!=null){
//			ActivityDailyDiscountTypeItem item = new ActivityDailyDiscountTypeItem();
//			String itemId = ActivityDailyDiscountTypeHelper.getItemId(player.getUserId(), ActivityDailyDiscountTypeEnum.getById(cfg.getEnumId()));
//			item.setId(itemId);
//			item.setEnumId(cfg.getEnumId());
//			item.setUserId(player.getUserId());
//			item.setCfgId(cfg.getId());
//			item.setVersion(cfg.getVersion());
//			item.setLastTime(System.currentTimeMillis());
//			item.setSubItemList(newSubItemList(cfg));
//			return item;
//		}else{
//			return null;
//		}	
//	}
	
	public List<ActivityDailyDiscountTypeSubItem> newSubItemList(ActivityDailyDiscountTypeCfg cfg) {
		int day ;
		if(cfg == null){
			day = 0;
		}else{
			day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());
		}		
		List<ActivityDailyDiscountTypeSubItem> subItemList = new ArrayList<ActivityDailyDiscountTypeSubItem>();
		List<ActivityDailyDiscountTypeSubCfg> subCfgList = ActivityDailyDiscountTypeSubCfgDAO.getInstance().getCfgListByParentId(cfg.getId());
		if(subCfgList == null){
			return subItemList;
		}
		ActivityDailyDiscountItemCfgDao activityDailyDiscountItemCfgDao = ActivityDailyDiscountItemCfgDao.getInstance();
		for(ActivityDailyDiscountTypeSubCfg activityVitalitySubCfg : subCfgList){
			if(activityVitalitySubCfg.getAfterSomeDays() != day){
				continue;
			}
			
			for(Integer itemId:activityVitalitySubCfg.getItemList()){
				ActivityDailyDiscountItemCfg itemCfg = activityDailyDiscountItemCfgDao.getCfgById(itemId+"");
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
		ActivityDailyDiscountTypeMgr activityDailyDiscountTypeMgr = ActivityDailyDiscountTypeMgr.getInstance();
		String id = item.getCfgId();
		String enumId = item.getEnumId();
		List<ActivityDailyDiscountTypeCfg> cfglist = enumIdCfgMapping.get(enumId);
		if(cfglist == null){
			return null;
		}
		List<ActivityDailyDiscountTypeCfg> cfgListByItem = new ArrayList<ActivityDailyDiscountTypeCfg>();
		for(ActivityDailyDiscountTypeCfg cfg : cfglist){
			if(!StringUtils.equals(cfg.getId(),id)&&activityDailyDiscountTypeMgr.isOpen(cfg)){
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
	
	public boolean isCfgByItemEmuidEmpty(String enumId){

		return enumIdCfgMapping.get(enumId) == null || enumIdCfgMapping.get(enumId).isEmpty();		
	}
	
	
	
	
}