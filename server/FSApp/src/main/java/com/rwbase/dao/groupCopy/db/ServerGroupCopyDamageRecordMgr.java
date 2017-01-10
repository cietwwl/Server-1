package com.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ServerGroupCopyDamageRecordMgr {
	private static ServerGroupCopyDamageRecordMgr instance = new ServerGroupCopyDamageRecordMgr();
	
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
	
	private boolean updateItem(ServerGroupCopyDamageRecord item){
		dataVersion.incrementAndGet();
		return getItemStore().updateItem(item);
	}
	
	/**
	 * @param levelId
	 * @param damageInfo
	 * @param player
	 * @param kill
	 */
	public synchronized void checkDamageRank(String levelId,
			GroupCopyArmyDamageInfo damageInfo, Player player, boolean kill) {
		ServerGroupCopyDamageRecord record = getItem(levelId);
		if(record == null){
			record = new ServerGroupCopyDamageRecord(groupId, levelId);
			addItem(record);
		}
		boolean suc = record.checkOrAddRecord(damageInfo, kill);
		if(suc){
			if(updateItem(record)){
				synSingleData(player, dataVersion.get(), levelId, false);//暂时不主动推送,
			}
		}
		
	}
	
	public void synSingleData(Player player, int version, String levelID, boolean compareVersion){
		//如果版本号不同才进行同步
		if(compareVersion && version == dataVersion.get() && version != 0){
			return;
		}
		ServerGroupCopyDamageRecord item = getItem(levelID);
		if(item != null){
			ClientDataSynMgr.synData(player, item, synType, eSynOpType.UPDATE_SINGLE, version);
		}
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
