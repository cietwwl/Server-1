package com.rw.routerServer.giftManger;

import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;

public class RouterGiftDataHolder {
	
	private static RouterGiftDataHolder instance = new RouterGiftDataHolder();
	
	protected RouterGiftDataHolder(){	}
	
	public static RouterGiftDataHolder getInstance(){
		return instance;
	}
	
	public void addItem(String userId, RouterGiftDataItem item){
		getItemStore(userId).addItem(item);
	}
	
	public void updateItem(String userId, RouterGiftDataItem item){
		getItemStore(userId).update(item.getId());
	}
	
	public RouterGiftDataItem getItem(String userId, Integer giftId){		
		return getItemStore(userId).get(giftId);
	}
	
	public RoleExtPropertyStore<RouterGiftDataItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<RouterGiftDataItem> cache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ROUTER_GIFT, RouterGiftDataItem.class);
		try {
			return cache.getStore(userId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
