package com.rwbase.dao.equipment;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.IHeroAction;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.hero.HeroExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class EquipItemHolder {

	// final private String ownerId; //
	// final private eSynType equipSynType = eSynType.EQUIP_ITEM;

	// // private Map<Integer, EquipItem> equipSlotMap = new HashMap<Integer,
	// EquipItem>();
	//
	// public EquipItemHolder(String ownerIdP) {
	// ownerId = ownerIdP;
	// // for (EquipItem equipItemTmp : getItemList()) {
	// // equipSlotMap.put(equipItemTmp.getEquipIndex(), equipItemTmp);
	// // }
	// }

	private static final EquipItemHolder _INSTANCE = new EquipItemHolder();

	public static final EquipItemHolder getInstance() {
		return _INSTANCE;
	}

	final private eSynType equipSynType = eSynType.EQUIP_ITEM;

	/*
	 * 获取用户已经拥有
	 */
	// public List<EquipItem> getItemList() {
	public List<EquipItem> getItemList(String heroId) {
		List<EquipItem> itemList = new ArrayList<EquipItem>();
		Enumeration<EquipItem> mapEnum = getItemStore(heroId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			EquipItem item = (EquipItem) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

	// public void updateItem(Player player, EquipItem item) {
	public void updateItem(Player player, String heroId, EquipItem item) {
		getItemStore(heroId).update(item.getId());
		ClientDataSynMgr.updateData(player, item, equipSynType, eSynOpType.UPDATE_SINGLE);
		// notifyChange();
		notifyChange(player.getUserId(), heroId);
	}

	// public EquipItem getItem(String itemId) {
	public EquipItem getItem(String heroId, Integer itemId) {
		return getItemStore(heroId).get(itemId);
	}

	// public boolean removeItem(Player player, EquipItem item) {
	public boolean removeItem(Player player, String heroId, EquipItem item) {
		boolean success = getItemStore(heroId).removeItem(item.getId());
		if (success) {
			ClientDataSynMgr.updateData(player, item, equipSynType, eSynOpType.REMOVE_SINGLE);
			// notifyChange();
			notifyChange(player.getUserId(), heroId);
		}
		return success;
	}

	public boolean removeAllItem(Player player, String heroId, List<EquipItem> items) {
		// 次方法主要是优化通知notifyChange()和调用mapItemStore的次数
		if (items.isEmpty()) {
			return true;
		} else if (items.size() == 1) {
			return removeItem(player, heroId, items.get(0));
		}
		List<Integer> idList = new ArrayList<Integer>();
		for (int i = 0, size = items.size(); i < size; i++) {
			idList.add(items.get(i).getId());
		}
		List<Integer> removeIds = getItemStore(heroId).removeItem(idList);
		boolean success = false;
		if (removeIds.size() > 0) {
			success = idList.size() == removeIds.size();
			for (int i = 0, size = items.size(); i < size; i++) {
				EquipItem temp = items.get(i);
				if (removeIds.contains(temp.getId())) {
					// 已经删除成功的
					ClientDataSynMgr.synData(player, temp, equipSynType, eSynOpType.REMOVE_SINGLE);
					removeIds.remove(temp.getId());
					if (removeIds.isEmpty()) {
						break;
					}
				}
			}
			notifyChange(player.getUserId(), heroId);
		}
		return success;
	}

	// public boolean wearEquip(Player player, int equipIndex, ItemData
	// itemData) {
	public boolean wearEquip(Player player, String heroId, int equipIndex, ItemData itemData) {
		// TODO 穿装备的逻辑是否有问题？如果原有装备
		EquipItem equipItemOld = null;
		Enumeration<EquipItem> mapEnum = getItemStore(heroId).getExtPropertyEnumeration();
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

	public boolean wearEquips(Player player, String heroId, Map<Integer, ItemData> itemDatasOfNewEquips) {
		if (itemDatasOfNewEquips.isEmpty()) {
			return false;
		}
		List<EquipItem> newEquipDatas = new ArrayList<EquipItem>();
		List<Integer> removeItemIds = new ArrayList<Integer>();
		List<EquipItem> removeItems = new ArrayList<EquipItem>();
		PlayerExtPropertyStore<EquipItem> mapItemStore = getItemStore(heroId);
		Enumeration<EquipItem> mapEnum = mapItemStore.getExtPropertyEnumeration();
		int newItemSize = itemDatasOfNewEquips.size();
		EquipItem equipItemOld = null;
		while (mapEnum.hasMoreElements()) {
			equipItemOld = mapEnum.nextElement();
			if (itemDatasOfNewEquips.containsKey(equipItemOld.getEquipIndex())) {
				removeItemIds.add(equipItemOld.getId());
				removeItems.add(equipItemOld);
				if (removeItemIds.size() == newItemSize) {
					break;
				}
			}
		}
		int index;
		for (Iterator<Integer> itr = itemDatasOfNewEquips.keySet().iterator(); itr.hasNext();) {
			index = itr.next();
			newEquipDatas.add(EquipItemHelper.toEquip(heroId, index, itemDatasOfNewEquips.get(index)));
		}
		boolean success = true;
		if (removeItemIds.size() > 0) {
			if ((success = (mapItemStore.removeItem(removeItemIds).size() == removeItemIds.size()))) {
				for (int i = 0, size = removeItems.size(); i < size; i++) {
					ClientDataSynMgr.updateData(player, removeItems.get(i), equipSynType, eSynOpType.REMOVE_SINGLE);
				}
			}
		}
		if (success) {
			try {
				success = mapItemStore.addItem(newEquipDatas);
			} catch (DuplicatedKeyException e) {
				GameLog.error("EquipItemHolder", heroId, "一键穿装异常！", e);
				success = false;
			}
			if (success) {
				for (int i = 0; i < newItemSize; i++) {
					ClientDataSynMgr.synData(player, newEquipDatas.get(i), equipSynType, eSynOpType.ADD_SINGLE);
				}
				notifyChange(player.getUserId(), heroId);
			} else if (removeItemIds.size() > 0) {
				this.notifyChange(player.getUserId(), heroId);
			}
		}
		return success;
	}

	private boolean addItem(Player player, String heroId, EquipItem item) {
		boolean addSuccess = getItemStore(heroId).addItem(item);
		if (addSuccess) {
			ClientDataSynMgr.updateData(player, item, equipSynType, eSynOpType.ADD_SINGLE);
			// notifyChange();
			notifyChange(player.getUserId(), heroId);
		}
		return addSuccess;
	}

	// public AttrData toAttrData() {
	// AttrData totalAttrData = new AttrData();
	// Enumeration<EquipItem> mapEnum = getItemStore().getEnum();
	// while (mapEnum.hasMoreElements()) {
	// EquipItem item = (EquipItem) mapEnum.nextElement();
	// HeroEquipCfg cfg =
	// HeroEquipCfgDAO.getInstance().getConfig(item.getModelId());
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
	
	/**
	 * 为机器人添加一些装备
	 * 
	 * @param equipIndex
	 * @param itemData
	 */
	public void addRobotEquip(String heroId, List<EquipItem> equipList) {
		// 添加
		try {
			getItemStore(heroId).addItem(equipList);
		} catch (DuplicatedKeyException e) {
			e.printStackTrace();
		}
	}

	private PlayerExtPropertyStore<EquipItem> getItemStore(String ownerId) {
		RoleExtPropertyStoreCache<EquipItem> cache = RoleExtPropertyFactory.getHeroExtCache(HeroExtPropertyType.EQUIP_ITEM, EquipItem.class);
		try {
			return cache.getStore(ownerId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}