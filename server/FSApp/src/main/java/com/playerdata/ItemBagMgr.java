package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.common.RefInt;
import com.log.GameLog;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.service.Email.EmailUtils;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.item.ItemBagCapacityCfgDAO;
import com.rwbase.dao.item.ItemBagHolder;
import com.rwbase.dao.item.exception.ItemCountNotEnoughException;
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
public class ItemBagMgr {
	private static String ITEM_BAG_FULL_EMAIL_ID = "10068";// 背包物品叠加到了上限的邮件提示
	private static String MAGIC_FULL_EMAIL_ID = "10069";// 法宝满了的邮件提示

	/**
	 * 消耗物品规则的比较器
	 */
	private static Comparator<ItemData> comparator = new Comparator<ItemData>() {

		@Override
		public int compare(ItemData o1, ItemData o2) {
			return o1.getCount() - o2.getCount();
		}
	};

	private static ItemBagMgr instance = new ItemBagMgr();

	public static ItemBagMgr getInstance() {
		return instance;
	}

	protected ItemBagMgr() {
	}

	/**
	 * 在登出游戏的时候保存
	 */
	public void save(String userId) {
		ItemBagHolder.getHolder().flush(userId);
	}

	/**
	 * 推送所有的物品数据到客户端
	 * 
	 * @param player
	 */
	public void syncAllItemData(Player player) {
		ItemBagHolder.getHolder().syncAllData(player);
	}

	/**
	 * 通过物品的背包位置获取物品的信息
	 * 
	 * @param userId
	 * @param id
	 * @return
	 */
	public ItemData findBySlotId(String userId, String id) {
		return ItemBagHolder.getHolder().getItemData(userId, id);
	}

	/**
	 * 获取物品数量
	 * 
	 * @param userId 角色的Id
	 * @param modelId 道具模板Id
	 * @return
	 */
	public int getItemCountByModelId(String userId, int modelId) {
		return ItemBagHolder.getHolder().getItemCountByModelId(userId, modelId);
	}

	/**
	 * <pre>
	 * 获取道具modelId与数量的映射
	 * </pre>
	 * 
	 * @return {key=modelId,value=count}
	 */
	public Map<Integer, RefInt> getModelCountMap(String userId) {
		return ItemBagHolder.getHolder().getModelCountMap(userId);
	}

	/**
	 * 获取道具ModelId于任意一个ItemData的映射
	 * 
	 * @return
	 */
	public Map<Integer, ItemData> getModelFirstItemDataMap(String userId) {
		return ItemBagHolder.getHolder().getModelFirstItemDataMap(userId);
	}

	/**
	 * <pre>
	 * 检查是否有足够的道具数量
	 * </pre>
	 * 
	 * @param itemsMap {key=modelId,value=count}
	 * @return
	 */
	public boolean hasEnoughItems(String userId, Map<Integer, Integer> itemsMap) {
		return ItemBagHolder.getHolder().hasEnoughItems(userId, itemsMap);
	}

