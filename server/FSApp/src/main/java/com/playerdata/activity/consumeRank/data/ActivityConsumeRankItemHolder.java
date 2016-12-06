package com.playerdata.activity.consumeRank.data;

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


public class ActivityConsumeRankItemHolder extends UserActivityChecker<ActivityConsumeRankItem>{
	
	private static ActivityConsumeRankItemHolder instance = new ActivityConsumeRankItemHolder();
	
	public static ActivityConsumeRankItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityConsumeRank;
	
	public void updateItem(Player player, ActivityConsumeRankItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	public void synAllData(Player player){
		List<ActivityConsumeRankItem> itemList = getItemList(player.getUserId());
		if(null != itemList && !itemList.isEmpty()){
			//ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
		}
	}
	
	public RoleExtPropertyStore<ActivityConsumeRankItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityConsumeRankItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_CONSUME_RANK, ActivityConsumeRankItem.class);
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
		return ActivityTypeFactory.ConsumeRank;
	}
}
