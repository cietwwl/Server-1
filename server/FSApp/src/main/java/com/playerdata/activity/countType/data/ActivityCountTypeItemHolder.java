package com.playerdata.activity.countType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.property.ActivityCountTypeProperty;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityCountTypeItemHolder {

	private static ActivityCountTypeItemHolder instance = new ActivityCountTypeItemHolder();

	public static ActivityCountTypeItemHolder getInstance() {
		return instance;
	}

	final private eSynType synType = eSynType.ActivityCountType;

	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityCountTypeItem> getItemList(String userId) {
		ActivityCountTypeCfgDAO typeCfgDAO = ActivityCountTypeCfgDAO
				.getInstance();
		List<ActivityCountTypeItem> itemList = new ArrayList<ActivityCountTypeItem>();
		Enumeration<ActivityCountTypeItem> mapEnum = getItemStore(userId)
				.getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityCountTypeItem item = (ActivityCountTypeItem) mapEnum
					.nextElement();
			if (!typeCfgDAO.hasCfgListByEnumId(item.getEnumId())) {
				continue;
			}
			itemList.add(item);
		}

		return itemList;
	}



	public void updateItem(Player player, ActivityCountTypeItem item) {
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType,
				eSynOpType.UPDATE_SINGLE);
	}

	public ActivityCountTypeItem getItem(String userId,
			ActivityCountTypeEnum countTypeEnum) {
//		String itemId = ActivityCountTypeHelper
//				.getItemId(userId, countTypeEnum);
		int id = Integer.parseInt(countTypeEnum.getCfgId());
		return getItemStore(userId).get(id);
	}

	public boolean addItem(Player player, ActivityCountTypeItem item) {

		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if (addSuccess) {
			ClientDataSynMgr.updateData(player, item, synType,
					eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}

	public boolean addItemList(Player player,
			List<ActivityCountTypeItem> itemList) {
		try {
			boolean addSuccess = getItemStore(player.getUserId()).addItem(
					itemList);
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

	public void synAllData(Player player) {
		List<ActivityCountTypeItem> itemList = getItemList(player.getUserId());
		ClientDataSynMgr.synDataList(player, itemList, synType,
				eSynOpType.UPDATE_LIST);
	}

	public PlayerExtPropertyStore<ActivityCountTypeItem> getItemStore(String userId) {
		PlayerExtPropertyStoreCache<ActivityCountTypeItem> storeCache = PlayerExtPropertyFactory.get(PlayerExtPropertyType.ACTIVITY_COUNTTYPE, ActivityCountTypeItem.class);
		try {
			return storeCache.getAttachmentStore(userId);
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
