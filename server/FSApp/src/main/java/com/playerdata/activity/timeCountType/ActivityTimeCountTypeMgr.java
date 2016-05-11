package com.playerdata.activity.timeCountType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfg;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfgDAO;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfg;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItemHolder;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeSubItem;

public class ActivityTimeCountTypeMgr {

	private static ActivityTimeCountTypeMgr instance = new ActivityTimeCountTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityTimeCountTypeMgr getInstance() {
		return instance;
	}

	public void synTimeCountTypeData(Player player) {
		ActivityTimeCountTypeItemHolder.getInstance().synAllData(player);
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkClose(player);

	}

	private void checkNewOpen(Player player) {
		ActivityTimeCountTypeItemHolder dataHolder = ActivityTimeCountTypeItemHolder.getInstance();
		List<ActivityTimeCountTypeCfg> allCfgList = ActivityTimeCountTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityTimeCountTypeItem> addItemList = null;
		for (ActivityTimeCountTypeCfg activityTimeCountTypeCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(activityTimeCountTypeCfg)) {
				// 活动未开启
				continue;
			}
			ActivityTimeCountTypeEnum TimeCountTypeEnum = ActivityTimeCountTypeEnum.getById(activityTimeCountTypeCfg.getId());
			if (TimeCountTypeEnum == null) {
				GameLog.error("ActivityTimeCountTypeMgr", "#checkNewOpen()", "找不到活动类型：" + activityTimeCountTypeCfg.getId());
				continue;
			}
			ActivityTimeCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), TimeCountTypeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {
				targetItem = ActivityTimeCountTypeCfgDAO.getInstance().newItem(player, TimeCountTypeEnum);// 生成新开启活动的数据
				if (targetItem == null) {
					// logger
					continue;
				}
				if (addItemList == null) {
					addItemList = new ArrayList<ActivityTimeCountTypeItem>();
				}
				addItemList.add(targetItem);
			} else {
				if (!StringUtils.equals(targetItem.getVersion(), activityTimeCountTypeCfg.getVersion())) {
					targetItem.reset(activityTimeCountTypeCfg, ActivityTimeCountTypeCfgDAO.getInstance().newItemList(player, activityTimeCountTypeCfg));
					dataHolder.updateItem(player, targetItem);
				}
			}
		}
		if (addItemList != null) {
			dataHolder.addItemList(player, addItemList);
		}
	}

	private void checkClose(Player player) {
		ActivityTimeCountTypeItemHolder dataHolder = ActivityTimeCountTypeItemHolder.getInstance();
		List<ActivityTimeCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());

		for (ActivityTimeCountTypeItem activityTimeCountTypeItem : itemList) {// 每种活动
			if (isClose(activityTimeCountTypeItem)) {

				List<ActivityTimeCountTypeSubItem> list = activityTimeCountTypeItem.getSubItemList();
				for (ActivityTimeCountTypeSubItem subItem : list) {// 配置表里的每种奖励
					ActivityTimeCountTypeSubCfg subItemCfg = ActivityTimeCountTypeSubCfgDAO.getInstance().getById(subItem.getCfgId());

					if (!subItem.isTaken() && activityTimeCountTypeItem.getCount() >= subItemCfg.getAwardCount()) {

						boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(player, subItemCfg.getAwardGift(), MAKEUPEMAIL + "");
						if (isAdd) {
							subItem.setTaken(true);
						} else {
							GameLog.error(LogModule.ComActivityTimeCount, player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
						}
					}
				}
				activityTimeCountTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityTimeCountTypeItem);
			}
		}

	}

	public boolean isClose(ActivityTimeCountTypeItem activityTimeCountTypeItem) {

		ActivityTimeCountTypeCfg cfgById = ActivityTimeCountTypeCfgDAO.getInstance().getCfgById(activityTimeCountTypeItem.getCfgId());

		long endTime = cfgById.getEndTime();
		long currentTime = System.currentTimeMillis();

		return currentTime > endTime;
	}

	public boolean isOpen(ActivityTimeCountTypeCfg activityTimeCountTypeCfg) {

		if (activityTimeCountTypeCfg != null) {
			long startTime = activityTimeCountTypeCfg.getStartTime();
			long endTime = activityTimeCountTypeCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}

	public void addCount(Player player, ActivityTimeCountTypeEnum TimeCountType, int countadd) {
		ActivityTimeCountTypeItemHolder dataHolder = ActivityTimeCountTypeItemHolder.getInstance();

		ActivityTimeCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), TimeCountType);
		dataItem.setCount(dataItem.getCount() + countadd);

		dataHolder.updateItem(player, dataItem);
	}

	public ActivityComResult takeGift(Player player, ActivityTimeCountTypeEnum TimeCountType, String subItemId) {
		ActivityTimeCountTypeItemHolder dataHolder = ActivityTimeCountTypeItemHolder.getInstance();

		ActivityTimeCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), TimeCountType);
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityTimeCountTypeSubItem targetItem = null;

			List<ActivityTimeCountTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityTimeCountTypeSubItem itemTmp : subItemList) {
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

	private void takeGift(Player player, ActivityTimeCountTypeSubItem targetItem) {
		ActivityTimeCountTypeSubCfg subCfg = ActivityTimeCountTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getAwardGift());

	}

}
