package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.Action;
import com.playerdata.readonly.EquipMgrIF;
import com.rw.service.Equip.EquipHandler;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.equipment.EquipItemHelper;
import com.rwbase.dao.equipment.EquipItemHolder;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.role.EquipAttachCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.EquipAttachCfg;
import com.rwproto.EquipProtos.TagMate;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class EquipMgr extends IDataMgr implements EquipMgrIF {

	private EquipItemHolder equipItemHolder;

	public boolean init(Hero pOwner) {
		initPlayer(pOwner);
		equipItemHolder = new EquipItemHolder(pOwner.getUUId());
		return true;
	}

	public void regChangeCallBack(Action callBack) {
		equipItemHolder.regChangeCallBack(callBack);
	}

	public AttrData getTotalEquipAttrData() {
		return equipItemHolder.toAttrData();
	}

	/**
	 * 装备附灵
	 * 
	 * @param slot
	 * @param exp
	 * @param mateList
	 * @return -1没有装备；-2不够钱；-3:当前等级读取不到配置;0成功
	 */
	public int EquipAttach(int slot, List<TagMate> mateList) {
		String ownerId = m_pOwner.getUUId();
		EquipItem equipItem = equipItemHolder.getItem(ownerId, slot);
		int result = 0;
		if (equipItem == null) {
			result = -1;
		} else {
			// 1.获得当前等级
			// 2.获得下一等级所需经验
			// 3.如果下一等级所需经验大于当前经验 + 所加经验，则跳出;否则，当前等级 +1，所加经验 = 所加经验 - 下一等级所需经验
			int addExp = getExpByMaterial(mateList);
			int curExp = equipItem.getExp();
			int totalExp = addExp + curExp;// 所有经验
			int totalSubCoin = 0;

			EquipAttachCfg pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(equipItem.getLevel());
			if (pEquipAttachCfg == null) {
				// 配置错误
				return -3;
			}

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
						return -3;
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
			if (m_pPlayer.getUserGameDataMgr().addCoin(-totalSubCoin) == -1) {
				result = -2;
			} else {
				for (TagMate mate : mateList) {
					m_pPlayer.getItemBagMgr().useItemBySlotId(mate.getId(), mate.getCount());
				}
				m_pPlayer.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Hero_Strength, 1);
				equipItem.setLevel(pEquipAttachCfg.getId());
				equipItem.setExp(totalExp);
				equipItemHolder.updateItem(m_pPlayer, equipItem);
			}
			//
			//
			// if(pEquipAttachCfg != null){
			// boolean needUpgrade = true;
			// int totalSubCoin = 0;
			// while (needUpgrade) { // 循环升级
			// if (pEquipAttachCfg.getNextId() == 0) {
			// break;
			// }
			// if (curExp >= pEquipAttachCfg.getNeedExp()) {
			// totalSubCoin = totalSubCoin + pEquipAttachCfg.getNeedCoin() * (pEquipAttachCfg.getNeedExp() - attachExp);
			// attachExp = 0;
			// curExp = curExp - pEquipAttachCfg.getNeedExp();
			// pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(pEquipAttachCfg.getNextId());
			// if(pEquipAttachCfg == null){
			// return -3;
			// }
			// continue;
			// }
			// totalSubCoin = totalSubCoin + pEquipAttachCfg.getNeedCoin() * (curExp - attachExp);
			// needUpgrade = false;
			// }
			// if (m_pPlayer.getUserGameDataMgr().addCoin(-totalSubCoin) == -1) {
			// result = -2;
			// }else{
			// for (TagMate mate : mateList) {
			// m_pPlayer.getItemBagMgr().useItemBySlotId(mate.getId(), mate.getCount());
			// }
			// m_pPlayer.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Hero_Strength, 1);
			// equipItem.setLevel(pEquipAttachCfg.getId());
			// equipItem.setExp(curExp);
			// equipItemHolder.updateItem(m_pPlayer, equipItem);
			// }
			// }else{
			// result = -3;
			// }
		}
		return result;
	}

	private boolean CheckIsHasNext(EquipAttachCfg cfg) {
		if (cfg == null || cfg.getNextId() == 0) {
			return false;
		}
		return true;
	}

	private int getExpByMaterial(List<TagMate> mateList) {

		int totalExp = 0;
		for (TagMate mate : mateList) {// 循环遍历统计物品的总附灵经验
			ItemData itemData = m_pPlayer.getItemBagMgr().findBySlotId(mate.getId());
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
	public int EquipOneKeyAttach(int slot) {
		String ownerId = m_pOwner.getUUId();
		EquipItem equipItem = equipItemHolder.getItem(ownerId, slot);
		if (equipItem == null) {
			return -1;
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
		if (m_pPlayer.getUserGameDataMgr().addGold(-(int) Math.ceil(0.6 * tempExp)) == -1) {
			return -2;
		}
		equipItem.setLevel(pEquipAttachCfg.getId());
		equipItem.setExp(0);
		equipItemHolder.updateItem(m_pPlayer, equipItem);
		return 0;
	}

	/**
	 * 穿法宝
	 * 
	 * @param slotId
	 * @param ordinal
	 */
	public boolean WearEquip(String slotId, int equipIndex) throws CloneNotSupportedException {

		ItemData item = m_pPlayer.getItemBagMgr().findBySlotId(slotId);
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
				isOpen = CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.Player_Wear_Equip, m_pPlayer.getLevel());
			} else {
				isOpen = CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.Hero_Wear_Equip, m_pPlayer.getLevel());
			}
			if (!isOpen) {
				m_pPlayer.NotifyCommonMsg(ErrorType.NOT_ENOUGH_LEVEL);
				return false;
			}
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachExp_VALUE, String.valueOf(0));// 初始装备经验
			HeroEquipCfg heroEquipCfg = (HeroEquipCfg) HeroEquipCfgDAO.getInstance().getCfgById(String.valueOf(equipId));
			int attachLevel = getEquipAttachInitId(heroEquipCfg.getQuality());
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE, String.valueOf(attachLevel));// 初始装备等级ID

			m_pPlayer.getItemBagMgr().useItemByCfgId(equipId, 1);
			equipItemHolder.wearEquip(m_pPlayer, equipIndex, equipItemData);
		}
		return true;
	}

	public int getEquipAttachInitId(int quality) {// 根据品质获取佣兵装备初始强化ID
		int id = 0;
		switch (quality) {
		case 1:
			id = 1000;
			break;
		case 2:
			id = 2000;
			break;
		case 3:
			id = 3000;
			break;
		case 4:
			id = 4000;
			break;

		default:
			break;
		}
		return id;
	}

	/**
	 * 穿装备
	 * 
	 * @param bagSlot
	 * @param nEquipSlotId
	 * @throws CloneNotSupportedException
	 */
	public boolean WearEquip(int equipIndex) throws CloneNotSupportedException {
		List<Integer> equips = RoleQualityCfgDAO.getInstance().getEquipList(m_pOwner.getQualityId());
		int equipId = equips.get(equipIndex);
		List<ItemData> itemList = m_pPlayer.getItemBagMgr().getItemListByCfgId(equipId);
		if (itemList == null || itemList.size() == 0) {
			return false;
		}
		ItemData item = itemList.get(0);
		return WearEquip(item.getId(), equipIndex);
	}

	public boolean canWearEquip() {
		List<Integer> equips = RoleQualityCfgDAO.getInstance().getEquipList(m_pOwner.getQualityId());

		for (Integer equipId : equips) {
			HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getConfig(equipId);
			if (hasEquip(equipId) || cfg.getLevel() > m_pOwner.getLevel()) {
				continue;
			}

			List<ItemData> itemList = m_pPlayer.getItemBagMgr().getItemListByCfgId(equipId);
			if (itemList != null && itemList.size() > 0) {
				return true;
			}
			if (EquipHandler.checkCompose(m_pPlayer, equipId) == 1) {
				return true;
			}
		}
		return false;
	}

	private boolean hasEquip(int modelid) {
		List<EquipItem> equipList = getEquipList();
		for (EquipItem equipItem : equipList) {
			if (equipItem.getModelId() == modelid) {
				return true;
			}
		}
		return false;
	}

	public boolean save() {
		equipItemHolder.flush();
		return true;
	}

	public void syncAllEquip(int version) {
		equipItemHolder.synAllData(m_pPlayer, version);
	}

	public int getEquipCount() {
		return getEquipList().size();
	}

	public List<EquipItem> getEquipList() {
		List<EquipItem> equipList = new ArrayList<EquipItem>();
		List<EquipItem> itemList = equipItemHolder.getItemList();
		for (EquipItem equipItem : itemList) {
			if (equipItem.getType() == EItemTypeDef.HeroEquip) {
				equipList.add(equipItem);
			}
		}

		return itemList;
	}

	/**
	 * 清除所有装备
	 * 
	 * @return
	 */
	public void subAllEquip() {
		List<EquipItem> equipList = getEquipList();
		for (EquipItem equipItem : equipList) {
			equipItemHolder.removeItem(m_pPlayer, equipItem);
		}
	}

	public void EquipAdvance(String nextId, final boolean isSubEquip) {
		String preQualityId = m_pOwner.getQualityId();
		m_pOwner.setQualityId(nextId);
		// 任务
		m_pPlayer.getTaskMgr().AddTaskTimes(eTaskFinishDef.Hero_Quality);
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
			sendBackAttachMaterial(getEquipList());
			subAllEquip();
		}
	}

	/**
	 * 转职返回装备和附灵材料
	 */
	public void changeEquip() {
		List<EquipItem> equipList = getEquipList();
		for (EquipItem equipItem : equipList) {
			ItemData equipItemData = EquipItemHelper.toEquipItemData(equipItem);
			m_pPlayer.getItemBagMgr().addItem(equipItemData.getModelId(), 1);
		}
		sendBackAttachMaterial(equipList);
		subAllEquip();
	}

	/**
	 * 装备返回附灵材料 背包添加
	 */
	private void sendBackAttachMaterial(List<EquipItem> equipList) {
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
			m_pPlayer.getItemBagMgr().addItem(backId, backNum);
		}
		// //= PublicDataCfgDAO.getInstance().getPublicDataStringValueById(PublicData.HERO_STRENGTH_RETURNE);
		// String[] levelList = strData.split(",");
		// for (EquipItem equipItem : equipList) {
		//
		// EItemTypeDef eItemType = equipItem.getType();
		// if (eItemType != EItemTypeDef.HeroEquip) {
		// continue;
		// }
		// int attachlevel = equipItem.getLevel();
		// EquipAttachCfg pEquipAttachCfg = EquipAttachCfgDAO.getInstance().getConfig(attachlevel);
		// if (pEquipAttachCfg == null)
		// continue;
		// int startLevel = pEquipAttachCfg.getStarLevel();
		// if (startLevel > 0 && startLevel < levelList.length) {
		// String itemStr = levelList[startLevel - 1];
		// String[] itemListStr = itemStr.split("_");
		// if (startLevel == Integer.valueOf(itemListStr[0])) {
		// int templateId = Integer.valueOf(itemListStr[1]);
		// int num = Integer.valueOf(itemListStr[2]);
		// m_pPlayer.getItemBagMgr().addItem(templateId, num);
		// }
		// }
		// }
	}

	/**
	 * 添加装备到机器人身上
	 * 
	 * @param equipList 穿戴装备的列表
	 */
	public void addRobotEquip(List<ItemData> equipList) {
		for (int i = 0, size = equipList.size(); i < size; i++) {
			ItemData itemData = equipList.get(i);
			int templateId = itemData.getModelId();
			HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getConfig(templateId);
			int equipType = cfg.getEquipType();
			int index = equipIndex.get(equipType);
			equipItemHolder.addRobotEquip(index, itemData);
		}
	}

	/**
	 * gm命令修改附灵等级 只限gm指令调用
	 * 
	 * @param slot
	 * @param level
	 */
	public void gmEquipAttach(int slot, int level) {
		String ownerId = m_pOwner.getUUId();
		EquipItem equipItem = equipItemHolder.getItem(ownerId, slot);
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
		}
		equipItem.setLevel(pEquipAttachCfg.getId());
		equipItem.setExp(0);
		equipItemHolder.updateItem(m_pPlayer, equipItem);
	}

	/**
	 * gm命令穿装备
	 * 
	 * @param slotId
	 * @param equipIndex
	 * @param limited
	 * @return
	 */
	public boolean gmEquip(int equipIndex) {
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
			int attachLevel = getEquipAttachInitId(heroEquipCfg.getQuality());
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE, String.valueOf(attachLevel));// 初始装备等级ID

			m_pPlayer.getItemBagMgr().useItemByCfgId(equipId, 1);
			equipItemHolder.wearEquip(m_pPlayer, equipIndex, equipItemData);
		}
		return true;
	}

	/**
	 * 作弊指令增加穿满装备
	 * 
	 * @param hero
	 * @return
	 */
	public void orderHeroWearEquip(Hero hero) {
		String qualityId = hero.getQualityId();
		List<Integer> equipList = RoleQualityCfgDAO.getInstance().getEquipList(qualityId);
		if (equipList.isEmpty()) {
			return;
		}

		for (int i = 0, size = equipList.size(); i < size; i++) {
			int equipId = equipList.get(i);
			if (hasEquip(equipId)) {
				continue;
			}

			// 新装备
			ItemData equipItemData = new ItemData();
			equipItemData.setCount(1);
			equipItemData.setModelId(equipId);
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachExp_VALUE, String.valueOf(0));// 初始装备经验
			HeroEquipCfg heroEquipCfg = (HeroEquipCfg) HeroEquipCfgDAO.getInstance().getCfgById(String.valueOf(equipId));
			int attachLevel = getEquipAttachInitId(heroEquipCfg.getQuality());
			equipItemData.setExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE, String.valueOf(attachLevel));// 初始装备等级ID
			equipItemHolder.wearEquip(m_pPlayer, i, equipItemData);
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