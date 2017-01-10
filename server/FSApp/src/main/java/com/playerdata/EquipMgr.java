package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.EquipHelper;
import com.common.IHeroAction;
import com.playerdata.hero.core.FSHeroBaseInfoMgr;
import com.playerdata.readonly.EquipMgrIF;
import com.playerdata.refactor.IDataMgrSingletone;
import com.rw.service.Equip.EquipHandler;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.equipment.EquipItemHelper;
import com.rwbase.dao.equipment.EquipItemHolder;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.role.EquipAttachCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.EquipAttachCfg;
import com.rwproto.EquipProtos.TagMate;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

//public class EquipMgr extends IDataMgr implements EquipMgrIF {
public class EquipMgr implements EquipMgrIF, IDataMgrSingletone {

	private static EquipMgr _instancce = new EquipMgr();

	public static EquipMgr getInstance() {
		return _instancce;
	}

	/**
	 * 装备附灵结果：成功
	 */
	public static final int EQUIP_ATTACH_SUCCESS = 0;
	/**
	 * 装备附灵结果：失败，没有该装备
	 */
	public static final int EQUIP_ATTACH_FAIL_NO_EQUIP = -1;
	/**
	 * 装备附灵结果：失败，没有足够的金钱
	 */
	public static final int EQUIP_ATTACH_FAIL_NOT_ENOUGH_MONEY = -2;
	/**
	 * 装备附灵结果：失败，没有该配置
	 */
	public static final int EQUIP_ATTACH_FAIL_NO_CFG = -3;

	protected EquipMgr() {
	}

	private final EquipItemHolder equipItemHolder = EquipItemHolder.getInstance();

	// private EquipItemHolder equipItemHolder;
	//
	// public boolean init(Hero pOwner) {
	// initPlayer(pOwner);
	// equipItemHolder = new EquipItemHolder(pOwner.getUUId());
	// return true;
	// }

	public boolean init(Hero pOwner) {
		return true;
	}

	public void regDataChangeCallback(IHeroAction callback) {
		equipItemHolder.regDataChangeCallback(callback);
	}

