package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupCopyMapRecordHolder{
	
	
	final private String groupId;
	final private eSynType synType = eSynType.GroupCopyMap;	
	
	final private AtomicInteger dataVersion = new AtomicInteger(1);
	
	
	public GroupCopyMapRecordHolder(String groupIdP) {
		groupId = groupIdP;
	}
	

	public List<GroupCopyMapRecord> getItemList()	
	{
		
		List<GroupCopyMapRecord> itemList = new ArrayList<GroupCopyMapRecord>();
		Enumeration<GroupCopyMapRecord> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupCopyMapRecord item = (GroupCopyMapRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem( GroupCopyMapRecord item ){
		getItemStore().updateItem(item);
		update();
//		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public GroupCopyMapRecord getItem(String itemId){
		return getItemStore().getItem(itemId);
	}
	
	
	public boolean addItem( GroupCopyMapRecord item ){
	
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			update();
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public void synAllData(Player player, int version){
		List<GroupCopyMapRecord> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	private void update(){
		dataVersion.incrementAndGet();
	}

	
	private MapItemStore<GroupCopyMapRecord> getItemStore(){
		MapItemStoreCache<GroupCopyMapRecord> itemStoreCache = MapItemStoreFactory.getGroupCopyMapRecordCache();
		return itemStoreCache.getMapItemStore(groupId, GroupCopyMapRecord.class);
	}
	
}
