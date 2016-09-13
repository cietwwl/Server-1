package com.playerdata.activity.rankType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.rankType.ActivityRankTypeEnum;
import com.playerdata.activity.rankType.ActivityRankTypeHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;

public class ActivityRankTypeItemHolder{
	
	private static ActivityRankTypeItemHolder instance = new ActivityRankTypeItemHolder();
	
	public static ActivityRankTypeItemHolder getInstance(){
		return instance;
	}	

	public List<ActivityRankTypeItem> getItemList(String userId)	
	{		
		List<ActivityRankTypeItem> itemList = new ArrayList<ActivityRankTypeItem>();
		Enumeration<ActivityRankTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityRankTypeItem item = (ActivityRankTypeItem) mapEnum.nextElement();
			//不需要和客户端通信syn所以不需要对老数据过滤
			itemList.add(item);
		}		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityRankTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
	}
	
	public ActivityRankTypeItem getItem(String userId, ActivityRankTypeEnum typeEnum){		
		String itemId = ActivityRankTypeHelper.getItemId(userId, typeEnum);
		return getItemStore(userId).getItem(itemId);
	}
	
	
	public boolean addItem(Player player, ActivityRankTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		return addSuccess;
	}

	
	public MapItemStore<ActivityRankTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityRankTypeItem> cache = MapItemStoreFactory.getActivityRankTypeItemCache();
		return cache.getMapItemStore(userId, ActivityRankTypeItem.class);
	}

	public boolean addItemList(Player player, List<ActivityRankTypeItem> addItemList) {
		try {
			boolean addSuccess = getItemStore(player.getUserId()).addItem(
					addItemList);
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			// handle..
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	
}
