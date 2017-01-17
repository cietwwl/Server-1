package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.evilBaoArrive.EvilBaoArriveMgr;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.playerdata.activity.rateType.ActivityRateTypeEnum;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItemHolder;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeEnum;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfg;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItemHolder;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeSubItem;
import com.playerdata.activityCommon.ActivityMgrHelper;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		ArrayList<String> activityList = new ArrayList<String>();
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
			ActivityRateTypeEnum typeEnum = ActivityRateTypeEnum.getById(String.valueOf(cfg.getEnumId()));
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
				activityList.add(String.valueOf(cfg.getId()));
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

		List<String> vitalityList = ActivityVitalityTypeMgr.getInstance().haveRedPoint(player);
		activityList.addAll(vitalityList);

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
				if (exchangeTypeMgr.isCanTaken(player, subitem)) {
					if (targetItem.getHistoryRedPoint().contains(subitem.getCfgId())) {
						continue;
					}
					activityList.add(String.valueOf(cfg.getId()));
					break;
				}
			}

		}

		List<String> redEnvelopeList = ActivityRedEnvelopeTypeMgr.getInstance().haveRedPoint(player);
		activityList.addAll(redEnvelopeList);

		List<String> subList = ActivityMgrHelper.getInstance().haveRedPoint(player);
		activityList.addAll(subList);

		// if (!activityList.isEmpty()) {
		map.put(RedPointType.HOME_WINDOW_ACTIVITY, activityList);
		// }
		
		List<String> evilBaoArriveList = EvilBaoArriveMgr.getInstance().getRedPoint(player);
		if(!evilBaoArriveList.isEmpty()){
			map.put(RedPointType.EVIL_BAO_ARRIVE, evilBaoArriveList);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}
}
