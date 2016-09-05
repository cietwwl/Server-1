package com.rwbase.dao.item;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.common.RefInt;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.RecordSynchronization;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/*
 * @author HC
 * @date 2015年10月17日 下午3:35:43
 * @Description 
 */
public class ItemBagHolder implements RecordSynchronization {
	// private MapItemStore<ItemData> itemDataStore;// 背包中的数据
	// private Map<String, ItemData> itemDataMap = new HashMap<String,
	// ItemData>();

	private final String userId;
	private AtomicLong generateId;// 生成Id

	private static final eSynType type = eSynType.USER_ITEM_BAG;// 更新背包数据

	public ItemBagHolder(String userId) {
		this.userId = userId;
		initMaxId();
	}

	/**
	 * 解析产生的Id
	 */
	private void initMaxId() {
		MapItemStore<ItemData> itemStore = getItemStore();
		Enumeration<ItemData> enumeration = itemStore.getEnum();

		int maxId = 1;
		while (enumeration.hasMoreElements()) {
			ItemData itemData = enumeration.nextElement();
			String id = itemData.getId();
			String[] arr = id.split("_");
			int idValue = Integer.parseInt(arr[1]);
			if (idValue > maxId) {
				maxId = idValue;
			}
		}

		generateId = new AtomicLong(maxId);
	}

	/**
	 * 推送所有的物品信息
	 * 
	 * @param player
	 */
	public void syncAllData(Player player) {
		Enumeration<ItemData> itemValues = getItemStore().getEnum();
		List<ItemData> itemDataList = new ArrayList<ItemData>();
		while (itemValues.hasMoreElements()) {
			itemDataList.add(itemValues.nextElement());
		}

		if (!itemDataList.isEmpty()) {
			ClientDataSynMgr.synDataList(player, itemDataList, type, eSynOpType.UPDATE_LIST, -1);
		}
	}

	private MapItemStore<ItemData> getItemStore() {
		MapItemStoreCache<ItemData> cache = MapItemStoreFactory.getItemCache();
		return cache.getMapItemStore(userId, ItemData.class);
	}

	/**
	 * 获取物品信息
	 * 
	 * @param id
	 * @return
	 */
	public ItemData getItemData(String id) {
		return getItemStore().getItem(id);
	}

	/**
	 * 通过物品的模版Id获取道具列表
	 * 
	 * @param modelId
	 * @return 相同物品模版Id的物品列表
	 */
	public List<ItemData> getItemDataByCfgId(int modelId) {
		List<ItemData> itemInfoList = new ArrayList<ItemData>();
		Enumeration<ItemData> itemValues = getItemStore().getEnum();
		while (itemValues.hasMoreElements()) {
			ItemData itemData = itemValues.nextElement();
			if (itemData.getModelId() == modelId) {
				itemInfoList.add(itemData);
			}
		}
		return itemInfoList;
	}

	/**
	 * 返回是否包含某种模型ID的道具
	 * 
	 * @param modelId
	 * @return
	 */
	public ItemData getFirstItemByModelId(int modelId) {
		Enumeration<ItemData> itemValues = getItemStore().getEnum();
		while (itemValues.hasMoreElements()) {
			ItemData itemData = itemValues.nextElement();
			if (itemData.getModelId() == modelId) {
				return itemData;
			}
		}
		return null;
	}

	/**
	 * 通过模版Id获取道具在背包中的数量
	 * 
	 * @param modelId
	 * @return
	 */
	public int getItemCountByModelId(int modelId) {
		int count = 0;

		Enumeration<ItemData> itemValues = getItemStore().getEnum();
		while (itemValues.hasMoreElements()) {
			ItemData itemData = itemValues.nextElement();
			if (itemData.getModelId() == modelId) {
				count += itemData.getCount();
			}
		}

		return count;
	}

	/**
	 * 获取道具modelId与数量的映射
	 * 
	 * @return
	 */
	public Map<Integer, RefInt> getModelCountMap() {
		HashMap<Integer, RefInt> map = new HashMap<Integer, RefInt>();
		Enumeration<ItemData> itemValues = getItemStore().getEnum();
		while (itemValues.hasMoreElements()) {
			ItemData itemData = itemValues.nextElement();
			Integer modelId = itemData.getModelId();
			RefInt intValue = map.get(modelId);
			int count = itemData.getCount();
			if (intValue == null) {
				map.put(modelId, new RefInt(count));
			} else {
				intValue.value += count;
			}
		}
		return map;
	}

