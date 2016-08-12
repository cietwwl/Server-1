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

	public MajorDataDataHolder(String userId) {
		this.userId = userId;
		initMajorData();
	}

	private void initMajorData() {
		MajorDataCache cache = MajorDataCacheFactory.getCache();
		MajorData majorData = cache.get(userId);
		if (majorData == null) {
			majorData = new MajorData();
			majorData.setId(userId);
			majorData.setOwnerId(userId);
			cache.update(majorData);
		}
	}

	public MajorData getMarjorData() {
		return MajorDataCacheFactory.getCache().get(this.userId);
	}

	public void syn(Player player, int version) {
		MajorData marjorData = getMarjorData();
		if (marjorData != null) {
			ClientDataSynMgr.synData(player, marjorData, synType, eSynOpType.UPDATE_LIST);
		} else {
			GameLog.error("MajorDataDataHolder", "#syn()", "find MajorData fail:" + userId);
		}
	}

	public void addCoin(Player player,MajorData data) {
		MajorDataCacheFactory.getCache().updateCoin(data);
		syn(player, data);
	}
	
	public void addGold(Player player,MajorData data){
		MajorDataCacheFactory.getCache().updateGold(data);
		syn(player, data);
	}
	
	public void addChargeGold(Player player, MajorData data){
		MajorDataCacheFactory.getCache().updateGold(data);
		syn(player, data);
	}
	
	private void syn(Player player, MajorData data){
		if (data != null) {
			ClientDataSynMgr.updateData(player, data, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("MajorDataDataHolder", "#syn()", "find MajorData fail:" + userId);
		}
	}
}
