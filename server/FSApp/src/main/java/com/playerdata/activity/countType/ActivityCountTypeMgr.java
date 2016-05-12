package com.playerdata.activity.countType;

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

public class ActivityCountTypeMgr {

	private static ActivityCountTypeMgr instance = new ActivityCountTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityCountTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		ActivityCountTypeItemHolder.getInstance().synAllData(player);
	}

	public void refreshDateFreshActivity(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityCountTypeCfg activityCountTypeCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if(isOpen(activityCountTypeCfg)){
				ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum.getById(activityCountTypeCfg.getId());
				if(countTypeEnum != null && activityCountTypeCfg.getIsAutoRefresh() == 1){
					ActivityCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);//已在之前生成数据的活动
					if(targetItem != null){
						targetItem.reset(activityCountTypeCfg,ActivityCountTypeCfgDAO.getInstance().newItemList(player, activityCountTypeCfg));
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
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		List<ActivityCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityCountTypeItem targetItem : itemList) {			
			ActivityCountTypeCfg targetCfg = ActivityCountTypeCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg, ActivityCountTypeCfgDAO.getInstance().newItemList(player, targetCfg));
				dataHolder.updateItem(player, targetItem);
			}
		}
		
		
	}
	private void checkNewOpen(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityCountTypeItem> addItemList = null;
		for (ActivityCountTypeCfg activityCountTypeCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(activityCountTypeCfg)) {
				// 活动未开启
				continue;
			}
			ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum.getById(activityCountTypeCfg.getId());
			if (countTypeEnum == null) {
				GameLog.error("ActivityCountTypeMgr", "#checkNewOpen()", "找不到活动类型枚举：" + activityCountTypeCfg.getId());
				continue;
			}
			ActivityCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {
				targetItem = ActivityCountTypeCfgDAO.getInstance().newItem(player, countTypeEnum);// 生成新开启活动的数据
				if (targetItem == null) {
					GameLog.error("ActivityCountTypeMgr", "#checkNewOpen()", "根据活动类型枚举找不到对应的cfg：" + activityCountTypeCfg.getId());
					continue;
				}
				if (addItemList == null) {
					addItemList = new ArrayList<ActivityCountTypeItem>();
				}
				addItemList.add(targetItem);
			}
		}
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

	public boolean isOpen(ActivityCountTypeCfg activityCountTypeCfg) {

		if (activityCountTypeCfg != null) {
			long startTime = activityCountTypeCfg.getStartTime();
			long endTime = activityCountTypeCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}

	public void addCount(Player player, ActivityCountTypeEnum countType, int countadd) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();

		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		dataItem.setCount(dataItem.getCount() + countadd);

			dataHolder.updateItem(player, dataItem);
	}

	public ActivityComResult takeGift(Player player, ActivityCountTypeEnum countType, String subItemId) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();

		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityCountTypeSubItem targetItem = null;

			List<ActivityCountTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityCountTypeSubItem itemTmp : subItemList) {
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

	private void takeGift(Player player, ActivityCountTypeSubItem targetItem) {
		ActivityCountTypeSubCfg subCfg = ActivityCountTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getAwardGift());

	}

}
