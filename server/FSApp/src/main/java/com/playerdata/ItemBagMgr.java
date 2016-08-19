package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.RefInt;
import com.log.GameLog;
import com.playerdata.readonly.ItemBagMgrIF;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.ItemBagHolder;
import com.rwbase.dao.item.exception.ItemCountNotEnoughException;
import com.rwbase.dao.item.exception.ItemNotExistException;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.NewItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
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

	private ItemBagHolder holder;// 背包的数据层

	// 初始化
	public boolean init(Player player) {
		this.player = player;
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
	}

	/**
	 * 增加物品
	 * 
	 * @param cfgId 物品的模版Id
	 * @param count 增加物品的个数
	 * @return 当前返回的只是一个状态，但是以后可能会返回失败的详细信息（这里要改成返回一个类型码）
	 */
	public boolean addItem(int cfgId, int count) {
		// 增加特殊物品时装的判断，时装物品不会设计为可以使用的物品
		// TODO franky 时装作为特殊物品占用了90000000 ~ 99999999
		if (ItemCfgHelper.isFashionSpecialItem(cfgId)) {
			RefInt fashionId = new RefInt();
			RefInt expireTimeCount = new RefInt();
			ItemCfgHelper.parseFashionSpecialItem(cfgId, fashionId, expireTimeCount);
			return FashionMgr.giveFashionItem(fashionId.value, expireTimeCount.value, player, false, true, null);
		}

		return addItem0(cfgId, count);
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

		return useItem0(slotId, count);
	}

	/**
	 * 通过模版Id消耗物品
	 * 
	 * @param cfgId
	 * @param count
	 * @return
	 */
	public boolean useItemByCfgId(int cfgId, int count) {
		return useItemByCfgId0(cfgId, count);
	}

	/**
	 * 
	 * @param cfgId
	 * @param count
	 * @param dataOperation 是否直接数据库操作
	 * @return
	 */
	private boolean useItemByCfgId0(int cfgId, int count) {
		if (count <= 0) {
			return false;
		}

		List<ItemData> itemList = holder.getItemDataByCfgId(cfgId);
		if (itemList.isEmpty()) {
			return false;
		}

		Collections.sort(itemList, comparator);// 物品排序

		return useItem0(itemList.get(0).getId(), count);
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

		return updateItemBg(player, useItemList, addItemList);
		// List<IUpdateItem> updateItemList = new ArrayList<IUpdateItem>();// 更新的物品列表
		// List<INewItem> newItemList = new ArrayList<INewItem>();// 要增加的物品
		//
		// // 要使用的物品
		// if (useItemList != null && !useItemList.isEmpty()) {
		// // 验证重复
		// Map<String, Integer> tempMap = new HashMap<String, Integer>();
		// Map<Integer, String> cacheSlotMap = new HashMap<Integer, String>();
		// for (int i = 0, size = useItemList.size(); i < size; i++) {
		// IUseItem useItem = useItemList.get(i);
		// int useCount = useItem.getUseCount();
		// if (useCount < 0) {
		// return false;
		// }
		//
		// String slotId = useItem.getSlotId();
		// ItemData itemData = findBySlotId(slotId);
		// if (itemData == null) {
		// return false;
		// }
		//
		// ItemBaseCfg baseCfg = ItemCfgHelper.GetConfig(itemData.getModelId());
		// int stackNum = baseCfg.getStackNum();
		//
		// if (stackNum > 1) {
		// if (!cacheSlotMap.containsKey(itemData.getModelId())) {
		// cacheSlotMap.put(itemData.getModelId(), slotId);
		// } else {
		// slotId = cacheSlotMap.get(itemData.getModelId());
		// }
		// }
		//
		// Integer hasValue = tempMap.get(slotId);
		// if (hasValue == null) {
		// tempMap.put(slotId, useCount);
		// } else {
		// tempMap.put(slotId, useCount + hasValue);
		// }
		// }
		//
		// for (Entry<String, Integer> e : tempMap.entrySet()) {
		// if (!useItem0(e.getKey(), e.getValue(), newItemList, updateItemList, false)) {
		// return false;
		// }
		// }
		// }
		//
		// // 要增加的物品
		// if (addItemList != null && !addItemList.isEmpty()) {
		// Map<Integer, Integer> tempMap = new HashMap<Integer, Integer>();
		// for (int i = 0, size = addItemList.size(); i < size; i++) {
		// INewItem newItem = addItemList.get(i);
		// int count = newItem.getCount();
		// if (count < 0) {
		// return false;
		// }
		//
		// int cfgId = newItem.getCfgId();
		// Integer hasValue = tempMap.get(cfgId);
		// if (hasValue == null) {
		// tempMap.put(cfgId, count);
		// } else {
		// tempMap.put(cfgId, count + hasValue);
		// }
		// }
		//
		// for (Entry<Integer, Integer> e : tempMap.entrySet()) {
		// if (!addItem0(e.getKey(), e.getValue(), newItemList, updateItemList, false)) {
		// return false;
		// }
		// }
		// }
		//
		// // 两个任意一个不为空就可以走下去更新背包处理
		// if (!updateItemList.isEmpty() || !newItemList.isEmpty()) {
		// holder.updateItemBag(player, newItemList, updateItemList);
		// return true;
		// }
	}

	/**
	 * 更新背包中的数据
	 * 
	 * @param player
	 * @param useItemList
	 * @param addItemList
	 * @return
	 */
	private boolean updateItemBg(Player player, List<IUseItem> useItemList, List<INewItem> addItemList) {
		String userId = player.getUserId();
		try {
			updateItemBag(player, useItemList, addItemList);
			return true;
		} catch (IllegalArgumentException e) {
			GameLog.error("Use like box item Handler", userId, "throws IllegalArgumentException", e);
			return false;
		} catch (ItemNotExistException e) {
			GameLog.error("Use like box item Handler", userId, "throws ItemNotExistException", e);
			return false;
		} catch (ItemCountNotEnoughException e) {
			GameLog.error("Use like box item Handler", userId, "throws ItemCountNotEnoughException", e);
			return false;
		}
	}

	/**
	 * 检查货币是否足够
	 * 
	 * @param cfgId
	 * @param count
	 * @return
	 */
	public boolean checkEnoughItem(int cfgId, int count) {
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
			} else if (cfgId == eSpecialItemId.WAKEN_PIECE.getValue()) {
				return player.getUserGameDataMgr().getWakenPiece() >= count;
			} else if (cfgId == eSpecialItemId.WAKEN_KEY.getValue()) {
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
				sum += itemData.getCount();
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
	private boolean addItem0(int cfgId, int count) {
		if (cfgId <= eSpecialItemId.eSpecial_End.getValue()) {
			modifyCurrency(cfgId, count);
		} else {// 操作道具
			if (count <= 0) {
				return false;
			}

			List<INewItem> newItemList = new ArrayList<INewItem>(1);
			newItemList.add(new NewItem(cfgId, count, null));

			return updateItemBg(player, null, newItemList);

			// ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(cfgId);// 检查物品的基础模版
			// if (itemBaseCfg == null) {
			// return false;
			// }
			//
			// if (newItemList == null) {
			// newItemList = new ArrayList<INewItem>();
			// }
			//
			// if (updateItemList == null) {
			// updateItemList = new ArrayList<IUpdateItem>();
			// }
			//
			// final int stackNum = itemBaseCfg.getStackNum();
			// if (stackNum > 1) {
			// List<ItemData> itemList = holder.getItemDataByCfgId(cfgId);
			// if (!itemList.isEmpty()) {// 不空
			//
			// // 排序
			// Collections.sort(itemList, comparator);
			//
			// // 选中数量最少的一组道具
			// ItemData minItem = itemList.get(0);
			//
			// int itemCount = minItem.getCount();
			// int leftCount = stackNum - itemCount;// 剩余的空间
			// if (leftCount > 0) {
			// int offAddCount = count > leftCount ? leftCount : count;// 实际增加多少
			// IUpdateItem updateItem = new UpdateItem(minItem.getId(), offAddCount, null);
			// updateItemList.add(updateItem);
			// count -= offAddCount;// 还有多少个没添加的
			// }
			// } else {
			// int newCount = count > stackNum ? stackNum : count;
			//
			// INewItem newItem = new NewItem(cfgId, newCount, null);
			// newItemList.add(newItem);
			//
			// }
			// } else {
			// for (int i = 0; i < count; i++) {
			// INewItem newItem = new NewItem(cfgId, 1, null);
			// newItemList.add(newItem);
			// }
			// }
			//
			// // 直接处理
			// if (bagOperation && ((newItemList != null && !newItemList.isEmpty()) || (updateItemList != null && !updateItemList.isEmpty()))) {
			// holder.updateItemBag(player, newItemList, updateItemList);
			// }
		}

		// 通知羁绊检查法宝
		if (ItemCfgHelper.getItemType(cfgId) == EItemTypeDef.Magic) {
			player.getMe_FetterMgr().notifyMagicChange(player);
		}
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
	private boolean useItem0(String slotId, int count) {
		if (count <= 0) {
			return false;
		}

		ItemData item = findBySlotId(slotId);// 查找对应的物品
		if (item == null) {
			return false;
		}

		List<IUseItem> useItemList = new ArrayList<IUseItem>(1);
		useItemList.add(new UseItem(slotId, count));

		return updateItemBg(player, useItemList, null);

		// int cfgId = item.getModelId();
		// ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(cfgId);// 检查物品的基础模版
		// if (itemBaseCfg == null) {
		// return false;
		// }
		//
		// if (newItemList == null) {
		// newItemList = new ArrayList<INewItem>();
		// }
		//
		// if (updateItemList == null) {
		// updateItemList = new ArrayList<IUpdateItem>();
		// }
		//
		// final int stackNum = itemBaseCfg.getStackNum();// 判断物品是否是可以叠加的
		// if (stackNum > 1) {// 可叠加
		// List<ItemData> itemList = holder.getItemDataByCfgId(cfgId);
		// if (itemList.isEmpty()) {// 空的
		// return false;
		// }
		//
		// // 排序
		// Collections.sort(itemList, comparator);
		//
		// // 开始处理
		// for (int i = 0, size = itemList.size(); i < size; i++) {
		// if (count <= 0) {
		// break;
		// }
		//
		// ItemData itemData = itemList.get(i);
		// int itemCount = itemData.getCount();
		// int offCount = count > itemCount ? itemCount : count;// 要扣掉的道具数量
		// count -= offCount;
		//
		// // 增加更新物品
		// IUpdateItem updateItem = new UpdateItem(itemData.getId(), -offCount, null);
		// updateItemList.add(updateItem);
		// }
		//
		// if (count > 0) {// 物品数量不足
		// return false;
		// }
		//
		// // 直接处理
		// if (bagOperation && ((newItemList != null && !newItemList.isEmpty()) || (updateItemList != null && !updateItemList.isEmpty()))) {
		// holder.updateItemBag(player, newItemList, updateItemList);
		// }
		// return true;
		// }
		//
		// // 不可叠加的物品
		// int itemCount = item.getCount();
		// if (count > itemCount) {
		// return false;
		// }
		//
		// IUpdateItem updateItem = new UpdateItem(slotId, -count, null);
		// updateItemList.add(updateItem);
		//
		// // 直接处理
		// if (bagOperation && ((newItemList != null && !newItemList.isEmpty()) || (updateItemList != null && !updateItemList.isEmpty()))) {
		// holder.updateItemBag(player, newItemList, updateItemList);
		// }
		// return true;
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

	/**
	 * 修改货币
	 * 
	 * @param cfgId
	 * @param value
	 */
	private void modifyCurrency(int cfgId, int value) {
		if (cfgId == eSpecialItemId.Coin.getValue()) {
			player.getUserGameDataMgr().addCoin(value);
		} else if (cfgId == eSpecialItemId.Gold.getValue()) {
			player.getUserGameDataMgr().addGold(value);
		} else if (cfgId == eSpecialItemId.Power.getValue()) {
			player.addPower(value);
		} else if (cfgId == eSpecialItemId.PlayerExp.getValue()) {
			player.addUserExp(value);
		} else if (cfgId == eSpecialItemId.MagicSecretCoin.getValue()) {
			player.getUserGameDataMgr().addMagicSecretCoin(value);
		} else if (cfgId == eSpecialItemId.BraveCoin.getValue()) {
			player.getUserGameDataMgr().addTowerCoin(value);
		} else if (cfgId == eSpecialItemId.GuildCoin.getValue()) {
			player.getUserGroupAttributeDataMgr().useUserGroupContribution(value);
		} else if (cfgId == eSpecialItemId.PeakArenaCoin.getValue()) {
			player.getUserGameDataMgr().addPeakArenaCoin(value);
		}// 新增竞技场货币处理
		else if (cfgId == eSpecialItemId.ArenaCoin.getValue()) {
			player.getUserGameDataMgr().addArenaCoin(value);
		} else if (cfgId == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
			player.getBattleTowerMgr().getTableBattleTower().modifyCopperKey(value);
		} else if (cfgId == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
			player.getBattleTowerMgr().getTableBattleTower().modifySilverKey(value);
		} else if (cfgId == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
			player.getBattleTowerMgr().getTableBattleTower().modifyGoldKey(value);
		} else if (cfgId == eSpecialItemId.WAKEN_PIECE.getValue()) {
			player.getUserGameDataMgr().addWakenPiece(value);
		} else if (cfgId == eSpecialItemId.WAKEN_KEY.getValue()) {
			player.getUserGameDataMgr().addWakenKey(value);
		}
	}

	/**
	 * 更新背包的操作
	 * 
	 * @param player 角色
	 * @param newItemList 新创建物品的列表
	 * @param updateItemList 更新物品的列表
	 * @throws ItemNotExistException
	 * @throws ItemCountNotEnoughException
	 */
	public void updateItemBag(Player player, List<IUseItem> useItemList, List<INewItem> addItemList) throws ItemNotExistException, IllegalArgumentException, ItemCountNotEnoughException {
		// 没有新增也没有要更新的
		if ((useItemList == null || useItemList.isEmpty()) && (addItemList == null || addItemList.isEmpty())) {
			throw new IllegalArgumentException("使用物品和要新增的物品为空");
		}

		List<ItemData> newItemList = new ArrayList<ItemData>();
		List<ItemData> updateItemList = new ArrayList<ItemData>();

		// 使用物品
		if (useItemList != null && !useItemList.isEmpty()) {
			updateItem(updateItemWrap(useItemList), newItemList, updateItemList);
		}

		// 增加新的物品
		if (addItemList != null && !addItemList.isEmpty()) {
			addItem(nonRepeatAddMap(addItemList), newItemList, updateItemList);
		}

		// 更新背包
		try {
			holder.updateItemBgData(player, newItemList, updateItemList);
		} catch (DuplicatedKeyException e) {
			GameLog.error("背包模块", player.getUserId(), String.format("添加物品出现了重复的Key"));
		}
	}

	/**
	 * 更新使用物品的列表
	 * 
	 * @param useItemList
	 * @return
	 * @throws ItemNotExistException
	 * @throws ItemCountNotEnoughException
	 */
	private HashMap<String, Integer> updateItemWrap(List<IUseItem> useItemList) throws ItemNotExistException, ItemCountNotEnoughException {
		int size = useItemList.size();

		HashMap<String, Integer> tempMap = new HashMap<String, Integer>(size);
		Map<Integer, String> cacheSlotMap = new HashMap<Integer, String>(size);
		for (int i = 0; i < size; i++) {
			IUseItem useItem = useItemList.get(i);
			int useCount = useItem.getUseCount();
			if (useCount <= 0) {
				throw new IllegalArgumentException("传递进来的使用数量<=0");
			}

			String slotId = useItem.getSlotId();
			ItemData itemData = findBySlotId(slotId);
			if (itemData == null) {
				throw new ItemNotExistException("在背包中不存在要使用的道具");
			}

			int modelId = itemData.getModelId();
			ItemBaseCfg cfg = ItemCfgHelper.GetConfig(modelId);
			if (cfg == null) {
				throw new ItemNotExistException(String.format("%s的道具模版找不到", modelId));
			}

			int itemCount = itemData.getCount();
			boolean isMerger = cfg.getStackNum() > 1;
			if (isMerger) {// 物品叠加在一起
				if (!cacheSlotMap.containsKey(modelId)) {
					cacheSlotMap.put(modelId, slotId);
				} else {
					slotId = cacheSlotMap.get(modelId);
				}

				itemCount = getItemCountByModelId(modelId);
			}

			// 检查使用数量
			Integer hasValue = tempMap.get(slotId);
			if (hasValue == null) {
				tempMap.put(slotId, useCount);
			} else {
				tempMap.put(slotId, useCount + hasValue);
				useCount += hasValue;
			}

			// 检查数量够不够
			if (useCount > itemCount) {
				throw new ItemCountNotEnoughException("使用的数量超过了拥有数量");
			}
		}

		return tempMap;
	}

	/**
	 * 获取更新的列表
	 * 
	 * @param updateItemMap
	 * @param addItemList
	 * @param updateItemList
	 * @return
	 */
	private void updateItem(HashMap<String, Integer> updateItemMap, List<ItemData> addItemList, List<ItemData> updateItemList) {
		// 转换成Update
		for (Entry<String, Integer> e : updateItemMap.entrySet()) {
			String slotId = e.getKey();
			int useCount = e.getValue();

			ItemData itemData = findBySlotId(slotId);
			if (itemData == null) {
				continue;
			}

			int modelId = itemData.getModelId();
			ItemBaseCfg cfg = ItemCfgHelper.GetConfig(modelId);
			if (cfg == null) {
				continue;
			}

			int stackCount = cfg.getStackNum();// 叠加数量
			boolean isMerger = stackCount > 1;
			if (isMerger) {// 叠加物品
				List<ItemData> itemList = holder.getItemDataByCfgId(modelId);
				if (itemList.isEmpty()) {// 空的
					return;
				}

				// 排序
				Collections.sort(itemList, comparator);

				// 开始处理
				for (int i = 0, size = itemList.size(); i < size; i++) {
					if (useCount <= 0) {
						break;
					}

					ItemData item = itemList.get(i);
					int hasCount = item.getCount();
					int offCount = useCount > hasCount ? hasCount : useCount;// 要扣掉的道具数量

					if (hasCount > offCount) {// 足够扣
						itemData.setCount(hasCount - offCount);
					} else {// 删除的物品
						itemData.setCount(0);
					}

					useCount -= offCount;

					updateItemList.add(item);
				}
			} else {
				int itemCount = itemData.getCount();
				if (itemCount > useCount) {// 足够扣
					itemData.setCount(itemCount - useCount);
				} else {// 删除的物品
					itemData.setCount(0);
				}

				updateItemList.add(itemData);
			}
		}
	}

	/**
	 * 去掉重复的添加元素
	 * 
	 * @param addItemList
	 * @return
	 */
	private HashMap<Integer, Integer> nonRepeatAddMap(List<INewItem> addItemList) {
		int size = addItemList.size();
		HashMap<Integer, Integer> addMap = new HashMap<Integer, Integer>(size);
		for (int i = 0; i < size; i++) {
			INewItem newItem = addItemList.get(i);
			int addCount = newItem.getCount();
			if (addCount <= 0) {
				throw new IllegalArgumentException("传递进来的增加数量<=0");
			}

			int modelId = newItem.getCfgId();
			Integer hasValue = addMap.get(modelId);
			if (hasValue == null) {
				addMap.put(modelId, addCount);
			} else {
				addMap.put(modelId, addCount + hasValue);
			}
		}

		return addMap;
	}

	/**
	 * 增加物品
	 * 
	 * @param addMap
	 * @param addItemList
	 * @param updateItemList
	 * @throws ItemNotExistException
	 */
	private void addItem(HashMap<Integer, Integer> addMap, List<ItemData> addItemList, List<ItemData> updateItemList) throws ItemNotExistException {
		int offSize = 0;
		// 增加数据
		for (Entry<Integer, Integer> e : addMap.entrySet()) {
			int modelId = e.getKey();
			ItemBaseCfg cfg = ItemCfgHelper.GetConfig(modelId);
			if (cfg == null) {
				throw new ItemNotExistException(String.format("%s的道具模版找不到", modelId));
			}

			int addCount = e.getValue();// 增加数量

			int stackNum = cfg.getStackNum();
			if (stackNum > 1) {// 叠加
				List<ItemData> itemList = holder.getItemDataByCfgId(modelId);
				if (itemList.isEmpty()) {// 空的
					return;
				}

				// 排序
				Collections.sort(itemList, comparator);

				// 开始处理
				// 选中数量最少的一组道具
				ItemData minItem = itemList.get(0);

				int itemCount = minItem.getCount();
				int leftCount = stackNum - itemCount;// 剩余的空间
				if (leftCount > 0) {
					int offAddCount = addCount > leftCount ? leftCount : addCount;// 实际增加多少
					minItem.setCount(itemCount + offAddCount);
					updateItemList.add(minItem);
				} else {
					offSize++;

					int count = addCount >= stackNum ? stackNum : addCount;
					ItemData newItemData = holder.newItemData(modelId, count);
					int capacity = getCapacityByType(newItemData.getType());
					if (capacity <= 0) {
						addItemList.add(newItemData);
					} else {
						int hasSize = getItemListByType(newItemData.getType()).size() + offSize;
						if (hasSize <= capacity) {
							addItemList.add(newItemData);
						}
					}
				}
			} else {
				for (int i = 0; i < addCount; i++) {
					offSize++;

					ItemData newItemData = holder.newItemData(modelId, 1);
					int capacity = getCapacityByType(newItemData.getType());
					if (capacity <= 0) {
						addItemList.add(newItemData);
					} else {
						int hasSize = getItemListByType(newItemData.getType()).size() + offSize;
						if (hasSize <= capacity) {
							addItemList.add(newItemData);
						}
					}
				}
			}
		}
	}

	/**
	 * 通过物品类型获取该种物品的上限
	 * 
	 * @param type
	 * @return
	 */
	private int getCapacityByType(EItemTypeDef type) {
		if (type == EItemTypeDef.Magic) {
			return 100;
		}

		return 0;
	}
}