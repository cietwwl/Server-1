package com.playerdata.fixEquip.exp.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.common.Action;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.fixEquip.FixEquipHelper;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FixExpEquipDataItemHolder{
	
	final private eSynType synType = eSynType.FIX_EXP_EQUIP;
	

	public List<FixExpEquipDataItem> getItemList(String ownerId)	
	{
		
		List<FixExpEquipDataItem> itemList = new ArrayList<FixExpEquipDataItem>();
		Enumeration<FixExpEquipDataItem> mapEnum = getItemStore(ownerId).getEnum();
		while (mapEnum.hasMoreElements()) {
			FixExpEquipDataItem item = (FixExpEquipDataItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, FixExpEquipDataItem item){
		getItemStore(item.getOwnerId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		notifyChange();
	}
	
	public FixExpEquipDataItem getItem(String ownerId, String cfgId){		
		String itemId = FixEquipHelper.getExpItemId(ownerId, cfgId);
		return getItemStore(ownerId).getItem(itemId);
	}
	
	
	public boolean initItems(Player player, String ownerId, List<FixExpEquipDataItem> itemList){
	
		boolean addSuccess = false;
		try {
			addSuccess = getItemStore(ownerId).addItem(itemList);
			if(addSuccess){
				ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
				notifyChange();
			}
		} catch (DuplicatedKeyException e) {
			GameLog.error(LogModule.FixEquip, "FixExpEquipDataItemHolder[initItems] ownerId:"+ownerId, "重复主键", e);
		}
		return addSuccess;
	}
	
	
	public void synAllData(Player player, Hero hero){
		List<FixExpEquipDataItem> itemList = getItemList(hero.getUUId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}
	

	private List<Action> callbackList = new ArrayList<Action>();

	public void regChangeCallBack(Action callBack) {
		callbackList.add(callBack);
	}

	private void notifyChange() {
		for (Action action : callbackList) {
			action.doAction();
		}
	}

	
	private MapItemStore<FixExpEquipDataItem> getItemStore(String ownerId) {
		MapItemStoreCache<FixExpEquipDataItem> cache = MapItemStoreFactory.getFixExpEquipDataItemCache();
		return cache.getMapItemStore(ownerId, FixExpEquipDataItem.class);
	}
	
}
