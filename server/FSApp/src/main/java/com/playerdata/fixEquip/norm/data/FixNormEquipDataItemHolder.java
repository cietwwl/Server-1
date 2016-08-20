package com.playerdata.fixEquip.norm.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.common.IHeroAction;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FixNormEquipDataItemHolder{
	
	final private eSynType synType = eSynType.FIX_NORM_EQUIP;
	
	private static final FixNormEquipDataItemHolder _INSTANCE = new FixNormEquipDataItemHolder();
	
	public static FixNormEquipDataItemHolder getInstance() {
		return _INSTANCE;
	}
	

	public List<FixNormEquipDataItem> getItemList(String ownerId)	
	{
		
		List<FixNormEquipDataItem> itemList = new ArrayList<FixNormEquipDataItem>();
		Enumeration<FixNormEquipDataItem> mapEnum = getItemStore(ownerId).getEnum();
		while (mapEnum.hasMoreElements()) {
			FixNormEquipDataItem item = (FixNormEquipDataItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, FixNormEquipDataItem item){
		getItemStore(item.getOwnerId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		notifyChange(player.getUserId(), item.getOwnerId());
	}
	public void updateItemList(Player player, List<FixNormEquipDataItem> itemList){
		if (itemList.size() > 0) {
			String heroId = itemList.get(0).getOwnerId();
			MapItemStore<FixNormEquipDataItem> itemStore = getItemStore(heroId);
			for (FixNormEquipDataItem item : itemList) {
//				getItemStore(item.getOwnerId()).updateItem(item);
				itemStore.updateItem(item);
			}
			ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
			notifyChange(player.getUserId(), heroId);
		}
	}
	
	
	public FixNormEquipDataItem getItem(String heroId, String itemId){		
		return getItemStore(heroId).getItem(itemId);
	}
	
	
	public boolean initItems(Player player, String heroId, List<FixNormEquipDataItem> itemList){
	
		boolean addSuccess = false;
		try {
			addSuccess = getItemStore(heroId).addItem(itemList);
			if(addSuccess){
				ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
				notifyChange(player.getUserId(), heroId);
			}
		} catch (DuplicatedKeyException e) {
			GameLog.error(LogModule.FixEquip, "FixNormEquipDataItemHolder[initItems] ownerId:"+heroId, "重复主键", e);
		}
		return addSuccess;
	}
	
	
	public void synAllData(Player player, Hero hero){
		List<FixNormEquipDataItem> itemList = getItemList(hero.getUUId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}
	
	private List<IHeroAction> _dataChangeCallback = new ArrayList<IHeroAction>();
	
	public void regDataChangeCallback(IHeroAction callback) {
		_dataChangeCallback.add(callback);
	}
	
	private void notifyChange(String userId, String heroId) {
		for (IHeroAction action : _dataChangeCallback) {
			action.doAction(userId, heroId);
		}
	}

	
	private MapItemStore<FixNormEquipDataItem> getItemStore(String ownerId) {
		MapItemStoreCache<FixNormEquipDataItem> cache = MapItemStoreFactory.getFixNormEquipDataItemCache();
		return cache.getMapItemStore(ownerId, FixNormEquipDataItem.class);
	}
	
}
