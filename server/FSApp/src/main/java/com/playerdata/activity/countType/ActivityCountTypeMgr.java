package com.playerdata.activity.countType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.dailyCharge.ActivityDailyRechargeTypeMgr;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;

public class ActivityCountTypeMgr implements ActivityRedPointUpdate {

	private static ActivityCountTypeMgr instance = new ActivityCountTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityCountTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		ActivityCountTypeItemHolder.getInstance().synAllData(player);
	}

	/**
	 * 
	 * @param player
	 *            通用活动数据同步,生成活动奖励空数据；应置于所有通用活动的统计之前；可后期放入初始化模块
	 */
	public void checkActivity(Player player) {
		ActivityCountTypeMgr.getInstance().checkActivityOpen(player);
		ActivityTimeCardTypeMgr.getInstance().checkActivityOpen(player);
		ActivityTimeCountTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRateTypeMgr.getInstance().checkActivityOpen(player);
		ActivityDailyTypeMgr.getInstance().checkActivityOpen(player);
		ActivityExchangeTypeMgr.getInstance().checkActivityOpen(player);
		ActivityVitalityTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRankTypeMgr.getInstance().checkActivityOpen(player);
		ActivityDailyDiscountTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRedEnvelopeTypeMgr.getInstance().checkActivityOpen(player);
		ActivityFortuneCatTypeMgr.getInstance().checkActivityOpen(player);
		ActivityDailyRechargeTypeMgr.getInstance().checkActivityOpen(player);
		ActivityLimitHeroTypeMgr.getInstance().checkActivityOpen(player);
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkClose(player);

	}

	/**
	 * 
	 * @param player
	 *            同类型活动同时激活两个以上，会add同样主键到数据报错；风险较高，需增加检查配置的方法；
	 *            也可以将方法里的addlist改为add
	 */
	private void checkNewOpen(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder
				.getInstance();
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO
				.getInstance().getAllCfg();
		ArrayList<ActivityCountTypeItem> addItemList = null;
		for (ActivityCountTypeCfg activityCountTypeCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!ActivityCountTypeCfgDAO.getInstance().isOpen(
					activityCountTypeCfg)) {
				// 活动未开启
				continue;
			}
			ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum
					.getById(activityCountTypeCfg.getEnumId());
			if (countTypeEnum == null) {
				continue;
			}
			ActivityCountTypeItem targetItem = dataHolder.getItem(
					player.getUserId(), countTypeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {
				targetItem = ActivityCountTypeCfgDAO.getInstance().newItem(
						player, countTypeEnum, activityCountTypeCfg);// 生成新开启活动的数据
				if (targetItem == null) {
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

	private void checkCfgVersion(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder
				.getInstance();
		List<ActivityCountTypeItem> itemList = dataHolder.getItemList(player
				.getUserId());
		for (ActivityCountTypeItem targetItem : itemList) {

			ActivityCountTypeCfg targetCfg = ActivityCountTypeCfgDAO
					.getInstance().getCfgByEnumId(targetItem);
			if (targetCfg == null) {
				continue;
			}
			if (!StringUtils.equals(targetItem.getVersion(),
					targetCfg.getVersion())) {
				targetItem.reset(targetCfg, ActivityCountTypeCfgDAO
						.getInstance().newItemList(player, targetCfg));
				dataHolder.updateItem(player, targetItem);
			}
		}
	}

	private void checkClose(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder
				.getInstance();
		List<ActivityCountTypeItem> itemList = dataHolder.getItemList(player
				.getUserId());

		for (ActivityCountTypeItem activityCountTypeItem : itemList) {// 每种活动
			if (isClose(activityCountTypeItem)) {
				List<ActivityCountTypeSubItem> list = activityCountTypeItem
						.getSubItemList();
				if (!activityCountTypeItem.isClosed()) {
					sendEmailIfGiftNotTaken(player, activityCountTypeItem, list);
					activityCountTypeItem.setClosed(true);
					activityCountTypeItem.setTouchRedPoint(true);
					dataHolder.updateItem(player, activityCountTypeItem);
				}
			}
		}
	}

	private void sendEmailIfGiftNotTaken(Player player,
			ActivityCountTypeItem activityCountTypeItem,
			List<ActivityCountTypeSubItem> list) {
		for (ActivityCountTypeSubItem subItem : list) {// 配置表里的每种奖励
			ActivityCountTypeSubCfg subItemCfg = ActivityCountTypeSubCfgDAO
					.getInstance().getById(subItem.getCfgId());
			if (subItemCfg == null) {
				continue;
			}
			if (!subItem.isTaken()
					&& activityCountTypeItem.getCount() >= subItemCfg
							.getAwardCount()) {

				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(
						player, subItemCfg.getAwardGift(), MAKEUPEMAIL + "",
						subItemCfg.getEmailTitle());
				if (isAdd) {
					subItem.setTaken(true);
				}
			}
		}
	}

	private boolean isClose(ActivityCountTypeItem activityCountTypeItem) {

		ActivityCountTypeCfg cfgById = ActivityCountTypeCfgDAO.getInstance()
				.getCfgById(activityCountTypeItem.getCfgId());
		if (cfgById == null) {
			return false;
		}
		long endTime = cfgById.getEndTime();
		long currentTime = System.currentTimeMillis();

		return currentTime > endTime;
	}

	public void addCount(Player player, ActivityCountTypeEnum countType,
			int countadd) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder
				.getInstance();

		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(),
				countType);
		if (dataItem == null) {
			return;
		}
		dataItem.setCount(dataItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}

	public ActivityComResult takeGift(Player player,
			ActivityCountTypeEnum countType, String subItemId) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder
				.getInstance();

		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(),
				countType);
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityCountTypeSubItem targetItem = null;

			List<ActivityCountTypeSubItem> subItemList = dataItem
					.getSubItemList();
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
		ActivityCountTypeSubCfg subCfg = ActivityCountTypeSubCfgDAO
				.getInstance().getById(targetItem.getCfgId());
		targetItem.setTaken(true);
		if (subCfg == null) {
			// logger
			return;
		}
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getAwardGift());

	}

	public void updateRedPoint(Player player, String target) {
		ActivityCountTypeItemHolder activityCountTypeItemHolder = new ActivityCountTypeItemHolder();
		ActivityCountTypeCfg cfg = ActivityCountTypeCfgDAO.getInstance()
				.getCfgById(target);
		if (cfg == null) {
			return;
		}
		ActivityCountTypeEnum eNum = ActivityCountTypeEnum.getById(cfg
				.getEnumId());
		if (eNum == null) {
			return;
		}
		ActivityCountTypeItem dataItem = activityCountTypeItemHolder.getItem(
				player.getUserId(), eNum);
		if (dataItem == null) {
			return;
		}
		if (!dataItem.isTouchRedPoint()) {
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}
	}

}
