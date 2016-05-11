package com.playerdata.activity.rankType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.rankType.ActivityRankTypeEnum;
import com.playerdata.activity.rankType.ActivityRankTypeHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityRankTypeItemHolder{
	
	private static ActivityRankTypeItemHolder instance = new ActivityRankTypeItemHolder();
	
	public static ActivityRankTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityRankType;
	

	public List<ActivityRankTypeItem> getItemList(String userId)	
	{
		
		List<ActivityRankTypeItem> itemList = new ArrayList<ActivityRankTypeItem>();
		Enumeration<ActivityRankTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityRankTypeItem item = (ActivityRankTypeItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityRankTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityRankTypeItem getItem(String userId, ActivityRankTypeEnum typeEnum){		
		String itemId = ActivityRankTypeHelper.getItemId(userId, typeEnum);
		return getItemStore(userId).getItem(itemId);
	}
	
	
	public boolean addItem(Player player, ActivityRankTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean removeItem(Player player,ActivityRankTypeEnum type){
		
		String uidAndId = ActivityRankTypeHelper.getItemId(player.getUserId(), type);
		boolean addSuccess = getItemStore(player.getUserId()).removeItem(uidAndId);
		return addSuccess;
	}
	
	public void synAllData(Player player){
		List<ActivityRankTypeItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<ActivityRankTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityRankTypeItem> cache = MapItemStoreFactory.getActivityRankTypeItemCache();
		return cache.getMapItemStore(userId, ActivityRankTypeItem.class);
	}
	
}
