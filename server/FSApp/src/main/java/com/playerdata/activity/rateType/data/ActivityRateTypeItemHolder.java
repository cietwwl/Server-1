package com.playerdata.activity.rateType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.rateType.ActivityRateTypeEnum;
import com.playerdata.activity.rateType.ActivityRateTypeHelper;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityRateTypeItemHolder {

	private static ActivityRateTypeItemHolder instance = new ActivityRateTypeItemHolder();

	public static ActivityRateTypeItemHolder getInstance() {
		return instance;
	}

	final private eSynType synType = eSynType.ActivityRateType;

	public List<ActivityRateTypeItem> getItemList(String userId) {
		ActivityRateTypeCfgDAO dao = ActivityRateTypeCfgDAO.getInstance();
		List<ActivityRateTypeItem> itemList = new ArrayList<ActivityRateTypeItem>();
		Enumeration<ActivityRateTypeItem> mapEnum = getItemStore(userId)
				.getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityRateTypeItem item = (ActivityRateTypeItem) mapEnum
					.nextElement();
			if(!dao.hasCfgByEnumId(item.getEnumId())){
				continue;
			}
			itemList.add(item);
		}
		return itemList;
	}

	public void updateItem(Player player, ActivityRateTypeItem item) {
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType,
				eSynOpType.UPDATE_SINGLE);
	}

	public ActivityRateTypeItem getItem(String userId,
			ActivityRateTypeEnum typeEnum) {
		String itemId = ActivityRateTypeHelper.getItemId(userId, typeEnum);
		return getItemStore(userId).getItem(itemId);
	}

	public boolean addItem(Player player, ActivityRateTypeItem item) {

		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if (addSuccess) {
			ClientDataSynMgr.updateData(player, item, synType,
					eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}

	public boolean removeItem(Player player, ActivityRateTypeEnum type) {

		String uidAndId = ActivityRateTypeHelper.getItemId(player.getUserId(),
				type);
		boolean addSuccess = getItemStore(player.getUserId()).removeItem(
				uidAndId);
		return addSuccess;
	}

	public void synAllData(Player player) {
		List<ActivityRateTypeItem> itemList = getItemList(player.getUserId());
		ClientDataSynMgr.synDataList(player, itemList, synType,
				eSynOpType.UPDATE_LIST);
	}

	public MapItemStore<ActivityRateTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityRateTypeItem> cache = MapItemStoreFactory
				.getActivityRateTypeItemCache();
		return cache.getMapItemStore(userId, ActivityRateTypeItem.class);
	}

	public boolean addItemList(Player player, List<ActivityRateTypeItem> addItemList) {
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
