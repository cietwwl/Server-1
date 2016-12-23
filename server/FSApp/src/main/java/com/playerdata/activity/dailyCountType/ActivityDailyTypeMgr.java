package com.playerdata.activity.dailyCountType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;

/**
 * 每日福利
 * @author aken
 *
 */
public class ActivityDailyTypeMgr extends AbstractActivityMgr<ActivityDailyTypeItem>{

	private static final int ACTIVITY_INDEX_BEGIN = 10000;
	private static final int ACTIVITY_INDEX_END = 20000;
	
	private static ActivityDailyTypeMgr instance = new ActivityDailyTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityDailyTypeMgr getInstance() {
		return instance;
	}

	public void addCount(Player player, ActivityDailyTypeEnum countType, int countadd) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		List<ActivityDailyTypeItem> dataItems = dataHolder.getItemList(player.getUserId());
		if (dataItems == null || dataItems.isEmpty()) {
			return;
		}
		for(ActivityDailyTypeItem dataItem : dataItems){
			ActivityDailyTypeSubItem subItem = getByDailyCountTypeEnum(player, countType, dataItem);
			if (subItem == null) {
				return;
			}
			subItem.setCount(subItem.getCount() + countadd);
		}
		dataHolder.updateItem(player, dataItems);
	}

	public ActivityDailyTypeSubItem getByDailyCountTypeEnum(Player player, ActivityDailyTypeEnum typeEnum, ActivityDailyTypeItem dataItem) {
		ActivityDailyTypeSubCfg cfg = null;
		List<ActivityDailyTypeSubCfg> subcfglist = ActivityDailyTypeSubCfgDAO.getInstance().getAllCfg();
		for (ActivityDailyTypeSubCfg subcfg : subcfglist) {
			if (StringUtils.equals(subcfg.getEnumId(), typeEnum.getCfgId()) && subcfg.getType() == Integer.parseInt(dataItem.getCfgId())) {
				cfg = subcfg;
				break;
			}
		}
		if (cfg == null) {
			return null;
		}
		ActivityDailyTypeSubItem subItem = null;
		if (dataItem != null) {
			List<ActivityDailyTypeSubItem> sublist = dataItem.getSubItemList();
			for (ActivityDailyTypeSubItem subitem : sublist) {
				if (StringUtils.equals(String.valueOf(cfg.getId()), subitem.getCfgId())) {
					subItem = subitem;
					break;
				}
			}
		}
		return subItem;
	}

	public ActivityComResult takeGift(Player player, int actEnumId, String subItemId) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		ActivityDailyTypeItem dataItem = dataHolder.getItem(player.getUserId(), String.valueOf(actEnumId));
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
				takeGift(player, targetItem, true);
				result.setSuccess(true);
				result.setReason("");
				dataHolder.updateItem(player, dataItem);
			}
		}
		return result;
	}

	/**
	 * 领取奖励
	 * @param player
	 * @param targetItem
	 */
	private void takeGift(Player player, ActivityDailyTypeSubItem targetItem, boolean isOnTime) {
		ActivityDailyTypeSubCfg subCfg = ActivityDailyTypeSubCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
		if (null == subCfg) {
			return;
		}
		if (!targetItem.isTaken() && targetItem.getCount() >= subCfg.getCount()) {
			targetItem.setTaken(true);
			if(isOnTime){
				ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());
			}else{
				ComGiftMgr.getInstance().addGiftTOEmailById(player, subCfg.getGiftId(), MAKEUPEMAIL + "", subCfg.getEmailTitle());
			}
		}
	}
	
	/**
	 * 邮件补发过期未领取的奖励
	 * 
	 * @param player
	 * @param item
	 */
	@Override
	public void expireActivityHandler(Player player, ActivityDailyTypeItem item) {
		List<ActivityDailyTypeSubItem> subItems = item.getSubItemList();
		ActivityDailyTypeCfg cfg = ActivityDailyTypeCfgDAO.getInstance().getCfgById(item.getCfgId());
		if (isLevelEnough(player, cfg)) {
			for (ActivityDailyTypeSubItem subItem : subItems) {
				takeGift(player, subItem, false);
			}
		}
		item.reset();
	}
	
	@Override
	protected List<String> checkRedPoint(Player player, ActivityDailyTypeItem item) {
		List<String> redPointList = new ArrayList<String>();
		ActivityDailyTypeSubCfgDAO subCfgDao = ActivityDailyTypeSubCfgDAO.getInstance();
		List<ActivityDailyTypeSubItem> subItems = item.getSubItemList();
		for (ActivityDailyTypeSubItem subItem : subItems) {
			ActivityDailyTypeSubCfg subCfg = subCfgDao.getCfgById(subItem.getCfgId());
			if (null == subCfg)
				continue;
			if ((subCfg.getCount() <= subItem.getCount() && !subItem.isTaken()) || !item.isHasViewed()) {
				redPointList.add(String.valueOf(item.getCfgId()));
				break;
			}
		}
		return redPointList;
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
