package com.playerdata.activity.VitalityType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;

import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfgDAO;
import com.playerdata.activity.VitalityType.data.ActivityVitalityItemHolder;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubBoxItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.ActivityDetector;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;

public class ActivityVitalityTypeMgr extends AbstractActivityMgr<ActivityVitalityTypeItem>{
	
	private static final int ACTIVITY_INDEX_BEGIN = 50000;
	private static final int ACTIVITY_INDEX_END = 60000;

	private static ActivityVitalityTypeMgr instance = new ActivityVitalityTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityVitalityTypeMgr getInstance() {
		return instance;
	}

	/**
	 * 补发活跃之王未领取的子项奖励
	 * @param player
	 * @param subItemList
	 */
	private void sendEmailIfGiftNotTaken(Player player, List<ActivityVitalityTypeSubItem> subItemList) {
		ActivityVitalitySubCfgDAO activityVitalitySubCfgDAO = ActivityVitalitySubCfgDAO.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		for (ActivityVitalityTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
			ActivityVitalitySubCfg subItemCfg = activityVitalitySubCfgDAO.getCfgById(subItem.getCfgId());
			if (subItemCfg == null) {
				return;
			}
			if (subItem.getCount() >= subItemCfg.getCount() && !subItem.isTaken()) {
				comGiftMgr.addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "", subItemCfg.getEmailTitle());
				subItem.setTaken(true);
			}
		}
	}

	/**
	 * 补发活跃之王未领取的箱子奖励（总活跃度奖励）
	 * @param player
	 * @param item
	 */
	private void sendEmailIfBoxGiftNotTaken(Player player, ActivityVitalityTypeItem item) {
		boolean isActive = ActivityDetector.getInstance().containsActivityByCfgId(ActivityTypeFactory.VitalityType, item.getCfgId(), item.getVersion());
		if(isActive){
			return;
		}
		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgById(item.getCfgId());
		if (cfg == null) {
			return;
		}
		if (!cfg.isCanGetReward()) {
			// 不派发宝箱
			return;
		}
		ActivityVitalityRewardCfgDAO activityVitalityRewardCfgDAO = ActivityVitalityRewardCfgDAO.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		List<ActivityVitalityTypeSubBoxItem> subBoxItemList = item.getSubBoxItemList();
		for (ActivityVitalityTypeSubBoxItem subItem : subBoxItemList) {// 配置表里的每种奖励
			ActivityVitalityRewardCfg subItemCfg = activityVitalityRewardCfgDAO.getCfgById(subItem.getCfgId());
			if (subItemCfg == null) {
				return;
			}
			if (item.getActiveCount() >= subItemCfg.getActivecount() && !subItem.isTaken()) {
				comGiftMgr.addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "", subItemCfg.getEmailTitle());
				subItem.setTaken(true);
			}
		}
	}
	
	/**
	 * 添加完成度
	 * @param player
	 * @param countType
	 * @param count
	 * @param isAdd 是否是添加数量（有些任务是设置，比如升级等级）
	 */
	public void addCount(Player player, ActivityVitalityTypeEnum countType, int count) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		List<ActivityVitalityTypeItem> vitalityItems = dataHolder.getItemList(player.getUserId());
		if(null == vitalityItems || vitalityItems.isEmpty()){
			return;
		}
		ActivityVitalitySubCfgDAO subCfgDAO = ActivityVitalitySubCfgDAO.getInstance();
		ActivityVitalitySubCfg subCfg = null;
		for(ActivityVitalityTypeItem dataItem : vitalityItems){
			ActivityVitalityTypeSubItem subItem = getByVitalityTypeEnum(player, countType, dataItem);
			if(null != subItem){
				if(null == subCfg){
					subCfg = subCfgDAO.getCfgById(subItem.getCfgId());
				}
				addVitalityCount(dataItem, subItem, subCfg, count, countType.isAdd());
				if(countType.isAdd()){
					subItem.setCount(subItem.getCount() + count);
				}else if(count > subItem.getCount()){
					subItem.setCount(count);
				}
				dataHolder.updateItem(player, dataItem);
			}
		}
	}

	/**
	 * 获取对应枚举的子项
	 * @param player
	 * @param countType
	 * @param dataItem
	 * @return
	 */
	private ActivityVitalityTypeSubItem getByVitalityTypeEnum(Player player, ActivityVitalityTypeEnum countType, ActivityVitalityTypeItem dataItem) {
		ActivityVitalityTypeSubItem subItem = null;
		if (dataItem != null) {
			List<ActivityVitalityTypeSubItem> sublist = dataItem.getSubItemList();
			for (ActivityVitalityTypeSubItem subitem : sublist) {
				if(StringUtils.equals(subitem.getType(), countType.getCfgId())){
					subItem = subitem;
					break;
				}
			}
		}
		return subItem;
	}
	
	/**
	 * 达到增加总活跃度的条件
	 * 增加总活跃度
	 * @param dataItem
	 * @param subItem
	 * @param subCfg
	 * @param count
	 */
	private void addVitalityCount(ActivityVitalityTypeItem dataItem, ActivityVitalityTypeSubItem subItem, ActivityVitalitySubCfg subCfg, int count, boolean isAdd) {
		int totalCount = count;
		if(isAdd){
			totalCount += subItem.getCount();
		}
		if (subItem.getCount() < subCfg.getCount() && (totalCount >= subCfg.getCount())) {
			dataItem.setActiveCount(dataItem.getActiveCount() + subCfg.getActiveCount());
		}
	}

	public ActivityComResult takeGift(Player player, String subItemId) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = null;
		ActivityVitalityTypeSubItem item = null;
		List<ActivityVitalityTypeItem> dataitemList = dataHolder.getItemList(player.getUserId());
		for (ActivityVitalityTypeItem dataitemtmp : dataitemList) {
			List<ActivityVitalityTypeSubItem> subitemlist = dataitemtmp.getSubItemList();
			for (ActivityVitalityTypeSubItem subitem : subitemlist) {
				if (StringUtils.equals(subItemId, subitem.getCfgId())) {
					item = subitem;
					dataItem = dataitemtmp;
					break;
				}
			}
		}
		ActivityComResult result = ActivityComResult.newInstance(false);

		if (dataItem == null) {
			result.setReason("活动尚未开启");
		} else {
			if (item.isTaken()) {
				result.setReason("已经领取");
				return result;
			}
			takeGift(player, item);
			result.setSuccess(true);
			dataHolder.updateItem(player, dataItem);

		}

		return result;
	}

	private void takeGift(Player player, ActivityVitalityTypeSubItem targetItem) {
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, targetItem.getGiftId());
	}

	public ActivityComResult openBox(Player player, String rewardItemId) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = null;
		ActivityVitalityTypeSubBoxItem item = null;
		List<ActivityVitalityTypeItem> dataitemList = dataHolder.getItemList(player.getUserId());
		for (ActivityVitalityTypeItem dataitemtmp : dataitemList) {
			List<ActivityVitalityTypeSubBoxItem> subitemlist = dataitemtmp.getSubBoxItemList();
			for (ActivityVitalityTypeSubBoxItem subitem : subitemlist) {
				if (StringUtils.equals(rewardItemId, subitem.getCfgId())) {
					item = subitem;
					dataItem = dataitemtmp;
					break;
				}
			}
		}

		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动未开启");
			return result;
		} else {
			if (item.getCount() > dataItem.getActiveCount()) {
				result.setReason("积分不足");
				return result;
			}
			if (item.isTaken()) {
				result.setReason("已经领取");
				return result;
			}
			takeBoxGift(player, item);
			result.setSuccess(true);
			dataHolder.updateItem(player, dataItem);
		}
		return result;
	}

	private void takeBoxGift(Player player, ActivityVitalityTypeSubBoxItem targetItem) {
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, targetItem.getGiftId());
	}
	
	/**
	 * 是否有活跃项
	 * @param item
	 * @param subCfgDAO
	 * @return
	 */
	private boolean isHaveSubItemReward(ActivityVitalityTypeItem item, ActivityVitalitySubCfgDAO subCfgDAO){
		List<ActivityVitalityTypeSubItem> vitalitySubItemList = item.getSubItemList();
		for (ActivityVitalityTypeSubItem subItem : vitalitySubItemList) {// 配置表里的每种奖励
			ActivityVitalitySubCfg subItemCfg = subCfgDAO.getCfgById(subItem.getCfgId());
			if (subItemCfg == null) {
				continue;
			}
			if (subItem.getCount() >= subItemCfg.getCount() && !subItem.isTaken()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否有活跃宝箱
	 * @param item
	 * @param rewardCfgDAO
	 * @return
	 */
	private boolean isHaveBoxReward(ActivityVitalityTypeItem item, ActivityVitalityRewardCfgDAO rewardCfgDAO){
		List<ActivityVitalityTypeSubBoxItem> vitalitySubBoxItemList = item.getSubBoxItemList();
		for (ActivityVitalityTypeSubBoxItem subItem : vitalitySubBoxItemList) {// 配置表里的每种奖励
			ActivityVitalityRewardCfg subItemCfg = rewardCfgDAO.getCfgById(subItem.getCfgId());
			if (subItemCfg == null) {
				continue;
			}
			if (item.getActiveCount() >= subItemCfg.getActivecount() && !subItem.isTaken()) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 邮件补发过期未领取的奖励
	 * 
	 * 发箱子奖励的时候，需要检测活动是否结束
	 * 
	 * @param player
	 * @param item
	 */
	@Override
	public void expireActivityHandler(Player player, ActivityVitalityTypeItem item) {
		sendEmailIfGiftNotTaken(player, item.getSubItemList());
		sendEmailIfBoxGiftNotTaken(player, item);
		ActivityVitalityItemHolder.getInstance().updateItem(player, item);
		item.reset();
	}

	@Override
	protected List<String> checkRedPoint(Player player, ActivityVitalityTypeItem item) {
		List<String> redPointList = new ArrayList<String>();
		ActivityVitalitySubCfgDAO subCfgDAO = ActivityVitalitySubCfgDAO.getInstance();
		ActivityVitalityRewardCfgDAO rewardCfgDAO = ActivityVitalityRewardCfgDAO.getInstance();
		if (!item.isTouchRedPoint() || isHaveSubItemReward(item, subCfgDAO) || isHaveBoxReward(item, rewardCfgDAO)) {
			redPointList.add(item.getCfgId());
		}
		return redPointList;
	}
	
	@Override
	public boolean isThisActivityIndex(int index) {
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}

	@Override
	protected UserActivityChecker<ActivityVitalityTypeItem> getHolder() {
		return ActivityVitalityItemHolder.getInstance();
	}
}
