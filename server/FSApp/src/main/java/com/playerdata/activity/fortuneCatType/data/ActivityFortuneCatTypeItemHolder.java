package com.playerdata.activity.fortuneCatType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeHelper;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.fortuneCatType.ActivityFortuneCatHelper;
import com.playerdata.activity.fortuneCatType.ActivityFortuneTypeEnum;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfgDAO;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityFortuneCatTypeItemHolder{
	
	private static ActivityFortuneCatTypeItemHolder instance = new ActivityFortuneCatTypeItemHolder();
	
	public static ActivityFortuneCatTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityFortuneCatType;
	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityFortuneCatTypeItem> getItemList(String userId)	
	{
		
		List<ActivityFortuneCatTypeItem> itemList = new ArrayList<ActivityFortuneCatTypeItem>();
		Enumeration<ActivityFortuneCatTypeItem> mapEnum = getItemStore(userId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityFortuneCatTypeItem item = (ActivityFortuneCatTypeItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityFortuneCatTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityFortuneCatTypeItem getItem(String userId){
//		String itemID = ActivityFortuneCatHelper.getItemId(userId, ActivityFortuneTypeEnum.FortuneCat);
		int id = Integer.parseInt(ActivityFortuneTypeEnum.FortuneCat.getCfgId());
		return getItemStore(userId).get(id);
	}
	
	public boolean addItem(Player player, ActivityFortuneCatTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<ActivityFortuneCatTypeItem> itemList){
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
	
//	public boolean removeitem(Player player,ActivityCountTypeEnum type){
//		
//		String uidAndId = ActivityCountTypeHelper.getItemId(player.getUserId(), type);
//		boolean addSuccess = getItemStore(player.getUserId()).removeItem(uidAndId);
//		return addSuccess;
//	}
//	
	public void synAllData(Player player){
		List<ActivityFortuneCatTypeItem> itemList = getItemList(player.getUserId());		
		Iterator<ActivityFortuneCatTypeItem> it = itemList.iterator();
		while(it.hasNext()){
			ActivityFortuneCatTypeItem item = (ActivityFortuneCatTypeItem)it.next();
			if(ActivityFortuneCatTypeCfgDAO.getInstance().getCfgById(item.getCfgId()) == null){
//				removeItem(player, item);
				it.remove();
			}
		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	public PlayerExtPropertyStore<ActivityFortuneCatTypeItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityFortuneCatTypeItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_FORTUNECAT, ActivityFortuneCatTypeItem.class);
		try {
			return storeCache.getStore(userId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;//	PlayerExtPropertyStore<ActivityCountTypeItem> store= storeCache.getAttachmentStore(userId);

	}
	
}
