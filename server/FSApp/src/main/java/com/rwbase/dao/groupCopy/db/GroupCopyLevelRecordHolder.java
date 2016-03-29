package com.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public GroupCopyLevelRecord getItem(String itemId){
		return itemStore.getItem(itemId);
	}
	
	public boolean removeItem(Player player, GroupCopyLevelRecord item){
		
		boolean success = itemStore.removeItem(item.getId());
		if(success){
			update();
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
		}
		return success;
	}
	
	public boolean addItem(Player player, GroupCopyLevelRecord item){
	
		boolean addSuccess = itemStore.addItem(item);
		if(addSuccess){
			update();
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public void synAllData(Player player, int version){
		if(dataVersion.get() == version){
			return;
		}
		List<GroupCopyLevelRecord> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}
	
	public void update(){
		dataVersion.incrementAndGet();
		updateFlag.set(true);
	}

	
	public void flush(){
		itemStore.flush();
	}
	
}
