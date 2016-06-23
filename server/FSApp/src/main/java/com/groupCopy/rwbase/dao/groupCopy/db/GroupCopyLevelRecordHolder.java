package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

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
	
	final private AtomicInteger dataVersion = new AtomicInteger(1);
	
	public GroupCopyLevelRecordHolder(String groupIdP) {
		groupId = groupIdP;
	}
	
	/*
	 * 获取帮派副本关卡数据
	 */
	public List<GroupCopyLevelRecord> getItemList()	{
		List<GroupCopyLevelRecord> itemList = new ArrayList<GroupCopyLevelRecord>();
		Enumeration<GroupCopyLevelRecord> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupCopyLevelRecord item = (GroupCopyLevelRecord) mapEnum.nextElement();
			itemList.add(item);
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
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		}
		return success;
	}
	
	public GroupCopyLevelRecord getItem(String itemId){
		return getItemStore().getItem(itemId);
	}	
	
	public boolean addItem(Player player, GroupCopyLevelRecord item){
	
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			updateVersion();
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public GroupCopyLevelRecord getByLevel(String level){
		GroupCopyLevelRecord target = null;
		for (GroupCopyLevelRecord item : getItemList()) {
			if(StringUtils.equals(item.getId() , level)){
				target = item;
			}
		}
		return target;
	}
	
	public void synSingleData(Player player, String level){
		GroupCopyLevelRecord groupRecord = getByLevel(level);
//		UserGroupCopyMapRecord userRecord = player.getUserGroupCopyRecordMgr().getByLevel(level);
//		GroupCopyLevelRecord4Client record4client = new GroupCopyLevelRecord4Client(groupRecord, userRecord);
		ClientDataSynMgr.updateData(player, groupRecord, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void synAllData(Player player, int version){
		int combineVersion = dataVersion.get()+player.getUserGroupCopyRecordMgr().getDataVersion();
		if(combineVersion == version){
			return;
		}
		List<GroupCopyLevelRecord> groupRecordList = getItemList();
//		List<UserGroupCopyMapRecord> userRecordList = player.getUserGroupCopyLevelRecordMgr().getUserMapRecordList();
//		List<GroupCopyLevelRecord4Client> record4ClientList = buildLevelRecord4ClientList(userRecordList, groupRecordList);
		ClientDataSynMgr.synDataList(player, groupRecordList, synType, eSynOpType.UPDATE_LIST,combineVersion);
	}
	
	@Deprecated
	private List<GroupCopyLevelRecord4Client> buildLevelRecord4ClientList(List<UserGroupCopyMapRecord> userRecordList, List<GroupCopyLevelRecord> groupRecordList){
		List<GroupCopyLevelRecord4Client> record4ClientList = new ArrayList<GroupCopyLevelRecord4Client>();
		
		Map<String,UserGroupCopyMapRecord> levelMap = toLevelMap(userRecordList);
		for (GroupCopyLevelRecord groupLevelRecordP : groupRecordList) {
			UserGroupCopyMapRecord userLevelRecord = levelMap.get(groupLevelRecordP.getId());
			GroupCopyLevelRecord4Client levelRecord4Client = new GroupCopyLevelRecord4Client(groupLevelRecordP, userLevelRecord);
			record4ClientList.add(levelRecord4Client);
		}
		return record4ClientList;
		
	}
	
	private Map<String,UserGroupCopyMapRecord> toLevelMap(List<UserGroupCopyMapRecord> userRecordList){
		Map<String,UserGroupCopyMapRecord> levelMap = new HashMap<String,UserGroupCopyMapRecord>();
		for (UserGroupCopyMapRecord recordItem : userRecordList) {
			levelMap.put(recordItem.getId(), recordItem);
		}
		return levelMap;
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
			list.add(record);
		}
		updateVersion();
		
		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
	}
	
}
