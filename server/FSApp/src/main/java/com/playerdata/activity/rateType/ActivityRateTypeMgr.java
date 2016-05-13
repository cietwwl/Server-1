package com.playerdata.activity.rateType;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeStartAndEndHourHelper;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItemHolder;
import com.rw.fsutil.util.DateUtils;

public class ActivityRateTypeMgr {

	private static ActivityRateTypeMgr instance = new ActivityRateTypeMgr();

	public static ActivityRateTypeMgr getInstance() {
		return instance;
	}

	public void synData(Player player) {
		ActivityRateTypeItemHolder.getInstance().synAllData(player);
	}

	public boolean isActivityOnGoing(Player player,
			ActivityRateTypeEnum activityRateTypeEnum) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		if (activityRateTypeEnum != null) {
			ActivityRateTypeItem targetItem = dataHolder.getItem(
					player.getUserId(), activityRateTypeEnum);// 已在之前生成数据的活动
			return targetItem != null && !targetItem.isClosed();
		} else {
			GameLog.error("activityratetypemgr", "enmu为空", "没有找到对应活动类型");
			return false;
		}
	}

	public int getmultiple(Player player,
			ActivityRateTypeEnum activityRateTypeEnum) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		if (activityRateTypeEnum == null) {
			GameLog.error("activityratetypemgr", "没有枚举", "获得倍数时无枚举");
			return 1;
		}
		ActivityRateTypeItem targetItem = dataHolder.getItem(
				player.getUserId(), activityRateTypeEnum);// 已在之前生成数据的活动
		if (targetItem == null) {
			GameLog.error("activityratetypemgr", "没有数据 ", "获得倍数时数据库无数据");
			return 1;
		}
		return targetItem.getMultiple();
	}

	public float getRate(ActivityRateTypeEnum activityRateTypeEnum) {
		ActivityRateTypeCfg cfgById = ActivityRateTypeCfgDAO.getInstance()
				.getCfgById(activityRateTypeEnum.getCfgId());
		return cfgById == null ? 1 : cfgById.getRate();
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkClose(player);

	}

	private void checkNewOpen(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		List<ActivityRateTypeCfg> allCfgList = ActivityRateTypeCfgDAO
				.getInstance().getAllCfg();
		for (ActivityRateTypeCfg activityRateTypeCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(activityRateTypeCfg)) {
				// 活动未开启
				continue;
			}
			ActivityRateTypeEnum typeEnum = ActivityRateTypeEnum
					.getById(activityRateTypeCfg.getId());
			if (typeEnum == null) {
				// 枚举没有配置
				continue;
			}
			ActivityRateTypeItem targetItem = dataHolder.getItem(
					player.getUserId(), typeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {
				targetItem = ActivityRateTypeCfgDAO.getInstance().newItem(
						player, typeEnum);// 生成新开启活动的数据
				if (targetItem != null) {
					dataHolder.addItem(player, targetItem);
				}
			} else {
				if (!StringUtils.equals(targetItem.getVersion(),
						activityRateTypeCfg.getVersion())) {
					targetItem.setVersion(activityRateTypeCfg.getVersion());
				}
				targetItem.setClosed(false);
				dataHolder.updateItem(player, targetItem);
			}

		}
	}

	private void checkClose(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		List<ActivityRateTypeItem> itemList = dataHolder.getItemList(player
				.getUserId());

		for (ActivityRateTypeItem activityRateTypeItem : itemList) {// 每种活动
			if (isClose(activityRateTypeItem)) {
				activityRateTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityRateTypeItem);
			}
		}

	}

	private boolean isClose(ActivityRateTypeItem ActivityRateTypeItem) {
		boolean isclose = false;
		ActivityRateTypeCfg cfgById = ActivityRateTypeCfgDAO.getInstance()
				.getCfgById(ActivityRateTypeItem.getCfgId());
		long endTime = cfgById.getEndTime();
		long currentTime = System.currentTimeMillis();
		long startTime = cfgById.getStartTime();
		isclose = currentTime > endTime ? true : false;
		if (!isclose) {
			isclose = currentTime < startTime ? true : false;
		}

		if (!isclose) {// 活动期间的小时区间是否开启
			int hour = DateUtils.getCurrentHour();
			for (ActivityRateTypeStartAndEndHourHelper timebyhour : cfgById
					.getStartAndEnd()) {
				isclose = hour >= timebyhour.getStarthour()
						&& hour < timebyhour.getEndhour() ? false : true;
				if (!isclose) {
					break;
				}
			}
		}
		return isclose;
	}

	private boolean isOpen(ActivityRateTypeCfg ActivityRateTypeCfg) {
		boolean isopen = false;
		long startTime = ActivityRateTypeCfg.getStartTime();
		long endTime = ActivityRateTypeCfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		isopen = currentTime < endTime && currentTime > startTime ? true
				: false;

		if (isopen) {
			int hour = DateUtils.getCurrentHour();
			for (ActivityRateTypeStartAndEndHourHelper timebyhour : ActivityRateTypeCfg
					.getStartAndEnd()) {
				isopen = hour >= timebyhour.getStarthour()
						&& hour < timebyhour.getEndhour() ? true : false;
				if (isopen) {
					break;
				}
			}
		}
		return isopen;
	}

	/** 通用活动三可能扩展的双倍需要发送给客户端显示的在此处理;只能存在一种枚举,需要双加的额外添组合 */
	public eSpecialItemIDUserInfo getesESpecialItemIDUserInfo(
			ActivityRateTypeEnum activityRateEnum,
			eSpecialItemIDUserInfo eSpecialItemIDUserInfo, int expvalue,
			int coinvalue) {
		if (activityRateEnum == null) {
			return null;
		}

		switch (activityRateEnum) {
		case Normal_copy_EXP_DOUBLE:
		case ELITE_copy_EXP_DOUBLE:
			eSpecialItemIDUserInfo.setPlayerExp(expvalue);
			break;
		case TOWER_DOUBLE:
			eSpecialItemIDUserInfo.setCoin(coinvalue);
			break;
		default:
			break;
		}
		return eSpecialItemIDUserInfo;
	}
}