	/**
	 * 装备附灵
	 * 
	 * @param heroId
	 * @param slot
	 * @param exp
	 * @param mateList
	 * @return -1没有装备；-2不够钱；-3:当前等级读取不到配置;0成功
	 */
	public int EquipAttach(Player player, String heroId, int slot, List<TagMate> mateList) {
		EquipItem equipItem = equipItemHolder.getItem(heroId, slot);
		int result = EQUIP_ATTACH_SUCCESS;
		if (equipItem == null) {
			result = EQUIP_ATTACH_FAIL_NO_EQUIP;
		} else {
			// 1.获得当前等级
			// 2.获得下一等级所需经验
			// 3.如果下一等级所需经验大于当前经验 + 所加经验，则跳出;否则，当前等级 +1，所加经验 = 所加经验 - 下一等级所需经验
			int addExp = getExpByMaterial(player, mateList);
			int curExp = equipItem.getExp();
			int totalExp = addExp + curExp;// 所有经验
			int totalSubCoin = 0;

			EquipAttachCfg pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(equipItem.getLevel());
			if (pEquipAttachCfg == null) {
				// 配置错误
				return EQUIP_ATTACH_FAIL_NO_CFG;
			}

			int tempStarLevel = pEquipAttachCfg.getStarLevel();
			int nextNeedExp = pEquipAttachCfg.getNeedExp();// 下一级需要的经验值
			while (totalExp >= nextNeedExp) {
				if (CheckIsHasNext(pEquipAttachCfg)) {
					totalSubCoin += pEquipAttachCfg.getNeedCoin() * (nextNeedExp - curExp);
					curExp = 0;// 当前经验
					totalExp -= nextNeedExp;// 总经验
					pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(pEquipAttachCfg.getNextId());// 下一级的模版
					if (pEquipAttachCfg != null) {
						nextNeedExp = pEquipAttachCfg.getNeedExp();// 下一级需要的经验值
					} else {
						break;
					}
				} else {
					if (pEquipAttachCfg == null) {
						// 配置错误
						return EQUIP_ATTACH_FAIL_NO_CFG;
					}

					// 最高等级
					totalExp = 0;
					break;
				}
			}

			// 吞完所有卡之后，达到了最大的等级，就要把total设置成当前最大的经验
			if (totalExp >= nextNeedExp) {
				totalExp = nextNeedExp;
			} else {
				totalSubCoin += pEquipAttachCfg.getNeedCoin() * (totalExp - curExp);
			}

			// 删除材料
			// 更新装备附灵等级的经验
			if (player.getUserGameDataMgr().addCoin(-totalSubCoin) == -1) {
				result = EQUIP_ATTACH_FAIL_NOT_ENOUGH_MONEY;
			} else {
				ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
				for (TagMate mate : mateList) {
					itemBagMgr.useItemBySlotId(player, mate.getId(), mate.getCount());
				}
				player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Hero_Strength, 1);
				equipItem.setLevel(pEquipAttachCfg.getId());
				equipItem.setExp(totalExp);
				equipItemHolder.updateItem(player, heroId, equipItem);
				//UserEventMgr.getInstance().attachDaily(player, pEquipAttachCfg.getStarLevel(), tempStarLevel);// pEquipAttachCfg.getId()-levelBeforeAttach;1次附灵升70级也计数1
			}
		}
		return result;
	}

	private boolean CheckIsHasNext(EquipAttachCfg cfg) {
		if (cfg == null || cfg.getNextId() == 0) {
			return false;
		}
		return true;
	}

	private int getExpByMaterial(Player player, List<TagMate> mateList) {

		int totalExp = 0;
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		String userId = player.getUserId();
		for (TagMate mate : mateList) {// 循环遍历统计物品的总附灵经验
			ItemData itemData = itemBagMgr.findBySlotId(userId, mate.getId());
			ItemBaseCfg pItemBaseCfg = ItemCfgHelper.GetConfig(itemData.getModelId());
			if (pItemBaseCfg != null) {
				totalExp = totalExp + pItemBaseCfg.getEnchantExp() * mate.getCount();
			}

		}
		return totalExp;
	}

	/**
	 * 一键附灵
	 * 
	 * @param slot
	 * @return 0-成功，-1:没有装备；-2：钻石不够
	 */
	public int EquipOneKeyAttach(Player player, String heroId, int slot) {
		EquipItem equipItem = equipItemHolder.getItem(heroId, slot);
		if (equipItem == null) {
			return EQUIP_ATTACH_FAIL_NO_EQUIP;
		}

		int attachExp = equipItem.getExp();
		int attachLevel = equipItem.getLevel();
		EquipAttachCfg pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(attachLevel);
		int tempExp = -attachExp;
		while (pEquipAttachCfg != null) { // 循环升级
			if (pEquipAttachCfg.getNextId() == 0) {
				break;
			}
			tempExp += pEquipAttachCfg.getNeedExp();
			pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(pEquipAttachCfg.getNextId());
		}
		if (player.getUserGameDataMgr().addGold(-(int) Math.ceil(0.6 * tempExp)) == -1) {
			return EQUIP_ATTACH_FAIL_NOT_ENOUGH_MONEY;
		}
		equipItem.setLevel(pEquipAttachCfg.getId());
		equipItem.setExp(0);
		equipItemHolder.updateItem(player, heroId, equipItem);
		return EQUIP_ATTACH_SUCCESS;
	}

	/**
	 * 穿法宝
	 * 
	 * @param player
	 * @param heroId
	 * @param slotId
	 * @param ordinal
	 */
	public boolean wearEquip(Player player, String heroId, String slotId, int equipIndex) {

		Hero m_pOwner = player.getHeroMgr().getHeroById(player, heroId);
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();

		String userId = player.getUserId();
		ItemData item = itemBagMgr.findBySlotId(userId, slotId);
		if (item == null) {
			return false;
		}
		// TODO HC @modify 注意：这里的代码一定要看清楚用意然后再做修改
		item = new ItemData(item);// 简单clone一个对象
		int equipId = item.getModelId();
		EItemTypeDef eItemType = ItemCfgHelper.getItemType(equipId);
		if (eItemType == EItemTypeDef.HeroEquip) {
			// 新装备
			ItemData equipItemData = new ItemData();
			equipItemData.setCount(1);
			equipItemData.setModelId(item.getModelId());

			boolean isOpen = false;
			if (m_pOwner.getRoleType() == eRoleType.Player) {
				isOpen = CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.Player_Wear_Equip, player);
			} else {
				isOpen = CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.Hero_Wear_Equip, player);
			}
			if (!isOpen) {
				player.NotifyCommonMsg(ErrorType.NOT_ENOUGH_LEVEL);
				return false;
			}
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachExp_VALUE, String.valueOf(0));// 初始装备经验
			HeroEquipCfg heroEquipCfg = (HeroEquipCfg) HeroEquipCfgDAO.getInstance().getCfgById(String.valueOf(equipId));
			int attachLevel = EquipHelper.getEquipAttachInitId(heroEquipCfg.getQuality());
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE, String.valueOf(attachLevel));// 初始装备等级ID

			itemBagMgr.useItemByCfgId(player, equipId, 1);
			equipItemHolder.wearEquip(player, heroId, equipIndex, equipItemData);
		}
		return true;
	}

	public boolean wearEquips(Player player, String heroId, Map<Integer, String> equipMap) {
		// 此方法暂时不可用：原因，客户端收到同步道具的消息的时候，会去检查装备有没有同步过来
		boolean isOpen = false;
		Hero m_pOwner = player.getHeroMgr().getHeroById(player, heroId);
		if (m_pOwner.getRoleType() == eRoleType.Player) {
			isOpen = CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.Player_Wear_Equip, player);
		} else {
			isOpen = CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.Hero_Wear_Equip, player);
		}
		if (!isOpen) {
			player.NotifyCommonMsg(ErrorType.NOT_ENOUGH_LEVEL);
			return false;
		}
		Map<Integer, ItemData> equipItemMap = new HashMap<Integer, ItemData>(equipMap.size() + 1, 1.0f);
		String slotId;
		int index;
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		String userId = player.getUserId();
		ItemData equipItem;
		ItemData item;
		List<IUseItem> removeItems = new ArrayList<IUseItem>(equipMap.size());
		HeroEquipCfgDAO heroEquipCfgDAO = HeroEquipCfgDAO.getInstance();
		for (Iterator<Integer> itr = equipMap.keySet().iterator(); itr.hasNext();) {
			index = itr.next();
			slotId = equipMap.get(index);
			item = itemBagMgr.findBySlotId(userId, slotId);
			if (item != null && item.getType() == EItemTypeDef.HeroEquip) {
				equipItem = new ItemData();
				equipItem.setModelId(item.getModelId());
				equipItem.setCount(1);
				equipItem.setExtendAttr(EItemAttributeType.Equip_AttachExp_VALUE, String.valueOf(0));// 初始装备经验
				HeroEquipCfg heroEquipCfg = heroEquipCfgDAO.getCfgById(String.valueOf(equipItem.getModelId()));
				int attachLevel = EquipHelper.getEquipAttachInitId(heroEquipCfg.getQuality());
				equipItem.setExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE, String.valueOf(attachLevel));// 初始装备等级ID
				equipItemMap.put(index, equipItem);
				removeItems.add(new UseItem(slotId, 1));
			} else {
				removeItems.clear();
				equipItemMap.clear();
				return false;
			}
		}
		itemBagMgr.useLikeBoxItem(player, removeItems, null);
		return equipItemHolder.wearEquips(player, heroId, equipItemMap);
	}

	/**
	 * 穿装备
	 * 
	 * @param player
	 * @param heroId
	 * @param equipIndex
	 * @throws CloneNotSupportedException
	 */
	public boolean WearEquip(Player player, String heroId, int equipIndex) {
		Hero m_pOwner = player.getHeroMgr().getHeroById(player, heroId);
		List<Integer> equips = RoleQualityCfgDAO.getInstance().getEquipList(m_pOwner.getQualityId());
		int equipId = equips.get(equipIndex);
		List<ItemData> itemList = ItemBagMgr.getInstance().getItemListByCfgId(player.getUserId(), equipId);
		if (itemList == null || itemList.isEmpty()) {
			return false;
		}
		ItemData item = itemList.get(0);
		return wearEquip(player, heroId, item.getId(), equipIndex);
	}

	public boolean canWearEquip(Player player, String heroId) {
		Hero hero = player.getHeroMgr().getHeroById(player, heroId);
		List<Integer> equips = RoleQualityCfgDAO.getInstance().getEquipList(hero.getQualityId());
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		String userId = player.getUserId();

		for (Integer equipId : equips) {
			HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getConfig(equipId);
			if (hasEquip(heroId, equipId) || cfg.getLevel() > hero.getLevel()) {
				continue;
			}

			List<ItemData> itemList = itemBagMgr.getItemListByCfgId(userId, equipId);
			if (itemList != null && itemList.size() > 0) {
				return true;
			}
			if (EquipHandler.checkCompose(player, equipId) == 1) {
				return true;
			}
		}
		return false;
	}

	private boolean hasEquip(String heroId, int modelid) {
		List<EquipItem> equipList = getEquipList(heroId);
		for (EquipItem equipItem : equipList) {
			if (equipItem.getModelId() == modelid) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean save(String heroId) {
		equipItemHolder.flush(heroId);
		return true;
	}

	@Override
	public boolean load(String heroId) {
		return true;
	}

	public void syncAllEquip(Player player, String heroId, int version) {
		equipItemHolder.synAllData(player, heroId, version);
	}

	public int getEquipCount(String heroId) {
		return getEquipList(heroId).size();
	}

	public List<EquipItem> getEquipList(String heroId) {
		List<EquipItem> equipList = new ArrayList<EquipItem>();
		List<EquipItem> itemList = equipItemHolder.getItemList(heroId);
		for (EquipItem equipItem : itemList) {
			if (equipItem.getType() == EItemTypeDef.HeroEquip) {
				equipList.add(equipItem);
			}
		}

		return itemList;
	}

	/**
	 * 
	 * @param player
	 * @param heroId
	 */
	public void subAllEquip(Player player, String heroId) {
		List<EquipItem> equipList = getEquipList(heroId);
		// for (EquipItem equipItem : equipList) {
		// equipItemHolder.removeItem(player, heroId, equipItem);
		// }
		equipItemHolder.removeAllItem(player, heroId, equipList);
	}

	/**
	 * 
	 * @param player
	 * @param heroId
	 * @param nextId
	 * @param isSubEquip
	 */
	public void EquipAdvance(Player player, String heroId, String nextId, final boolean isSubEquip) {
		Hero hero = player.getHeroMgr().getHeroById(player, heroId);
		String preQualityId = hero.getQualityId();
		// hero.setQualityId(nextId);
		FSHeroBaseInfoMgr.getInstance().setQualityId(hero, nextId);
		// 任务
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Hero_Quality);
		boolean subEquip = isSubEquip;
		if (!subEquip) {
			// 转职检测有装备有没有变更，没有则不变，有则吃掉装备
			List<Integer> preequips = RoleQualityCfgDAO.getInstance().getEquipList(preQualityId);
			List<Integer> curequips = RoleQualityCfgDAO.getInstance().getEquipList(nextId);
			for (int i = 0; i < preequips.size(); i++) {
				// System.out.print(preequips.get(i) + "," + curequips.get(i));
				if (!preequips.get(i).equals(curequips.get(i))) {
					subEquip = true;
					break;
				}
			}
		}
		if (subEquip) {
			// 吃掉装备
			sendBackAttachMaterial(player, getEquipList(heroId));
			subAllEquip(player, heroId);
		}
	}

	/**
	 * 
	 * @param player
	 * @param heroId
	 */
	public void changeEquip(Player player, String heroId) {
		List<EquipItem> equipList = getEquipList(heroId);
		// for (EquipItem equipItem : equipList) {
		// ItemData equipItemData = EquipItemHelper.toEquipItemData(equipItem);
		// player.getItemBagMgr().addItem(equipItemData.getModelId(), 1);
		// }
		List<ItemInfo> list = new ArrayList<ItemInfo>(equipList.size());
		for (EquipItem equipItem : equipList) {
			ItemData equipItemData = EquipItemHelper.toEquipItemData(equipItem);
			list.add(new ItemInfo(equipItemData.getModelId(), 1));
		}
		ItemBagMgr.getInstance().addItem(player, list);
		sendBackAttachMaterial(player, equipList);
		subAllEquip(player, heroId);
	}

	/**
	 * 
	 * @param player
	 * @param equipList
	 */
	private void sendBackAttachMaterial(Player player, List<EquipItem> equipList) {
		// caro:策划要求暂时全部返回
		int totalExp = 0;
		for (EquipItem equipItem : equipList) {
			EItemTypeDef eItemType = equipItem.getType();
			if (eItemType != EItemTypeDef.HeroEquip) {
				continue;
			}
			totalExp += equipItem.getExp();
			EquipAttachCfg pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(equipItem.getLevel());
			while (pEquipAttachCfg != null && pEquipAttachCfg.getPreId() != 0) {
				pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(pEquipAttachCfg.getPreId());
				totalExp += pEquipAttachCfg.getNeedExp();
			}
		}

		int backId = 804001;
		ItemBaseCfg itemcfg = ItemCfgHelper.GetConfig(backId);
		if (itemcfg != null) {
			int backNum = itemcfg.getEnchantExp() == 0 ? totalExp : (int) (totalExp / itemcfg.getEnchantExp());
			ItemBagMgr.getInstance().addItem(player, backId, backNum);
		}
	}

	/**
	 * 添加装备到机器人身上
	 * 
	 * @param heroId
	 * @param equipList 穿戴装备的列表
	 */
	public void addRobotEquip(String heroId, List<ItemData> equipList) {
		int size = equipList.size();
		ArrayList<EquipItem> equipItemList = new ArrayList<EquipItem>(size);
		for (int i = 0; i < size; i++) {
			ItemData itemData = equipList.get(i);
			int templateId = itemData.getModelId();
			HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getConfig(templateId);
			int equipType = cfg.getEquipType();
			int index = equipIndex.get(equipType);
			EquipItem equipItem = EquipItemHelper.toEquip(heroId, index, itemData);
			equipItemList.add(equipItem);
		}
		equipItemHolder.addRobotEquip(heroId, equipItemList);
	}

	/**
	 * gm命令修改附灵等级 只限gm指令调用
	 * 
	 * @param player
	 * @param heroId
	 * @param slot
	 * @param level
	 */
	public void gmEquipAttach(Player player, String heroId, int slot, int level) {
		EquipItem equipItem = equipItemHolder.getItem(heroId, slot);
		if (equipItem == null) {
			return;
		}
		HeroEquipCfg heroEquipCfg = (HeroEquipCfg) HeroEquipCfgDAO.getInstance().getCfgById(String.valueOf(equipItem.getModelId()));
		if (heroEquipCfg.getEnchantLimit() == 0) {
			return;
		}
		int attachLevel = equipItem.getLevel();
		EquipAttachCfg pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(attachLevel);
		while (pEquipAttachCfg != null) { // 循环升级
			if (pEquipAttachCfg.getNextId() == 0) {
				break;
			}
			if (pEquipAttachCfg.getStarLevel() == level) {
				break;
			}
			pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(pEquipAttachCfg.getNextId());
			equipItem.setLevel(pEquipAttachCfg.getId());
		}
		equipItem.setExp(0);
		equipItemHolder.updateItem(player, heroId, equipItem);
	}

	/**
	 * gm命令穿装备
	 * 
	 * @param player
	 * @param heroId
	 * @param equipIndex
	 * @return
	 */
	public boolean gmEquip(Player player, String heroId, int equipIndex) {
		Hero m_pOwner = player.getHeroMgr().getHeroById(player, heroId);
		// TODO HC @modify 注意：这里的代码一定要看清楚用意然后再做修改
		List<Integer> equips = RoleQualityCfgDAO.getInstance().getEquipList(m_pOwner.getQualityId());
		int equipId = equips.get(equipIndex);
		EItemTypeDef eItemType = ItemCfgHelper.getItemType(equipId);
		if (eItemType == EItemTypeDef.HeroEquip) {
			// 新装备
			ItemData equipItemData = new ItemData();
			equipItemData.setCount(1);
			equipItemData.setModelId(equipId);
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachExp_VALUE, String.valueOf(0));// 初始装备经验
			HeroEquipCfg heroEquipCfg = (HeroEquipCfg) HeroEquipCfgDAO.getInstance().getCfgById(String.valueOf(equipId));
			int attachLevel = EquipHelper.getEquipAttachInitId(heroEquipCfg.getQuality());
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE, String.valueOf(attachLevel));// 初始装备等级ID

			ItemBagMgr.getInstance().useItemByCfgId(player, equipId, 1);
			equipItemHolder.wearEquip(player, heroId, equipIndex, equipItemData);
		}
		return true;
	}

	/**
	 * 作弊指令增加穿满装备
	 * 
	 * @param player
	 * @param hero
	 * @return
	 */
	public void orderHeroWearEquip(Player player, Hero hero) {
		String qualityId = hero.getQualityId();
		List<Integer> equipList = RoleQualityCfgDAO.getInstance().getEquipList(qualityId);
		if (equipList.isEmpty()) {
			return;
		}

		for (int i = 0, size = equipList.size(); i < size; i++) {
			int equipId = equipList.get(i);
			if (hasEquip(hero.getUUId(), equipId)) {
				continue;
			}

			// 新装备
			ItemData equipItemData = new ItemData();
			equipItemData.setCount(1);
			equipItemData.setModelId(equipId);
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachExp_VALUE, String.valueOf(0));// 初始装备经验
			HeroEquipCfg heroEquipCfg = (HeroEquipCfg) HeroEquipCfgDAO.getInstance().getCfgById(String.valueOf(equipId));
			int attachLevel = EquipHelper.getEquipAttachInitId(heroEquipCfg.getQuality());
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE, String.valueOf(attachLevel));// 初始装备等级ID
			equipItemHolder.wearEquip(player, hero.getUUId(), i, equipItemData);
		}
	}

	private static Map<Integer, Integer> equipIndex;// 装备索引对应的装备类型
	static {
		equipIndex = new HashMap<Integer, Integer>(6);
		equipIndex.put(0, 3);
		equipIndex.put(1, 0);
		equipIndex.put(2, 1);
		equipIndex.put(3, 4);
		equipIndex.put(4, 5);
		equipIndex.put(5, 2);
	}
}