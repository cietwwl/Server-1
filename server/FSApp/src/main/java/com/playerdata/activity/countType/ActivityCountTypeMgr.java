package com.playerdata.activity.countType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.retrieve.ActivityRetrieveTypeMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.ActivityMgrHelper;
import com.playerdata.activityCommon.UserActivityChecker;

/**
 * //登录活动等基础活动
 * @author aken
 *
 */
public class ActivityCountTypeMgr extends AbstractActivityMgr<ActivityCountTypeItem> {
	
	private static final int ACTIVITY_INDEX_BEGIN = 0;
	private static final int ACTIVITY_INDEX_END = 10000;

	private static ActivityCountTypeMgr instance = new ActivityCountTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityCountTypeMgr getInstance() {
		return instance;
	}
	
	/**
	 * 
	 * @param player
	 * 通用活动数据同步,生成活动奖励空数据；应置于所有通用活动的统计之前；可后期放入初始化模块
	 */
	public void checkActivity(Player player) {
		ActivityTimeCardTypeMgr.getInstance().checkActivityOpen(player);
		ActivityTimeCountTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRateTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRedEnvelopeTypeMgr.getInstance().checkActivityOpen(player);
		ActivityLimitHeroTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRetrieveTypeMgr.getInstance().checkActivityOpen(player);
		ActivityMgrHelper.getInstance().checkActivity(player);
	}

	public void addCount(Player player, ActivityCountTypeEnum countType, int countadd) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		if (dataItem == null) {
			return;
		}
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
		if (subCfg == null) {
			// logger
			return;
		}
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getAwardGift());

	}
	
	/**
	 * 邮件补发过期未领取的奖励
	 * 
	 * @param player
	 * @param item
	 */
	@Override
	public void expireActivityHandler(Player player, ActivityCountTypeItem item) {
		List<ActivityCountTypeSubItem> subItems = item.getSubItemList();
		ActivityCountTypeSubCfgDAO activityCountTypeSubCfgDAO = ActivityCountTypeSubCfgDAO.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		for (ActivityCountTypeSubItem subItem : subItems) {// 配置表里的每种奖励
			ActivityCountTypeSubCfg subItemCfg = activityCountTypeSubCfgDAO.getById(subItem.getCfgId());
			if (subItemCfg == null) {
				continue;
			}
			if (!subItem.isTaken() && item.getCount() >= subItemCfg.getAwardCount()) {
				boolean isAdd = comGiftMgr.addGiftTOEmailById(player, subItemCfg.getAwardGift(), MAKEUPEMAIL + "", subItemCfg.getEmailTitle());
				if (isAdd) {
					subItem.setTaken(true);
				}
			}
		}
		item.reset();
	}

	@Override
	protected List<String> checkRedPoint(Player player, ActivityCountTypeItem item) {
		List<String> redPointList = new ArrayList<String>();
		ActivityCountTypeSubCfgDAO subCfgDao = ActivityCountTypeSubCfgDAO.getInstance();
		List<ActivityCountTypeSubItem> subItems = item.getSubItemList();
		for (ActivityCountTypeSubItem subItem : subItems) {
			ActivityCountTypeSubCfg subCfg = subCfgDao.getCfgById(subItem.getCfgId());
			if (null == subCfg)
				continue;
			if ((subCfg.getAwardCount() <= item.getCount() && !subItem.isTaken()) || !item.isHasViewed()) {
				redPointList.add(String.valueOf(item.getCfgId()));
				break;
			}
		}
		return redPointList;
	}
	
	@Override
	protected UserActivityChecker<ActivityCountTypeItem> getHolder() {
		return ActivityCountTypeItemHolder.getInstance();
	}

	@Override
	public boolean isThisActivityIndex(int index) {
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
