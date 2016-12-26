package com.playerdata.activity.chargeRank.data;

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


public class ActivityChargeRankItemHolder extends UserActivityChecker<ActivityChargeRankItem>{
		
	protected ActivityChargeRankItemHolder(){
		super(ActivityChargeRankItem.class);
	}

	private static ActivityChargeRankItemHolder instance = new ActivityChargeRankItemHolder();
	
	public static ActivityChargeRankItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityChargeRank;
	
	public void updateItem(Player player, ActivityChargeRankItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	public void synAllData(Player player){
		List<ActivityChargeRankItem> itemList = getItemList(player.getUserId());
		if(null != itemList && !itemList.isEmpty()){
			ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
		}
	}
	
	public RoleExtPropertyStore<ActivityChargeRankItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityChargeRankItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_CHARGE_RANK, ActivityChargeRankItem.class);
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
		return ActivityTypeFactory.ChargeRank;
	}

	@Override
	public PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_CHARGE_RANK;
	}
}
