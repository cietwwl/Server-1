package com.rw.service.guide.datamodel;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.INotifyChange;
import com.rwbase.common.MapItemStoreFactory;

public class GiveItemHistoryHolder {
	private static GiveItemHistoryHolder instance;
	
	public static GiveItemHistoryHolder getInstance(){
		if (instance == null){
			instance = new GiveItemHistoryHolder();
		}
		return instance;
	}
	
	public boolean add(GiveItemHistory item,INotifyChange notifyProxy){
		if (item == null){
			GameLog.info("引导", "", "无效参数", null);
			return false;//illegal argument
		}
		MapItemStore<GiveItemHistory> cache = getCache(item.getUserId());
		if (cache.getItem(item.getStoreId()) != null){
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
		MapItemStore<GiveItemHistory> cache = getCache(item.getUserId());
		if (cache.getItem(item.getStoreId()) == null){
			GameLog.info("引导", item.getUserId(), "找不到记录:"+item.getId(), null);
			return false;//item exits
		}
		if (!cache.updateItem(item)){
			GameLog.info("引导", item.getUserId(), "缓存更新失败", null);
			return false;//failed to add item
		}
		if (notifyProxy!=null){
			notifyProxy.delayNotify();
		}
		return true;
	}
	
	private MapItemStore<GiveItemHistory> getCache(String userId){
		MapItemStoreCache<GiveItemHistory> cache = MapItemStoreFactory.getNewGuideGiveItemHistoryCache();
		return cache.getMapItemStore(userId, GiveItemHistory.class);
	}

	public GiveItemHistory getHistory(String userId, int actId) {
		MapItemStore<GiveItemHistory> cache = getCache(userId);
		GiveItemHistory history = cache.getItem(GiveItemHistory.Convert(userId, actId));
		return history;
	}

}
