package com.rwbase.dao.store.pojo;

import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.IRole;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.store.TableStoreDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/**
 * 商店数据
 * 
 * @author lida
 *
 */
public class StoreDataHolder {
	
	private TableStoreDao tableStoreDao = TableStoreDao.getInstance();
	private static eSynType synType = eSynType.Store_Data;
	private final String userId;

	// 初始化
	public StoreDataHolder(String userId) {
		this.userId = userId;
	}
	
	public void syn(Player player, int version){
		TableStore storeTable = get();
		if(storeTable != null){
			ClientDataSynMgr.synData(player, storeTable, synType, eSynOpType.UPDATE_SINGLE);
		}
	}
	
	public TableStore get(){
		return tableStoreDao.get(userId);
	}
	
	public void update(Player player, int type){
		tableStoreDao.update(userId);
		ConcurrentHashMap<Integer,StoreData> storeDataMap = get().getStoreDataMap();
		StoreData storeData = storeDataMap.get(type);
		ClientDataSynMgr.updateData(player, storeData, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void add(Player player, int type){
		tableStoreDao.update(userId);
		ConcurrentHashMap<Integer,StoreData> storeDataMap = get().getStoreDataMap();
		StoreData storeData = storeDataMap.get(type);
		ClientDataSynMgr.updateData(player, storeData, synType, eSynOpType.ADD_SINGLE);
	}
	
	public void remove(Player player, StoreData storeData){
		tableStoreDao.update(userId);
		ClientDataSynMgr.updateData(player, storeData, synType, eSynOpType.REMOVE_SINGLE);
	}
	
	public void flush(){
	}

}
