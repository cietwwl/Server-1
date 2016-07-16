package com.rwbase.dao.inlay;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.common.IHeroAction;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class InlayItemHolder {
	
	private static final InlayItemHolder _INSTANCE = new InlayItemHolder();
	
	public static InlayItemHolder getInstance() {
		return _INSTANCE;
	}

	// private final String heroModelId;// 英雄的模版Id
//	final private String ownerId; //
	final private eSynType inlaySynType = eSynType.INLAY_ITEM;

	// private final Hero pOwner;
	
	public InlayItemHolder() {
		
	}

//	public InlayItemHolder(Hero pOwner) {
//		ownerId = pOwner.getUUId();
//		// this.pOwner = pOwner;
//		// this.heroModelId = String.valueOf(heroModelId);
//	}

	/*
	 * 获取用户已经拥有
	 */
//	public List<InlayItem> getItemList() {
	public List<InlayItem> getItemList(String heroId) {

		List<InlayItem> itemList = new ArrayList<InlayItem>();
		Enumeration<InlayItem> mapEnum = getMapItemStore(heroId).getEnum();
		while (mapEnum.hasMoreElements()) {
			InlayItem item = (InlayItem) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

	public void updateItem(Player player, InlayItem item) {
		getMapItemStore(item.getOwnerId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, inlaySynType, eSynOpType.UPDATE_SINGLE);
		notifyChange(player.getUserId(), item.getOwnerId());
	}

	public InlayItem getItem(String heroId, int modelId) {
		String itemId = InlayItemHelper.getItemId(heroId, modelId);
		return getItem(heroId, itemId);
	}

//	public InlayItem getItem(String itemId) {
	public InlayItem getItem(String heroId, String itemId) {
		return getMapItemStore(heroId).getItem(itemId);
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

	public void flush(String heroId) {
		getMapItemStore(heroId).flush();
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
	
	private List<IHeroAction> _dataChangeCallbacks = new ArrayList<IHeroAction>();
	
	public void regDataChangeCallback(IHeroAction callback) {
		_dataChangeCallbacks.add(callback);
	}
	
	private void notifyChange(String userId, String heroId) {
		for(IHeroAction heroAction : _dataChangeCallbacks) {
			heroAction.doAction(userId, heroId);
		} 
	}

//	private MapItemStore<InlayItem> getMapItemStore() {
//		MapItemStoreCache<InlayItem> inlayItemCache = MapItemStoreFactory.getInlayItemCache();
//		return inlayItemCache.getMapItemStore(ownerId, InlayItem.class);
//	}
	
	private MapItemStore<InlayItem> getMapItemStore(String heroId) {
		MapItemStoreCache<InlayItem> inlayItemCache = MapItemStoreFactory.getInlayItemCache();
		return inlayItemCache.getMapItemStore(heroId, InlayItem.class);
	}
}
