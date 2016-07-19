package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.groupCopy.bm.groupCopy.GroupCopyLevelBL;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.monster.cfg.CopyMonsterCfg;
import com.monster.cfg.CopyMonsterCfgDao;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupCopyLevelRecordHolder{
	
	
	final private String groupId;
	final private eSynType synType = eSynType.GROUP_COPY_LEVEL;
	
	final private AtomicInteger dataVersion = new AtomicInteger(0);
	
	public GroupCopyLevelRecordHolder(String groupIdP) {
		groupId = groupIdP;
		checkAndInitData();
	}
	
	/**
	 * 检查副本配置并初始化
	 */
	public void checkAndInitData(){
		List<GroupCopyLevelCfg> list = GroupCopyLevelCfgDao.getInstance().getAllCfg();
		GroupCopyLevelRecord record;
		for (GroupCopyLevelCfg cfg : list) {
			record = getByLevel(cfg.getId());
			if(record == null){
				record = createLevelRecord(cfg.getId());
				getItemStore().addItem(record);
			}
			if(record.getProgress() == null){
				record.setProgress(GroupCopyLevelBL.createProgress(cfg.getId()));
				getItemStore().updateItem(record);
			}
			
		}
	}
	
	private String getRecordID(String levelID){
		return groupId+"_"+levelID;
	}
	
	private GroupCopyLevelRecord createLevelRecord(String level) {
		GroupCopyLevelRecord lvData = null;
		try {
			lvData = new GroupCopyLevelRecord();
			lvData.setId(getRecordID(level));
			lvData.setLevelID(level);
			lvData.setGroupId(groupId);
			//这里还要初始化一下怪物信息
			GroupCopyProgress p = GroupCopyLevelBL.createProgress(level);
			
			lvData.setProgress(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lvData;
	}
	
	
	
	/*
	 * 获取帮派副本关卡数据
	 */
	public List<GroupCopyLevelRecord> getItemList()	{
		List<GroupCopyLevelRecord> itemList = new ArrayList<GroupCopyLevelRecord>();
		
		List<GroupCopyLevelCfg> allCfg = GroupCopyLevelCfgDao.getInstance().getAllCfg();
		GroupCopyLevelRecord item;
		for (GroupCopyLevelCfg cfg : allCfg) {
			item = getItemStore().getItem(getRecordID(cfg.getId()));
			if(item != null){
				itemList.add(item);
			}
		}
		
		return itemList;
	}
	
	/**
	 * 更新帮派副本
	 * @param player
	 * @param item
	 * @return
	 */
	public boolean updateItem(Player player, GroupCopyLevelRecord item){
		boolean success = getItemStore().updateItem(item);
		if(success){
			updateVersion();
			ClientDataSynMgr.synData(player, item, synType, eSynOpType.UPDATE_SINGLE, dataVersion.get());
		}
		return success;
	}
	
	
	
	
	public GroupCopyLevelRecord getByLevel(String level){
		return getItemStore().getItem(getRecordID(level));
	}
	
	public void synSingleData(Player player, String level){
		GroupCopyLevelRecord groupRecord = getByLevel(level);
		ClientDataSynMgr.synData(player, groupRecord, synType, eSynOpType.UPDATE_SINGLE, dataVersion.get());
	}
	
	public void synAllData(Player player, int version){
		
		List<GroupCopyLevelRecord> groupRecordList = getItemList();
		ClientDataSynMgr.synDataList(player, groupRecordList, synType, eSynOpType.UPDATE_LIST,dataVersion.get());
	}
	
	private void updateVersion(){
		dataVersion.incrementAndGet();
	}

	
	
	private MapItemStore<GroupCopyLevelRecord> getItemStore(){
		MapItemStoreCache<GroupCopyLevelRecord> itemStoreCache = MapItemStoreFactory.getGroupCopyLevelRecordCache();
		return itemStoreCache.getMapItemStore(groupId, GroupCopyLevelRecord.class);
	}

	public void resetLevelData(Player player,Set<String> set) {
		
		List<GroupCopyLevelRecord> list = new ArrayList<GroupCopyLevelRecord>();
		GroupCopyLevelRecord record;
		for (String id : set) {
			record = getByLevel(id);
			record.resetLevelData();
			getItemStore().updateItem(record);
			list.add(record);
		}
		updateVersion();
		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST, dataVersion.get());
	}
	
}
