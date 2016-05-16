package com.rwbase.dao.inlay;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.common.Action;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class InlayItemHolder {

	// private final String heroModelId;// 英雄的模版Id
	final private String ownerId; //
	final private eSynType inlaySynType = eSynType.INLAY_ITEM;

	// private final Hero pOwner;

	public InlayItemHolder(Hero pOwner) {
		ownerId = pOwner.getUUId();
		// this.pOwner = pOwner;
		// this.heroModelId = String.valueOf(heroModelId);
	}

	/*
	 * 获取用户已经拥有
	 */
	public List<InlayItem> getItemList() {

		List<InlayItem> itemList = new ArrayList<InlayItem>();
		Enumeration<InlayItem> mapEnum = getMapItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			InlayItem item = (InlayItem) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

	public void updateItem(Player player, InlayItem item) {
		getMapItemStore().updateItem(item);
		ClientDataSynMgr.updateData(player, item, inlaySynType, eSynOpType.UPDATE_SINGLE);
		notifyChange();
	}

	public InlayItem getItem(String ownerId, int modelId) {
		String itemId = InlayItemHelper.getItemId(ownerId, modelId);
		return getItem(itemId);
	}

	public InlayItem getItem(String itemId) {
		return getMapItemStore().getItem(itemId);
	}

	public boolean removeItem(Player player, InlayItem item) {

		boolean success = getMapItemStore().removeItem(item.getId());
		if (success) {
			ClientDataSynMgr.updateData(player, item, inlaySynType, eSynOpType.REMOVE_SINGLE);
			notifyChange();
		}
		return success;
	}

	public boolean addItem(Player player, InlayItem item) {

		boolean addSuccess = getMapItemStore().addItem(item);
		if (addSuccess) {
			ClientDataSynMgr.updateData(player, item, inlaySynType, eSynOpType.ADD_SINGLE);
			notifyChange();
		}
		return addSuccess;
	}

	public void synAllData(Player player, int version) {
		List<InlayItem> itemList = getItemList();
		ClientDataSynMgr.synDataList(player, itemList, inlaySynType, eSynOpType.UPDATE_LIST);
	}

	public void flush() {
		getMapItemStore().flush();
	}

	// public AttrData toAttrData() {
	// List<InlayItem> itemList = getItemList();
	// AttrData totalAttrData = InlayItemHelper.getInlayAttrData(itemList, String.valueOf(this.pOwner.getModelId()));
	// return totalAttrData;
	// }

	// public AttrData toPercentAttrData() {
	// List<InlayItem> itemList = getItemList();
	// AttrData totalAttrData = InlayItemHelper.getPercentInlayAttrData(itemList);
	// return totalAttrData;
	// }

	private List<Action> callbackList = new ArrayList<Action>();

	public void regChangeCallBack(Action callBack) {
		callbackList.add(callBack);
	}

	private void notifyChange() {
		for (Action action : callbackList) {
			action.doAction();
		}
	}

	private MapItemStore<InlayItem> getMapItemStore() {
		MapItemStoreCache<InlayItem> inlayItemCache = MapItemStoreFactory.getInlayItemCache();
		return inlayItemCache.getMapItemStore(ownerId, InlayItem.class);
	}
}
