package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.RefInt;
import com.playerdata.readonly.ItemBagMgrIF;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.ItemBagHolder;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.IUpdateItem;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.NewItem;
import com.rwbase.dao.item.pojo.itembase.UpdateItem;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/**
 * 背包数据管理类。
 * 
 * @author HC
 * @Modified-by HC
 * @date 2015-08-06
 */
public class ItemBagMgr implements ItemBagMgrIF {
	private Player player;
	/**
	 * 消耗物品规则的比较器
	 */
	private static final Comparator<ItemData> comparator = new Comparator<ItemData>() {

		@Override
		public int compare(ItemData o1, ItemData o2) {
			return o1.getCount() - o2.getCount();
		}
	};

	// private TableItem tableItem;// 背包
	// private TableItemDAO tableItemDao = TableItemDAO.getInstance();// 背包DAO实例
	// private HashMap<Integer, ItemData> itemChangeInfo;// 背包物品变化信息

	private ItemBagHolder holder;// 背包的数据层

	// 初始化
	public boolean init(Player player) {
		this.player = player;
		// tableItem = tableItemDao.get(pOwner.getUserId());
		// if (tableItem == null) {
		// tableItem = new TableItem();
		// tableItem.setUserId(pOwner.getUserId());
		// tableItemDao.update(tableItem);
		// }
		holder = new ItemBagHolder(player.getUserId());
		return true;
	}

	/**
	 * 在登出游戏的时候保存
	 */
	public void save() {
		holder.flush();
	}

	/**
	 * 推送所有的道具信息到客户端
	 */
	public void syncAllItemData() {
		holder.syncAllData(player);
	}

	// /**
	// * 登录游戏的时候，发送的数据
	// */
	// public void onLogin() {
	// if (tableItem.getItemSize() > 0) {
	// Map<String, ItemData> map = new HashMap<String, ItemData>();
	// Enumeration<String> itemKeys = tableItem.getItemKeys();
	// while (itemKeys.hasMoreElements()) {
	// String slotId = itemKeys.nextElement();
	// map.put(slotId, tableItem.getItemData(slotId));
	// }
	// // sendList(map);
	//
	// ItemBagHelper.sendItemInfo(player, map);
	// }
	// }

	/**
	 * 通过物品的背包位置获取物品的信息
	 * 
	 * @param id 物品的位置
	 * @return
	 */
	public ItemData findBySlotId(String id) {
		return holder.getItemData(id);
	}

	/**
	 * 获取物品数量
	 * 
	 * @param modelId 道具模板Id
	 * @return
	 */
	public int getItemCountByModelId(int modelId) {
		return holder.getItemCountByModelId(modelId);
	}

	/**
	 *
	 * 解析某种物品，然后放入到背包中去
	 * 
	 * @param strPrize
	 */
	public void addItemByPrizeStr(String strPrize) {
		String[] arrPrizes = strPrize.split(",");

		int size = arrPrizes.length;
		List<INewItem> newItemList = new ArrayList<INewItem>(size);
		for (int i = 0; i < size; i++) {
			String[] arrItem = arrPrizes[i].split("~");
			if (arrItem.length < 2)
				continue;
			int itemId = Integer.valueOf(arrItem[0]);
			int itemCount = Integer.valueOf(arrItem[1]);

			if (itemId < eSpecialItemId.eSpecial_End.getValue() || ItemCfgHelper.isFashionSpecialItem(itemId)) {
				addItem(itemId, itemCount);
			} else {
				INewItem newItem = new NewItem(itemId, itemCount, null);
				newItemList.add(newItem);
			}
		}

		// 增加新的道具
		if (!newItemList.isEmpty()) {
			useLikeBoxItem(null, newItemList);
		}
	}

	/**
	 * 清除背包中的数据
	 */
	public void removeAllItems() {
		// Iterator<Entry<Integer, ItemData>> iter = tableItem.getItemList().entrySet().iterator();
		// Enumeration<String> itemKeys = tableItem.getItemKeys();
		// while (itemKeys.hasMoreElements()) {
		// String slotId = itemKeys.nextElement();
		// ItemData itemData = tableItem.getItemData(slotId);
		// useItemBySlotId(slotId, itemData.getCount());
		// }
	}

