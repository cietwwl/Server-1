//package com.rwbase.dao.item.pojo;
//
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.concurrent.ConcurrentHashMap;
//
//import javax.persistence.Id;
//import javax.persistence.Table;
//import javax.persistence.Transient;
//
//import org.codehaus.jackson.annotate.JsonIgnore;
//import org.codehaus.jackson.annotate.JsonIgnoreProperties;
//
//import com.playerdata.Player;
//import com.rwbase.dao.item.TableItemDAO;
//import com.rwbase.dao.item.pojo.itembase.INewItem;
//import com.rwbase.dao.item.pojo.itembase.IUpdateItem;
//import com.rwbase.dao.item.pojo.readonly.TableItemIF;
//
//@JsonIgnoreProperties(ignoreUnknown = true)
//@Table(name = "mt_table_item_bag")
//public class TableItem implements TableItemIF {
//	@Id
//	private String userId;
//	private int extSlotIndex = 0;// 扩展到的Slot index
//	private ConcurrentHashMap<String, ItemData> itemList = new ConcurrentHashMap<String, ItemData>();// 物品列表,ItemData:物品数据
//	/** 背包中物品变化的信息 */
//	@JsonIgnore
//	@Transient
//	private ConcurrentHashMap<String, ItemData> bagChangeItemInfo = new ConcurrentHashMap<String, ItemData>();
//
//	public String getUserId() {
//		return userId;
//	}
//
//	public void setUserId(String userId) {
//		this.userId = userId;
//	}
//
//	public int getExtSlotIndex() {
//		return extSlotIndex;
//	}
//
//	public void setExtSlotIndex(int extSlotIndex) {
//		this.extSlotIndex = extSlotIndex;
//	}
//
//	/** 仅仅是为了防止出错 */
//	public Map<String, ItemData> getItemList() {
//		return this.itemList;
//	}
//
//	public ItemData getItemData(String slotId) {
//		return itemList.get(slotId);
//	}
//
//	public int getItemSize() {
//		return itemList.size();
//	}
//
//	public void setItemList(Map<String, ItemData> itemList) {
//		this.itemList = new ConcurrentHashMap<String, ItemData>(itemList);
//	}
//
//	/**
//	 * 清除物品的缓存
//	 */
//	public void clearItemChangeInfo() {
//		this.bagChangeItemInfo.clear();
//	}
//
//	/**
//	 * 增加物品的改变信息到列表中
//	 * 
//	 * @param id
//	 * @param itemData
//	 */
//	public void putItemChangeInfo(String id, ItemData itemData) {
//		this.bagChangeItemInfo.put(id, itemData);
//	}
//
//	/**
//	 * 获取背包中变化的信息
//	 * 
//	 * @return
//	 */
//	@JsonIgnore
//	public Map<String, ItemData> getItemChangeInfo() {
//		return new HashMap<String, ItemData>(bagChangeItemInfo);
//	}
//
//	/**
//	 * 通过物品的模版Id获取道具列表
//	 * 
//	 * @param cfgId
//	 * @return 相同物品模版Id的物品列表
//	 */
//	@JsonIgnore
//	public List<ItemData> getItemDataByCfgId(int cfgId) {
//		List<ItemData> itemInfoList = new ArrayList<ItemData>();
//		for (Entry<String, ItemData> entry : this.itemList.entrySet()) {
//			ItemData item = entry.getValue();
//			if (item != null && item.getModelId() == cfgId) {
//				item.setId(entry.getKey());
//				itemInfoList.add(item);
//			}
//		}
//
//		return itemInfoList;
//	}
//
//	/**
//	 * 更新背包的操作
//	 * 
//	 * @param player 角色
//	 * @param newItemList 新创建物品的列表
//	 * @param updateItemList 更新物品的列表
//	 */
//	public void updateItemBag(Player player, List<INewItem> newItemList, List<IUpdateItem> updateItemList) {
//		boolean hasChange = false;
//		if (newItemList != null && !newItemList.isEmpty()) {// 新创建物品列表不为空
//			for (int i = 0, size = newItemList.size(); i < size; i++) {
//				INewItem newItem = newItemList.get(i);
//				ItemData itemData = new ItemData();
//				if (itemData.init(newItem.getCfgId(), newItem.getCount())) {
//					String slotId = generateSlotId();
//					itemData.setId(slotId);// 设置物品Id
//					this.itemList.put(slotId, itemData);
//
//					// 回调
//					if (newItem.getCallback() != null) {
//						newItem.getCallback().call(itemData);
//					}
//
//					bagChangeItemInfo.put(slotId, itemData);
//					hasChange = true;
//				}
//			}
//		}
//
//		if (updateItemList != null && !updateItemList.isEmpty()) {// 更新物品列表不为空
//			for (int i = 0, size = updateItemList.size(); i < size; i++) {
//				IUpdateItem updateItem = updateItemList.get(i);
//				ItemData itemData = this.getItemData(updateItem.getSlotId());
//				if (itemData == null) {
//					continue;
//				}
//
//				int count = itemData.getCount() + updateItem.getCount();
//				itemData.setCount(count);// 更新数量
//				if (count <= 0) {
//					this.itemList.remove(updateItem.getSlotId());// 物品删除
//				}
//
//				// 回调
//				if (updateItem.getCallback() != null) {
//					updateItem.getCallback().call(itemData);
//				}
//
//				bagChangeItemInfo.put(updateItem.getSlotId(), itemData);
//				hasChange = true;
//			}
//		}
//
//		// 更新数据
//		if (hasChange) {
//			TableItemDAO.getInstance().update(this);
//		}
//
//		syncItemBagChangeInfo(player);
//	}
//
//	private void syncItemBagChangeInfo(Player player) {
//		if (!this.bagChangeItemInfo.isEmpty()) {
//			// 推送背包的修改的物品数据
//			ItemBagHelper.sendItemInfo(player, bagChangeItemInfo);
//			this.bagChangeItemInfo.clear();
//		}
//	}
//
//	/**
//	 * 生成背包中物品的格子Id
//	 * 
//	 * @return
//	 */
//	private String generateSlotId() {
//		int nSlotId = 1;
//		while (nSlotId < 10000) {
//			if (!this.itemList.containsKey(nSlotId)) {
//				return userId + "|" + nSlotId;
//			}
//			nSlotId++;
//		}
//		return userId + "|" + nSlotId;
//	}
//
//	/**
//	 * 直接添加物品
//	 * 
//	 * @param itemData
//	 */
//	public void addItem(ItemData itemData) {
//		String slotId = generateSlotId();
//		itemData.setId(slotId);
//		this.itemList.put(slotId, itemData);
//
//		// 增加推送
//		this.bagChangeItemInfo.put(slotId, itemData);
//	}
//
//	public void addSyncItemData(ItemData itemData) {
//		this.bagChangeItemInfo.put(itemData.getId(), itemData);
//	}
//
//	@Override
//	@JsonIgnore
//	public Enumeration<String> getItemKeys() {
//		return this.itemList.keys();
//	}
//
//	/**
//	 * 获取背包中更改的道具数量的长度
//	 * 
//	 * @return
//	 */
//	public int getItemBagChangeInfoSize() {
//		return this.bagChangeItemInfo.size();
//	}
// }