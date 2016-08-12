package com.playerdata.activity.dailyCharge.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeHelper;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityDailyRechargeTypeItemHolder{
	
	private static ActivityDailyRechargeTypeItemHolder instance = new ActivityDailyRechargeTypeItemHolder();
	
	public static ActivityDailyRechargeTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityExchangeType;
	
	public List<ActivityDailyRechargeTypeItem> getItemList(String userId)	
	{
		List<ActivityDailyRechargeTypeItem> itemList = new ArrayList<ActivityDailyRechargeTypeItem>();
		Enumeration<ActivityDailyRechargeTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityDailyRechargeTypeItem item = (ActivityDailyRechargeTypeItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityDailyRechargeTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityDailyRechargeTypeItem getItem(String userId, ActivityExChangeTypeEnum exChangeTypeEnum){		
		String itemId = ActivityExChangeTypeHelper.getItemId(userId, exChangeTypeEnum);
		return getItemStore(userId).getItem(itemId);
	}
	
	public boolean addItem(Player player, ActivityDailyRechargeTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<ActivityDailyRechargeTypeItem> itemList){
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
		List<ActivityDailyRechargeTypeItem> itemList = getItemList(player.getUserId());		
		Iterator<ActivityDailyRechargeTypeItem> it = itemList.iterator();
		while(it.hasNext()){
			ActivityDailyRechargeTypeItem item = (ActivityDailyRechargeTypeItem)it.next();
			if(ActivityExchangeTypeCfgDAO.getInstance().getCfgById(item.getCfgId()) == null){
//				removeItem(player, item);
				it.remove();
			}
		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}
	
	private MapItemStore<ActivityDailyRechargeTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityDailyRechargeTypeItem> cache = MapItemStoreFactory.getActivityDailyRechargeItemCache();
		return cache.getMapItemStore(userId, ActivityDailyRechargeTypeItem.class);
	}
}
