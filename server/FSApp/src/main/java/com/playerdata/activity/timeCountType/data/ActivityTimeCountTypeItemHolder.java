package com.playerdata.activity.timeCountType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeEnum;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityTimeCountTypeItemHolder{
	
	private static ActivityTimeCountTypeItemHolder instance = new ActivityTimeCountTypeItemHolder();
	
	public static ActivityTimeCountTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityTimeCountType;	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityTimeCountTypeItem> getItemList(String userId)	
	{
		
		List<ActivityTimeCountTypeItem> itemList = new ArrayList<ActivityTimeCountTypeItem>();
		Enumeration<ActivityTimeCountTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityTimeCountTypeItem item = (ActivityTimeCountTypeItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityTimeCountTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityTimeCountTypeItem getItem(String userId, ActivityTimeCountTypeEnum countTypeEnum){		
		String itemId = ActivityTimeCountTypeHelper.getItemId(userId, countTypeEnum);
		return getItemStore(userId).getItem(itemId);
	}
	
	public boolean addItem(Player player, ActivityTimeCountTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<ActivityTimeCountTypeItem> itemList){
		try {
			boolean addSuccess = getItemStore(player.getUserId()).addItem(itemList);
			if(addSuccess){
				ClientDataSynMgr.updateDataList(player, getItemList(player.getUserId()), synType, eSynOpType.UPDATE_LIST);
			}
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			//handle..
			e.printStackTrace();
			return false;
		}
	}
	
//	
	public void synAllData(Player player){
		List<ActivityTimeCountTypeItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<ActivityTimeCountTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityTimeCountTypeItem> cache = MapItemStoreFactory.getActivityTimeCountTypeItemCache();
		return cache.getMapItemStore(userId, ActivityTimeCountTypeItem.class);
	}
	
}