	// /**
	// * 红点？？
	// *
	// * @param nid
	// */
	// private void CheckEnhanceHot(int nid) {
	// HashMap<Integer, ConsumeCfg> cfglist = ConsumeCfgDAO.getInstance().getEnhanceMap();
	// if (cfglist.containsKey(nid)) {
	// m_pPlayer.m_EquipMgr.CheckHot();
	// }
	// }

	// /**
	// * 检测佣兵品质为参数的佣兵装备
	// *
	// * @param quality
	// * @return
	// */
	// public int checkQuality(int count,int quality) {
	// // for (ItemData item : tableItem.getItemList().values()) {
	// Enumeration<Integer> itemKeys = tableItem.getItemKeys();
	// while (itemKeys.hasMoreElements()) {
	// Integer slotId = itemKeys.nextElement();
	// ItemData item = tableItem.getItemData(slotId);
	// if (item == null) {
	// continue;
	// }
	//
	// eItemTypeDef eItemType = ItemCfgHelper.getItemType(item.getId());
	// if (item.getCount() > 0 && eItemType == eItemTypeDef.HeroEquip) {
	// HeroEquipCfg cfg = ItemCfgHelper.getHeroEquipCfg(item.getId());
	// if (cfg.getQuality() == quality) {
	// return 1;
	// }
	// }
	// }
	// return 0;
	// }

	// /**
	// * 发送物品背包数据，响应消息不用每次都设置消息类型 response.setEventType(EItemBagEventType.ItemBag_Sync);应该设置到while循环外，只设置一次
	// *
	// * @param syncItemMap
	// */
	// private void sendList(Map<Integer, ItemData> syncItemMap) {
	// MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
	// // Iterator<Entry<Integer, ItemData>> iter = list.entrySet().iterator();
	// // while (iter.hasNext()) {
	// // Map.Entry<Integer, ItemData> entry = iter.next();
	// for (Entry<Integer, ItemData> entry : syncItemMap.entrySet()) {
	// int soltId = entry.getKey();
	// ItemData item = entry.getValue();
	//
	// TagItemData.Builder tagItem = TagItemData.newBuilder();
	// tagItem.setSolt(soltId);
	// tagItem.setId(item.getId());
	// tagItem.setCount(item.getCount());
	//
	// Enumeration<eItemAttrIdDef> keys = item.getEnumerationKeys();
	// while (keys.hasMoreElements()) {
	// eItemAttrIdDef attrId = keys.nextElement();
	// TagItemAttriData.Builder attrData = TagItemAttriData.newBuilder();
	// attrData.setAttrId(attrId.ordinal());
	// attrData.setAttValue(item.getExtendAttr(attrId));
	// tagItem.addExtendAttr(attrData);
	// }
	//
	// // if (extendAttr != null) {
	// // Iterator<Entry<eItemAttrIdDef, String>> iter1 = extendAttr.entrySet().iterator();
	// // while (iter1.hasNext()) {
	// // Map.Entry<eItemAttrIdDef, String> entry1 = iter1.next();
	// // TagItemAttriData.Builder attrData = TagItemAttriData.newBuilder();
	// // attrData.setAttrId(entry1.getKey().ordinal());
	// // attrData.setAttValue(entry1.getValue());
	// // tagItem.addExtendAttr(attrData);
	// // }
	// // }
	// response.addItemSyncDatas(tagItem.build());
	// }
	// response.setEventType(EItemBagEventType.ItemBag_Sync);
	// m_pPlayer.SendMsg(Command.MSG_ItemBag, response.build().toByteString());
	// }
	//
	// /**
	// * 直接增加一组物品到数据库，这个方法只适用于单个叠加的物品，例如法宝这类 其他操作也能成功，但是不保证是想要的结果
	// *
	// * @param itemData
	// * @return
	// */
	// public boolean addItem(ItemData itemData) {
	// tableItem.addItem(itemData);
	// return true;
	// }

