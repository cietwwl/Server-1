package com.playerdata.fixEquip.exp.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.dateType.ActivityDateTypeEnum;
import com.playerdata.activity.dateType.ActivityDateTypeHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.fixEquip.FixEquipHelper;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FixExpEquipDataItemHolder{
	
	private static FixExpEquipDataItemHolder instance = new FixExpEquipDataItemHolder();
	
	public static FixExpEquipDataItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDateType;
	

	public List<FixExpEquipDataItem> getItemList(String heroId)	
	{
		
		List<FixExpEquipDataItem> itemList = new ArrayList<FixExpEquipDataItem>();
		Enumeration<FixExpEquipDataItem> mapEnum = getItemStore(heroId).getEnum();
		while (mapEnum.hasMoreElements()) {
			FixExpEquipDataItem item = (FixExpEquipDataItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, FixExpEquipDataItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public FixExpEquipDataItem getItem(String ownerId, String cfgId){		
		String itemId = FixEquipHelper.getExpItemId(ownerId, cfgId);
		return getItemStore(ownerId).getItem(itemId);
	}
	
	
	public boolean addItem(Player player, FixExpEquipDataItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean removeItem(Player player,ActivityDateTypeEnum type){		
		String uidAndId = ActivityDateTypeHelper.getItemId(player.getUserId(), type);
		boolean addSuccess = getItemStore(player.getUserId()).removeItem(uidAndId);
		return addSuccess;
	}
	
	public void synAllData(Player player){
		List<FixExpEquipDataItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<FixExpEquipDataItem> getItemStore(String userId) {
		MapItemStoreCache<FixExpEquipDataItem> cache = MapItemStoreFactory.getFixExpEquipDataItemCache();
		return cache.getMapItemStore(userId, FixExpEquipDataItem.class);
	}
	
}
