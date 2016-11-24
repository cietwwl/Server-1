package com.rwbase.dao.inlay;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.common.IHeroAction;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.hero.HeroExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class InlayItemHolder {

	private static InlayItemHolder _instance = new InlayItemHolder();

	public static InlayItemHolder getInstance() {
		return _instance;
	}

	// private final String heroModelId;// 英雄的模版Id
	// final private String ownerId; //
	final private eSynType inlaySynType = eSynType.INLAY_ITEM;

	// private final Hero pOwner;

	protected InlayItemHolder() {

	}

	// public InlayItemHolder(Hero pOwner) {
	// ownerId = pOwner.getUUId();
	// // this.pOwner = pOwner;
	// // this.heroModelId = String.valueOf(heroModelId);
	// }

	/*
	 * 获取用户已经拥有
	 */
	// public List<InlayItem> getItemList() {
	public List<InlayItem> getItemList(String heroId) {

		List<InlayItem> itemList = new ArrayList<InlayItem>();
		Enumeration<InlayItem> mapEnum = getMapItemStore(heroId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			InlayItem item = (InlayItem) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

	public void updateItem(Player player, InlayItem item) {
		getMapItemStore(item.getOwnerId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, inlaySynType, eSynOpType.UPDATE_SINGLE);
		notifyChange(player.getUserId(), item.getOwnerId());
	}

	public InlayItem getItem(String heroId, Integer itemId) {
		return getMapItemStore(heroId).get(itemId);
	}

	public boolean removeItem(Player player, InlayItem item) {

		boolean success = getMapItemStore(item.getOwnerId()).removeItem(item.getId());
		if (success) {
			ClientDataSynMgr.updateData(player, item, inlaySynType, eSynOpType.REMOVE_SINGLE);
			notifyChange(player.getUserId(), item.getOwnerId());
		}
		return success;
	}

	public boolean addItem(Player player, String heroId, InlayItem item) {

		boolean addSuccess = getMapItemStore(heroId).addItem(item);
		if (addSuccess) {
			ClientDataSynMgr.updateData(player, item, inlaySynType, eSynOpType.ADD_SINGLE);
			notifyChange(player.getUserId(), heroId);
		}
		return addSuccess;
	}

	public void synAllData(Player player, String heroId, int version) {
		List<InlayItem> itemList = getItemList(heroId);
		ClientDataSynMgr.synDataList(player, itemList, inlaySynType, eSynOpType.UPDATE_LIST);
	}

	private List<IHeroAction> _dataChangeCallbacks = new ArrayList<IHeroAction>();

	public void regDataChangeCallback(IHeroAction callback) {
		_dataChangeCallbacks.add(callback);
	}

	private void notifyChange(String userId, String heroId) {
		for (IHeroAction heroAction : _dataChangeCallbacks) {
			heroAction.doAction(userId, heroId);
		}
	}

	private RoleExtPropertyStore<InlayItem> getMapItemStore(String heroId) {
		RoleExtPropertyStoreCache<InlayItem> inlayItemCache = RoleExtPropertyFactory.getHeroExtCache(HeroExtPropertyType.INLAY_ITEM, InlayItem.class);
		try {
			return inlayItemCache.getStore(heroId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
