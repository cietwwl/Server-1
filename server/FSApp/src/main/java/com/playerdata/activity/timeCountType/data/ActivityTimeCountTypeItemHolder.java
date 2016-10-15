package com.playerdata.activity.timeCountType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeEnum;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeHelper;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityTimeCountTypeItemHolder{
	
	private static ActivityTimeCountTypeItemHolder instance = new ActivityTimeCountTypeItemHolder();
	
	public static ActivityTimeCountTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityTimeCountType;	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityTimeCountTypeItem> getItemList(String userId)	
	{
		
		List<ActivityTimeCountTypeItem> itemList = new ArrayList<ActivityTimeCountTypeItem>();
		Enumeration<ActivityTimeCountTypeItem> mapEnum = getItemStore(userId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityTimeCountTypeItem item = (ActivityTimeCountTypeItem) mapEnum.nextElement();		
			if(ActivityTimeCountTypeCfgDAO.getInstance().getCfgById(item.getCfgId()) == null){
				continue;
			}
			itemList.add(item);
		}		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityTimeCountTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void lazyUpdateItem(Player player, ActivityTimeCountTypeItem item){
		getItemStore(player.getUserId()).lazyUpdate(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityTimeCountTypeItem getItem(String userId, ActivityTimeCountTypeEnum countTypeEnum){		
		int id = Integer.parseInt(countTypeEnum.getCfgId());
		return getItemStore(userId).get(id);
	}
	
	public boolean addItemList(Player player, List<ActivityTimeCountTypeItem> itemList){
		try {
			boolean addSuccess = getItemStore(player.getUserId()).addItem(itemList);
			if(addSuccess){
				List<ActivityTimeCountTypeItem> itemListTmp = getItemList(player.getUserId());									
				ClientDataSynMgr.updateDataList(player, itemListTmp, synType, eSynOpType.UPDATE_LIST);
			}
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			//handle..
			e.printStackTrace();
			return false;
		}
	}
	
//	
	public void synAllData(Player player){
		List<ActivityTimeCountTypeItem> itemList = getItemList(player.getUserId());	
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	public RoleExtPropertyStore<ActivityTimeCountTypeItem> getItemStore(String userId) {
		RoleExtPropertyStore<ActivityTimeCountTypeItem> store =null;
		RoleExtPropertyStoreCache<ActivityTimeCountTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_TIMECOUNT, ActivityTimeCountTypeItem.class);
		try {
			store = cach.getStore(userId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return store;
	}
	
}
