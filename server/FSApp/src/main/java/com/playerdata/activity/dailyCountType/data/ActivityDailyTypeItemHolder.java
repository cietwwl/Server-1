package com.playerdata.activity.dailyCountType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
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
				.getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityDailyTypeItem item = (ActivityDailyTypeItem) mapEnum
					.nextElement();
			itemList.add(item);
		}
		return itemList;
	}

	public void updateItem(Player player, ActivityDailyTypeItem item) {
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType,
				eSynOpType.UPDATE_SINGLE);
	}

	public ActivityDailyTypeItem getItem(String userId) {
		return getItemStore(userId).getItem(userId);
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
		Iterator<ActivityDailyTypeItem> it = itemList.iterator();
		while (it.hasNext()) {
			ActivityDailyTypeItem item = it.next();
			if (ActivityDailyTypeCfgDAO.getInstance().getCfgById(
					item.getCfgid()) == null) {
				it.remove();
			}
		}
		ClientDataSynMgr.synDataList(player, itemList, synType,
				eSynOpType.UPDATE_LIST);
	}

	public MapItemStore<ActivityDailyTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityDailyTypeItem> cache = MapItemStoreFactory
				.getActivityDailyCountTypeItemCache();
		return cache.getMapItemStore(userId, ActivityDailyTypeItem.class);
	}

}
