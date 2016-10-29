package com.playerdata.activity.dailyCharge.data;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


public class ActivityDailyRechargeTypeItemHolder extends UserActivityChecker<ActivityDailyRechargeTypeItem>{
	
	private static ActivityDailyRechargeTypeItemHolder instance = new ActivityDailyRechargeTypeItemHolder();
	
	public static ActivityDailyRechargeTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDailyRechargeType;
	
	public void updateItem(Player player, ActivityDailyRechargeTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	public void synAllData(Player player){
		List<ActivityDailyRechargeTypeItem> itemList = getItemList(player.getUserId());
		if(null != itemList && !itemList.isEmpty()){
			ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
		}
	}
	
	public RoleExtPropertyStore<ActivityDailyRechargeTypeItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityDailyRechargeTypeItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_DAILYCHARGE, ActivityDailyRechargeTypeItem.class);
		try {
			return storeCache.getStore(userId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.DailyRecharge;
	}
}
