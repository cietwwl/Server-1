package com.playerdata.activity.timeCardType;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfgDAO;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItemHolder;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeSubItem;
import com.rw.fsutil.util.DateUtils;


public class ActivityTimeCardTypeMgr {

	private static ActivityTimeCardTypeMgr instance = new ActivityTimeCardTypeMgr();

	public static ActivityTimeCardTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		ActivityTimeCardTypeItemHolder.getInstance().synAllData(player);
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkTimeIsOver(player);
	}

	private void checkTimeIsOver(Player player) {
		ActivityTimeCardTypeItemHolder activityTimecardHolder = ActivityTimeCardTypeItemHolder
				.getInstance();
		ActivityTimeCardTypeItem dataItem = activityTimecardHolder.getItem(
				player.getUserId(), ActivityTimeCardTypeEnum.Month);
		List<ActivityTimeCardTypeSubItem> monthCardList = dataItem
				.getSubItemList();
		long logintime = dataItem.getActivityLoginTime();
		int dayDistance = DateUtils.getDayDistance(logintime,
				System.currentTimeMillis());
		if (dayDistance > 0) {
			for (ActivityTimeCardTypeSubItem sub : monthCardList) {
				int dayless = (sub.getDayLeft() - dayDistance) > 0 ? (sub
						.getDayLeft() - dayDistance) : 0;
				sub.setDayLeft(dayless);
			}
		}
		dataItem.setActivityLoginTime(System.currentTimeMillis());
		activityTimecardHolder.updateItem(player, dataItem);
	}

	private void checkNewOpen(Player player) {
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder
				.getInstance();
		List<ActivityTimeCardTypeCfg> allCfgList = ActivityTimeCardTypeCfgDAO
				.getInstance().getAllCfg();
		for (ActivityTimeCardTypeCfg activityTimeCardTypeCfg : allCfgList) {

			ActivityTimeCardTypeEnum typeEnum = ActivityTimeCardTypeEnum
					.getById(activityTimeCardTypeCfg.getId());
			if (typeEnum != null) {
				ActivityTimeCardTypeItem targetItem = dataHolder.getItem(
						player.getUserId(), typeEnum);// 已在之前生成数据的活动
				if (targetItem == null) {
					ActivityTimeCardTypeItem newItem = ActivityTimeCardTypeCfgDAO
							.getInstance().newItem(player, typeEnum);
					dataHolder.addItem(player, newItem);
				}

			}
		}
	}

	public boolean isTimeCardOnGoing(Player player, String timeCardTypeCfgId,
			String timeCardTypeSubItemCfgId) {

		boolean isTimeCardOnGoing = false;
		ActivityTimeCardTypeSubItem targetSubItem = null;
		targetSubItem = getSubItem(player, timeCardTypeCfgId,
				timeCardTypeSubItemCfgId);
		if (targetSubItem != null) {
			// if(targetSubItem.getDayLeft()>
			// DateUtils.getDayDistance(targetSubItem.getLastTakeAwardTime(),
			// System.currentTimeMillis())&&DateUtils.dayChanged(targetSubItem.getLastTakeAwardTime())){
			// targetSubItem.setLastTakeAwardTime(System.currentTimeMillis());//生成奖励的日期更新
			// isTimeCardOnGoing = true;
			// }

			isTimeCardOnGoing = (targetSubItem.getDayLeft()) > 0;
			if (isTimeCardOnGoing) {
				isTimeCardOnGoing = DateUtils.dayChanged(targetSubItem
						.getLastTakeAwardTime());// 隔天登陆或注册当天核实
			}
			if (isTimeCardOnGoing) {
				if (StringUtils.isBlank(targetSubItem.getLastTakeAwardTime()
						+ "")
						|| StringUtils.equals(
								targetSubItem.getLastTakeAwardTime() + "", "0")) {// 注册当天getlasttake可能为空或0；无购买记录时购买触发

				} else {
					isTimeCardOnGoing = targetSubItem.getDayLeft() > DateUtils
							.getDayDistance(
									targetSubItem.getLastTakeAwardTime(),
									System.currentTimeMillis());// 确定隔天没超过月卡期限
					if (!isTimeCardOnGoing) {
						targetSubItem.setLastTakeAwardTime(0);// 月卡还有，隔天登陆，且隔天数超过月卡天数，登陆的时候将获取奖励时间改为0，默认为清空记录；有购买记录但过期时修正为无购买
						targetSubItem.setDayLeft(0);

					}
				}
			}
			if (isTimeCardOnGoing) {
				targetSubItem.setLastTakeAwardTime(System.currentTimeMillis());

			}
		}
		return isTimeCardOnGoing;
	}

	private ActivityTimeCardTypeSubItem getSubItem(Player player,
			String timeCardTypeCfgId, String timeCardTypeSubItemCfgId) {
		ActivityTimeCardTypeSubItem targetSubItem = null;
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder
				.getInstance();

		ActivityTimeCardTypeEnum typeEnum = ActivityTimeCardTypeEnum
				.getById(timeCardTypeCfgId);
		if (typeEnum != null) {
			ActivityTimeCardTypeItem targetItem = dataHolder.getItem(
					player.getUserId(), typeEnum);// 已在之前生成数据的活动
			if (targetItem != null) {
				List<ActivityTimeCardTypeSubItem> subItemList = targetItem
						.getSubItemList();
				for (ActivityTimeCardTypeSubItem activityTimeCardTypeSubItem : subItemList) {
					if (StringUtils.equals(timeCardTypeSubItemCfgId,
							activityTimeCardTypeSubItem.getId())) {
						targetSubItem = activityTimeCardTypeSubItem;
						break;
					}
				}
			}

		}
		return targetSubItem;
	}

}