	// /**
	// * 更新背包的操作
	// *
	// * @param player 角色
	// * @param newItemList 新创建物品的列表
	// * @param updateItemList 更新物品的列表
	// */
	// public void updateItemBag(Player player, List<INewItem> newItemList,
	// List<IUpdateItem> updateItemList) {
	// List<ItemData> updateItems = new ArrayList<ItemData>();
	// String userId = player.getUserId();
	// MapItemStore<ItemData> itemDataStore = getItemStore();
	// if (newItemList != null && !newItemList.isEmpty()) {// 新创建物品列表不为空
	// for (int i = 0, size = newItemList.size(); i < size; i++) {
	// INewItem newItem = newItemList.get(i);
	// ItemData itemData = new ItemData();
	// int templateId = newItem.getCfgId();
	//
	// // 检测获取的是不是AS级的法宝
	// if (ItemCfgHelper.getItemType(templateId) == EItemTypeDef.Magic) {//
	// 检测是不是法宝
	// MagicCfg magicCfg = (MagicCfg)
	// MagicCfgDAO.getInstance().getCfgById(String.valueOf(templateId));
	// if (magicCfg != null) {// 是S，A级法宝
	// MainMsgHandler.getInstance().sendPmdFb(player, magicCfg.getName(),
	// magicCfg.getQuality());
	// /** 稀有法宝的获得,B,A、S级 **/
	// }
	// }
	//
	// if (itemData.init(templateId, newItem.getCount())) {
	// String slotId = generateSlotId(userId);
	// itemData.setId(slotId);// 设置物品Id
	// itemData.setUserId(userId);// 设置角色Id
	// itemDataStore.addItem(itemData);// 添加新的
	//
	// // 回调
	// if (newItem.getCallback() != null) {
	// newItem.getCallback().call(itemData);
	// }
	//
	// updateItems.add(itemData);
	// }
	// }
	// }
	//
	// if (updateItemList != null && !updateItemList.isEmpty()) {// 更新物品列表不为空
	// for (int i = 0, size = updateItemList.size(); i < size; i++) {
	// IUpdateItem updateItem = updateItemList.get(i);
	// ItemData itemData = this.getItemData(updateItem.getSlotId());
	// if (itemData == null) {
	// continue;
	// }
	//
	// int count = itemData.getCount() + updateItem.getCount();
	// itemData.setCount(count);// 更新数量
	// if (count <= 0) {
	// itemDataStore.removeItem(itemData.getId());// 移除
	// } else {
	// itemDataStore.updateItem(itemData);// 刷新
	// // 回调
	// if (updateItem.getCallback() != null) {
	// updateItem.getCallback().call(itemData);
	// }
	// }
	//
	// updateItems.add(itemData);
	// }
	// }
	//
	// // 推送数据改变
	// if (!updateItems.isEmpty()) {
	// player.getFresherActivityMgr().doCheck(eActivityType.A_CollectionLevel);
	// player.getFresherActivityMgr().doCheck(eActivityType.A_CollectionType);
	// player.getFresherActivityMgr().doCheck(eActivityType.A_CollectionMagic);
	// syncItemData(player, updateItems);
	// }
	// }

	/**
	 * 更新数据
	 */
	public void syncItemData(Player player, List<ItemData> itemList) {
		// 推送数据改变
		if (!itemList.isEmpty()) {
			ClientDataSynMgr.synDataList(player, itemList, type, eSynOpType.UPDATE_LIST);
		}
	}

	/**
	 * 刷新背包数据
	 * 
	 * @param itemData
	 */
	public void updateItem(ItemData itemData) {
		getItemStore().updateItem(itemData);
	}

