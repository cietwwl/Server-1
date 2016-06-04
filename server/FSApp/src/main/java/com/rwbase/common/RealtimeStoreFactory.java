package com.rwbase.common;

import java.util.ArrayList;
import java.util.List;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.RealtimeStoreCache;
import com.rw.manager.GameManager;
import com.rw.manager.ServerPerformanceConfig;
import com.rwbase.dao.majorDatas.pojo.MajorData;

public class RealtimeStoreFactory {
	private static RealtimeStoreCache<MajorData> majorDataCache;
	
	private static List<RealtimeStoreCache> list;
	
	private static boolean init = false;
	
	static{
		init();
	}
	
	public static void init(){
		synchronized (RealtimeStoreFactory.class) {
			if(init){
				return;
			}else{
				init = true;
			}
			
		}
		
		ServerPerformanceConfig config = GameManager.getPerformanceConfig();
		int heroCapacity = config.getPlayerCapacity();
		list = new ArrayList<RealtimeStoreCache>();
		
		majorDataCache = new RealtimeStoreCache<MajorData>(MajorData.class, "ownerId", heroCapacity);
		list.add(majorDataCache);
	}
	
	public static void notifyPlayerCreate(String userId){
		for (int i = list.size(); --i >= 0;) {
			RealtimeStoreCache cache = list.get(i);
			cache.notifyPlayerCreate(userId);
		}
	}
	
	/**
	 * 获取关键数据缓存
	 * @return
	 */
	public static RealtimeStoreCache<MajorData> getMajorDataCache(){
		return majorDataCache;
	}
}