	/**
	 * 增加物品
	 * 
	 * @param cfgId 物品的模版Id
	 * @param count 增加物品的个数
	 * @return 当前返回的只是一个状态，但是以后可能会返回失败的详细信息（这里要改成返回一个类型码）
	 */
	public boolean addItem(int cfgId, int count) {
		
		//增加特殊物品时装的判断，时装物品不会设计为可以使用的物品
		//TODO franky 时装作为特殊物品占用了90000000 ~ 99999999
		if (ItemCfgHelper.isFashionSpecialItem(cfgId)){
			RefInt fashionId = new RefInt();
			RefInt expireTimeCount=new RefInt();
			ItemCfgHelper.parseFashionSpecialItem(cfgId, fashionId, expireTimeCount);
			return FashionMgr.giveFashionItem(fashionId.value, expireTimeCount.value, player, false, true, null);
		}
		return addItem0(cfgId, count, null, null, true);
	}

	/**
	 * 使用道具，为了简化外部调用者的处理，就直接把所有的错误或者异常信息都包含在当前这个方法里
	 * 
	 * @param slotId 使用道具的数据库Id
	 * @param count 使用道具的数量
	 * @return 当前返回的只是一个状态，但是以后可能会返回失败的详细信息（这里要改成返回一个类型码）
	 */
	public boolean useItemBySlotId(String slotId, int count) {
		if (count <= 0) {
			return false;
		}

		return useItem0(slotId, count, null, null, true);
	}

	/**
	 * 通过模版Id消耗物品
	 * 
	 * @param cfgId
	 * @param count
	 * @return
	 */
	public boolean useItemByCfgId(int cfgId, int count) {
		return useItemByCfgId0(cfgId, count, null, null, true);
	}

	/**
	 * 
	 * @param cfgId
	 * @param count
	 * @param dataOperation 是否直接数据库操作
	 * @return
	 */
	private boolean useItemByCfgId0(int cfgId, int count, List<INewItem> newItemList, List<IUpdateItem> updateItemList, boolean dataOperation) {
		if (count <= 0) {
			return false;
		}

		List<ItemData> itemList = holder.getItemDataByCfgId(cfgId);
		if (itemList.isEmpty()) {
			return false;
		}

		Collections.sort(itemList, comparator);// 物品排序

		return useItem0(itemList.get(0).getId(), count, newItemList, updateItemList, dataOperation);
	}

	/**
	 * 使用类宝箱类道具
	 * 
	 * @param useItemList 要使用物品列表(只能是物品类，不能是货币)
	 * @param addItemList 要产生的物品
	 * @param modifyMoneyMap 要使用的货币，使用货币的修改<如果是正数：加；如果是负数：减>
	 * @return
	 */
	public boolean useLikeBoxItem(List<IUseItem> useItemList, List<INewItem> addItemList, Map<Integer, Integer> modifyMoneyMap) {
		// 金钱操作
		if (modifyMoneyMap != null) {
			for (Entry<Integer, Integer> e : modifyMoneyMap.entrySet()) {
				Integer useCount = e.getValue();
				if (useCount == 0) {
					continue;
				}

				if (!addItem(e.getKey(), useCount)) {
					return false;
				}
			}
		}

		// 证明只用扣钱
		if ((useItemList == null || useItemList.isEmpty()) && (addItemList == null || addItemList.isEmpty())) {
			return true;
		}

		return useLikeBoxItem(useItemList, addItemList);
	}

