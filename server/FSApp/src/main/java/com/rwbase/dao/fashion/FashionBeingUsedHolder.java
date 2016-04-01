package com.rwbase.dao.fashion;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.DataRdbDao;

/**
 * 缓存数据以用户ID作为索引
 */
public class FashionBeingUsedHolder extends DataRdbDao<FashionBeingUsed>{

	private static FashionBeingUsedHolder instance = new FashionBeingUsedHolder();
	private FashionBeingUsedHolder(){}
	public static FashionBeingUsedHolder getInstance(){
		return instance;
	}
	
	public FashionBeingUsed get(String userId){
		return super.getObject(userId);
	}

	public boolean update(FashionBeingUsed fashionUsed) {
		if (fashionUsed == null){
			return false;
		}
		FashionBeingUsed used = super.getObject(fashionUsed.getUserId());
		if (used == null) {
			GameLog.error("时装", fashionUsed.getUserId(), "更新FashionBeingUsed失败，ID="+fashionUsed.getUserId());
			return false;
		}
		super.update(fashionUsed.getUserId());
//		boolean updateResult = getCache().updateItem(fashionUsed);
//		if (!updateResult){
//			GameLog.error("时装", fashionUsed.getUserId(), "更新FashionBeingUsed失败，ID="+fashionUsed.getId());
//		}
		
		return true;
	}

	/**
	 * 如果新增了一个时装记录，调用着负责向客户端发送同步数据
	 * @param uId
	 * @return
	 */
	public FashionBeingUsed newFashion(String uId) {
		FashionBeingUsed used = super.getObject(uId);
//		MapItemStore<FashionBeingUsed> cache = getCache();
//		FashionBeingUsed used = cache.getItem(uId);
		if (used == null){
			used = new FashionBeingUsed();
			used.setUserId(uId);
			boolean addresult = super.saveOrUpdate(used);
			if (!addresult){
				GameLog.error("时装", uId, "添加FashionBeingUsed失败");
			}
		}else{
			GameLog.info("时装", uId, "用户已经有时装记录，不需要重新生成", null);
		}
		return used;
	}
}
