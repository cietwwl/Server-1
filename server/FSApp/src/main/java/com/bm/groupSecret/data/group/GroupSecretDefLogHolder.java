package com.bm.groupSecret.data.group;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;

public class GroupSecretDefLogHolder{
	
	private static GroupSecretDefLogHolder instance = new GroupSecretDefLogHolder();
	
	public static GroupSecretDefLogHolder getInstance(){
		return instance;
	}

	/*
	 * 获取用户已经拥有的时装
	 */
	public List<GroupSecretDefLog> getItemList(String secretId)	
	{
		
		List<GroupSecretDefLog> itemList = new ArrayList<GroupSecretDefLog>();
		Enumeration<GroupSecretDefLog> mapEnum = getItemStore(secretId).getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupSecretDefLog item = (GroupSecretDefLog) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(String secretId, GroupSecretDefLog item){
		getItemStore(secretId).updateItem(item);		
	}
	
	public boolean removeItem(String secretId, GroupSecretDefLog item){		
		boolean success = getItemStore(secretId).removeItem(item.getId());
		return success;
	}

	
	private MapItemStore<GroupSecretDefLog> getItemStore(String userId) {
		MapItemStoreCache<GroupSecretDefLog> cache = MapItemStoreFactory.getGroupSecretDefLogCache();
		return cache.getMapItemStore(userId, GroupSecretDefLog.class);
	}
	
}
