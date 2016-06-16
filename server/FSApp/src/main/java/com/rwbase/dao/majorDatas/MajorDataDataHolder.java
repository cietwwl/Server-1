package com.rwbase.dao.majorDatas;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.majorDatas.pojo.MajorData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class MajorDataDataHolder {
	private final String userId;
	private static eSynType synType = eSynType.MajorData;
	
	public MajorDataDataHolder(String userId){
		this.userId = userId;
		initMajorData();
	}
	
	private void initMajorData(){
		MajorData majorData = getMapItemStore().getItem(this.userId);
		if(majorData == null){
			majorData = new MajorData();
			majorData.setId(userId);
			majorData.setOwnerId(userId);
			getMapItemStore().addItem(majorData);
		}
	}
	
	private MapItemStore<MajorData> getMapItemStore(){
		MapItemStoreCache<MajorData> cache = MapItemStoreFactory.getMajorDataCache();
		return cache.getMapItemStore(userId, MajorData.class);
	}
	
	public void updateItem(Player player, MajorData majorData){
		getMapItemStore().updateItem(majorData);
	}
	
	public MajorData getMarjorData(){
		return getMapItemStore().getItem(this.userId);
	}
	
	public void syn(Player player, int version){
		MajorData marjorData = getMarjorData();
		if(marjorData != null){
			ClientDataSynMgr.synData(player, marjorData, synType, eSynOpType.UPDATE_LIST);
		}else{
			GameLog.error("MajorDataDataHolder", "#syn()", "find MajorData fail:" + userId);
		}
	}
	
	public void update(Player player){
		getMapItemStore().update(this.userId);
		MajorData marjorData = getMarjorData();
		if(marjorData != null){
			ClientDataSynMgr.updateData(player, marjorData, synType, eSynOpType.UPDATE_SINGLE);
		}else{
			GameLog.error("MajorDataDataHolder", "#syn()", "find MajorData fail:" + userId);
		}
	}
}
