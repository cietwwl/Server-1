package com.playerdata.activity.countType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityCountTypeItemHolder{
	
	private static ActivityCountTypeItemHolder instance = new ActivityCountTypeItemHolder();
	
	public static ActivityCountTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityCountType;
	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityCountTypeItem> getItemList(String userId)	
	{
		
		List<ActivityCountTypeItem> itemList = new ArrayList<ActivityCountTypeItem>();
		Enumeration<ActivityCountTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityCountTypeItem item = (ActivityCountTypeItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityCountTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityCountTypeItem getItem(String userId, ActivityCountTypeEnum countTypeEnum){		
		String itemId = ActivityCountTypeHelper.getItemId(userId, countTypeEnum);
		return getItemStore(userId).getItem(itemId);
	}
	
//	public boolean removeItem(Player player, ActivityCountTypeItem item){
//		
//		boolean success = getItemStore(player.getUserId()).removeItem(item.getId());
//		if(success){
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
//		}
//		return success;
//	}
	
	public boolean addItem(Player player, ActivityCountTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
//	public boolean removeitem(Player player,ActivityCountTypeEnum type){
//		
//		String uidAndId = ActivityCountTypeHelper.getItemId(player.getUserId(), type);
//		boolean addSuccess = getItemStore(player.getUserId()).removeItem(uidAndId);
//		return addSuccess;
//	}
//	
	public void synAllData(Player player){
		List<ActivityCountTypeItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<ActivityCountTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityCountTypeItem> cache = MapItemStoreFactory.getActivityCountTypeItemCache();
		return cache.getMapItemStore(userId, ActivityCountTypeItem.class);
	}
	
}
