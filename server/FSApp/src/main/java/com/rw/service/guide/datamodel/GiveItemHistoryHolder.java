package com.rw.service.guide.datamodel;

import com.log.GameLog;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rwbase.common.INotifyChange;

public class GiveItemHistoryHolder {
	private static GiveItemHistoryHolder instance = new GiveItemHistoryHolder();
	
	public static GiveItemHistoryHolder getInstance(){
		return instance;
	}
	
	public boolean add(GiveItemHistory item,INotifyChange notifyProxy){
		if (item == null){
			GameLog.info("引导", "", "无效参数", null);
			return false;//illegal argument
		}
		RoleExtPropertyStore<GiveItemHistory> cache = getCache(item.getUserId());
		if (cache.get(item.getId()) != null){
			GameLog.info("引导", item.getUserId(), "已经存在无法添加", null);
			return false;//item exits
		}
		if (!cache.addItem(item)){
			GameLog.info("引导", item.getUserId(), "缓存保存失败", null);
			return false;//failed to add item
		}
		if (notifyProxy!=null){
			notifyProxy.delayNotify();
		}
		return true;
	}
	
	public boolean update(GiveItemHistory item,INotifyChange notifyProxy){
		if (item == null){
			GameLog.info("引导", "", "无效参数", null);
			return false;//illegal argument
		}
		RoleExtPropertyStore<GiveItemHistory> cache = getCache(item.getUserId());
		if (cache.get(item.getId()) == null){
			GameLog.info("引导", item.getUserId(), "找不到记录:"+item.getId(), null);
			return false;//item exits
		}
		if (!cache.update(item.getId())){
			GameLog.info("引导", item.getUserId(), "缓存更新失败", null);
			return false;//failed to add item
		}
		if (notifyProxy!=null){
			notifyProxy.delayNotify();
		}
		return true;
	}
	
	private RoleExtPropertyStore<GiveItemHistory> getCache(String userId){
		RoleExtPropertyStoreCache<GiveItemHistory> heroExtCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.GIVEITEM_HISTORY, GiveItemHistory.class);
		RoleExtPropertyStore<GiveItemHistory> store = null;
		try {
			
			store = heroExtCache.getStore(userId);
		} catch (Throwable e) {
			GameLog.error("fashion", "userId:"+userId, "can not get PlayerExtPropertyStore.", e);
		}
		return store;
	}

	public GiveItemHistory getHistory(String userId, int actId) {
		RoleExtPropertyStore<GiveItemHistory> cache = getCache(userId);
		GiveItemHistory history = cache.get(actId);
		return history;
	}

}
