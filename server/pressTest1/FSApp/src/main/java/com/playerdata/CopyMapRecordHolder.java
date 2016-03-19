package com.playerdata;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.copy.pojo.CopyMapRecord;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class CopyMapRecordHolder{
	
	private String userId;
	
	public CopyMapRecordHolder(String roleIdP) {
		userId = roleIdP;
	}

	/*
	 * 同步章节记录所有数据
	 */
	public void SynAll(Player player)
	{
		Enumeration<CopyMapRecord> mapEnum = getCopyMapRecord().getEnum();
		List<CopyMapRecord> updatedList = new ArrayList<CopyMapRecord>();
		boolean isModified = false;
		while (mapEnum.hasMoreElements()) {
			updatedList.add((CopyMapRecord) mapEnum.nextElement());
			isModified = true;
		}
		if(isModified){
			ClientDataSynMgr.updateDataList(player, updatedList, eSynType.COPY_MAP_RECORD, eSynOpType.UPDATE_LIST);
		}else{
			ClientDataSynMgr.synDataList(player, updatedList, eSynType.COPY_MAP_RECORD, eSynOpType.UPDATE_LIST);
		}
	}
	
	/*
	 * 获取当前用户的副本地图记录,以"0,0,0"记录下"是否领取1,是否领取2,是否领取3"，
	 */
	public List<String> getMapRecordList()	
	{
		
		List<String> listMapRecords = new ArrayList<String>();
		Enumeration<CopyMapRecord> mapEnum = getCopyMapRecord().getEnum();
		while (mapEnum.hasMoreElements()) {
			CopyMapRecord mapRecord = (CopyMapRecord) mapEnum.nextElement();
			listMapRecords.add(mapRecord.toClientData());
		}
		
		return listMapRecords;
	}
	
	public CopyMapRecord getMapReord(int mapId){
		String mapRecordId = getMapRecordId(mapId);
		return getCopyMapRecord().getItem(mapRecordId);
	}
	
	public CopyMapRecord addMapRecord(Player player, int mapID){
		CopyMapRecord mapRecord = new CopyMapRecord();
		String mapRecordId = getMapRecordId(mapID);
		mapRecord.setId(mapRecordId);
		mapRecord.setMapId(mapID);
		mapRecord.setUserId(userId);
		final String newMapGiftStates = "0,0,0";
		mapRecord.setGiftStates(newMapGiftStates);
		boolean addSuccess = getCopyMapRecord().addItem(mapRecord);		
		if(addSuccess){
			ClientDataSynMgr.updateData(player, mapRecord, eSynType.COPY_MAP_RECORD, eSynOpType.ADD_SINGLE);
			return mapRecord;
		}else{
			return null;
		}
	}
	
	public boolean takeGift(Player player, CopyMapRecord mapRecord, int giftIndex){
		
		boolean success = mapRecord.takeGift(giftIndex);
		if(success){
			getCopyMapRecord().updateItem(mapRecord);
			ClientDataSynMgr.updateData(player, mapRecord, eSynType.COPY_MAP_RECORD, eSynOpType.UPDATE_SINGLE);
		}
		return success;
	}

	private String getMapRecordId(int mapId) {
		return userId + "_" + mapId;
	}
	
	public void flush(){
		getCopyMapRecord().flush();
	}
	
	private MapItemStore<CopyMapRecord> getCopyMapRecord(){
		MapItemStoreCache<CopyMapRecord> cache = MapItemStoreFactory.getCopyMapRecordCache();
		return cache.getMapItemStore(userId, CopyMapRecord.class);
	}

}
