package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
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
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.dailyCharge.ActivityDailyRechargeTypeMgr;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeEnum;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeEnum;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfgDAO;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItemHolder;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfgDAO;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItemHolder;
import com.playerdata.activity.rateType.ActivityRateTypeEnum;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItemHolder;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfg;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfgDAO;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeItemHolder;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeEnum;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfg;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItemHolder;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeSubItem;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		ArrayList<String> activityList = new ArrayList<String>();
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		ActivityCountTypeCfgDAO countTypeCfgDAO = ActivityCountTypeCfgDAO.getInstance();
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		long current = System.currentTimeMillis();
		for (ActivityCountTypeCfg cfg : allCfgList) {
			if (!countTypeCfgDAO.isOpen(cfg, current)) {
				continue;
			}
			ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum.getById(cfg.getEnumId());
			if (countTypeEnum == null) {
				GameLog.error("ActivityCountTypeMgr", "#checkNewOpen()", "找不到活动类型枚举：" + cfg.getId());
				continue;
			}
			ActivityCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);
			if (targetItem == null) {
				continue;
			}
			if (!targetItem.isTouchRedPoint()) {
				activityList.add(cfg.getId());
				continue;
			}
			List<ActivityCountTypeSubItem> subitemlist = targetItem.getSubItemList();
			for (ActivityCountTypeSubItem subitem : subitemlist) {
				if (subitem.getCount() <= targetItem.getCount() && !subitem.isTaken()) {
					activityList.add(cfg.getId());
					break;
				}
			}
		}

		// ------------------------------
		ActivityDailyTypeItemHolder dailyDataHolder = ActivityDailyTypeItemHolder.getInstance();
		ActivityDailyTypeItem dailyTargetItem = dailyDataHolder.getItem(player.getUserId());
		if (dailyTargetItem != null) {
			ActivityDailyTypeCfgDAO dailyTypeCfgDAO = ActivityDailyTypeCfgDAO.getInstance();
			ActivityDailyTypeCfg activityDailyTypeCfg = ActivityDailyTypeCfgDAO.getInstance().getConfig(dailyTargetItem.getCfgid());
			if (activityDailyTypeCfg != null && dailyTypeCfgDAO.isOpen(activityDailyTypeCfg)) {
				if (!dailyTargetItem.isTouchRedPoint()) {
					activityList.add(activityDailyTypeCfg.getId());
				} else {
					ActivityDailyTypeSubCfgDAO subCfgDAO = ActivityDailyTypeSubCfgDAO.getInstance();
					for (ActivityDailyTypeSubItem subitem : dailyTargetItem.getSubItemList()) {
						ActivityDailyTypeSubCfg subItemCfg = subCfgDAO.getById(subitem.getCfgId());
						if (subitem.getCount() >= subItemCfg.getCount() && !subitem.isTaken()) {
							activityList.add(activityDailyTypeCfg.getId());
							break;
						}
					}
				}
			}
		}
		// ------------------------------
		ActivityRateTypeItemHolder datarateholder = new ActivityRateTypeItemHolder();
		List<ActivityRateTypeCfg> rateAllCfgList = ActivityRateTypeCfgDAO.getInstance().getAllCfg();
		ActivityRateTypeMgr activityRateTypeMgr = ActivityRateTypeMgr.getInstance();
		for (ActivityRateTypeCfg cfg : rateAllCfgList) {
			if (!activityRateTypeMgr.isOpen(cfg)) {
				// 活动未开启
				continue;
			}
			if (!activityRateTypeMgr.isLevelEnough(player, cfg)) {
				continue;
			}
			ActivityRateTypeEnum typeEnum = ActivityRateTypeEnum.getById(cfg.getEnumId());
			if (typeEnum == null) {
				// 枚举没有配置
				continue;
			}
			ActivityRateTypeItem rateItem = datarateholder.getItem(player.getUserId(), typeEnum);
			if (rateItem == null) {
				// 登录时活动没开启，没生成数据；持续在线到活动开启了
				continue;
			}
			if (!rateItem.isTouchRedPoint()) {
				activityList.add(cfg.getId());
				continue;
			}
		}

		// ------------------------------
		// ArrayList<String> activityTimeCardTypeList = new ArrayList<String>();
		// 检查可召唤佣兵

		// ------------------------------
		ActivityTimeCountTypeItemHolder timeCountDataHolder = ActivityTimeCountTypeItemHolder.getInstance();
		ActivityTimeCountTypeItem timeCountTargetItem = timeCountDataHolder.getItem(player.getUserId(), ActivityTimeCountTypeEnum.role_online);
		if (timeCountTargetItem != null) {
			List<ActivityTimeCountTypeSubItem> subitemlist = timeCountTargetItem.getSubItemList();

			List<ActivityTimeCountTypeSubCfg> subCfgList = ActivityTimeCountTypeSubCfgDAO.getInstance().getAllCfg();

			for (ActivityTimeCountTypeSubItem subitem : subitemlist) {
				ActivityTimeCountTypeSubCfg subcfg = null;
				for (ActivityTimeCountTypeSubCfg cfg : subCfgList) {
					if (StringUtils.equals(cfg.getId(), subitem.getCfgId())) {
						subcfg = cfg;
						break;
					}
				}
				if (subcfg == null) {
					continue;
				}
				if (!subitem.isTaken() && timeCountTargetItem.getCount() > subcfg.getCount()) {
					// activityList.add(ActivityTimeCountTypeEnum.role_online.getCfgId());
					break;
				}
			}
		}

		// ------------------------------
		ActivityVitalityItemHolder vitalityDataHolder = ActivityVitalityItemHolder.getInstance();
		List<ActivityVitalityTypeItem> vitalityItemList = vitalityDataHolder.getItemList(player.getUserId());
		ActivityVitalitySubCfgDAO subCfgDAO = ActivityVitalitySubCfgDAO.getInstance();
		ActivityVitalityCfgDAO vitalityCfgDAO = ActivityVitalityCfgDAO.getInstance();
		ActivityVitalityTypeMgr activityVitalityTypeMgr = ActivityVitalityTypeMgr.getInstance();
		for (ActivityVitalityTypeItem activityVitalityTypeItem : vitalityItemList) {// 每种活动
			if (!activityVitalityTypeMgr.isHasCfg(activityVitalityTypeItem)) {
				continue;
			}
			if (!activityVitalityTypeItem.isTouchRedPoint()) {
				activityList.add(activityVitalityTypeItem.getCfgId());
				continue;
			}

			List<ActivityVitalityTypeSubItem> vitalitySubItemList = activityVitalityTypeItem.getSubItemList();
			for (ActivityVitalityTypeSubItem subItem : vitalitySubItemList) {// 配置表里的每种奖励
				ActivityVitalitySubCfg subItemCfg = subCfgDAO.getCfgById(subItem.getCfgId());
				if (subItemCfg == null) {
					continue;
				}
				if (subItem.getCount() >= subItemCfg.getCount() && !subItem.isTaken()) {
					activityList.add(activityVitalityTypeItem.getCfgId());
					break;
				}
			}
			ActivityVitalityCfg cfg = vitalityCfgDAO.getCfgById(activityVitalityTypeItem.getCfgId());
			List<ActivityVitalityTypeSubBoxItem> vitalitySubBoxItemList = activityVitalityTypeItem.getSubBoxItemList();
			for (ActivityVitalityTypeSubBoxItem subItem : vitalitySubBoxItemList) {// 配置表里的每种奖励
				ActivityVitalitySubCfg subItemCfg = subCfgDAO.getCfgById(subItem.getCfgId());
				if (cfg.isCanGetReward()) {
					break;
				}
				if (subItemCfg == null) {
					continue;
				}
				if (subItem.getCount() >= subItemCfg.getActiveCount() && !subItem.isTaken()) {
					activityList.add(activityVitalityTypeItem.getCfgId());
					break;
				}
			}

		}

		// ------------------------------
		// 检查可召唤佣兵
		ActivityExchangeTypeItemHolder exchangeDataHolder = ActivityExchangeTypeItemHolder.getInstance();
		List<ActivityExchangeTypeCfg> exchangeAllCfgList = ActivityExchangeTypeCfgDAO.getInstance().getAllCfg();
		ActivityExchangeTypeMgr exchangeTypeMgr = ActivityExchangeTypeMgr.getInstance();
		for (ActivityExchangeTypeCfg cfg : exchangeAllCfgList) {
			if (!exchangeTypeMgr.isOpen(cfg)) {
				continue;
			}
			if (!exchangeTypeMgr.isLevelEnough(player, cfg)) {
				continue;
			}

			ActivityExChangeTypeEnum activityExChangeTypeEnum = ActivityExChangeTypeEnum.getById(cfg.getEnumId());
			if (activityExChangeTypeEnum == null) {
				continue;
			}
			ActivityExchangeTypeItem targetItem = exchangeDataHolder.getItem(player.getUserId(), activityExChangeTypeEnum);
			if (targetItem == null) {
				continue;
			}

			if (!targetItem.isTouchRedPoint()) {
				activityList.add(targetItem.getCfgId());
				continue;
			}

			List<ActivityExchangeTypeSubItem> exchangeSubitemlist = targetItem.getSubItemList();
			for (ActivityExchangeTypeSubItem subitem : exchangeSubitemlist) {
				if (exchangeTypeMgr.isCanTaken(player, subitem, false)) {
					if (targetItem.getHistoryRedPoint().contains(subitem.getCfgId())) {
						continue;
					}
					activityList.add(cfg.getId());
					break;
				}
			}

		}
		// ----------------------------------
		ActivityDailyDiscountTypeItemHolder dailyDiscountDataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();
		List<ActivityDailyDiscountTypeCfg> dailyDiscountAllCfgList = ActivityDailyDiscountTypeCfgDAO.getInstance().getAllCfg();
		ActivityDailyDiscountTypeMgr activityDailyDiscountTypeMgr = ActivityDailyDiscountTypeMgr.getInstance();
		for (ActivityDailyDiscountTypeCfg cfg : dailyDiscountAllCfgList) {
			if (!activityDailyDiscountTypeMgr.isOpen(cfg)) {
				continue;
			}
			ActivityDailyDiscountTypeEnum dailyDiscountEnum = ActivityDailyDiscountTypeEnum.getById(cfg.getEnumId());
			if (dailyDiscountEnum == null) {
				continue;
			}
			ActivityDailyDiscountTypeItem targetItem = dailyDiscountDataHolder.getItem(player.getUserId(), dailyDiscountEnum);
			if (targetItem == null) {
				continue;
			}
			if (!targetItem.isTouchRedPoint()) {
				activityList.add(targetItem.getCfgId());
				continue;
			}
		}
		// ----------------------------------
		ActivityRankTypeItemHolder rankHolder = ActivityRankTypeItemHolder.getInstance();
		List<ActivityRankTypeItem> rankItemList = rankHolder.getItemList(player.getUserId());
		ActivityRankTypeCfgDAO rankTypeCfgDAO = ActivityRankTypeCfgDAO.getInstance();
		ActivityRankTypeMgr activityRankTypeMgr = ActivityRankTypeMgr.getInstance();
		for (ActivityRankTypeItem rankItem : rankItemList) {
			ActivityRankTypeCfg cfg = rankTypeCfgDAO.getCfgById(rankItem.getCfgId());
			if (cfg == null) {
				continue;
			}
			if (!activityRankTypeMgr.isOpen(cfg)) {
				continue;
			}
			if (!rankItem.isTouchRedPoint()) {
				activityList.add(rankItem.getCfgId());
				continue;
			}
		}
		// ----------------------------------
		ActivityRedEnvelopeItemHolder redEnvelopeHolder = ActivityRedEnvelopeItemHolder.getInstance();
		ActivityRedEnvelopeTypeMgr redEnvelopeTypeMgr = ActivityRedEnvelopeTypeMgr.getInstance();
		List<ActivityRedEnvelopeTypeItem> redenvolopeItemList = redEnvelopeHolder.getItemList(player.getUserId());
		ActivityRedEnvelopeTypeCfgDAO activityRedEnvelopeTypeCfgDAO = ActivityRedEnvelopeTypeCfgDAO.getInstance();
		
		for (ActivityRedEnvelopeTypeItem redEnvelopeItem : redenvolopeItemList) {
			ActivityRedEnvelopeTypeCfg cfg = activityRedEnvelopeTypeCfgDAO.getCfgById(redEnvelopeItem.getCfgId());
			if (cfg == null) {
				continue;
			}
			if (!redEnvelopeTypeMgr.isOpen(cfg) && !redEnvelopeTypeMgr.isCanTakeGift(redEnvelopeItem)) {
				continue;
			}
			if (!redEnvelopeItem.isTouchRedPoint()) {
				activityList.add(redEnvelopeItem.getCfgId());
				continue;
			}
		}
		List<String> dailyChargeList = ActivityDailyRechargeTypeMgr.getInstance().haveRedPoint(player);
		activityList.addAll(dailyChargeList);

		// if (!activityList.isEmpty()) {
		map.put(RedPointType.HOME_WINDOW_ACTIVITY, activityList);
		// }
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}
}
