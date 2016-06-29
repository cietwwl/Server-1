package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.service.player.PlayerService;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.PlayerDataMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupCopyRewardDistRecordHolder{
	
	final private String groupId;
	final private eSynType synType = eSynType.GROUP_COPY_REWARD;
	
	public GroupCopyRewardDistRecordHolder(String groupIdP) {
		groupId = groupIdP;
		initData();
	}
	
	private void initData(){
		GroupCopyRewardDistRecord item = getItem(groupId);
		if(item == null){
			item = new GroupCopyRewardDistRecord();
			item.setGroupId(groupId);
			item.setId(groupId);
			getItemStore().addItem(item);
		}
		
	}
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<GroupCopyRewardDistRecord> getItemList()	
	{
		
		List<GroupCopyRewardDistRecord> itemList = new ArrayList<GroupCopyRewardDistRecord>();
		Enumeration<GroupCopyRewardDistRecord> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupCopyRewardDistRecord item = (GroupCopyRewardDistRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, GroupCopyRewardDistRecord item){
		getItemStore().updateItem(item);
	}
	
	public GroupCopyRewardDistRecord getItem(String itemId){
		return getItemStore().getItem(itemId);
	}
	
	
	public boolean addItem(Player player, GroupCopyRewardDistRecord item){
	
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public void synAllData(Player player, int version){
		List<GroupCopyRewardDistRecord> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	
	private MapItemStore<GroupCopyRewardDistRecord> getItemStore(){
		MapItemStoreCache<GroupCopyRewardDistRecord> cache = MapItemStoreFactory.getGroupCopyRewardRecordCache();
		return cache.getMapItemStore(groupId, GroupCopyRewardDistRecord.class);
	}
}
