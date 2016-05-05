package com.playerdata.activity.dailyCountType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeSubCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeSubItem;

public class ActivityDailyCountTypeMgr {

	private static ActivityDailyCountTypeMgr instance = new ActivityDailyCountTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityDailyCountTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		ActivityDailyCountTypeItemHolder.getInstance().synAllData(player);
	}

	public void refreshDateFreshActivity(Player player) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
		List<ActivityDailyCountTypeCfg> allCfgList = ActivityDailyCountTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityDailyCountTypeCfg activityCountTypeCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if(isOpen(activityCountTypeCfg)){
				ActivityDailyCountTypeEnum countTypeEnum = ActivityDailyCountTypeEnum.getById(activityCountTypeCfg.getId());
				if(countTypeEnum != null){
					ActivityDailyCountTypeItem targetItem = dataHolder.getItem(player.getUserId());//已在之前生成数据的活动
					if(targetItem != null){
						dataHolder.updateItem(player, targetItem);
					}				
				}
			}
		}
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);	
		checkClose(player);

	}

	private void checkCfgVersion(Player player) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
		List<ActivityDailyCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityDailyCountTypeItem targetItem : itemList) {			
			ActivityDailyCountTypeCfg targetCfg = ActivityDailyCountTypeCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg);
				dataHolder.updateItem(player, targetItem);
			}
		}
		
		
	}
	private void checkNewOpen(Player player) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
		List<ActivityDailyCountTypeCfg> allCfgList = ActivityDailyCountTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityDailyCountTypeItem> addItemList = null;
		
		if(allCfgList == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "不存在每日活动" + allCfgList.size());
			return;			
		}
		
		if(allCfgList.size() != 1){
			GameLog.error("activityDailyCountTypeMgr", "list", "同时存在多个每日活动" + allCfgList.size());
			return;
		}
		
		ActivityDailyCountTypeCfg activityCountTypeCfg = allCfgList.get(0);
		if(!isOpen(activityCountTypeCfg)){
			//活动未开启
			return ;
		}
		
		ActivityDailyCountTypeItem targetItem = dataHolder.getItem(player.getUserId());
		
		
		
		
		
		
		
		
		
		
		
	
			
			
			
			
//			ActivityDailyCountTypeEnum countTypeEnum = ActivityDailyCountTypeEnum.getById(activityCountTypeCfg.getId());
//			if (countTypeEnum == null) {
//				GameLog.error("ActivityCountTypeMgr", "#checkNewOpen()", "找不到活动类型枚举：" + activityCountTypeCfg.getId());
//				continue;
//			}
//			ActivityDailyCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);// 已在之前生成数据的活动
//			if (targetItem == null) {
//						
//				targetItem = ActivityDailyCountTypeCfgDAO.getInstance().newItem(player, countTypeEnum);// 生成新开启活动的数据
//				if (targetItem == null) {
//					GameLog.error("ActivityCountTypeMgr", "#checkNewOpen()", "根据活动类型枚举找不到对应的cfg：" + activityCountTypeCfg.getId());
//					continue;
//				}
//				if (addItemList == null) {
//					addItemList = new ArrayList<ActivityDailyCountTypeItem>();
//				}
//				addItemList.add(targetItem);
//			}
		if (addItemList != null) {
			dataHolder.addItemList(player, addItemList);
		}
	}


	private void checkClose(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		List<ActivityCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());

		for (ActivityCountTypeItem activityCountTypeItem : itemList) {// 每种活动
			if (isClose(activityCountTypeItem)) {

				List<ActivityCountTypeSubItem> list = activityCountTypeItem.getSubItemList();
				sendEmailIfGiftNotTaken(player, activityCountTypeItem, list);
				activityCountTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityCountTypeItem);
			}
		}

	}

	private void sendEmailIfGiftNotTaken(Player player,ActivityCountTypeItem activityCountTypeItem,List<ActivityCountTypeSubItem> list) {
		for (ActivityCountTypeSubItem subItem : list) {// 配置表里的每种奖励
			ActivityCountTypeSubCfg subItemCfg = ActivityCountTypeSubCfgDAO.getInstance().getById(subItem.getCfgId());

			if (!subItem.isTaken() && activityCountTypeItem.getCount() >= subItemCfg.getAwardCount()) {

				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(player, subItemCfg.getAwardGift(), MAKEUPEMAIL + "");
				if (isAdd) {
					subItem.setTaken(true);
				} else {
					GameLog.error(LogModule.ComActivityCount, player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
				}
			}
		}
	}

	public boolean isClose(ActivityCountTypeItem activityCountTypeItem) {

		ActivityCountTypeCfg cfgById = ActivityCountTypeCfgDAO.getInstance().getCfgById(activityCountTypeItem.getCfgId());

		long endTime = cfgById.getEndTime();
		long currentTime = System.currentTimeMillis();

		return currentTime > endTime;
	}

	public boolean isOpen(ActivityDailyCountTypeCfg activityCountTypeCfg) {

		if (activityCountTypeCfg != null) {
			long startTime = activityCountTypeCfg.getStartTime();
			long endTime = activityCountTypeCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}

	public void addCount(Player player, ActivityDailyCountTypeEnum countType, int countadd) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();

		ActivityDailyCountTypeItem dataItem = dataHolder.getItem(player.getUserId());
		dataItem.setCount(dataItem.getCount() + countadd);

			dataHolder.updateItem(player, dataItem);
	}

	public ActivityComResult takeGift(Player player, ActivityDailyCountTypeEnum countType, String subItemId) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();

		ActivityDailyCountTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityDailyCountTypeSubItem targetItem = null;

			List<ActivityDailyCountTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityDailyCountTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			if (targetItem != null && !targetItem.isTaken()) {
				takeGift(player, targetItem);
				result.setSuccess(true);
				dataHolder.updateItem(player, dataItem);
			}

		}

		return result;
	}

	private void takeGift(Player player, ActivityDailyCountTypeSubItem targetItem) {
		ActivityDailyCountTypeSubCfg subCfg = ActivityDailyCountTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());

	}

}
