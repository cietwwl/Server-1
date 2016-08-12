package com.playerdata.activity.dailyDiscountType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeEnum;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityDailyDiscountTypeItemHolder{
	
	private static ActivityDailyDiscountTypeItemHolder instance = new ActivityDailyDiscountTypeItemHolder();
	
	public static ActivityDailyDiscountTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDailyDiscountType;
	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityDailyDiscountTypeItem> getItemList(String userId)	
	{
		
		List<ActivityDailyDiscountTypeItem> itemList = new ArrayList<ActivityDailyDiscountTypeItem>();
		Enumeration<ActivityDailyDiscountTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityDailyDiscountTypeItem item = (ActivityDailyDiscountTypeItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityDailyDiscountTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityDailyDiscountTypeItem getItem(String userId,ActivityDailyDiscountTypeEnum countTypeEnum){
		String itemId=ActivityDailyDiscountTypeHelper.getItemId(userId, countTypeEnum);
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
	
	public boolean addItem(Player player, ActivityDailyDiscountTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<ActivityDailyDiscountTypeItem> itemList){
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
	
//	public boolean removeitem(Player player,ActivityCountTypeEnum type){
//		
//		String uidAndId = ActivityCountTypeHelper.getItemId(player.getUserId(), type);
//		boolean addSuccess = getItemStore(player.getUserId()).removeItem(uidAndId);
//		return addSuccess;
//	}
//	
	public void synAllData(Player player){
		List<ActivityDailyDiscountTypeItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<ActivityDailyDiscountTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityDailyDiscountTypeItem> cache = MapItemStoreFactory.getActivityDailyDiscountTypeItemCache();
		MapItemStore<ActivityDailyDiscountTypeItem> map = cache.getMapItemStore(userId, ActivityDailyDiscountTypeItem.class);
		return map;
	}
	
}