	/**
	 * 生成背包中物品的格子Id
	 * 
	 * @return
	 */
	private String generateSlotId(String userId) {
		// int nSlotId = 1;
		// String id = userId + "_" + nSlotId;
		// MapItemStore<ItemData> itemDataStore = getItemStore();
		// while (nSlotId < 10000) {
		// if (itemDataStore.getItem(id) == null) {
		// return id;
		// }
		//
		// id = userId + "_" + ++nSlotId;
		// }
		// return id;

		long newId = generateId.incrementAndGet();
		StringBuilder sb = new StringBuilder();
		return sb.append(userId).append("_").append(newId).toString();
	}

	public void flush() {
		getItemStore().flush();
	}

	/**
	 * 根据物品类型获取背包中的物品数据
	 * 
	 * @param itemType
	 * @return
	 */
	public List<ItemData> getItemListByType(EItemTypeDef itemType) {
		List<ItemData> itemList = new ArrayList<ItemData>();
		Enumeration<ItemData> allItem = getItemStore().getEnum();
		while (allItem.hasMoreElements()) {
			ItemData item = allItem.nextElement();
			if (item.getType() == itemType) {
				itemList.add(item);
			}
		}
		return itemList;
	}

	/**
	 * 增加数据
	 * 
	 * @param modelId
	 * @param count
	 * @return
	 */
	public ItemData newItemData(int modelId, int count) {
		ItemData itemData = new ItemData();
		if (itemData.init(modelId, count)) {
			String slotId = generateSlotId(userId);
			itemData.setId(slotId);// 设置物品Id
			itemData.setUserId(userId);// 设置角色Id
		}
		return itemData;
	}

	/**
	 * 更新背包中的改变数据
	 * 
	 * @param player
	 * @param addItemList
	 * @param updateItemList
	 * @throws DuplicatedKeyException
	 * @throws DataNotExistException
	 * @throws ItemNotExistException
	 */
	public void updateItemBgData(Player player, List<ItemData> addItemList, List<ItemData> updateItemList) throws DuplicatedKeyException, DataNotExistException {
		if ((addItemList == null || addItemList.isEmpty()) && (updateItemList == null || updateItemList.isEmpty())) {
			return;
		}

		List<ItemData> synUpdateItemList = new ArrayList<ItemData>();
		MapItemStore<ItemData> itemStore = getItemStore();
		if (addItemList != null && !addItemList.isEmpty()) {
			// itemStore.addItem(addItemList);// 新增加

			synUpdateItemList.addAll(addItemList);// 修改的数据

			// 通知法宝
			for (int i = 0, size = addItemList.size(); i < size; i++) {
				ItemData item = addItemList.get(i);
				int templateId = item.getModelId();
				// 检测获取的是不是AS级的法宝
				if (ItemCfgHelper.getItemType(templateId) == EItemTypeDef.Magic) {// 检测是不是法宝
					MagicCfg magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(templateId));
					if (magicCfg != null) {// 是S，A级法宝
						MainMsgHandler.getInstance().sendPmdFb(player, magicCfg.getName(), magicCfg.getQuality());
					}
				}
			}
		}

		List<String> updateIdList = new ArrayList<String>();
		List<String> deleteIdList = new ArrayList<String>();

		// 更新
		if (updateItemList != null && !updateItemList.isEmpty()) {
			for (int i = 0, size = updateItemList.size(); i < size; i++) {
				ItemData itemData = updateItemList.get(i);
				int count = itemData.getCount();
				String id = itemData.getId();
				if (count <= 0) {
					deleteIdList.add(id);
				} else {
					updateIdList.add(id);
				}

				// itemStore.updateItem(itemData);
			}

			synUpdateItemList.addAll(updateItemList);
		}

		// 删除
		if (deleteIdList != null && !deleteIdList.isEmpty()) {
			itemStore.removeItem(deleteIdList);
		}

		// 更新跟增加
		itemStore.updateItems(addItemList, updateIdList);

		// 推送数据改变
		if (!synUpdateItemList.isEmpty()) {
			player.getFresherActivityMgr().doCheck(eActivityType.A_CollectionLevel);
			player.getFresherActivityMgr().doCheck(eActivityType.A_CollectionType);
			player.getFresherActivityMgr().doCheck(eActivityType.A_CollectionMagic);
			// 推送数据到前台
			syncItemData(player, synUpdateItemList);
		}
	}

	@Override
	public void synAllData(Player player, int version) {
	}
}