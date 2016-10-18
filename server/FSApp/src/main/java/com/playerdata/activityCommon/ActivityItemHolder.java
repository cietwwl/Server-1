package com.playerdata.activityCommon;

import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityItemHolder extends UserActivityChecker<ActivityDailyRechargeTypeItem>{
	
	private static ActivityItemHolder instance = new ActivityItemHolder();
	
	public static ActivityItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDailyRechargeType;
	
//	public List<ActivityDailyRechargeTypeItem> getItemList(String userId){
//		return refreshDailyRecharge(userId);
//	}
//	
//	public void updateItem(Player player, ActivityDailyRechargeTypeItem item){
//		getItemStore(player.getUserId()).update(item.getId());
//		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
//	}
//	
//	public ActivityDailyRechargeTypeItem getItem(String userId, String cfgId){		
////		String itemId = getActivityID(cfgId, userId);
////		return getItemStore(userId).getItem(itemId);
//		int id = Integer.parseInt(cfgId);
//		return getItemStore(userId).get(id);
//		
//	}
//
//	public void synAllData(Player player){
//		List<ActivityDailyRechargeTypeItem> itemList = getItemList(player.getUserId());
//		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
//	}
//
//	
//	private String getActivityID(String cfgId, String userId){
//		return cfgId + "_" + userId;
//	}
//	
	private RoleExtPropertyStore<ActivityDailyRechargeTypeItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityDailyRechargeTypeItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_DAILYCHARGE, ActivityDailyRechargeTypeItem.class);
		try {
			return storeCache.getStore(userId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
