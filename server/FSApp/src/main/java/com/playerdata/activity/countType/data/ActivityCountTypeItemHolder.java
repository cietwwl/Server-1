package com.playerdata.activity.countType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityCountTypeItemHolder{
	
	private static ActivityCountTypeItemHolder instance = new ActivityCountTypeItemHolder();
	
	public static ActivityCountTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityCountType;
	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityCountTypeItem> getItemList(String userId)	
	{
		
		List<ActivityCountTypeItem> itemList = new ArrayList<ActivityCountTypeItem>();
		Enumeration<ActivityCountTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityCountTypeItem item = (ActivityCountTypeItem) mapEnum.nextElement();			
			if(ActivityCountTypeCfgDAO.getInstance().getCfgListByEnumId(item.getEnumId()).isEmpty()){
				continue;
			}
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityCountTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityCountTypeItem getItem(String userId, ActivityCountTypeEnum countTypeEnum){		
		String itemId = ActivityCountTypeHelper.getItemId(userId, countTypeEnum);
		return getItemStore(userId).getItem(itemId);
	}
	
	public boolean addItem(Player player, ActivityCountTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<ActivityCountTypeItem> itemList){
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
		List<ActivityCountTypeItem> itemList = getItemList(player.getUserId());			
//		Iterator<ActivityCountTypeItem> it = itemList.iterator();
//		while(it.hasNext()){
//			ActivityCountTypeItem item = (ActivityCountTypeItem)it.next();
//			if(ActivityCountTypeCfgDAO.getInstance().getCfgById(item.getCfgId()) == null){
////				removeItem(player, item);
//				it.remove();
//			}
//		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<ActivityCountTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityCountTypeItem> cache = MapItemStoreFactory.getActivityCountTypeItemCache();
		return cache.getMapItemStore(userId, ActivityCountTypeItem.class);
	}
	
}
