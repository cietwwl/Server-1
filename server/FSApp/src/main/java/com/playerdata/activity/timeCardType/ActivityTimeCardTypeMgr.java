package com.playerdata.activity.timeCardType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfgDAO;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfgDAO;
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
		ActivityTimeCardTypeItem dataItem = activityTimecardHolder
				.getItem(player.getUserId());
		List<ActivityTimeCardTypeSubItem> monthCardList = dataItem.getSubItemList();
		long logintime = dataItem.getActivityLoginTime();
		long now = DateUtils.getSecondLevelMillis();
		int dayDistance = DateUtils.getDayDistance(logintime,now);
		dataItem.setActivityLoginTime(now);
		if (dayDistance > 0) {
			for (ActivityTimeCardTypeSubItem sub : monthCardList) {
				int dayless = (sub.getDayLeft() - dayDistance) > 0 ? (sub
						.getDayLeft() - dayDistance) : 0;
				sub.setDayLeft(dayless);
			}
			activityTimecardHolder.updateItem(player, dataItem);
		}
	}

	private void checkNewOpen(Player player) {

	}

	public List<ActivityTimeCardTypeItem> creatItems(String userId,boolean isHasPlayer){
		List<ActivityTimeCardTypeItem> addItemList = null;
//		String itemId = ActivityTimeCardTypeHelper.getItemId(userId, ActivityTimeCardTypeEnum.Month);
		int id = Integer.parseInt(ActivityTimeCardTypeEnum.Month.getCfgId());
		ActivityTimeCardTypeCfgDAO dao = ActivityTimeCardTypeCfgDAO.getInstance();
		List<ActivityTimeCardTypeCfg> allcfglist = dao.getAllCfg();
		Long now = DateUtils.getSecondLevelMillis();
		for(ActivityTimeCardTypeCfg cfg: allcfglist){
			
			ActivityTimeCardTypeItem item = new ActivityTimeCardTypeItem();
			item.setId(id);
			item.setUserId(userId);
			item.setCfgId(cfg.getId());
			item.setActivityLoginTime(now);
			List<ActivityTimeCardTypeSubItem> subItemList = new ArrayList<ActivityTimeCardTypeSubItem>();
			List<ActivityTimeCardTypeSubCfg> subItemCfgList = ActivityTimeCardTypeSubCfgDAO.getInstance().getByParentCfgId(cfg.getId());
			if(subItemCfgList == null){
				subItemCfgList = new ArrayList<ActivityTimeCardTypeSubCfg>();
			}
			for (ActivityTimeCardTypeSubCfg subCfg : subItemCfgList) {
				ActivityTimeCardTypeSubItem subItem = new ActivityTimeCardTypeSubItem();
				subItem.setId(subCfg.getId());
				GameLog.error(LogModule.ComActivityTimeCard, userId, "月卡数据的sub.id = "+subCfg.getId(), null);
				subItem.setDayLeft(0);
				subItem.setTimeCardType(subCfg.getTimeCardType());
				subItem.setChargetype(subCfg.getChargeType().getCfgId());
				subItemList.add(subItem);
			}
			item.setSubItemList(subItemList);
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityTimeCardTypeItem>();
			}
			if (addItemList.size() >= 1) {
				// 同时生成了两条以上数据；
				GameLog.error(LogModule.ComActivityTimeCard, userId, "同时有多个活动开启", null);
				continue;
			}

			addItemList.add(item);
		}		
		return addItemList;
	}
	
	
	public boolean isTimeCardOnGoing(String userId, String timeCardTypeCfgId,
			String timeCardTypeSubItemCfgId) {

		boolean isTimeCardOnGoing = false;
		ActivityTimeCardTypeSubItem targetSubItem = null;
		targetSubItem = getSubItem(userId, timeCardTypeCfgId,
				timeCardTypeSubItemCfgId);
		if (targetSubItem != null) {
			isTimeCardOnGoing = (targetSubItem.getDayLeft()) > 0;
		}
		return isTimeCardOnGoing;
	}

	private ActivityTimeCardTypeSubItem getSubItem(String userId,
			String timeCardTypeCfgId, String timeCardTypeSubItemCfgId) {
		ActivityTimeCardTypeSubItem targetSubItem = null;
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder
				.getInstance();

		ActivityTimeCardTypeEnum typeEnum = ActivityTimeCardTypeEnum
				.getById(ActivityTimeCardTypeEnum.Month.getCfgId());//临时逗比处理下，合并后要将日常任务的表要统一改为100001
		if (typeEnum != null) {
			ActivityTimeCardTypeItem targetItem = dataHolder.getItem(userId);// 已在之前生成数据的活动
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

	public boolean isOpen() {
		return ActivityTimeCardTypeCfgDAO.getInstance().getEntryCount() > 0;
	}

}