	/**
	 *
	 * 解析某种物品，然后放入到背包中去
	 * 
	 * @param strPrize
	 */
	public void addItemByPrizeStr(Player player, String strPrize) {
		String[] arrPrizes = strPrize.split(",");

		int size = arrPrizes.length;
		List<INewItem> newItemList = new ArrayList<INewItem>(size);
		List<ItemInfo> items = new ArrayList<ItemInfo>();

		for (int i = 0; i < size; i++) {
			String[] arrItem = arrPrizes[i].split("~");
			if (arrItem.length < 2)
				continue;
			int itemId = Integer.valueOf(arrItem[0]);
			int itemCount = Integer.valueOf(arrItem[1]);

			if (itemId < eSpecialItemId.eSpecial_End.getValue()) {
				ItemInfo item = new ItemInfo();
				item.setItemID(itemId);
				item.setItemNum(itemCount);
				items.add(item);
			} else if (ItemCfgHelper.isFashionSpecialItem(itemId)) {
				addItem(player, itemId, itemId);
			} else {
				INewItem newItem = new NewItem(itemId, itemCount, null);
				newItemList.add(newItem);
			}
		}
		if (!items.isEmpty()) {
			addItem(player, items);
		}
		// 增加新的道具
		if (!newItemList.isEmpty()) {
			useLikeBoxItem(player, null, newItemList);
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
	public boolean addItem(Player player, int cfgId, int count) {
		boolean success = false;
		// 增加特殊物品时装的判断，时装物品不会设计为可以使用的物品
		// TODO franky 时装作为特殊物品占用了90000000 ~ 99999999
		if (ItemCfgHelper.isFashionSpecialItem(cfgId)) {
			success = addFashion(player, cfgId);
		} else {
			success = addItem0(player, cfgId, count);
		}

		return success;
	}

	private boolean addFashion(Player player, int cfgId) {
		boolean success;
		RefInt fashionId = new RefInt();
		RefInt expireTimeCount = new RefInt();
		ItemCfgHelper.parseFashionSpecialItem(cfgId, fashionId, expireTimeCount);
		success = FashionMgr.giveFashionItem(fashionId.value, expireTimeCount.value, player, false, true, null);
		return success;
	}

	/**
	 * 使用道具，为了简化外部调用者的处理，就直接把所有的错误或者异常信息都包含在当前这个方法里
	 * 
	 * @param slotId 使用道具的数据库Id
	 * @param count 使用道具的数量
	 * @return 当前返回的只是一个状态，但是以后可能会返回失败的详细信息（这里要改成返回一个类型码）
	 */
	public boolean useItemBySlotId(Player player, String slotId, int count) {
		if (count <= 0) {
			return false;
		}

		return useItem0(player, slotId, count);
	}

	/**
	 * 通过模版Id消耗物品
	 * 
	 * @param cfgId
	 * @param count
	 * @return
	 */
	public boolean useItemByCfgId(Player player, int cfgId, int count) {
		return useItemByCfgId0(player, cfgId, count);
	}

	/**
	 * 
	 * @param cfgId
	 * @param count
	 * @param dataOperation 是否直接数据库操作
	 * @return
	 */
	private boolean useItemByCfgId0(Player player, int cfgId, int count) {
		if (count <= 0) {
			return false;
		}

		List<ItemData> itemList = ItemBagHolder.getHolder().getItemDataByCfgId(player.getUserId(), cfgId);
		if (itemList.isEmpty()) {
			return false;
		}

		Collections.sort(itemList, comparator);// 物品排序

		return useItem0(player, itemList.get(0).getId(), count);
	}

	/**
	 * 使用类宝箱类道具
	 * 
	 * @param useItemList 要使用物品列表(只能是物品类，不能是货币)
	 * @param addItemList 要产生的物品
	 * @param modifyMoneyMap 要使用的货币，使用货币的修改<如果是正数：加；如果是负数：减>
	 * @return
	 */
	public boolean useLikeBoxItem(Player player, List<IUseItem> useItemList, List<INewItem> addItemList, Map<Integer, Integer> modifyMoneyMap) {
		// 金钱操作
		if (modifyMoneyMap != null) {
			for (Entry<Integer, Integer> e : modifyMoneyMap.entrySet()) {
				Integer useCount = e.getValue();
				if (useCount == 0) {
					continue;
				}

				if (!addItem(player, e.getKey(), useCount)) {
					return false;
				}
			}
		}

		// 证明只用扣钱
		if ((useItemList == null || useItemList.isEmpty()) && (addItemList == null || addItemList.isEmpty())) {
			return true;
		}

		return useLikeBoxItem(player, useItemList, addItemList);
	}

	/**
	 * 使用类宝箱类道具
	 * 
	 * @param useItemList 要使用物品列表(只能是物品类，不能是货币)
	 * @param addItemList 要产生的物品
	 */
	public boolean useLikeBoxItem(Player player, List<IUseItem> useItemList, List<INewItem> addItemList) {
		if ((useItemList == null || useItemList.isEmpty()) && (addItemList == null || addItemList.isEmpty())) {
			return false;
		}

		return updateItemBg(player, useItemList, addItemList);
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
		} catch (DataNotExistException e) {
			GameLog.error("Use like box item Handler", userId, "throws DataNotExistException", e);
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
	public boolean checkEnoughItem(Player player, int cfgId, int count) {
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
				return UserGroupAttributeDataMgr.getMgr().getUserGroupContribution(player.getUserId()) >= count;
			} else if (cfgId == eSpecialItemId.PeakArenaCoin.getValue()) {
				return player.getUserGameDataMgr().getPeakArenaCoin() >= count;
			}// 新增竞技场货币处理
			else if (cfgId == eSpecialItemId.ArenaCoin.getValue()) {
				return player.getUserGameDataMgr().getArenaCoin() >= count;
			} else if (cfgId == eSpecialItemId.WAKEN_PIECE.getValue()) {
				return player.getUserGameDataMgr().getWakenPiece() >= count;
			} else if (cfgId == eSpecialItemId.WAKEN_KEY.getValue()) {
				return player.getUserGameDataMgr().getWakenKey() >= count;
			} else if (cfgId == eSpecialItemId.TEAM_BATTLE_GOLD.getValue()) {
				return player.getUserGameDataMgr().getTeamBattleCoin() >= count;
			}
		} else {// 操作道具
			if (count <= 0) {
				return false;
			}

			ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(cfgId);// 检查物品的基础模版
			if (itemBaseCfg == null) {
				return false;
			}

			List<ItemData> itemList = ItemBagHolder.getHolder().getItemDataByCfgId(player.getUserId(), cfgId);
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
	private boolean addItem0(Player player, int cfgId, int count) {
		boolean success = true;
		if (cfgId <= eSpecialItemId.eSpecial_End.getValue()) {
			modifyCurrency(player, cfgId, count);
		} else {// 操作道具
			if (count <= 0) {
				return false;
			}

			List<INewItem> newItemList = new ArrayList<INewItem>(1);
			newItemList.add(new NewItem(cfgId, count, null));

			success = updateItemBg(player, null, newItemList);
			if (success) {
				// 通知羁绊检查法宝
				if (ItemCfgHelper.getItemType(cfgId) == EItemTypeDef.Magic) {
					player.getMe_FetterMgr().notifyMagicChange(player);
				}
			}
		}

		return success;
	}

	/**
	 * 添加机器人的道具，临时方法 解决不会触发战力的问题
	 * 
	 * @param cfgId
	 * @param count
	 * @return
	 */
	public boolean addRobotItem(Player player, int cfgId, int count) {
		boolean success = true;
		if (cfgId <= eSpecialItemId.eSpecial_End.getValue()) {
			modifyCurrency(player, cfgId, count);
		} else {// 操作道具
			if (count <= 0) {
				return false;
			}
			List<INewItem> newItemList = new ArrayList<INewItem>(1);
			newItemList.add(new NewItem(cfgId, count, null));
			success = updateItemBg(player, null, newItemList);
		}
		return success;
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
	private boolean useItem0(Player player, String slotId, int count) {
		if (count <= 0) {
			return false;
		}

		ItemData item = findBySlotId(player.getUserId(), slotId);// 查找对应的物品
		if (item == null) {
			return false;
		}

		List<IUseItem> useItemList = new ArrayList<IUseItem>(1);
		useItemList.add(new UseItem(slotId, count));

		return updateItemBg(player, useItemList, null);
	}

	/**
	 * 
	 * @param cfgId
	 * @return
	 */
	public List<ItemData> getItemListByCfgId(String userId, int cfgId) {
		return ItemBagHolder.getHolder().getItemDataByCfgId(userId, cfgId);
	}

	/**
	 * 返回是否包含某种模型ID的道具
	 * 
	 * @param modelId
	 * @return
	 */
	public ItemData getFirstItemByModelId(String userId, int modelId) {
		return ItemBagHolder.getHolder().getFirstItemByModelId(userId, modelId);
	}

	/**
	 * 更新数据
	 */
	public void syncItemData(Player player, List<ItemData> itemList) {
		ItemBagHolder.getHolder().syncItemData(player, itemList);
	}

	/**
	 * 刷新物品的属性
	 * 
	 * @param itemData
	 */
	public void updateItem(ItemData itemData) {
		ItemBagHolder.getHolder().updateItem(itemData);
	}

	/**
	 * 通过物品类型获取背包中的数据
	 * 
	 * @param itemType
	 * @return
	 */
	public List<ItemData> getItemListByType(String userId, EItemTypeDef itemType) {
		return ItemBagHolder.getHolder().getItemListByType(userId, itemType);
	}

	/**
	 * 修改货币
	 * 
	 * @param cfgId
	 * @param value
	 */
	private void modifyCurrency(Player player, int cfgId, int value) {
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
			UserGroupAttributeDataMgr.getMgr().useUserGroupContribution(player.getUserId(), value);
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
		} else if (cfgId == eSpecialItemId.TEAM_BATTLE_GOLD.getValue()) {
			player.getUserGameDataMgr().addTeamBattleCoin(value);
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
	public void updateItemBag(Player player, List<IUseItem> useItemList, List<INewItem> addItemList) throws DataNotExistException, IllegalArgumentException, ItemCountNotEnoughException {
		// 没有新增也没有要更新的
		if ((useItemList == null || useItemList.isEmpty()) && (addItemList == null || addItemList.isEmpty())) {
			throw new IllegalArgumentException("使用物品和要新增的物品为空");
		}

		String userId = player.getUserId();
		List<ItemData> newItemList = new ArrayList<ItemData>();
		List<ItemData> updateItemList = new ArrayList<ItemData>();
		// 叠加上限超出
		List<Integer> modelIdList = new ArrayList<Integer>();
		// 背包容量超出
		List<EItemTypeDef> typeDefList = new ArrayList<EItemTypeDef>();

		// 使用物品
		if (useItemList != null && !useItemList.isEmpty()) {
			useItem(userId, updateItemWrap(userId, useItemList), newItemList, updateItemList, modelIdList, typeDefList);
		}

		// 增加新的物品
		if (addItemList != null && !addItemList.isEmpty()) {
			addItem(player, nonRepeatAddMap(addItemList), newItemList, updateItemList, modelIdList, typeDefList);
		}

		// 更新背包
		try {
			ItemBagHolder.getHolder().updateItemBgData(player, newItemList, updateItemList);
		} catch (DuplicatedKeyException e) {
			GameLog.error("背包模块", userId, "添加物品出现了重复的Key", e);
			return;
		} catch (DataNotExistException e) {
			GameLog.error("背包模块", userId, "操作的物品中有无法从数据库找到的", e);
			return;
		}

		// 超出叠加上限的道具列表
		if (!modelIdList.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0, size = modelIdList.size(); i < size; i++) {
				ItemBaseCfg cfg = ItemCfgHelper.GetConfig(modelIdList.get(i));
				if (cfg == null) {
					continue;
				}

				sb.append(cfg.getName());
				if (i < size - 1) {
					sb.append(",");
				}
			}

			Object[] param = new Object[1];
			param[0] = sb.toString();

			sendEmail(userId, ITEM_BAG_FULL_EMAIL_ID, param);
		}

		// 某类型物品超出规定的上限
		if (!typeDefList.isEmpty()) {
			ItemBagCapacityCfgDAO cfgDAO = ItemBagCapacityCfgDAO.getCfgDAO();
			StringBuilder sb = new StringBuilder();
			for (int i = 0, size = typeDefList.size(); i < size; i++) {
				String name = cfgDAO.getItemBagName(typeDefList.get(i));

				if (StringUtils.isEmpty(name)) {
					continue;
				}

				sb.append(name);
				if (i < size - 1) {
					sb.append(",");
				}
			}

			Object[] param = new Object[1];
			param[0] = sb.toString();

			sendEmail(userId, MAGIC_FULL_EMAIL_ID, param);
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
	private HashMap<String, Integer> updateItemWrap(String userId, List<IUseItem> useItemList) throws DataNotExistException, ItemCountNotEnoughException {
		int size = useItemList.size();

		Map<Integer, RefInt> modelCountMap = getModelCountMap(userId);
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>(size);
		Map<Integer, String> cacheSlotMap = new HashMap<Integer, String>(size);
		for (int i = 0; i < size; i++) {
			IUseItem useItem = useItemList.get(i);
			int useCount = useItem.getUseCount();
			if (useCount <= 0) {
				throw new IllegalArgumentException("传递进来的使用数量<=0");
			}

			String slotId = useItem.getSlotId();
			ItemData itemData = findBySlotId(userId, slotId);
			if (itemData == null) {
				throw new DataNotExistException("在背包中不存在要使用的道具");
			}

			int modelId = itemData.getModelId();
			ItemBaseCfg cfg = ItemCfgHelper.GetConfig(modelId);
			if (cfg == null) {
				throw new DataNotExistException(String.format("%s的道具模版找不到", modelId));
			}

			int itemCount = itemData.getCount();
			boolean isMerger = cfg.getStackNum() > 1;
			if (isMerger) {// 物品叠加在一起
				if (!cacheSlotMap.containsKey(modelId)) {
					cacheSlotMap.put(modelId, slotId);
				} else {
					slotId = cacheSlotMap.get(modelId);
				}

				RefInt refInt = modelCountMap.get(modelId);
				itemCount = refInt == null ? 0 : refInt.value;
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
	private void useItem(String userId, HashMap<String, Integer> updateItemMap, List<ItemData> addItemList, List<ItemData> updateItemList, List<Integer> modelIdList, List<EItemTypeDef> typeDefList) {
		// 转换成Update
		for (Entry<String, Integer> e : updateItemMap.entrySet()) {
			String slotId = e.getKey();
			int useCount = e.getValue();

			ItemData itemData = findBySlotId(userId, slotId);
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
				List<ItemData> itemList = ItemBagHolder.getHolder().getItemDataByCfgId(userId, modelId);
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
	private HashMap<Integer, Integer> nonRepeatAddMap(List<INewItem> addItemList) throws IllegalArgumentException {
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
	 */
	private void addItem(Player player, HashMap<Integer, Integer> addMap, List<ItemData> addItemList, List<ItemData> updateItemList, List<Integer> modelIdList, List<EItemTypeDef> typeDefList) {
		EnumMap<EItemTypeDef, Integer> offSizeMap = new EnumMap<EItemTypeDef, Integer>(EItemTypeDef.class);// 叠加物品的数量
		// 增加数据
		String userId = player.getUserId();
		for (Entry<Integer, Integer> e : addMap.entrySet()) {
			int modelId = e.getKey();
			if (ItemCfgHelper.isFashionSpecialItem(modelId)) {
				// 2016-11-12 添加对时装的判断
				addFashion(player, modelId);
				continue;
			} else if (modelId < eSpecialItemId.eSpecial_End.getValue()) {
				modifyCurrency(player, modelId, e.getValue());
				continue;
			}
			ItemBaseCfg cfg = ItemCfgHelper.GetConfig(modelId);
			if (cfg == null) {
				GameLog.error("背包模块添加物品", "new", String.format("%s的modelId找不到对应的配置", modelId));
				continue;
			}

			int addCount = e.getValue();// 增加数量

			int stackNum = cfg.getStackNum();
			if (stackNum > 1) {// 叠加
				List<ItemData> itemList = ItemBagHolder.getHolder().getItemDataByCfgId(userId, modelId);
				if (!itemList.isEmpty()) {// 空的
					// 排序
					Collections.sort(itemList, comparator);

					// 开始处理
					// 选中数量最少的一组道具
					ItemData minItem = itemList.get(0);

					int itemCount = minItem.getCount();
					int leftCount = stackNum - itemCount;// 剩余的空间
					if (leftCount > 0) {
						if (addCount >= leftCount) {
							addCount = leftCount;
							modelIdList.add(minItem.getModelId());// 把超出叠加上限的添加出来
						}
						minItem.setCount(itemCount + addCount);
						updateItemList.add(minItem);
					} else {
						modelIdList.add(minItem.getModelId());// 把超出叠加上限的添加出来
					}
				} else {
					if (addCount >= stackNum) {
						addCount = stackNum;
						modelIdList.add(modelId);// 把超出叠加上限的添加出来
					}

					addItem1(player, modelId, addCount, addItemList, offSizeMap, typeDefList);
				}
			} else {
				for (int i = 0; i < addCount; i++) {
					addItem1(player, modelId, 1, addItemList, offSizeMap, typeDefList);
				}
			}
		}
	}

	/**
	 * 增加道具，并且要验证下偏移的数量
	 * 
	 * @param modelId
	 * @param count
	 * @param addItemList
	 * @param offSizeMap
	 * 
	 * @return 是否打到了某类型物品的叠加上限
	 */
	private boolean addItem1(Player player, int modelId, int count, List<ItemData> addItemList, EnumMap<EItemTypeDef, Integer> offSizeMap, List<EItemTypeDef> typeDefList) {
		ItemData newItemData = player.newItemData(modelId, count);
		EItemTypeDef type = newItemData.getType();
		int capacity = getCapacityByType(type);
		if (capacity <= 0) {
			addItemList.add(newItemData);
		} else {
			Integer hasValue = offSizeMap.get(type);
			int offSize = hasValue == null ? 0 : hasValue;
			int hasSize = getItemListByType(player.getUserId(), type).size() + offSize;
			if (hasSize < capacity) {
				addItemList.add(newItemData);
				offSizeMap.put(type, ++offSize);
			} else {
				if (!typeDefList.contains(type)) {
					typeDefList.add(type);
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * 通过物品类型获取该种物品的上限
	 * 
	 * @param type
	 * @return
	 */
	private int getCapacityByType(EItemTypeDef type) {
		return ItemBagCapacityCfgDAO.getCfgDAO().getItemBagCapacity(type);
	}

	/**
	 * 提供一个方法获取背包中某类型的物品是否已经达到了上限
	 * 
	 * @param type
	 * @return
	 */
	public boolean checkItemCapacityIsFull(String userId, EItemTypeDef type) {
		int capacity = getCapacityByType(type);
		if (capacity <= 0) {
			return false;
		}

		return getItemListByType(userId, type).size() < capacity;
	}

	/**
	 * 发送邮件
	 * 
	 * @param userId
	 * @param emailId
	 * @param param 拼接到内容的参数
	 */
	private void sendEmail(String userId, String emailId, Object... param) {
		EmailCfg emailCfg = EmailCfgDAO.getInstance().getEmailCfg(emailId);
		if (emailCfg == null) {
			return;
		}
		// 邮件内容
		EmailData emailData = new EmailData();
		emailData.setTitle(emailCfg.getTitle());
		String content = emailCfg.getContent();
		if (param != null && param.length > 0) {
			content = String.format(content, param);
		}
		emailData.setContent(content);
		emailData.setDeleteType(EEmailDeleteType.valueOf(emailCfg.getDeleteType()));
		emailData.setDelayTime(emailCfg.getDelayTime());// 整个帮派邮件只保留7天
		emailData.setSender(emailCfg.getSender());
		// 发送邮件
		EmailUtils.sendEmail(userId, emailData);
	}

	/**
	 * <pre>
	 * 向背包添加物品的方法
	 * 
	 * <font color="ff0000"><b>注意：此方法只能针对增加物品或者货币</b></font>
	 * 
	 * </pre>
	 * 
	 * @param itemInfoList {@link ItemInfo}
	 * 
	 * @throws IllegalArgumentException 当传递进来ItemInfo列表中，有任何一个itemNum < 0 就会抛出参数错误异常
	 * 
	 * @return
	 */
	public boolean addItem(Player player, List<ItemInfo> itemInfoList) throws IllegalArgumentException {
		if (itemInfoList == null || itemInfoList.isEmpty()) {
			return true;
		}

		int size = itemInfoList.size();

		// =========================解析数据

		List<INewItem> newItemList = new ArrayList<INewItem>(size);

		HashMap<Integer, Integer> currencyMap = new HashMap<Integer, Integer>();

		for (int i = 0; i < size; i++) {
			ItemInfo itemInfo = itemInfoList.get(i);
			if (itemInfo == null) {
				continue;
			}

			int itemID = itemInfo.getItemID();
			int itemNum = itemInfo.getItemNum();
			if (itemNum < 0) {
				throw new IllegalArgumentException("被传递了一个数量小于0的参数进来");
			}

			if (itemNum == 0) {
				continue;
			}

			if (itemID < eSpecialItemId.eSpecial_End.getValue()) {
				Integer hasValue = currencyMap.get(itemID);
				if (hasValue == null) {
					currencyMap.put(itemID, itemNum);
				} else {
					currencyMap.put(itemID, itemNum + hasValue);
				}
			} else if (ItemCfgHelper.isFashionSpecialItem(itemID)) {
				// 2016-11-01 添加对时装的判断
				addFashion(player, itemID);
			} else {
				newItemList.add(new NewItem(itemID, itemNum, null));
			}
		}

		// ==============================处理结果
		if ((newItemList == null || newItemList.isEmpty()) && (currencyMap == null || currencyMap.isEmpty())) {
			return false;
		}

		return useLikeBoxItem(player, null, newItemList, currencyMap);
	}

	/**
	 * 区分叠加和不叠加是否足够道具
	 * 
	 * @param itemModelId
	 * @param count
	 * @param selfModelId
	 * @return
	 */
	public List<IUseItem> checkEnoughItem(Player player, int itemModelId, int count, ItemData selfItem, ICheckItemWare checker) {
		String userId = player.getUserId();
		List<IUseItem> useItemList = new ArrayList<IUseItem>();
		ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(itemModelId);
		int stackNum = itemBaseCfg.getStackNum();
		if (stackNum > 1) {
			if (!checkEnoughItem(player, itemModelId, count)) {
				return null;
			} else {
				Map<Integer, ItemData> modelFirstItemDataMap = getModelFirstItemDataMap(userId);
				ItemData itemData = modelFirstItemDataMap.get(itemModelId);
				useItemList.add(new UseItem(itemData.getId(), count));
				return useItemList;
			}
		} else {
			List<ItemData> itemDatas = getItemListByCfgId(userId, itemModelId);
			String id = selfItem == null ? "" : selfItem.getId();
			for (int i = 0; i < itemDatas.size(); i++) {
				ItemData itemData = itemDatas.get(i);
				if ((itemData.getId().equals(id)) || checker.checkWare(itemData)) {
					continue;
				}
				useItemList.add(new UseItem(itemData.getId(), 1));
				if (useItemList.size() >= count) {
					break;
				}
			}
			if (useItemList.size() >= count) {
				return useItemList;
			} else {
				return null;
			}
		}
	}

	/**
	 * 解析产生的Id
	 */
	public int initMaxId(String userId) {
		return ItemBagHolder.getHolder().initMaxId(userId);
	}
}