package com.rwbase.dao.fashion;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.NotifyChangeCallBack;

/**
 * 缓存数据以用户ID作为索引
 */
public class FashionBeingUsedHolder extends NotifyChangeCallBack{
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
			// 发送通知，更新战斗加成属性变更
			notifyChange();
		}
		return updateResult;
	}

	/**
	 * 如果新增了一个时装记录，调用着负责向客户端发送同步数据
	 * @param uId
	 * @return
	 */
	public FashionBeingUsed newFashion(String uId) {
		MapItemStore<FashionBeingUsed> cache = getCache();
		FashionBeingUsed used = cache.getItem(uId);
		if (used == null){
			used = new FashionBeingUsed();
			used.setUserId(uId);
			boolean addresult = cache.addItem(used);
			if (!addresult){
				GameLog.error("时装", userId, "添加FashionBeingUsed失败");
			}
		}else{
			GameLog.info("时装", uId, "用户已经有时装记录，不需要重新生成", null);
		}
		return used;
	}

}
