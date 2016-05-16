package com.playerdata.fixEquip.norm.data;

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

public class FixNormEquipDataItemHolder{
	
	private static FixNormEquipDataItemHolder instance = new FixNormEquipDataItemHolder();
	
	public static FixNormEquipDataItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDateType;
	

	public List<FixNormEquipDataItem> getItemList(String heroId)	
	{
		
		List<FixNormEquipDataItem> itemList = new ArrayList<FixNormEquipDataItem>();
		Enumeration<FixNormEquipDataItem> mapEnum = getItemStore(heroId).getEnum();
		while (mapEnum.hasMoreElements()) {
			FixNormEquipDataItem item = (FixNormEquipDataItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, FixNormEquipDataItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public FixNormEquipDataItem getItem(String ownerId, String cfgId){		
		String itemId = FixEquipHelper.getExpItemId(ownerId, cfgId);
		return getItemStore(ownerId).getItem(itemId);
	}
	
	
	public boolean addItem(Player player, FixNormEquipDataItem item){
	
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
		List<FixNormEquipDataItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<FixNormEquipDataItem> getItemStore(String userId) {
		MapItemStoreCache<FixNormEquipDataItem> cache = MapItemStoreFactory.getFixNormEquipDataItemCache();
		return cache.getMapItemStore(userId, FixNormEquipDataItem.class);
	}
	
}
