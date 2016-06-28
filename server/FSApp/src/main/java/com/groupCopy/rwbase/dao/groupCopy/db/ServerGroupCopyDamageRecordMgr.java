package com.groupCopy.rwbase.dao.groupCopy.db;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;

public class ServerGroupCopyDamageRecordMgr {
	private final static ServerGroupCopyDamageRecordMgr instance = new ServerGroupCopyDamageRecordMgr();
	
	private final String groupId = "server_group_copy_record";
	
	
	public static ServerGroupCopyDamageRecordMgr getInstance(){
		return instance;
	}
	
	private MapItemStore<ServerGroupCopyDamageRecord> getItemStore(){
		MapItemStoreCache<ServerGroupCopyDamageRecord> cache = MapItemStoreFactory.getServerGroupCopyDamageRecordCache();
		return cache.getMapItemStore(groupId, ServerGroupCopyDamageRecord.class);
	}

	public ServerGroupCopyDamageRecord getItem(String id){
		return getItemStore().getItem(id);
	}
	
	private boolean addItem(ServerGroupCopyDamageRecord item){
		return getItemStore().addItem(item);
	}
	
	public synchronized void checkDamageRank(String levelId,
			GroupCopyArmyDamageInfo damageInfo) {
		ServerGroupCopyDamageRecord record = getItem(levelId);
		if(record == null){
			record = new ServerGroupCopyDamageRecord(groupId, levelId);
			addItem(record);
		}
		record.checkOrAddRecord(damageInfo);
		
	}

}
