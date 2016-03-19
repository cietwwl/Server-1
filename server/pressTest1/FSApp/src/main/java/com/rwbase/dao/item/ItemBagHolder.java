package com.rwbase.dao.item;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.service.role.MainMsgHandler;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.RecordSynchronization;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.IUpdateItem;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/*
 * @author HC
 * @date 2015年10月17日 下午3:35:43
 * @Description 
 */
public class ItemBagHolder implements RecordSynchronization{
	//private MapItemStore<ItemData> itemDataStore;// 背包中的数据
	// private Map<String, ItemData> itemDataMap = new HashMap<String,
	// ItemData>();
	
	private final String userId;

	private static final eSynType type = eSynType.USER_ITEM_BAG;// 更新背包数据

	public ItemBagHolder(String userId) {
		this.userId = userId;
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
			ClientDataSynMgr.synDataList(player, itemDataList, type, eSynOpType.UPDATE_LIST);
		}
	}
	
	private MapItemStore<ItemData> getItemStore(){
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
	 * 更新背包的操作
	 * 
	 * @param player
	 *            角色
	 * @param newItemList
	 *            新创建物品的列表
	 * @param updateItemList
	 *            更新物品的列表
	 */
	public void updateItemBag(Player player, List<INewItem> newItemList, List<IUpdateItem> updateItemList) {
		List<ItemData> updateItems = new ArrayList<ItemData>();
		String userId = player.getUserId();
		MapItemStore<ItemData> itemDataStore = getItemStore();
		if (newItemList != null && !newItemList.isEmpty()) {// 新创建物品列表不为空
			for (int i = 0, size = newItemList.size(); i < size; i++) {
				INewItem newItem = newItemList.get(i);
				ItemData itemData = new ItemData();
				int templateId = newItem.getCfgId();
				// 检测获取的是不是AS级的法宝
				if (ItemCfgHelper.getItemType(templateId) == EItemTypeDef.Magic) {// 检测是不是法宝
					MagicCfg magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(templateId));
					if (magicCfg != null) {// 是S，A级法宝
						MainMsgHandler.getInstance().sendPmdFb(player, magicCfg.getName(), magicCfg.getQuality());
						/** 稀有法宝的获得,B,A、S级 **/
					}
				}

				if (itemData.init(templateId, newItem.getCount())) {
					String slotId = generateSlotId(userId);
					itemData.setId(slotId);// 设置物品Id
					itemData.setUserId(userId);// 设置角色Id
					itemDataStore.addItem(itemData);// 添加新的

					// 回调
					if (newItem.getCallback() != null) {
						newItem.getCallback().call(itemData);
					}

					updateItems.add(itemData);
				}
			}
		}

		if (updateItemList != null && !updateItemList.isEmpty()) {// 更新物品列表不为空
			for (int i = 0, size = updateItemList.size(); i < size; i++) {
				IUpdateItem updateItem = updateItemList.get(i);
				ItemData itemData = this.getItemData(updateItem.getSlotId());
				if (itemData == null) {
					continue;
				}

				int count = itemData.getCount() + updateItem.getCount();
				itemData.setCount(count);// 更新数量
				if (count <= 0) {
					itemDataStore.removeItem(itemData.getId());// 移除
				} else {
					itemDataStore.updateItem(itemData);// 刷新
					// 回调
					if (updateItem.getCallback() != null) {
						updateItem.getCallback().call(itemData);
					}
				}

				updateItems.add(itemData);
			}
		}

		// 推送数据改变
		if (!updateItems.isEmpty()) {
//			player.getFresherActivityMgr().doCheck(eActivityType.A_CollectionLevel);
//			player.getFresherActivityMgr().doCheck(eActivityType.A_CollectionType);
			ClientDataSynMgr.synDataList(player, updateItems, type, eSynOpType.UPDATE_LIST);
		}
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
		getItemStore().updateItem(itemData);
	}

	/**
	 * 生成背包中物品的格子Id
	 * 
	 * @return
	 */
	private String generateSlotId(String userId) {
		int nSlotId = 1;
		String id = userId + "_" + nSlotId;
		MapItemStore<ItemData> itemDataStore = getItemStore();
		while (nSlotId < 10000) {
			if (itemDataStore.getItem(id) == null) {
				return id;
			}

			id = userId + "_" + ++nSlotId;
		}
		return id;
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

	@Override
	public void synAllData(Player player, int version) {
		// TODO Auto-generated method stub
		
	}
}