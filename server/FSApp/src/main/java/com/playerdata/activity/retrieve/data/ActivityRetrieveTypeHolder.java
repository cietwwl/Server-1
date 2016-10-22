package com.playerdata.activity.retrieve.data;

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
import com.playerdata.activity.limitHeroType.ActivityLimitHeroEnum;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroHelper;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.retrieve.ActivityRetrieveTypeEnum;
import com.playerdata.activity.retrieve.ActivityRetrieveTypeHelper;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityRetrieveTypeHolder{
	
	private static ActivityRetrieveTypeHolder instance = new ActivityRetrieveTypeHolder();
	
	public static ActivityRetrieveTypeHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityRetrieveType;
	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<RewardBackItem> getItemList(String userId)	
	{
		
		List<RewardBackItem> itemList = new ArrayList<RewardBackItem>();
		Enumeration<RewardBackItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			RewardBackItem item = (RewardBackItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, RewardBackItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void synData(Player player, RewardBackItem item){
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	
	public RewardBackItem getItem(String userId){	
		String itemId = ActivityRetrieveTypeHelper.getItemId(userId, ActivityRetrieveTypeEnum.retrieve);
		return getItemStore(userId).getItem(itemId);
	}
	
//	public boolean removeItem(Player player, ActivityCountTypeItem item){
//		
//		boolean success = getItemStore(player.getUserId()).removeItem(item.getId());
//		if(success){
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
//		}
//		return success;
//	}
	
	public boolean addItem(Player player, RewardBackItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<RewardBackItem> itemList){
		try {
//			boolean addSuccess = getItemStore(player.getUserId()).addItem(itemList);
			MapItemStore<RewardBackItem> itemstore = getItemStore(player.getUserId());
			boolean addSuccess = itemstore.addItem(itemList);
			
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
		List<RewardBackItem> itemList = getItemList(player.getUserId());
		RewardBackItem item = new RewardBackItem();
		if(!itemList.isEmpty()){
			item = itemList.get(0);
		}
		ClientDataSynMgr.synData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	
	public MapItemStore<RewardBackItem> getItemStore(String userId) {
		MapItemStoreCache<RewardBackItem> cache = MapItemStoreFactory.getRewardBackItemCache();
		return cache.getMapItemStore(userId, RewardBackItem.class);
	}
	
}
