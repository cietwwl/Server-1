package com.playerdata.activity.dateType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.dateType.ActivityDateTypeEnum;
import com.playerdata.activity.dateType.ActivityDateTypeHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityDateTypeItemHolder{
	
	private static ActivityDateTypeItemHolder instance = new ActivityDateTypeItemHolder();
	
	public static ActivityDateTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDateType;
	

	public List<ActivityDateTypeItem> getItemList(String userId)	
	{
		
		List<ActivityDateTypeItem> itemList = new ArrayList<ActivityDateTypeItem>();
		Enumeration<ActivityDateTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityDateTypeItem item = (ActivityDateTypeItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityDateTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityDateTypeItem getItem(String userId, ActivityDateTypeEnum typeEnum){		
		String itemId = ActivityDateTypeHelper.getItemId(userId, typeEnum);
		return getItemStore(userId).getItem(itemId);
	}
	
	
	public boolean addItem(Player player, ActivityDateTypeItem item){
	
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
		List<ActivityDateTypeItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<ActivityDateTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityDateTypeItem> cache = MapItemStoreFactory.getActivityDateTypeItemCache();
		return cache.getMapItemStore(userId, ActivityDateTypeItem.class);
	}
	
}
