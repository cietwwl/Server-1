package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfgDAO;
import com.playerdata.activity.VitalityType.data.ActivityVitalityItemHolder;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubBoxItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeEnum;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.rateType.data.ActivityRateTypeItemHolder;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeEnum;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfg;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItemHolder;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeSubItem;
import com.rw.service.redpoint.RedPointType;

public class ActivityCollector implements RedPointCollector{

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		ArrayList<String> activityList = new ArrayList<String>();
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityCountTypeCfg cfg:allCfgList){
			if(!ActivityCountTypeMgr.getInstance().isOpen(cfg)){
				continue;
			}
			ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum.getById(cfg.getId());
			if (countTypeEnum == null) {
				GameLog.error("ActivityCountTypeMgr", "#checkNewOpen()", "找不到活动类型枚举：" + cfg.getId());
				continue;
			}
			ActivityCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);
			if(targetItem==null){
				continue;
			}
			List<ActivityCountTypeSubItem> subitemlist = targetItem.getSubItemList();
			for(ActivityCountTypeSubItem subitem:subitemlist){
				if(subitem.getCount()<=targetItem.getCount()&&!subitem.isTaken()){
					activityList.add(cfg.getId());
					break;
				}
			}				
		}
//		if (!activityCountTypeList.isEmpty()) {
//			map.put(RedPointType.HOME_WINDOW_ACTIVITY, activityCountTypeList);
//		}
		//------------------------------			
//		ArrayList<String> activityDailyCountTypeList = new ArrayList<String>();
		ActivityDailyTypeItemHolder dailyDataHolder = ActivityDailyTypeItemHolder.getInstance();
		ActivityDailyTypeCfg activityCountTypeCfg = ActivityDailyTypeMgr.getInstance().getparentCfg();
		ActivityDailyTypeItem dailyTargetItem = dailyDataHolder.getItem(player.getUserId());
		if(activityCountTypeCfg != null){
			if(ActivityDailyTypeMgr.getInstance().isOpen(activityCountTypeCfg)&&dailyTargetItem!=null){
				for(ActivityDailyTypeSubItem subitem:dailyTargetItem.getSubItemList()){
					ActivityDailyTypeSubCfg subItemCfg = ActivityDailyTypeSubCfgDAO.getInstance().getById(subitem.getCfgId());
					if(subitem.getCount()>=subItemCfg.getCount()&&!subitem.isTaken()){
						activityList.add(activityCountTypeCfg.getId());
						break;
					}
				}				
			}			
		}
//		if (!activityDailyCountTypeList.isEmpty()) {
//			map.put(RedPointType.HOME_WINDOW_ACTIVITY, activityDailyCountTypeList);
//		}
		//------------------------------
//		ArrayList<String> activityRateTypeList = new ArrayList<String>();
		List<ActivityRateTypeCfg> rateAllCfgList = ActivityRateTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRateTypeCfg cfg:rateAllCfgList){
			if (!ActivityRateTypeMgr.getInstance().isOpen(cfg)) {
				// 活动未开启
				continue;
			}
			ActivityRateTypeEnum typeEnum = ActivityRateTypeEnum.getById(cfg.getId());
			if (typeEnum == null) {
				// 枚举没有配置
				continue;
			}
//			activityList.add(cfg.getId());
		}
//		if (!activityRateTypeList.isEmpty()) {
//			map.put(RedPointType.HOME_WINDOW_ACTIVITY, activityRateTypeList);
//		}
		//------------------------------	
		ArrayList<String> activityTimeCardTypeList = new ArrayList<String>();
		// 检查可召唤佣兵
		

		if (!activityTimeCardTypeList.isEmpty()) {
//			map.put(RedPointType.ACTIVITY_TIMECARD_TYPE, activityTimeCardTypeList);
		}
		//------------------------------
//		ArrayList<String> activityTimeCountTypeList = new ArrayList<String>();
		ActivityTimeCountTypeItemHolder timeCountDataHolder = ActivityTimeCountTypeItemHolder.getInstance();
		ActivityTimeCountTypeItem timeCountTargetItem = timeCountDataHolder.getItem(player.getUserId(),  ActivityTimeCountTypeEnum.role_online);
		List<ActivityTimeCountTypeSubItem> subitemlist = timeCountTargetItem.getSubItemList();
		
		List<ActivityTimeCountTypeSubCfg> subCfgList = ActivityTimeCountTypeSubCfgDAO.getInstance().getAllCfg();
		
		for(ActivityTimeCountTypeSubItem subitem:subitemlist){
			ActivityTimeCountTypeSubCfg subcfg = null;
			for(ActivityTimeCountTypeSubCfg cfg : subCfgList){
				if(StringUtils.equals(cfg.getId(), subitem.getCfgId())){
					subcfg = cfg;
					break;
				}				
			}
			if(subcfg==null){
				continue;
			}
			if(!subitem.isTaken()&&timeCountTargetItem.getCount()>subcfg.getCount()){
				activityList.add(ActivityTimeCountTypeEnum.role_online.getCfgId());
				break;
			}
		}