	/**
	 * 使用类宝箱类道具
	 * 
	 * @param useItemList 要使用物品列表(只能是物品类，不能是货币)
	 * @param addItemList 要产生的物品
	 */
	public boolean useLikeBoxItem(List<IUseItem> useItemList, List<INewItem> addItemList) {
		if ((useItemList == null || useItemList.isEmpty()) && (addItemList == null || addItemList.isEmpty())) {
			return false;
		}

		List<IUpdateItem> updateItemList = new ArrayList<IUpdateItem>();// 更新的物品列表
		List<INewItem> newItemList = new ArrayList<INewItem>();// 要增加的物品

		// 要使用的物品
		if (useItemList != null && !useItemList.isEmpty()) {
			// 验证重复
			Map<String, Integer> tempMap = new HashMap<String, Integer>();
			Map<Integer, String> cacheSlotMap = new HashMap<Integer, String>();
			for (int i = 0, size = useItemList.size(); i < size; i++) {
				IUseItem useItem = useItemList.get(i);
				int useCount = useItem.getUseCount();
				if (useCount < 0) {
					return false;
				}

				String slotId = useItem.getSlotId();
				ItemData itemData = findBySlotId(slotId);
				if (itemData == null) {
					return false;
				}

				ItemBaseCfg baseCfg = ItemCfgHelper.GetConfig(itemData.getModelId());
				int stackNum = baseCfg.getStackNum();

				if (stackNum > 1) {
					if (!cacheSlotMap.containsKey(itemData.getModelId())) {
						cacheSlotMap.put(itemData.getModelId(), slotId);
					} else {
						slotId = cacheSlotMap.get(itemData.getModelId());
					}
				}

				Integer hasValue = tempMap.get(slotId);
				if (hasValue == null) {
					tempMap.put(slotId, useCount);
				} else {
					tempMap.put(slotId, useCount + hasValue);
				}
			}

			for (Entry<String, Integer> e : tempMap.entrySet()) {
				if (!useItem0(e.getKey(), e.getValue(), newItemList, updateItemList, false)) {
					return false;
				}
			}
		}

		// 要增加的物品
		if (addItemList != null && !addItemList.isEmpty()) {
			Map<Integer, Integer> tempMap = new HashMap<Integer, Integer>();
			for (int i = 0, size = addItemList.size(); i < size; i++) {
				INewItem newItem = addItemList.get(i);
				int count = newItem.getCount();
				if (count < 0) {
					return false;
				}

				int cfgId = newItem.getCfgId();
				Integer hasValue = tempMap.get(cfgId);
				if (hasValue == null) {
					tempMap.put(cfgId, count);
				} else {
					tempMap.put(cfgId, count + hasValue);
				}
			}

			for (Entry<Integer, Integer> e : tempMap.entrySet()) {
				if (!addItem0(e.getKey(), e.getValue(), newItemList, updateItemList, false)) {
					return false;
				}
			}
		}

		// 两个任意一个不为空就可以走下去更新背包处理
		if (!updateItemList.isEmpty() || !newItemList.isEmpty()) {
			holder.updateItemBag(player, newItemList, updateItemList);
			return true;
		}
		return false;
	}
	
	
	public boolean checkEnoughItem(int cfgId, int count){
		if (cfgId <= eSpecialItemId.eSpecial_End.getValue()) {
			if (cfgId == eSpecialItemId.Coin.getValue()) {
				return player.getUserGameDataMgr().getCoin() >= count;
			} else if (cfgId == eSpecialItemId.Gold.getValue()) {
				return player.getUserGameDataMgr().getGold() >= count;
			} else if (cfgId == eSpecialItemId.MagicSecretCoin.getValue()) {
				return player.getUserGameDataMgr().getMagicSecretCoin() >= count;
			} else if (cfgId == eSpecialItemId.BraveCoin.getValue()) {
				return player.getUserGameDataMgr().getTowerCoin() >= count;
			} else if (cfgId == eSpecialItemId.GuildCoin.getValue()) {
				return player.getUserGroupAttributeDataMgr().getUserGroupContribution() >= count;
			} else if (cfgId == eSpecialItemId.PeakArenaCoin.getValue()) {
				return player.getUserGameDataMgr().getPeakArenaCoin() >= count;
			}// 新增竞技场货币处理
			else if (cfgId == eSpecialItemId.ArenaCoin.getValue()) {
				return player.getUserGameDataMgr().getArenaCoin() >= count;
			}
			else if(cfgId == eSpecialItemId.WAKEN_PIECE.getValue()){
				return player.getUserGameDataMgr().getWakenPiece() >= count;
			}
			else if(cfgId == eSpecialItemId.WAKEN_KEY.getValue()){
				return player.getUserGameDataMgr().getWakenKey() >= count;
			}
		} else {// 操作道具
			if (count <= 0) {
				return false;
			}

			ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(cfgId);// 检查物品的基础模版
			if (itemBaseCfg == null) {
				return false;
			}

			List<ItemData> itemList = holder.getItemDataByCfgId(cfgId);
			int sum = 0;
			for (ItemData itemData : itemList) {
				sum+= itemData.getCount();
			}
			return sum >= count;
			
		}
		return false;
	}

