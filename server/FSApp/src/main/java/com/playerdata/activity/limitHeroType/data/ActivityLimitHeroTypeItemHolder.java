package com.playerdata.activity.limitHeroType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeHelper;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityLimitHeroTypeItemHolder{
	
	private static ActivityLimitHeroTypeItemHolder instance = new ActivityLimitHeroTypeItemHolder();
	
	public static ActivityLimitHeroTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityLimitHeroType;
	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityLimitHeroTypeItem> getItemList(String userId)	
	{
		
		List<ActivityLimitHeroTypeItem> itemList = new ArrayList<ActivityLimitHeroTypeItem>();
		Enumeration<ActivityLimitHeroTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityLimitHeroTypeItem item = (ActivityLimitHeroTypeItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityLimitHeroTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void synData(Player player, ActivityLimitHeroTypeItem item){
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	
	public ActivityLimitHeroTypeItem getItem(String userId){		
		return getItemStore(userId).getItem(userId);
	}
	
//	public boolean removeItem(Player player, ActivityCountTypeItem item){
//		
//		boolean success = getItemStore(player.getUserId()).removeItem(item.getId());
//		if(success){
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
//		}
//		return success;
//	}
	
	public boolean addItem(Player player, ActivityLimitHeroTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<ActivityLimitHeroTypeItem> itemList){
		try {
			boolean addSuccess = getItemStore(player.getUserId()).addItem(itemList);
			if(addSuccess){
				ClientDataSynMgr.updateDataList(player, getItemList(player.getUserId()), synType, eSynOpType.UPDATE_LIST);
			}
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			//handle..
			e.printStackTrace();
			return false;
		}
	}
	
	public void synAllData(Player player){
		List<ActivityLimitHeroTypeItem> itemList = getItemList(player.getUserId());		
		Iterator<ActivityLimitHeroTypeItem> it = itemList.iterator();
		ActivityLimitHeroCfgDAO activityLimitHeroCfgDAO = ActivityLimitHeroCfgDAO.getInstance();
		while(it.hasNext()){
			ActivityLimitHeroTypeItem item = (ActivityLimitHeroTypeItem)it.next();
			if(activityLimitHeroCfgDAO.getCfgById(item.getCfgId()) == null){
//				removeItem(player, item);
				it.remove();
			}
		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<ActivityLimitHeroTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityLimitHeroTypeItem> cache = MapItemStoreFactory.getActivityLimitHeroTypeItemCache();
		return cache.getMapItemStore(userId, ActivityLimitHeroTypeItem.class);
	}
	
}
