package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupCopyRewardRecordHolder{
	
	
	
	final private String goupId;
	final private MapItemStore<GroupCopyRewardRecord> itemStore;
	final private eSynType synType = eSynType.GroupCopyReward;
	
	public GroupCopyRewardRecordHolder(String groupIdP) {
		goupId = groupIdP;
		itemStore = new MapItemStore<GroupCopyRewardRecord>("groupId", goupId, GroupCopyRewardRecord.class);
	}
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<GroupCopyRewardRecord> getItemList()	
	{
		
		List<GroupCopyRewardRecord> itemList = new ArrayList<GroupCopyRewardRecord>();
		Enumeration<GroupCopyRewardRecord> mapEnum = itemStore.getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupCopyRewardRecord item = (GroupCopyRewardRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, GroupCopyRewardRecord item){
		itemStore.updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public GroupCopyRewardRecord getItem(String itemId){
		return itemStore.getItem(itemId);
	}
	
	public boolean removeItem(Player player, GroupCopyRewardRecord item){
		
		boolean success = itemStore.removeItem(item.getId());
		if(success){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
		}
		return success;
	}
	
	public boolean addItem(Player player, GroupCopyRewardRecord item){
	
		boolean addSuccess = itemStore.addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public void synAllData(Player player, int version){
		List<GroupCopyRewardRecord> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	public void flush(){
		itemStore.flush();
	}
	
}
