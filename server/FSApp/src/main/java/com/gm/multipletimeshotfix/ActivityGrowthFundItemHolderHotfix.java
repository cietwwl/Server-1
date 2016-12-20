package com.gm.multipletimeshotfix;

import java.util.Collections;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.growthFund.GrowthFundGlobalData;
import com.playerdata.activity.growthFund.GrowthFundSubItemComparator;
import com.playerdata.activity.growthFund.cfg.GrowthFundRewardAbsCfg;
import com.playerdata.activity.growthFund.cfg.GrowthFundSubCfgDAO;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItem;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItemHolder;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundSubItem;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;
import com.rwproto.DataSynProtos.eSynOpType;


public class ActivityGrowthFundItemHolderHotfix extends ActivityGrowthFundItemHolder{
	
	private static GrowthFundSubItemComparator _comparator = new GrowthFundSubItemComparator();
	
	private GrowthFundGlobalData _globalData;
	
	public ActivityGrowthFundItemHolderHotfix(){
		_globalData = ActivityGrowthFundItemHolder.getInstance().getGlobalData();
	}
	
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
	
	public void synAllDataWithoutEmpty(Player player){
		List<ActivityGrowthFundItem> itemList = getItemList(player.getUserId());
		if(null != itemList && !itemList.isEmpty() && null != getSynType()){
			checkGrowthFundItemData(itemList);
			ClientDataSynMgr.synDataList(player, itemList, getSynType(), eSynOpType.UPDATE_LIST);
		}
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
	
	public RoleExtPropertyStore<ActivityGrowthFundItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityGrowthFundItem> cache = RoleExtPropertyFactory.getPlayerExtCache(getExtPropertyType(), ActivityGrowthFundItem.class);
		try {
			return cache.getStore(userId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
