package com.playerdata.activity.dailyCountType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.util.DateUtils;

public class ActivityDailyTypeMgr extends AbstractActivityMgr<ActivityDailyTypeItem>{

	private static final int ACTIVITY_INDEX_BEGIN = 10000;
	private static final int ACTIVITY_INDEX_END = 20000;
	
	private static ActivityDailyTypeMgr instance = new ActivityDailyTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityDailyTypeMgr getInstance() {
		return instance;
	}

	private void sendEmailIfGiftNotTaken(Player player, List<ActivityDailyTypeSubItem> subItemList) {
		ActivityDailyTypeSubCfgDAO activityDailyTypeSubCfgDAO = ActivityDailyTypeSubCfgDAO.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		for (ActivityDailyTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
			ActivityDailyTypeSubCfg subItemCfg = activityDailyTypeSubCfgDAO.getById(subItem.getCfgId());
			if (subItemCfg == null) {
				return;
			}
			if (subItem.getCount() >= subItemCfg.getCount() && !subItem.isTaken()) {
				comGiftMgr.addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "", subItemCfg.getEmailTitle());
				subItem.setTaken(true);
			}
		}
	}

	public void addCount(Player player, ActivityDailyTypeEnum countType, int countadd) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		ActivityDailyTypeItem dataItem = dataHolder.getItem(player.getUserId());
		if (dataItem == null) {
			return;
		}
		ActivityDailyTypeSubItem subItem = getbyDailyCountTypeEnum(player, countType, dataItem);
		if (subItem == null) {
			return;
		}
		subItem.setCount(subItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}

	public ActivityDailyTypeSubItem getbyDailyCountTypeEnum(Player player, ActivityDailyTypeEnum typeEnum, ActivityDailyTypeItem dataItem) {
		ActivityDailyTypeSubCfgDAO activityDailyTypeSubCfgDAO = ActivityDailyTypeSubCfgDAO.getInstance();
		ActivityDailyTypeSubItem subItem = null;
		ActivityDailyTypeSubCfg cfg = null;
		List<ActivityDailyTypeSubCfg> subcfglist = ActivityDailyTypeSubCfgDAO.getInstance().getAllCfg();
		for (ActivityDailyTypeSubCfg subcfg : subcfglist) {
			if (StringUtils.equals(subcfg.getEnumId(), typeEnum.getCfgId()) && activityDailyTypeSubCfgDAO.isOpen(subcfg)) {
				cfg = subcfg;
			}
		}

		if (cfg == null) {
			return subItem;
		}

		if (dataItem != null) {
			List<ActivityDailyTypeSubItem> sublist = dataItem.getSubItemList();
			for (ActivityDailyTypeSubItem subitem : sublist) {
				if (StringUtils.equals(cfg.getId(), subitem.getCfgId())) {
					subItem = subitem;
					break;
				}
			}

		}

		return subItem;
	}

	public ActivityComResult takeGift(Player player, ActivityDailyTypeEnum countType, String subItemId) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();

		ActivityDailyTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityDailyTypeSubItem targetItem = null;

			List<ActivityDailyTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityDailyTypeSubItem itemTmp : subItemList) {
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

	private void takeGift(Player player, ActivityDailyTypeSubItem targetItem) {
		ActivityDailyTypeSubCfg subCfg = ActivityDailyTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if (subCfg == null) {
			return;
		}
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());

	}

	@Override
	protected UserActivityChecker<ActivityDailyTypeItem> getHolder() {
		return ActivityDailyTypeItemHolder.getInstance();
	}

	@Override
	public boolean isThisActivityIndex(int index) {
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
