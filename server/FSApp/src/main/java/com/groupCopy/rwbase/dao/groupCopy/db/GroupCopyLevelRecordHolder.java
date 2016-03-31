package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupCopyLevelRecordHolder{
	
	
	final private String goupId;
	final private MapItemStore<GroupCopyLevelRecord> itemStore;
	final private eSynType synType = eSynType.GroupCopyLevel;
	
	final private AtomicInteger dataVersion = new AtomicInteger(1);
	
	final private AtomicBoolean updateFlag = new AtomicBoolean(false);
	
	public GroupCopyLevelRecordHolder(String groupIdP) {
		goupId = groupIdP;
		itemStore = new MapItemStore<GroupCopyLevelRecord>("groupId", goupId, GroupCopyLevelRecord.class);
	}
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<GroupCopyLevelRecord> getItemList()	
	{
		
		List<GroupCopyLevelRecord> itemList = new ArrayList<GroupCopyLevelRecord>();
		Enumeration<GroupCopyLevelRecord> mapEnum = itemStore.getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupCopyLevelRecord item = (GroupCopyLevelRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, GroupCopyLevelRecord item){
		itemStore.updateItem(item);
		update();
//		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public GroupCopyLevelRecord getItem(String itemId){
		return itemStore.getItem(itemId);
	}	
	
	public boolean addItem(Player player, GroupCopyLevelRecord item){
	
		boolean addSuccess = itemStore.addItem(item);
		if(addSuccess){
			update();
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public GroupCopyLevelRecord getByLevel(String level){
		GroupCopyLevelRecord target = null;
		for (GroupCopyLevelRecord item : getItemList()) {
			if(StringUtils.equals(item.getLevel() , level)){
				target = item;
			}
		}
		return target;
	}
	
	public void synSingleData(Player player, String level){
		GroupCopyLevelRecord groupRecord = getByLevel(level);
		UserGroupCopyLevelRecord userRecord = player.getUserGroupCopyLevelRecordMgr().getByLevel(level);
		GroupCopyLevelRecord4Client record4client = new GroupCopyLevelRecord4Client(groupRecord, userRecord);
		ClientDataSynMgr.updateData(player, record4client, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void synAllData(Player player, int version){
		if(dataVersion.get() == version){
			return;
		}
		List<GroupCopyLevelRecord> groupRecordList = getItemList();
		List<UserGroupCopyLevelRecord> userRecordList = player.getUserGroupCopyLevelRecordMgr().getRecordList();
		List<GroupCopyLevelRecord4Client> record4ClientList = buildLevelRecord4ClientList(userRecordList, groupRecordList);
		ClientDataSynMgr.synDataList(player, record4ClientList, synType, eSynOpType.UPDATE_LIST);
	}
	
	private List<GroupCopyLevelRecord4Client> buildLevelRecord4ClientList(List<UserGroupCopyLevelRecord> userRecordList, List<GroupCopyLevelRecord> groupRecordList){
		List<GroupCopyLevelRecord4Client> record4ClientList = new ArrayList<GroupCopyLevelRecord4Client>();
		
		Map<String,UserGroupCopyLevelRecord> levelMap = toLevelMap(userRecordList);
		for (GroupCopyLevelRecord groupLevelRecordP : groupRecordList) {
			UserGroupCopyLevelRecord userLevelRecord = levelMap.get(groupLevelRecordP.getLevel());
			GroupCopyLevelRecord4Client levelRecord4Client = new GroupCopyLevelRecord4Client(groupLevelRecordP, userLevelRecord);
			record4ClientList.add(levelRecord4Client);
		}
		return record4ClientList;
		
	}
	
	private Map<String,UserGroupCopyLevelRecord> toLevelMap(List<UserGroupCopyLevelRecord> userRecordList){
		Map<String,UserGroupCopyLevelRecord> levelMap = new HashMap<String,UserGroupCopyLevelRecord>();
		for (UserGroupCopyLevelRecord recordItem : userRecordList) {
			levelMap.put(recordItem.getLevel(), recordItem);
		}
		return levelMap;
	}
	
	
	public void update(){
		dataVersion.incrementAndGet();
		updateFlag.set(true);
	}

	
	public void flush(){
		itemStore.flush();
	}
	
}