//		if (!activityTimeCountTypeList.isEmpty()) {
//			map.put(RedPointType.HOME_WINDOW_ACTIVITY, activityTimeCountTypeList);
//		}
		//------------------------------
//		ArrayList<String> activityVitalityTypeList = new ArrayList<String>();
		ActivityVitalityItemHolder vitalityDataHolder = ActivityVitalityItemHolder.getInstance();
		List<ActivityVitalityTypeItem> vitalityItemList = vitalityDataHolder.getItemList(player.getUserId());

		for (ActivityVitalityTypeItem activityVitalityTypeItem : vitalityItemList) {// 每种活动
			if (!ActivityVitalityTypeMgr.getInstance().isClose(activityVitalityTypeItem)) {
				List<ActivityVitalityTypeSubItem> vitalitySubItemList = activityVitalityTypeItem.getSubItemList();
				for (ActivityVitalityTypeSubItem subItem : vitalitySubItemList) {// 配置表里的每种奖励
					ActivityVitalitySubCfg subItemCfg = ActivityVitalitySubCfgDAO.getInstance().getById(subItem.getCfgId());
					if (subItemCfg == null) {
						continue;
					}
					if (subItem.getCount() >= subItemCfg.getCount()
							&& !subItem.isTaken()) {
						activityList.add(activityVitalityTypeItem.getCfgId());
						break;
					}
				}
				ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgByItem(activityVitalityTypeItem);
				List<ActivityVitalityTypeSubBoxItem> vitalitySubBoxItemList = activityVitalityTypeItem.getSubBoxItemList();
				for (ActivityVitalityTypeSubBoxItem subItem : vitalitySubBoxItemList) {// 配置表里的每种奖励
					ActivityVitalitySubCfg subItemCfg = ActivityVitalitySubCfgDAO.getInstance().getById(subItem.getCfgId());
					if(cfg.isCanGetReward()){
						break;
					}					
					if (subItemCfg == null) {
						continue;
					}
					if (subItem.getCount() >= subItemCfg.getActiveCount()&& !subItem.isTaken()) {
						activityList.add(activityVitalityTypeItem.getCfgId());
						break;
					}
				}
				
			}
		}
//		if (!activityVitalityTypeList.isEmpty()) {
//			map.put(RedPointType.HOME_WINDOW_ACTIVITY, activityVitalityTypeList);
//		}
		//------------------------------
//		ArrayList<String> activityExchangeTypeList = new ArrayList<String>();
		// 检查可召唤佣兵
		ActivityExchangeTypeItemHolder exchangeDataHolder = ActivityExchangeTypeItemHolder.getInstance();
		List<ActivityExchangeTypeCfg> exchangeAllCfgList = ActivityExchangeTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityExchangeTypeCfg cfg:exchangeAllCfgList){
			if(!ActivityExchangeTypeMgr.getInstance().isOpen(cfg)){
				continue;
			}
			ActivityExChangeTypeEnum  activityExChangeTypeEnum = ActivityExChangeTypeEnum.getById(cfg.getId());
			if (activityExChangeTypeEnum == null) {
				continue;
			}
			ActivityExchangeTypeItem targetItem = exchangeDataHolder.getItem(player.getUserId(), activityExChangeTypeEnum);
			if(targetItem==null){
				continue;
			}
			List<ActivityExchangeTypeSubItem> exchangeSubitemlist= targetItem.getSubItemList();
			for(ActivityExchangeTypeSubItem subitem:exchangeSubitemlist){
				if(ActivityExchangeTypeMgr.getInstance().isCanTaken(player, subitem,false)){
					activityList.add(cfg.getId());
					break;
				}				
			}
		}
		
		
//		if (!activityList.isEmpty()) {
			map.put(RedPointType.HOME_WINDOW_ACTIVITY, activityList);
//		}		
	}	
}
