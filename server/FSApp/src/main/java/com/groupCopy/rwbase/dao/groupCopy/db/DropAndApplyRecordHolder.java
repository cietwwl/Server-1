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

public class DropAndApplyRecordHolder {
	
	private final String groupId;
	private final eSynType synType = eSynType.GROUP_ITEM_DROP_APPLY;
	private final AtomicInteger dataVersion = new AtomicInteger(1);

	public DropAndApplyRecordHolder(String groupId) {
		this.groupId = groupId;
	}

	
	public List<CopyItemDropAndApplyRecord> getItemList()	
	{
		
		List<CopyItemDropAndApplyRecord> itemList = new ArrayList<CopyItemDropAndApplyRecord>();
		Enumeration<CopyItemDropAndApplyRecord> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			CopyItemDropAndApplyRecord item = (CopyItemDropAndApplyRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, CopyItemDropAndApplyRecord item){
		getItemStore().updateItem(item);
		updateVersion();
	}
	
	public CopyItemDropAndApplyRecord getItem(String itemId){
		return getItemStore().getItem(itemId);
	}
	
	
	public boolean addItem(Player player, CopyItemDropAndApplyRecord item){
	
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			updateVersion();
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public void synAllData(Player player, int version){
		List<CopyItemDropAndApplyRecord> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	
	private MapItemStore<CopyItemDropAndApplyRecord> getItemStore(){
		MapItemStoreCache<CopyItemDropAndApplyRecord> cache = MapItemStoreFactory.getItemDropAndApplyRecordCache();
		return cache.getMapItemStore(groupId, CopyItemDropAndApplyRecord.class);
	}
	
	private void updateVersion(){
		dataVersion.incrementAndGet();
	}
	
}
