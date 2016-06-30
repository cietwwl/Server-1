package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ServerGroupCopyDamageRecordMgr {
	private final static ServerGroupCopyDamageRecordMgr instance = new ServerGroupCopyDamageRecordMgr();
	
	private final String groupId = "server_group_copy_record";
	
	private final eSynType synType  = eSynType.GROUP_COPY_SERVER_RANK;
	private final AtomicInteger dataVersion = new AtomicInteger(0);
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
		dataVersion.incrementAndGet();
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
	
	public void synSingleData(Player player, int version, String levelID){
		//如果版本号不同才进行同步
		if(version == dataVersion.get() && version != 0){
			return;
		}
		ServerGroupCopyDamageRecord item = getItem(levelID);
		ClientDataSynMgr.synData(player, item, synType, eSynOpType.UPDATE_SINGLE, version);
	}
	
	public void synAllData(Player player, int version){
		//如果版本号不同才进行同步
		if(version == dataVersion.get() && version != 0){
			return;
		}
		List<ServerGroupCopyDamageRecord> itemList = getItemList();
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST, version);
	}

	private List<ServerGroupCopyDamageRecord> getItemList() {
		List<ServerGroupCopyDamageRecord> list = new ArrayList<ServerGroupCopyDamageRecord>();
		ServerGroupCopyDamageRecord item;
		List<GroupCopyLevelCfg> allCfg = GroupCopyLevelCfgDao.getInstance().getAllCfg();
		for (GroupCopyLevelCfg cfg : allCfg) {
			 item = getItem(cfg.getId());
			 if(item != null){
				 list.add(item);
			 }
		}
		return list;
	}

}
