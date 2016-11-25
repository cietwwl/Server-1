package com.rwbase.dao.item;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static ItemBagHolder holder = new ItemBagHolder();

	public static ItemBagHolder getHolder() {
		return holder;
	}

	private static final eSynType type = eSynType.USER_ITEM_BAG;// 更新背包数据

	protected ItemBagHolder() {
	}

	/**
	 * 解析产生的Id
	 */
	public int initMaxId(String userId) {
		MapItemStore<ItemData> itemStore = getItemStore(userId);
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

		return maxId;
	}

	/**
	 * 推送所有的物品信息
	 * 
	 * @param player
	 */
	public void syncAllData(Player player) {
		Enumeration<ItemData> itemValues = getItemStore(player.getUserId()).getEnum();
		List<ItemData> itemDataList = new ArrayList<ItemData>();
		while (itemValues.hasMoreElements()) {
			itemDataList.add(itemValues.nextElement());
		}

		if (!itemDataList.isEmpty()) {
			ClientDataSynMgr.synDataList(player, itemDataList, type, eSynOpType.UPDATE_LIST, -1);
		}
	}

	/**
	 * 获取ItemStore
	 * 
	 * @param userId
	 * @return
	 */
	private MapItemStore<ItemData> getItemStore(String userId) {
		MapItemStoreCache<ItemData> cache = MapItemStoreFactory.getItemCache();
		return cache.getMapItemStore(userId, ItemData.class);
	}

	// /**
	// * 获取物品信息
	// *
	// * @param id
	// * @return
	// */
	// public ItemData getItemData(String id) {
	// return getItemStore().getItem(id);
	// }

	/**
	 * 通过物品的模版Id获取道具列表
	 * 
	 * @param userId
	 * @param modelId
	 * @return 相同物品模版Id的物品列表
	 */
	public List<ItemData> getItemDataByCfgId(String userId, int modelId) {
		List<ItemData> itemInfoList = new ArrayList<ItemData>();
		Enumeration<ItemData> itemValues = getItemStore(userId).getEnum();
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
	 * @param userId
	 * @param modelId
	 * @return
	 */
	public ItemData getFirstItemByModelId(String userId, int modelId) {
		Enumeration<ItemData> itemValues = getItemStore(userId).getEnum();
		while (itemValues.hasMoreElements()) {
			ItemData itemData = itemValues.nextElement();
			if (itemData.getModelId() == modelId) {
				return itemData;
			}
		}
		return null;
	}

	/**
	 * 获取道具modelId与数量的映射
	 * 
	 * @return {key=modelId,value=count}
	 */
	public Map<Integer, RefInt> getModelCountMap(String userId) {
		HashMap<Integer, RefInt> map = new HashMap<Integer, RefInt>();
		Enumeration<ItemData> itemValues = getItemStore(userId).getEnum();
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

	/**
	 * 获取背包中某模版ID对应的任意一个物品列表
	 * 
	 * @param userId
	 * @return
	 */
	public Map<Integer, ItemData> getModelFirstItemDataMap(String userId) {
		HashMap<Integer, ItemData> map = new HashMap<Integer, ItemData>();
		Enumeration<ItemData> itemValues = getItemStore(userId).getEnum();
		while (itemValues.hasMoreElements()) {
			ItemData itemData = itemValues.nextElement();
			Integer modelId = itemData.getModelId();
			ItemData item = map.get(modelId);
			if (item == null) {
				map.put(modelId, itemData);
			}
		}
		return map;
	}

	/**
	 * <pre>
	 * 检查是否有足够的道具数量
	 * 
	 * </pre>
	 * 
	 * @param userId
	 * @param itemsMap {key=modelId,value=count}
	 * @return
	 */
	public boolean hasEnoughItems(String userId, Map<Integer, Integer> itemsMap) {
		HashMap<Integer, RefInt> map = new HashMap<Integer, RefInt>();
		Enumeration<ItemData> itemValues = getItemStore(userId).getEnum();
		while (itemValues.hasMoreElements()) {
			ItemData itemData = itemValues.nextElement();
			Integer modelId = itemData.getModelId();
			// 不是要检查的道具跳过
			if (!itemsMap.containsKey(modelId)) {
				continue;
			}
			int count = itemData.getCount();
			RefInt intValue = map.get(modelId);
			if (intValue == null) {
				map.put(modelId, new RefInt(count));
			} else {
				intValue.value += count;
			}
		}
		for (Map.Entry<Integer, Integer> entry : itemsMap.entrySet()) {
			int value = entry.getValue();
			// 0或负数跳过
			if (value <= 0) {
				continue;
			}
			RefInt refValue = map.get(entry.getKey());
			if (refValue == null) {
				return false;
			}
			if (refValue.value < value) {
				return false;
			}
		}
		return true;
	}

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
		getItemStore(itemData.getUserId()).updateItem(itemData);
	}

	/**
	 * 更新数据
	 * 
	 * @param userId
	 */
	public void flush(String userId) {
		getItemStore(userId).flush();
	}

	/**
	 * 获取物品数据
	 * 
	 * @param userId
	 * @param id
	 * @return
	 */
	public ItemData getItemData(String userId, String id) {
		return getItemStore(userId).getItem(id);
	}

	/**
	 * 通过模版Id获取道具在背包中的数量
	 * 
	 * @param userId
	 * @param modelId
	 * @return
	 */
	public int getItemCountByModelId(String userId, int modelId) {
		int count = 0;

		Enumeration<ItemData> itemValues = getItemStore(userId).getEnum();
		while (itemValues.hasMoreElements()) {
			ItemData itemData = itemValues.nextElement();
			if (itemData.getModelId() == modelId) {
				count += itemData.getCount();
			}
		}

		return count;
	}

	/**
	 * 根据物品类型获取背包中的物品数据
	 * 
	 * @param itemType
	 * @return
	 */
	public List<ItemData> getItemListByType(String userId, EItemTypeDef itemType) {
		List<ItemData> itemList = new ArrayList<ItemData>();
		Enumeration<ItemData> allItem = getItemStore(userId).getEnum();
		while (allItem.hasMoreElements()) {
			ItemData item = allItem.nextElement();
			if (item.getType() == itemType) {
				itemList.add(item);
			}
		}
		return itemList;
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

		String userId = player.getUserId();
		List<ItemData> synUpdateItemList = new ArrayList<ItemData>();
		MapItemStore<ItemData> itemStore = getItemStore(userId);
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