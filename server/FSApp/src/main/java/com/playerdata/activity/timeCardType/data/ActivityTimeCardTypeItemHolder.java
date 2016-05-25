package com.playerdata.activity.timeCardType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeEnum;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityTimeCardTypeItemHolder{
	
	private static ActivityTimeCardTypeItemHolder instance = new ActivityTimeCardTypeItemHolder();
	
	public static ActivityTimeCardTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityTimeCardType;
	

	public List<ActivityTimeCardTypeItem> getItemList(String userId)	
	{
		
		List<ActivityTimeCardTypeItem> itemList = new ArrayList<ActivityTimeCardTypeItem>();
		Enumeration<ActivityTimeCardTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityTimeCardTypeItem item = (ActivityTimeCardTypeItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityTimeCardTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityTimeCardTypeItem getItem(String userId, ActivityTimeCardTypeEnum typeEnum){		
		String itemId = ActivityTimeCardTypeHelper.getItemId(userId, typeEnum);
		return getItemStore(userId).getItem(itemId);
	}
	
	
	public boolean addItem(Player player, ActivityTimeCardTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	
	public void synAllData(Player player){
		List<ActivityTimeCardTypeItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<ActivityTimeCardTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityTimeCardTypeItem> cache = MapItemStoreFactory.getActivityTimeCardTypeItemCache();
		return cache.getMapItemStore(userId, ActivityTimeCardTypeItem.class);
	}
	
}
