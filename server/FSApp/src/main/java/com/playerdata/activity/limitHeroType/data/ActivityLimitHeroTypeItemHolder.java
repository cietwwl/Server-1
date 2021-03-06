package com.playerdata.activity.limitHeroType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroEnum;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroHelper;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
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
		Enumeration<ActivityLimitHeroTypeItem> mapEnum = getItemStore(userId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityLimitHeroTypeItem item = (ActivityLimitHeroTypeItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityLimitHeroTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void synData(Player player, ActivityLimitHeroTypeItem item){
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	
	public ActivityLimitHeroTypeItem getItem(String userId){	
		String itemId = ActivityLimitHeroHelper.getItemId(userId, ActivityLimitHeroEnum.LimitHero);
		int id = Integer.parseInt(ActivityLimitHeroEnum.LimitHero.getCfgId());
		return getItemStore(userId).get(id);
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

	
	public RoleExtPropertyStore<ActivityLimitHeroTypeItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityLimitHeroTypeItem> cache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_LIMITHERO, ActivityLimitHeroTypeItem.class);
		try {
			return cache.getStore(userId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
