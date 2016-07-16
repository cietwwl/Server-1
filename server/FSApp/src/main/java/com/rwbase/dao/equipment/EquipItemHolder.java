package com.rwbase.dao.equipment;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.common.Action;
import com.common.IHeroAction;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class EquipItemHolder {

//	final private String ownerId; //
//	final private eSynType equipSynType = eSynType.EQUIP_ITEM;

//	// private Map<Integer, EquipItem> equipSlotMap = new HashMap<Integer, EquipItem>();
//
//	public EquipItemHolder(String ownerIdP) {
//		ownerId = ownerIdP;
//		// for (EquipItem equipItemTmp : getItemList()) {
//		// equipSlotMap.put(equipItemTmp.getEquipIndex(), equipItemTmp);
//		// }
//	}
	
	private static final EquipItemHolder _INSTANCE = new EquipItemHolder();
	
	public static final EquipItemHolder getInstance() {
		return _INSTANCE;
	}
	
	final private eSynType equipSynType = eSynType.EQUIP_ITEM;

	/*
	 * 获取用户已经拥有
	 */
//	public List<EquipItem> getItemList() {
	public List<EquipItem> getItemList(String heroId) {
		List<EquipItem> itemList = new ArrayList<EquipItem>();
		Enumeration<EquipItem> mapEnum = getItemStore(heroId).getEnum();
		while (mapEnum.hasMoreElements()) {
			EquipItem item = (EquipItem) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

//	public void updateItem(Player player, EquipItem item) {
	public void updateItem(Player player, String heroId, EquipItem item) {
		getItemStore(heroId).updateItem(item);
		ClientDataSynMgr.updateData(player, item, equipSynType, eSynOpType.UPDATE_SINGLE);
		notifyChange();
		notifyChange(player.getUserId(), heroId);
	}

	public EquipItem getItem(String ownerId, int equipIndex) {
		String itemId = EquipItemHelper.getItemId(ownerId, equipIndex);
		return getItem(ownerId, itemId);
	}

//	public EquipItem getItem(String itemId) {
	public EquipItem getItem(String heroId, String itemId) {
		return getItemStore(heroId).getItem(itemId);
	}

//	public boolean removeItem(Player player, EquipItem item) {
	public boolean removeItem(Player player, String heroId, EquipItem item) {
		boolean success = getItemStore(heroId).removeItem(item.getId());
		if (success) {
			ClientDataSynMgr.updateData(player, item, equipSynType, eSynOpType.REMOVE_SINGLE);
			notifyChange();
			notifyChange(player.getUserId(), heroId);
		}
		return success;
	}

//	public boolean wearEquip(Player player, int equipIndex, ItemData itemData) {
	public boolean wearEquip(Player player, String heroId, int equipIndex, ItemData itemData) {
		// TODO 穿装备的逻辑是否有问题？如果原有装备
		EquipItem equipItemOld = null;
		Enumeration<EquipItem> mapEnum = getItemStore(heroId).getEnum();
		while (mapEnum.hasMoreElements()) {
			EquipItem item = (EquipItem) mapEnum.nextElement();
			if (item.getEquipIndex() == equipIndex) {
				equipItemOld = item;
				break;
			}
		}

		boolean success = true;
		if (equipItemOld != null) {
			success = removeItem(player, heroId, equipItemOld);
		}
		if (success) {
			EquipItem newItem = EquipItemHelper.toEquip(heroId, equipIndex, itemData);
			success = addItem(player, heroId, newItem);
		}
		return success;
	}

	private boolean addItem(Player player, String heroId, EquipItem item) {
		boolean addSuccess = getItemStore(heroId).addItem(item);
		if (addSuccess) {
			ClientDataSynMgr.updateData(player, item, equipSynType, eSynOpType.ADD_SINGLE);
			notifyChange();
			notifyChange(player.getUserId(), heroId);
		}
		return addSuccess;
	}

	// public AttrData toAttrData() {
	// AttrData totalAttrData = new AttrData();
	// Enumeration<EquipItem> mapEnum = getItemStore().getEnum();
	// while (mapEnum.hasMoreElements()) {
	// EquipItem item = (EquipItem) mapEnum.nextElement();
	// HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getConfig(item.getModelId());
	// if (cfg != null) {
	// totalAttrData.plus(EquipItemHelper.toAttrData(cfg, item.getLevel()));
	// }
	// }
	// return totalAttrData;
	// }

	public void synAllData(Player player, String heroId, int version) {
		List<EquipItem> itemList = getItemList(heroId);
		ClientDataSynMgr.synDataList(player, itemList, equipSynType, eSynOpType.UPDATE_LIST);
	}

	public void flush(String heroId) {
		getItemStore(heroId).flush();
	}

	@Deprecated
	private List<Action> callbackList = new ArrayList<Action>();

	@Deprecated
	public void regChangeCallBack(Action callBack) {
		callbackList.add(callBack);
	}

	@Deprecated
	private void notifyChange() {
		for (Action action : callbackList) {
			action.doAction();
		}
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

	/**
	 * 为机器人添加一个新的装备
	 * 
	 * @param equipIndex
	 * @param itemData
	 */
	public void addRobotEquip(String heroId, int equipIndex, ItemData itemData) {
		// 添加
		EquipItem equipItem = EquipItemHelper.toEquip(heroId, equipIndex, itemData);
		getItemStore(heroId).addItem(equipItem);
	}

//	private MapItemStore<EquipItem> getItemStore() {
	private MapItemStore<EquipItem> getItemStore(String ownerId) {
		MapItemStoreCache<EquipItem> cache = MapItemStoreFactory.getEquipCache();
		return cache.getMapItemStore(ownerId, EquipItem.class);
	}
}