	/**
	 * 增加物品
	 * 
	 * @param cfgId 物品的模版Id
	 * @param count 增加物品的个数
	 * @param newItemList
	 * @param updateItemList
	 * @param bagOperation 是否直接背包操作
	 */
	private boolean addItem0(int cfgId, int count, List<INewItem> newItemList, List<IUpdateItem> updateItemList, boolean bagOperation) {
		// final int nCount = count;
		if (cfgId <= eSpecialItemId.eSpecial_End.getValue()) {
			if (cfgId == eSpecialItemId.Coin.getValue()) {
				player.getUserGameDataMgr().addCoin(count);
			} else if (cfgId == eSpecialItemId.Gold.getValue()) {
				player.getUserGameDataMgr().addGold(count);
			} else if (cfgId == eSpecialItemId.Power.getValue()) {
				player.addPower(count);
			} else if (cfgId == eSpecialItemId.PlayerExp.getValue()) {
				player.addUserExp(count);
			} else if (cfgId == eSpecialItemId.MagicSecretCoin.getValue()) {
				player.getUserGameDataMgr().addMagicSecretCoin(count);
			} else if (cfgId == eSpecialItemId.BraveCoin.getValue()) {
				player.getUserGameDataMgr().addTowerCoin(count);
			} else if (cfgId == eSpecialItemId.GuildCoin.getValue()) {
				player.getUserGroupAttributeDataMgr().useUserGroupContribution(count);
			} else if (cfgId == eSpecialItemId.PeakArenaCoin.getValue()) {
				player.getUserGameDataMgr().addPeakArenaCoin(count);
			}// 新增竞技场货币处理
			else if (cfgId == eSpecialItemId.ArenaCoin.getValue()) {
				player.getUserGameDataMgr().addArenaCoin(count);
			} else if (cfgId == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
				player.getBattleTowerMgr().getTableBattleTower().modifyCopperKey(count);
			} else if (cfgId == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
				player.getBattleTowerMgr().getTableBattleTower().modifySilverKey(count);
			} else if (cfgId == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
				player.getBattleTowerMgr().getTableBattleTower().modifyGoldKey(count);
			} else if(cfgId == eSpecialItemId.WAKEN_PIECE.getValue()){
				player.getUserGameDataMgr().addWakenPiece(count);
			}else if(cfgId == eSpecialItemId.WAKEN_KEY.getValue()){
				player.getUserGameDataMgr().addWakenKey(count);
			}
		} else {// 操作道具
			if (count <= 0) {
				return false;
			}

			ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(cfgId);// 检查物品的基础模版
			if (itemBaseCfg == null) {
				return false;
			}

			if (newItemList == null) {
				newItemList = new ArrayList<INewItem>();
			}

			if (updateItemList == null) {
				updateItemList = new ArrayList<IUpdateItem>();
			}

			final int stackNum = itemBaseCfg.getStackNum();
			if (stackNum > 1) {
				List<ItemData> itemList = holder.getItemDataByCfgId(cfgId);
				if (!itemList.isEmpty()) {// 不空

					// 排序
					Collections.sort(itemList, comparator);

					// 选中数量最少的一组道具
					ItemData minItem = itemList.get(0);

					int itemCount = minItem.getCount();
					int leftCount = stackNum - itemCount;// 剩余的空间
					if (leftCount > 0) {
						int offAddCount = count > leftCount ? leftCount : count;// 实际增加多少
						IUpdateItem updateItem = new UpdateItem(minItem.getId(), offAddCount, null);
						updateItemList.add(updateItem);
						count -= offAddCount;// 还有多少个没添加的
					}
				}

				// 循环添加物品
				while (count > 0) {
					int newCount = count > stackNum ? stackNum : count;

					INewItem newItem = new NewItem(cfgId, newCount, null);
					newItemList.add(newItem);

					count -= newCount;
				}
			} else {
				for (int i = 0; i < count; i++) {
					INewItem newItem = new NewItem(cfgId, 1, null);
					newItemList.add(newItem);
				}
			}

			// 直接处理
			if (bagOperation && ((newItemList != null && !newItemList.isEmpty()) || (updateItemList != null && !updateItemList.isEmpty()))) {
				holder.updateItemBag(player, newItemList, updateItemList);
			}
		}

		// 应该是红点吧？？？
		// CheckEnhanceHot(cfgId);
		// 获得佣兵装备检测任务情况
		// eItemTypeDef eItemType = ItemCfgHelper.getItemType(cfgId);
		// if (nCount > 0 && eItemType == eItemTypeDef.HeroEquip) {
		// m_pPlayer.getTaskMgr().AddTaskTimes(eTaskFinishDef.Hero_Quality);
		// }
		return true;
	}

