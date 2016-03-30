package com.rwbase.dao.fashion;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;

/**
 * 缓存数据以用户ID作为索引
 */
public class FashionBeingUsedHolder {
	final private String userId;

	public FashionBeingUsedHolder(String id) {
		userId = id;
	}
	
	public FashionBeingUsed get(String userId){
		return getCache().getItem(userId);
	}

	private MapItemStore<FashionBeingUsed> getCache(){
		MapItemStoreCache<FashionBeingUsed> cache = MapItemStoreFactory.getFashionUsedCache();
		return cache.getMapItemStore(userId, FashionBeingUsed.class);
	}

	public boolean update(FashionBeingUsed fashionUsed,boolean notifyAll) {
		boolean updateResult = getCache().updateItem(fashionUsed);
		if (!updateResult){
			GameLog.error("时装", fashionUsed.getUserId(), "更新FashionBeingUsed失败，ID="+fashionUsed.getId());
		}
		
		if (notifyAll){
			//TODO 发送全局通知？
		}
		return updateResult;
	}

	public FashionBeingUsed newFashion(String uId) {
		FashionBeingUsed used = new FashionBeingUsed();
		used.setUserId(uId);
		boolean addresult = getCache().addItem(used);
		if (addresult){
			//TODO 记录新增对象，准备同步数据
		}else{
			GameLog.error("时装", userId, "添加FashionBeingUsed失败");
		}
		return used;
	}

}
