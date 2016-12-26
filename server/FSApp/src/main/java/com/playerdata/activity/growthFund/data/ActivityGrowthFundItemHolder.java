package com.playerdata.activity.growthFund.data;

import java.util.Collections;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.growthFund.GrowthFundGlobalData;
import com.playerdata.activity.growthFund.GrowthFundSubItemComparator;
import com.playerdata.activity.growthFund.cfg.GrowthFundRewardAbsCfg;
import com.playerdata.activity.growthFund.cfg.GrowthFundSubCfgDAO;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


public class ActivityGrowthFundItemHolder extends UserActivityChecker<ActivityGrowthFundItem>{
	
	private static ActivityGrowthFundItemHolder instance = new ActivityGrowthFundItemHolder();
	private static GrowthFundSubItemComparator _comparator = new GrowthFundSubItemComparator();
	
	public static ActivityGrowthFundItemHolder getInstance(){
		return instance;
	}
	
	private GrowthFundGlobalData _globalData;
	
	private void checkGrowthFundItemData(List<ActivityGrowthFundItem> itemList) {
		int alreadyBoughtCount = _globalData.getAlreadyBoughtCount();
		GrowthFundSubCfgDAO cfgDAO = GrowthFundSubCfgDAO.getInstance();
		for (ActivityGrowthFundItem item : itemList) {
			item.setBoughtCount(alreadyBoughtCount);
			if (!item.isSorted()) {
				List<ActivityGrowthFundSubItem> subItemList = item.getSubItemList();
				for (ActivityGrowthFundSubItem subItem : subItemList) {
					GrowthFundRewardAbsCfg cfgIF = (GrowthFundRewardAbsCfg) cfgDAO.getCfgById(subItem.getCfgId());
					subItem.setRequiredCondition(cfgIF.getRequiredCondition());
				}
				Collections.sort(subItemList, _comparator);
				item.setSorted(true);
			}
		}
	}

	public void synAllData(Player player) {
		List<ActivityGrowthFundItem> itemList = getItemList(player.getUserId());
		if (null != itemList && !itemList.isEmpty()) {
			checkGrowthFundItemData(itemList);
			ClientDataSynMgr.synDataList(player, itemList, getSynType(), eSynOpType.UPDATE_LIST);
		}
	}
	
	public void synAllDataWithoutEmpty(Player player){
		List<ActivityGrowthFundItem> itemList = getItemList(player.getUserId());
		if(null != itemList && !itemList.isEmpty() && null != getSynType()){
			checkGrowthFundItemData(itemList);
			ClientDataSynMgr.synDataList(player, itemList, getSynType(), eSynOpType.UPDATE_LIST);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.GrowthFund;
	}
	
	public void loadGlobalData() {
		String attribute = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROWTH_FUND);
		if (attribute != null && (attribute = attribute.trim()).length() > 0) {
			_globalData = JsonUtil.readValue(attribute, GrowthFundGlobalData.class);
		} else {
			_globalData = GrowthFundGlobalData.newInstance();
		}
	}

	public GrowthFundGlobalData getGlobalData() {
		return _globalData;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_GROWTHFUND;
	}
	
	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityGrowthFund;
	}
}