	/**
	 * 使用道具，为了简化外部调用者的处理，就直接把所有的错误或者异常信息都包含在当前这个方法里
	 * 
	 * @param slotId 使用道具的数据库Id
	 * @param count 使用道具的数量
	 * @param newItemList
	 * @param updateItemList
	 * @param bagOperation 是否直接把结果更新到背包中去
	 * @return 当前返回的只是一个状态，但是以后可能会返回失败的详细信息（这里要改成返回一个类型码）
	 */
	private boolean useItem0(String slotId, int count, List<INewItem> newItemList, List<IUpdateItem> updateItemList, boolean bagOperation) {
		if (count <= 0) {
			return false;
		}

		ItemData item = findBySlotId(slotId);// 查找对应的物品
		if (item == null) {
			return false;
		}

		int cfgId = item.getModelId();
		ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(cfgId);// 检查物品的基础模版
		if (itemBaseCfg == null) {
			return false;
		}

		if (newItemList == null) {
			newItemList = new ArrayList<INewItem>();
		}

		if (updateItemList == null) {
			updateItemList = new ArrayList<IUpdateItem>();
		}

		final int stackNum = itemBaseCfg.getStackNum();// 判断物品是否是可以叠加的
		if (stackNum > 1) {// 可叠加
			List<ItemData> itemList = holder.getItemDataByCfgId(cfgId);
			if (itemList.isEmpty()) {// 空的
				return false;
			}

			// 排序
			Collections.sort(itemList, comparator);

			// 开始处理
			for (int i = 0, size = itemList.size(); i < size; i++) {
				if (count <= 0) {
					break;
				}

				ItemData itemData = itemList.get(i);
				int itemCount = itemData.getCount();
				int offCount = count > itemCount ? itemCount : count;// 要扣掉的道具数量
				count -= offCount;

				// 增加更新物品
				IUpdateItem updateItem = new UpdateItem(itemData.getId(), -offCount, null);
				updateItemList.add(updateItem);
			}

			if (count > 0) {// 物品数量不足
				return false;
			}

			// 直接处理
			if (bagOperation && ((newItemList != null && !newItemList.isEmpty()) || (updateItemList != null && !updateItemList.isEmpty()))) {
				holder.updateItemBag(player, newItemList, updateItemList);
			}
			return true;
		}

		// 不可叠加的物品
		int itemCount = item.getCount();
		if (count > itemCount) {
			return false;
		}

		IUpdateItem updateItem = new UpdateItem(slotId, -count, null);
		updateItemList.add(updateItem);

		// 直接处理
		if (bagOperation && ((newItemList != null && !newItemList.isEmpty()) || (updateItemList != null && !updateItemList.isEmpty()))) {
			holder.updateItemBag(player, newItemList, updateItemList);
		}
		return true;
	}

	/**
	 * 
	 * @param cfgId
	 * @return
	 */
	public List<ItemData> getItemListByCfgId(int cfgId) {
		return holder.getItemDataByCfgId(cfgId);
	}

	/**
	 * 返回是否包含某种模型ID的道具
	 * 
	 * @param modelId
	 * @return
	 */
	public ItemData getFirstItemByModelId(int modelId) {
		return holder.getFirstItemByModelId(modelId);
	}

	/**
	 * 更新数据
	 */
	public void syncItemData(List<ItemData> itemList) {
		holder.syncItemData(player, itemList);
	}

	/**
	 * 刷新物品的属性
	 * 
	 * @param itemData
	 */
	public void updateItem(ItemData itemData) {
		holder.updateItem(itemData);
	}

	/**
	 * 通过物品类型获取背包中的数据
	 * 
	 * @param itemType
	 * @return
	 */
	public List<ItemData> getItemListByType(EItemTypeDef itemType) {
		return holder.getItemListByType(itemType);
	}
	// public void addSyncItemData(ItemData itemData) {
	// tableItem.addSyncItemData(itemData);
	// }
}