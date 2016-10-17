package com.playerdata.activity.timeCardType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeEnum;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeHelper;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfgDAO;
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

public class ActivityTimeCardTypeItemHolder{
	
	private static ActivityTimeCardTypeItemHolder instance = new ActivityTimeCardTypeItemHolder();
	
	public static ActivityTimeCardTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityTimeCardType;
	

	public List<ActivityTimeCardTypeItem> getItemList(String userId)	
	{
		ActivityTimeCardTypeCfgDAO dao = ActivityTimeCardTypeCfgDAO.getInstance();
		List<ActivityTimeCardTypeItem> itemList = new ArrayList<ActivityTimeCardTypeItem>();
		Enumeration<ActivityTimeCardTypeItem> mapEnum = getItemStore(userId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityTimeCardTypeItem item = (ActivityTimeCardTypeItem) mapEnum.nextElement();
			if(dao.getCfgById(item.getCfgId()) == null){
				continue;
			}
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityTimeCardTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityTimeCardTypeItem getItem(String userId){
		int id = Integer.parseInt(ActivityTimeCardTypeEnum.Month.getCfgId());
		
		return getItemStore(userId).get(id);
	}
	
	
	public boolean addItem(Player player, ActivityTimeCardTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	
	public void synAllData(Player player){
		List<ActivityTimeCardTypeItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	public PlayerExtPropertyStore<ActivityTimeCardTypeItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityTimeCardTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_TIMECARD, ActivityTimeCardTypeItem.class);
		PlayerExtPropertyStore<ActivityTimeCardTypeItem> store = null;
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

	public boolean addItemList(Player player, List<ActivityTimeCardTypeItem> addItemList) {
		try {
			boolean addSuccess = getItemStore(player.getUserId()).addItem(
					addItemList);
			if (addSuccess) {
				ClientDataSynMgr.updateDataList(player,
						getItemList(player.getUserId()), synType,
						eSynOpType.UPDATE_LIST);
			}
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			// handle..
			e.printStackTrace();
			return false;
		}
		
	}
	
}
