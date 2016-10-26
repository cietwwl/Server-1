package com.playerdata.activity.dailyCountType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeHelper;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
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

public class ActivityDailyTypeItemHolder {

	private static ActivityDailyTypeItemHolder instance = new ActivityDailyTypeItemHolder();

	public static ActivityDailyTypeItemHolder getInstance() {
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDailyType;

	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityDailyTypeItem> getItemList(String userId) {
		List<ActivityDailyTypeItem> itemList = new ArrayList<ActivityDailyTypeItem>();
		Enumeration<ActivityDailyTypeItem> mapEnum = getItemStore(userId)
				.getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityDailyTypeItem item = (ActivityDailyTypeItem) mapEnum
					.nextElement();
			if (ActivityDailyTypeCfgDAO.getInstance().getCfgById(
					item.getCfgid()) == null) {
				continue;
			}
			itemList.add(item);
		}
		return itemList;
	}

	public void updateItem(Player player, ActivityDailyTypeItem item) {
		getItemStore(player.getUserId()).update(item.getId());
		List<ActivityDailyTypeSubItem> subList = item.getSubItemList();
		for(ActivityDailyTypeSubItem sub : subList){
//			System.out.println("~~~~~~~~~~~~~~~update.singel .sub.id=" + sub.getCfgId() + "     count = " + sub.getCount());
		}
		ClientDataSynMgr.updateData(player, item, synType,
				eSynOpType.UPDATE_SINGLE);
	}

	public ActivityDailyTypeItem getItem(String userId) {
//		String itemId = ActivityDailyTypeHelper.getItemId(userId, ActivityDailyTypeEnum.Daily);
		int id = Integer.parseInt(ActivityDailyTypeEnum.Daily.getCfgId());
		return getItemStore(userId).get(id);
	}	

	public boolean addItem(Player player, ActivityDailyTypeItem item) {

		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if (addSuccess) {
			ClientDataSynMgr.updateData(player, item, synType,
					eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}

	public void synAllData(Player player) {
		List<ActivityDailyTypeItem> itemList = getItemList(player.getUserId());
		if(itemList == null || itemList.isEmpty()){
			return;
		}
		ActivityDailyTypeItem item = itemList.get(0);
		List<ActivityDailyTypeSubItem> subList = item.getSubItemList();
		for(ActivityDailyTypeSubItem sub : subList){
//			System.out.println("~~~~~~~~~~~~~~~update.all .sub.id=" + sub.getCfgId() + "     count = " + sub.getCount());
		}
		ClientDataSynMgr.synDataList(player, itemList, synType,
				eSynOpType.UPDATE_LIST);
	}

	public RoleExtPropertyStore<ActivityDailyTypeItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityDailyTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_DAILYTYPE, ActivityDailyTypeItem.class);
		RoleExtPropertyStore<ActivityDailyTypeItem> store = null;
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

	public boolean addItemList(Player player, List<ActivityDailyTypeItem> addItemList) {
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
