package com.playerdata.activity.rankType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.rankType.ActivityRankTypeEnum;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;

public class ActivityRankTypeItemHolder{
	
	private static ActivityRankTypeItemHolder instance = new ActivityRankTypeItemHolder();
	
	public static ActivityRankTypeItemHolder getInstance(){
		return instance;
	}	

	public List<ActivityRankTypeItem> getItemList(String userId)	
	{		
		List<ActivityRankTypeItem> itemList = new ArrayList<ActivityRankTypeItem>();
		Enumeration<ActivityRankTypeItem> mapEnum = getItemStore(userId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityRankTypeItem item = (ActivityRankTypeItem) mapEnum.nextElement();
			//不需要和客户端通信syn所以不需要对老数据过滤
			itemList.add(item);
		}		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityRankTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
	}
	
	public ActivityRankTypeItem getItem(String userId, ActivityRankTypeEnum typeEnum){		
//		String itemId = ActivityRankTypeHelper.getItemId(userId, typeEnum);
		int id = Integer.parseInt(typeEnum.getCfgId());
		return getItemStore(userId).get(id);
	}
	
	
	public boolean addItem(Player player, ActivityRankTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		return addSuccess;
	}

	
	public RoleExtPropertyStore<ActivityRankTypeItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityRankTypeItem> cache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_RANK, ActivityRankTypeItem.class);
		try {
			return cache.getStore(userId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
