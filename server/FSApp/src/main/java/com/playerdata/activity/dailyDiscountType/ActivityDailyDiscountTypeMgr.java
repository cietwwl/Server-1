package com.playerdata.activity.dailyDiscountType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfgDAO;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItemHolder;


public class ActivityDailyDiscountTypeMgr {

	private static ActivityDailyDiscountTypeMgr instance = new ActivityDailyDiscountTypeMgr();


	public static ActivityDailyDiscountTypeMgr getInstance() {
		return instance;
	}
	
	public void synCountTypeData(Player player) {
		ActivityDailyDiscountTypeItemHolder.getInstance().synAllData(player);
	}



	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);	
//		checkOtherDay(player);
//		checkClose(player);

	}

	
	
	
	private void checkNewOpen(Player player) {
		ActivityDailyDiscountTypeItemHolder dataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();
		List<ActivityDailyDiscountTypeCfg> activitydailydiscountcfglist = ActivityDailyDiscountTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityDailyDiscountTypeItem> addItemList = null;
		for (ActivityDailyDiscountTypeCfg activityCountTypeCfg : activitydailydiscountcfglist) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(activityCountTypeCfg)) {
				// 活动未开启
				continue;
			}
			ActivityDailyDiscountTypeEnum countTypeEnum = ActivityDailyDiscountTypeEnum.getById(activityCountTypeCfg.getId());
			if (countTypeEnum == null) {
				GameLog.error("ActivityDailyDisCountTypeMgr", "#checkNewOpen()", "找不到活动类型枚举：" + activityCountTypeCfg.getId());
				continue;
			}
			ActivityDailyDiscountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {
						
				targetItem = ActivityDailyDiscountTypeCfgDAO.getInstance().newItem(player, countTypeEnum);// 生成新开启活动的数据
				if (targetItem == null) {
					GameLog.error("ActivityDailyDisCountTypeMgr", "#checkNewOpen()", "根据活动类型枚举找不到对应的cfg：" + activityCountTypeCfg.getId());
					continue;
				}
				if (addItemList == null) {
					addItemList = new ArrayList<ActivityDailyDiscountTypeItem>();
				}
				addItemList.add(targetItem);
			}
		}
		if (addItemList != null) {
			dataHolder.addItemList(player, addItemList);
		}
	}
	
	private boolean isOpen(ActivityDailyDiscountTypeCfg activityCountTypeCfg) {

		if (activityCountTypeCfg != null) {
			long startTime = activityCountTypeCfg.getStartTime();
			long endTime = activityCountTypeCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}
	
	
	
	private void checkCfgVersion(Player player) {
		ActivityDailyDiscountTypeItemHolder dataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();
		List<ActivityDailyDiscountTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityDailyDiscountTypeItem targetItem : itemList) {			
			ActivityDailyDiscountTypeCfg targetCfg = ActivityDailyDiscountTypeCfgDAO.getInstance().getConfig(targetItem.getCfgId());
			if(targetCfg == null){
				GameLog.error(LogModule.ComActivityDailyDisCount, null, "通用活动找不到配置文件", null);
				return;
			}			
			
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
//				targetItem.reset(targetCfg);
				dataHolder.updateItem(player, targetItem);
			}
		}		
	}
	
//	private void checkOtherDay(Player player) {
//		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
//		List<ActivityDailyTypeItem> item = dataHolder.getItemList(player.getUserId());
//		ActivityDailyTypeCfg targetCfg = ActivityDailyTypeCfgDAO.getInstance().getConfig(ActivityDailyTypeEnum.Daily.getCfgId());
//		if(targetCfg == null){
//			GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
//			return;
//		}
//		for (ActivityDailyTypeItem targetItem : item) {
//			if(DateUtils.getDayDistance(targetItem.getLastTime(), System.currentTimeMillis())>0){
//				targetItem.reset(targetCfg);
//				dataHolder.updateItem(player, targetItem);
//			}
//		}
//	}
	
//	private void checkClose(Player player) {
//		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
//		List<ActivityDailyTypeItem> itemList = dataHolder.getItemList(player.getUserId());
//
//		for (ActivityDailyTypeItem activityDailyCountTypeItem : itemList) {// 每种活动
//			if (isClose(activityDailyCountTypeItem)) {
//				activityDailyCountTypeItem.setClosed(true);
//				dataHolder.updateItem(player, activityDailyCountTypeItem);
//			}
//		}
//	}
	
//	private boolean isClose(ActivityDailyTypeItem activityDailyCountTypeItem) {
//		if (activityDailyCountTypeItem != null) {
//			ActivityDailyTypeCfg cfgById = ActivityDailyTypeCfgDAO.getInstance().getCfgById(ActivityDailyTypeEnum.Daily.getCfgId());
//			if(cfgById!=null){
//				long endTime = cfgById.getEndTime();
//				long currentTime = System.currentTimeMillis();
//				return currentTime > endTime;
//			}else{
//				GameLog.error("activitydailycounttypemgr","" , "配置文件找不到数据奎对应的活动"+ ActivityDailyTypeEnum.Daily);
//			}
//		}
//		return false;
//	}
	
//	public boolean isLevelEnough(Player player) {
//		ActivityDailyTypeCfg activityCountTypeCfg = getparentCfg();
//		if(activityCountTypeCfg == null){
////			GameLog.error("activityDailyCountTypeMgr", "list", "配置文件总表错误" );
//			return false;
//		}
//		if(player.getLevel() < activityCountTypeCfg.getLevelLimit()){
//			return false;
//		}		
//		return true;
//	}
		
//	public ActivityComResult takeGift(Player player, ActivityDailyTypeEnum countType, String subItemId) {
//		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
//
//		ActivityDailyTypeItem dataItem = dataHolder.getItem(player.getUserId());
//		ActivityComResult result = ActivityComResult.newInstance(false);
//
//		// 未激活
//		if (dataItem == null) {
//			result.setReason("活动尚未开启");
//
//		} else {
//			ActivityDailyTypeSubItem targetItem = null;
//
//			List<ActivityDailyTypeSubItem> subItemList = dataItem.getSubItemList();
//			for (ActivityDailyTypeSubItem itemTmp : subItemList) {
//				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
//					targetItem = itemTmp;
//					break;
//				}
//			}
//			if (targetItem != null && !targetItem.isTaken()) {
//				takeGift(player, targetItem);
//				result.setSuccess(true);
//				dataHolder.updateItem(player, dataItem);
//			}
//		}
//		return result;
//	}
//
//	private void takeGift(Player player, ActivityDailyTypeSubItem targetItem) {
//		ActivityDailyTypeSubCfg subCfg = ActivityDailyTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
//		if(subCfg == null){
//			GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
//			return;
//		}
//		targetItem.setTaken(true);
//		ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());
//	}	
	
}
