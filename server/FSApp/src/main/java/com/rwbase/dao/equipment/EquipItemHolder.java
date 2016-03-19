package com.rwbase.dao.equipment;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.Action;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class EquipItemHolder {

	final private String ownerId; //
	final private eSynType equipSynType = eSynType.EQUIP_ITEM;
	//private Map<Integer, EquipItem> equipSlotMap = new HashMap<Integer, EquipItem>();

	public EquipItemHolder(String ownerIdP) {
		ownerId = ownerIdP;
//		for (EquipItem equipItemTmp : getItemList()) {
//			equipSlotMap.put(equipItemTmp.getEquipIndex(), equipItemTmp);
//		}
	}

	/*
	 * 获取用户已经拥有
	 */
	public List<EquipItem> getItemList() {
		List<EquipItem> itemList = new ArrayList<EquipItem>();
		Enumeration<EquipItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			EquipItem item = (EquipItem) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

	public void updateItem(Player player, EquipItem item) {
		getItemStore().updateItem(item);
		ClientDataSynMgr.updateData(player, item, equipSynType, eSynOpType.UPDATE_SINGLE);
		notifyChange();
	}

	public EquipItem getItem(String ownerId, int equipIndex) {
		String itemId = EquipItemHelper.getItemId(ownerId, equipIndex);
		return getItem(itemId);
	}

	public EquipItem getItem(String itemId) {
		return getItemStore().getItem(itemId);
	}

	public boolean removeItem(Player player, EquipItem item) {
		boolean success = getItemStore().removeItem(item.getId());
		if (success) {
			ClientDataSynMgr.updateData(player, item, equipSynType, eSynOpType.REMOVE_SINGLE);
			notifyChange();
		}
		return success;
	}

	public boolean wearEquip(Player player, int equipIndex, ItemData itemData) {
		//TODO 穿装备的逻辑是否有问题？如果原有装备
		EquipItem equipItemOld = null;
		Enumeration<EquipItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			EquipItem item = (EquipItem) mapEnum.nextElement();
			if(item.getEquipIndex() == equipIndex){
				equipItemOld = item;
				break;
			}
		}
		
		boolean success = true;
		if (equipItemOld != null) {
			success = removeItem(player, equipItemOld);
		}
		if (success) {
			EquipItem newItem = EquipItemHelper.toEquip(ownerId, equipIndex, itemData);
			success = addItem(player, newItem);
		}
		return success;
	}

	private boolean addItem(Player player, EquipItem item) {
		boolean addSuccess = getItemStore().addItem(item);
		if (addSuccess) {
			ClientDataSynMgr.updateData(player, item, equipSynType, eSynOpType.ADD_SINGLE);
			notifyChange();
		}
		return addSuccess;
	}

	public AttrData toAttrData() {
		AttrData totalAttrData = new AttrData();
		Enumeration<EquipItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			EquipItem item = (EquipItem) mapEnum.nextElement();
			HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getConfig(item.getModelId());
			if (cfg != null) {
				totalAttrData.plus(EquipItemHelper.toAttrData(cfg, item.getLevel()));
			}
		}
		return totalAttrData;
	}

	public void synAllData(Player player, int version) {
		List<EquipItem> itemList = getItemList();
		ClientDataSynMgr.synDataList(player, itemList, equipSynType, eSynOpType.UPDATE_LIST);
	}

	public void flush() {
		getItemStore().flush();
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

	/**
	 * 为机器人添加一个新的装备
	 * 
	 * @param equipIndex
	 * @param itemData
	 */
	public void addRobotEquip(int equipIndex, ItemData itemData) {
		// 添加
		EquipItem equipItem = EquipItemHelper.toEquip(ownerId, equipIndex, itemData);
		getItemStore().addItem(equipItem);
	}
	
	private MapItemStore<EquipItem> getItemStore(){
		MapItemStoreCache<EquipItem> cache = MapItemStoreFactory.getEquipCache();
		return cache.getMapItemStore(ownerId, EquipItem.class);
	}
}