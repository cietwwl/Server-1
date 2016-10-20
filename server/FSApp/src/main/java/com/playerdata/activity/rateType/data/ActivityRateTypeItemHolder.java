package com.playerdata.activity.rateType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.rateType.ActivityRateTypeEnum;
import com.playerdata.activity.rateType.ActivityRateTypeHelper;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
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
				.getExtPropertyEnumeration();
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
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType,
				eSynOpType.UPDATE_SINGLE);
	}

	public ActivityRateTypeItem getItem(String userId,
			ActivityRateTypeEnum typeEnum) {
//		String itemId = ActivityRateTypeHelper.getItemId(userId, typeEnum);
		int id = Integer.parseInt(typeEnum.getCfgId());
		return getItemStore(userId).get(id);
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

//		String uidAndId = ActivityRateTypeHelper.getItemId(player.getUserId(),
//				type);
		int id = Integer.parseInt(type.getCfgId());
		boolean addSuccess = getItemStore(player.getUserId()).removeItem(
				id);
		return addSuccess;
	}

	public void synAllData(Player player) {
		List<ActivityRateTypeItem> itemList = getItemList(player.getUserId());
		ClientDataSynMgr.synDataList(player, itemList, synType,
				eSynOpType.UPDATE_LIST);
	}

	public RoleExtPropertyStore<ActivityRateTypeItem> getItemStore(String userId) {
//		RoleExtPropertyStoreCache<ActivityRateTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(null, ActivityRateTypeItem.class);
		RoleExtPropertyStoreCache<ActivityRateTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_RATE, ActivityRateTypeItem.class);
		
		RoleExtPropertyStore<ActivityRateTypeItem> store = null;
		try {
			store = cach.getStore(userId);
			
			return store;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return store;
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
