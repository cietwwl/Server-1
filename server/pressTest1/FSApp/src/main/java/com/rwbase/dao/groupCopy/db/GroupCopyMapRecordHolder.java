package com.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupCopyMapRecordHolder{
	
	
	final private String goupId;
	final private MapItemStore<GroupCopyMapRecord> itemStore;
	final private eSynType synType = eSynType.GroupCopyMap;
	
	public GroupCopyMapRecordHolder(String groupIdP) {
		goupId = groupIdP;
		itemStore = new MapItemStore<GroupCopyMapRecord>("groupId", goupId, GroupCopyMapRecord.class);
	}
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<GroupCopyMapRecord> getItemList()	
	{
		
		List<GroupCopyMapRecord> itemList = new ArrayList<GroupCopyMapRecord>();
		Enumeration<GroupCopyMapRecord> mapEnum = itemStore.getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupCopyMapRecord item = (GroupCopyMapRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, GroupCopyMapRecord item){
		itemStore.updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public GroupCopyMapRecord getItem(String itemId){
		return itemStore.getItem(itemId);
	}
	
	public boolean removeItem(Player player, GroupCopyMapRecord item){
		
		boolean success = itemStore.removeItem(item.getId());
		if(success){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
		}
		return success;
	}
	
	public boolean addItem(Player player, GroupCopyMapRecord item){
	
		boolean addSuccess = itemStore.addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public void synAllData(Player player, int version){
		List<GroupCopyMapRecord> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	public void flush(){
		itemStore.flush();
	}
	
}
