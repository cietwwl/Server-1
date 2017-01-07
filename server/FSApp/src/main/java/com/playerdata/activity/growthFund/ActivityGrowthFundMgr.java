package com.playerdata.activity.growthFund;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.growthFund.cfg.GrowthFundBasicCfg;
import com.playerdata.activity.growthFund.cfg.GrowthFundBasicCfgDAO;
import com.playerdata.activity.growthFund.cfg.GrowthFundRewardAbsCfg;
import com.playerdata.activity.growthFund.cfg.GrowthFundSubCfgDAO;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItem;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItemHolder;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;
import com.rw.fsutil.shutdown.ShutdownService;

public class ActivityGrowthFundMgr extends AbstractActivityMgr<ActivityGrowthFundItem> {
	
	private static final int ACTIVITY_INDEX_BEGIN = 140000;
	private static final int ACTIVITY_INDEX_END = 150000;

	private static ActivityGrowthFundMgr instance = new ActivityGrowthFundMgr();

	public static ActivityGrowthFundMgr getInstance() {
		return instance;
	}
	
	public void serverStartComplete() {
		ActivityGrowthFundItemHolder.getInstance().loadGlobalData();
		ShutdownService.registerShutdownService(new GrowthFundShutdownHandler());
	}
	
	private boolean canGetReward(Player player, ActivityGrowthFundSubItem subItem, GrowthFundType fundType) {
		if (subItem.getRequiredCondition() == 0) {
			GrowthFundRewardAbsCfg rewardCfg = (GrowthFundRewardAbsCfg) GrowthFundSubCfgDAO.getInstance().getCfgById(subItem.getCfgId());
			subItem.setRequiredCondition(rewardCfg.getRequiredCondition());
		}
		switch (fundType) {
		case GIFT:
			// 等级到达，并且没有领取过
			return subItem.getRequiredCondition() <= player.getLevel() && !subItem.isGet();
		case REWARD:
			// 人数到达，并且没有领取过
			return subItem.getRequiredCondition() <= ActivityGrowthFundItemHolder.getInstance().getGlobalData().getAlreadyBoughtCount() && !subItem.isGet();
		}
		return false;
	}
	
	private void checkIfFundTypeNull(ActivityGrowthFundItem item) {
		if (item.getGrowthFundType() == null) {
			GrowthFundBasicCfg cfg = GrowthFundBasicCfgDAO.getInstance().getCfgById(item.getCfgId());
			item.setGrowthFundType(cfg.getFundType());
		}
	}
	
	GrowthFundGlobalData getGlobalData() {
		return ActivityGrowthFundItemHolder.getInstance().getGlobalData();
	}
	
	@Override
	protected List<String> checkRedPoint(Player player, ActivityGrowthFundItem item) {
		checkIfFundTypeNull(item);
		List<String> redPointList = new ArrayList<String>();
		switch (item.getGrowthFundType()) {
		case GIFT:
			if (!item.isBought() && item.isHasViewed()) {
				// 如果没有买成长基金礼包的话
				return redPointList;
			}
		default:
			break;
		}
		GrowthFundType fundType = item.getGrowthFundType();
		List<ActivityGrowthFundSubItem> subItems = (List<ActivityGrowthFundSubItem>) item.getSubItemList();
		for (ActivityGrowthFundSubItem subItem : subItems) {
			if (canGetReward(player, subItem, fundType) || !item.isHasViewed()) {
				redPointList.add(String.valueOf(item.getCfgId()));
				break;
			}
		}
		return redPointList;
	}
	
	protected UserActivityChecker<ActivityGrowthFundItem> getHolder() {
		return ActivityGrowthFundItemHolder.getInstance();
	}

	@Override
	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
	
	public ActivityGrowthFundItem getByType(String userId, GrowthFundType type) {
		List<ActivityGrowthFundItem> list = ActivityGrowthFundItemHolder.getInstance().getItemList(userId);
		for (ActivityGrowthFundItem temp : list) {
			checkIfFundTypeNull(temp);
			if (temp.getGrowthFundType() == type) {
				return temp;
			}
		}
		return null;
	}
	
	public void onPlayerBuyGrowthFundGift(Player player) {
		ActivityGrowthFundItemHolder _dataHolder = ActivityGrowthFundItemHolder.getInstance();
		List<ActivityGrowthFundItem> list = _dataHolder.getItemList(player.getUserId());
		_dataHolder.getGlobalData().increaseAlreadyBoughtCount();
		for (ActivityGrowthFundItem temp : list) {
			checkIfFundTypeNull(temp);
			temp.setBought(true);
			_dataHolder.updateItem(player, temp);
		}
	}
	
	public void onPlayerGetReward(Player player, ActivityGrowthFundItem item, ActivityGrowthFundSubItem subItem) {
		subItem.setGet(true);
		ActivityGrowthFundItemHolder.getInstance().updateItem(player, item);
	}
	
	public int getBoughtCount() {
		return ActivityGrowthFundItemHolder.getInstance().getGlobalData().getAlreadyBoughtCount();
	}
}
