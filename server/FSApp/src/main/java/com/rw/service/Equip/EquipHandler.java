package com.rw.service.Equip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.common.RefInt;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.EquipMgr;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.bm.GFOnlineListenerPlayerChange;
import com.playerdata.teambattle.bm.TBListenerPlayerChange;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.enu.ECareer;
import com.rwbase.common.enu.EHeroQuality;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.item.ComposeCfgDAO;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.ComposeCfg;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.NewItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwproto.EquipProtos.EquipEventType;
import com.rwproto.EquipProtos.EquipResponse;
import com.rwproto.EquipProtos.TagMate;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.PrivilegeProtos.HeroPrivilegeNames;

public class EquipHandler {

	private static EquipHandler instance = new EquipHandler();

	public static EquipHandler getInstance() {
		return instance;
	}

	/**
	 * 进阶
	 * 
	 * @param player
	 * @param roleId
	 * @return
	 */
	public ByteString advance(Player player, String roleId) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Advance);
		EquipMgr pEquipMgr = getEquipMgr(player, roleId);
		if (pEquipMgr == null) {
			response.setError(ErrorType.NOT_ROLE);
			return response.build().toByteString();
		}
		boolean canUpgrade = pEquipMgr.getEquipCount(roleId) >= 6;
		if (canUpgrade) {
			Hero role = player.getHeroMgr().getHeroById(roleId);

			RoleQualityCfg pNextCfg = RoleQualityCfgDAO.getInstance().getNextConfig(role.getQualityId());
			if (pNextCfg != null) {
				if (roleId.equals(player.getUserId()) && player.getCareer() == ECareer.None.ordinal() && pNextCfg.getQuality() > EHeroQuality.Green.ordinal()) {
					player.NotifyCommonMsg("没有职业不能进下一阶！");
				} else {
					pEquipMgr.EquipAdvance(player, roleId, pNextCfg.getId(), true);
					response.setError(ErrorType.SUCCESS);
					UserEventMgr.getInstance().advanceDaily(player, 1);
					GFOnlineListenerPlayerChange.defenderChangeHandler(player);
					TBListenerPlayerChange.heroChangeHandler(player);
				}
			} else {
				response.setError(ErrorType.FAIL);
			}
		} else {
			response.setError(ErrorType.NOT_EQUIP_ADVANCE);
		}
		return response.build().toByteString();
	}

	/**
	 * 装备附灵
	 * 
	 * @param player
	 * @param roleId
	 * @param equipIndex
	 * @param mateList
	 * @return
	 */
	public ByteString equipAttach(Player player, String roleId, int equipIndex, List<TagMate> mateList) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Equip_Attach);
		EquipMgr pEquipMgr = getEquipMgr(player, roleId);
		if (pEquipMgr == null) {
			response.setError(ErrorType.NOT_ROLE);
			return response.build().toByteString();
		}
		for (TagMate mate : mateList) {// 循环遍历统计物品的总附灵经验
			ItemData itemData = player.getItemBagMgr().findBySlotId(mate.getId());
			if (itemData == null || itemData.getCount() < mate.getCount()) {
				response.setError(ErrorType.NOT_ENOUGH_MATE);
				return response.build().toByteString();
			}
		}

		int result = pEquipMgr.EquipAttach(player, roleId, equipIndex, mateList);// 增加装备的附灵经验
		switch (result) {
//		case -1:
		case EquipMgr.EQUIP_ATTACH_FAIL_NO_EQUIP:
			response.setError(ErrorType.NOT_EQUIP);
			break;
//		case -2:
		case EquipMgr.EQUIP_ATTACH_FAIL_NOT_ENOUGH_MONEY:
			response.setError(ErrorType.NOT_ENOUGH_COIN);
			break;
//		case 0:
		case EquipMgr.EQUIP_ATTACH_SUCCESS:
			response.setError(ErrorType.SUCCESS);
			break;
//		case -3:
		case EquipMgr.EQUIP_ATTACH_FAIL_NO_CFG:
			response.setError(ErrorType.CONFIG_ERROR);
			break;
		}

		return response.build().toByteString();
	}

	/**
	 * 装备一键附灵
	 * 
	 * @param player
	 * @param roleId
	 * @param equipIndex
	 * @return
	 */
	public ByteString equipOnekeyAttach(Player player, String roleId, int equipIndex) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Equip_OnekeyAttach);
		boolean isOpen = player.getPrivilegeMgr().getBoolPrivilege(HeroPrivilegeNames.isAllowAttach);
		if (!isOpen) {
			GameLog.error("一键附灵", player.getUserId(), String.format("对英雄Id为[%s]的英雄进行一键附灵,Vip等级不足", roleId));
			response.setError(ErrorType.NOT_ENOUGH_VIP);
			return response.build().toByteString();
		}

		EquipMgr pEquipMgr = getEquipMgr(player, roleId);
		if (pEquipMgr == null) {
			response.setError(ErrorType.NOT_ROLE);
			return response.build().toByteString();
		}

		int result = pEquipMgr.EquipOneKeyAttach(player, roleId, equipIndex);// 一键附灵
		switch (result) {
//		case -1:
		case EquipMgr.EQUIP_ATTACH_FAIL_NO_EQUIP:
			response.setError(ErrorType.NOT_EQUIP);
			break;
//		case -2:
		case EquipMgr.EQUIP_ATTACH_FAIL_NOT_ENOUGH_MONEY:
			response.setError(ErrorType.NOT_ENOUGH_GOLD);
			break;
//		case 0:
		case EquipMgr.EQUIP_ATTACH_SUCCESS:
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Hero_Strength, 1);
			response.setError(ErrorType.SUCCESS);
			break;
		default:
			break;
		}
		return response.build().toByteString();
	}

	/**
	 * 装备合成
	 * 
	 * @param player
	 * @param equipId
	 * @return
	 */
	public ByteString equipCompose(Player player, int equipId) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Equip_Compose);

		String userId = player.getUserId();
		ComposeCfg cfg = ComposeCfgDAO.getInstance().getCfg(equipId);
		if (cfg == null) {
			GameLog.error("装备合成", userId, String.format("装备Id[%s]找不到对应的ComposeCfg配置", equipId));
			response.setError(ErrorType.FAIL);
			return response.build().toByteString();
		}

		RefInt out = new RefInt();
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		Map<Integer, Integer> composeNeedMateMap = getComposeNeedMateMap(itemBagMgr, equipId, 1, out);
		if (composeNeedMateMap == null || composeNeedMateMap.isEmpty()) {
			GameLog.error("装备合成", userId, String.format("装备Id[%s]需要的材料不足或者根本就不需要合成", equipId));
			response.setError(ErrorType.NOT_ENOUGH_MATE);
			return response.build().toByteString();
		}

		out.value += cfg.getCost();
		long coinVlaue = player.getReward(eSpecialItemId.Coin);
		if (coinVlaue < out.value) {
			GameLog.error("装备合成", userId, String.format("装备Id[%s]需要金币是[%s]，实际上只有[%s]", equipId, out.value, coinVlaue));
			response.setError(ErrorType.NOT_ENOUGH_COIN);
			return response.build().toByteString();
		}

		List<IUseItem> useItemList = new ArrayList<IUseItem>();
		List<INewItem> addItemList = new ArrayList<INewItem>();

		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, Integer> e : composeNeedMateMap.entrySet()) {
			sb.append(e.getKey()).append("_").append(e.getValue()).append(";");
			IUseItem useItem = new UseItem(itemBagMgr.getItemListByCfgId(e.getKey()).get(0).getId(), e.getValue());
			useItemList.add(useItem);
		}

		INewItem newItem = new NewItem(equipId, 1, null);
		addItemList.add(newItem);

		if (!itemBagMgr.addItem(eSpecialItemId.Coin.getValue(), -out.value)) {
			GameLog.error("装备合成", userId, String.format("装备Id[%s]需要金币是[%s]，实际上只有[%s]", equipId, out.value, coinVlaue));
			response.setError(ErrorType.NOT_ENOUGH_COIN);
			return response.build().toByteString();
		}

		if (!itemBagMgr.useLikeBoxItem(useItemList, addItemList)) {
			GameLog.error("装备合成", userId, String.format("装备Id[%s]消耗道具失败，需要消耗的道具是[%s]", equipId, sb.toString()));
			response.setError(ErrorType.FAIL);
			return response.build().toByteString();
		}

		response.setError(ErrorType.SUCCESS);
		return response.build().toByteString();
	}

	/**
	 * 获取装备合成需要的材料数量
	 * 
	 * @param itemBagMgr
	 * @param id
	 * @param needCount
	 * @param out
	 * @return
	 */
	private static Map<Integer, Integer> getComposeNeedMateMap(ItemBagMgr itemBagMgr, int id, int needCount, RefInt out) {
		Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();
		ComposeCfgDAO cfgDAO = ComposeCfgDAO.getInstance();
		Map<Integer, Integer> mateMap = cfgDAO.getMate(id);// 获取需要的所有材料
		if (mateMap == null || mateMap.isEmpty()) {
			return idMap;
		}

		for (Entry<Integer, Integer> e : mateMap.entrySet()) {
			int templateId = e.getKey();// 需要的材料模版Id
			int count = e.getValue() * needCount;// 需要的数量

			int bagCount = itemBagMgr.getItemCountByModelId(templateId);
			if (bagCount < count) {// 如果数量不足，检查是否还能有其他材料辅助合成
				int canUseCount = count - bagCount;
				Map<Integer, Integer> composeNeedMateMap = getComposeNeedMateMap(itemBagMgr, templateId, canUseCount, out);// 需要的辅助材料实际要消耗数量
				if (composeNeedMateMap == null || composeNeedMateMap.isEmpty()) {// 确实材料不够了
					return null;
				}

				if (bagCount > 0) {// 背包里这个也要消耗掉先
					Integer hasCount = idMap.get(templateId);
					if (hasCount == null) {
						idMap.put(templateId, bagCount);
					} else {
						idMap.put(templateId, bagCount + hasCount);
					}
				}

				// 实际材料要消耗的数量
				for (Entry<Integer, Integer> entry : composeNeedMateMap.entrySet()) {
					int tmpId = entry.getKey();
					int count0 = entry.getValue();
					Integer hasCount = idMap.get(tmpId);
					if (hasCount == null) {
						idMap.put(tmpId, count0);
					} else {
						idMap.put(tmpId, count0 + hasCount);
					}
				}

				ComposeCfg mateCfg = cfgDAO.getCfg(templateId);
				if (mateCfg != null) {
					out.value += mateCfg.getCost() * canUseCount;
					// System.err.println("Id: " + templateId + "," + canUseCount + "," + mateCfg.getCost() + "," + out.value);
				}

				continue;
			}

			Integer hasCount = idMap.get(templateId);
			if (hasCount == null) {
				idMap.put(templateId, count);
			} else {
				idMap.put(templateId, count + hasCount);
			}
		}

		// out.value += cfg.getCost();
		// System.err.println("----Id: " + id + "," + needCount + "," + cfg.getCost() + "," + out.value);

		return idMap;
	}

	/**
	 * 检查物品是否可合成
	 * 
	 * @param player
	 * @param id
	 * @return -1:底层物品不能合成；0：材料不足不能合成；1：可合成
	 */
	public static int checkCompose(Player player, int id) {
		RefInt out = new RefInt();
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		Map<Integer, Integer> composeNeedMateMap = getComposeNeedMateMap(itemBagMgr, id, 1, out);
		if (composeNeedMateMap == null || composeNeedMateMap.isEmpty()) {
			return -1;
		}

		for (Entry<Integer, Integer> e : composeNeedMateMap.entrySet()) {
			int mateId = e.getKey().intValue();
			int needCount = e.getValue().intValue();
			if (itemBagMgr.getItemCountByModelId(mateId) < needCount) {
				return 0;
			}
		}

		return 1;
	}

	/**
	 * 穿上装备
	 * 
	 * @param player
	 * @param roleId
	 * @param equipIndex
	 * @param bagSlot
	 * @return
	 */
	public ByteString wearEquip(Player player, String roleId, int equipIndex) {
		EquipResponse.Builder response = EquipResponse.newBuilder();
		response.setEventType(EquipEventType.Wear_Equip);
		response.setEquipIndex(equipIndex);
		EquipMgr pEquipMgr = getEquipMgr(player, roleId);
		if (pEquipMgr == null) {
			response.setError(ErrorType.NOT_ROLE);
			return response.build().toByteString();
		}
		Hero role = player.getHeroMgr().getHeroById(roleId);
		List<Integer> equips = RoleQualityCfgDAO.getInstance().getEquipList(role.getQualityId());
		if (equips.isEmpty()) {
			response.setError(ErrorType.FAIL);
			return response.build().toByteString();
		}
		int equipId = equips.get(equipIndex);
		int count = player.getItemBagMgr().getItemCountByModelId(equipId);
		if (count <= 0) {
			response.setError(ErrorType.NOT_EQUIP);
			return response.build().toByteString();
		}
		HeroEquipCfg pHeroEquipCfg = ItemCfgHelper.getHeroEquipCfg(equipId);
		RoleType pRoleType = getRoleType(player, roleId);
		boolean isEnoughLevel = true;
		if (pRoleType == RoleType.Hero) {
			Hero pHero = player.getHeroMgr().getHeroById(roleId);
			isEnoughLevel = pHeroEquipCfg.getLevel() <= pHero.getHeroData().getLevel();
		} else {
			isEnoughLevel = pHeroEquipCfg.getLevel() <= player.getLevel();
		}
		if (!isEnoughLevel) {
			response.setError(ErrorType.NOT_ENOUGH_LEVEL);
			return response.build().toByteString();
		}

		// try {
		if (pEquipMgr.WearEquip(player, roleId, equipIndex)) {
			response.setError(ErrorType.SUCCESS);
		} else {
			response.setError(ErrorType.FAIL);
		}
		// } catch (CloneNotSupportedException e) {
		// e.printStackTrace();
		// response.setError(ErrorType.FAIL);
		// }
		return response.build().toByteString();
	}

	/**
	 * 一键穿装
	 * 
	 * @param player 角色
	 * @param roleId 英雄Id
	 * @return
	 */
	public ByteString oneKeyWearEquip(Player player, String roleId) {
		String userId = player.getUserId();

		EquipResponse.Builder rsp = EquipResponse.newBuilder();
		rsp.setEventType(EquipEventType.OneKeyWearEquip);

		// 检查英雄是否有
		Hero hero = player.getHeroMgr().getHeroById(roleId);
		if (hero == null) {
			GameLog.error("一键穿装", userId, String.format("英雄Id是[%s]没有找到对应的Hero", roleId));
			return fillFailMsg(rsp, ErrorType.NOT_ROLE, "英雄不存在");
		}

		// 检查身上的装备
		EquipMgr equipMgr = hero.getEquipMgr();
		if (equipMgr == null) {
			GameLog.error("一键穿装", userId, String.format("英雄Id是[%s]没有找到对应的Hero的EquipMgr", roleId));
			return fillFailMsg(rsp, ErrorType.NOT_ROLE, "英雄不存在");
		}
		//配置的装备列表
		List<Integer> equipList = RoleQualityCfgDAO.getInstance().getEquipList(hero.getQualityId());
		if (equipList.isEmpty()) {
			GameLog.error("一键穿装", userId, String.format("英雄Id是[%s]，品质[%s]，没有装备列表", roleId, hero.getQualityId()));
			return fillFailMsg(rsp, ErrorType.FAIL, "当前没有可穿戴装备");
		}
		//已装备列表
		List<EquipItem> hasEquipList = equipMgr.getEquipList(roleId);
		int size = hasEquipList.size();
		if (size == 6) {// 装备穿满了
			GameLog.error("一键穿装", userId, String.format("英雄Id是[%s]装备已经穿戴满了，不需要一键穿装", roleId));
			return fillFailMsg(rsp, ErrorType.FAIL, "装备已经穿满，请进阶");
		}

		int level = hero.getLevel();// 英雄的等级
		HeroEquipCfgDAO cfgDAO = HeroEquipCfgDAO.getInstance();

		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		/** <格子Id,装备的数据库Id> */
		Map<Integer, String> needEquipMap = new HashMap<Integer, String>();// 需要穿的装备
		/** <装备的模版Id> */
		List<Integer> hasEquipTmpIdList = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			EquipItem equipItem = hasEquipList.get(i);
			if (equipItem == null) {
				continue;
			}

			int templateId = equipItem.getModelId();
			if (equipList.contains(templateId)) {// 穿在身上
				hasEquipTmpIdList.add(templateId);
			}
		}

		// 检查所有装备
		for (int i = 0, equipSize = equipList.size(); i < equipSize; i++) {
			int templateId = equipList.get(i);
			if (hasEquipTmpIdList.contains(templateId)) {
				continue;
			}

			//还没有穿在身上
			HeroEquipCfg cfg = cfgDAO.getConfig(templateId);
			if (cfg == null) {
				continue;
			}

			//搜索背包
			List<ItemData> itemDataList = itemBagMgr.getItemListByCfgId(templateId);
			if (itemDataList == null || itemDataList.isEmpty()) {
				continue;
			}

			if (cfg.getLevel() > level) {
				continue;
			}

			needEquipMap.put(i, itemDataList.get(0).getId());
		}

		if (needEquipMap.isEmpty()) {// 不需要穿戴装备
			GameLog.error("一键穿装", userId, String.format("英雄Id是[%s]背包中没有空位需要穿戴的装备，不需要一键穿装", roleId));
			return fillFailMsg(rsp, ErrorType.FAIL, "当前没有可穿戴装备");
		}

		// 准备穿戴装备
		for (Entry<Integer, String> e : needEquipMap.entrySet()) {
			Integer index = e.getKey();
			if (equipMgr.wearEquip(player, roleId, e.getValue(), index)) {
				rsp.addOneKeySuccessIndex(index);
			}
		}

		rsp.setError(ErrorType.SUCCESS);
		rsp.setTipMsg("一键穿装成功");
		return rsp.build().toByteString();
	}

	private EquipMgr getEquipMgr(Player player, String roleId) {

		if (player.getUserId().equals(roleId)) {
			return player.getMainRoleHero().getEquipMgr();
		}
		Hero pHero = player.getHeroMgr().getHeroById(roleId);
		if (pHero != null) {
			return pHero.getEquipMgr();
		}
		return null;
	}

	private RoleType getRoleType(Player player, String roleId) {
		if (player.getUserId().equals(roleId)) {
			return RoleType.Player;
		}
		return RoleType.Hero;
	}

	/**
	 * 填充失败消息
	 * 
	 * @param rsp
	 * @param err
	 * @return
	 */
	private ByteString fillFailMsg(EquipResponse.Builder rsp, ErrorType err, String tipMsg) {
		rsp.setError(err);
		if (!StringUtils.isEmpty(tipMsg)) {
			rsp.setTipMsg(tipMsg);
		}
		return rsp.build().toByteString();
	}
}

enum RoleType {
	Player, // 角色
	Hero, // 佣兵
};