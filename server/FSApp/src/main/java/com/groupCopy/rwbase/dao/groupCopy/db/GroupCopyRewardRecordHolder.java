package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupCopyRewardRecordHolder{
	
	
	
	final private String groupId;
	final private eSynType synType = eSynType.GroupCopyReward;
	
	public GroupCopyRewardRecordHolder(String groupIdP) {
		groupId = groupIdP;
	}
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<GroupCopyRewardRecord> getItemList()	
	{
		
		List<GroupCopyRewardRecord> itemList = new ArrayList<GroupCopyRewardRecord>();
		Enumeration<GroupCopyRewardRecord> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupCopyRewardRecord item = (GroupCopyRewardRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, GroupCopyRewardRecord item){
		getItemStore().updateItem(item);
	}
	
	public GroupCopyRewardRecord getItem(String itemId){
		return getItemStore().getItem(itemId);
	}
	
	
	public boolean addItem(Player player, GroupCopyRewardRecord item){
	
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public void synAllData(Player player, int version){
		List<GroupCopyRewardRecord> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	
	private MapItemStore<GroupCopyRewardRecord> getItemStore(){
		MapItemStoreCache<GroupCopyRewardRecord> cache = MapItemStoreFactory.getGroupCopyRewardRecordCache();
		return cache.getMapItemStore(groupId, GroupCopyRewardRecord.class);
	}
}
