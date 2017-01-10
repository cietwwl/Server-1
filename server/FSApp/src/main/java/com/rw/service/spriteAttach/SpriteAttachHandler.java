package com.rw.service.spriteAttach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.RefInt;
import com.common.RefLong;
import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.SpriteAttachMgr;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.item.checkWare.CheckCommonItemWare;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.spriteattach.SpriteAttachCfgDAO;
import com.rwbase.dao.spriteattach.SpriteAttachHolder;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.spriteattach.SpriteAttachLevelCostCfgDAO;
import com.rwbase.dao.spriteattach.SpriteAttachRoleCfgDAO;
import com.rwbase.dao.spriteattach.SpriteAttachSyn;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachCfg;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachLevelCostCfg;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfg;
import com.rwproto.SpriteAttachProtos.SpriteAttachRequest;
import com.rwproto.SpriteAttachProtos.SpriteAttachResponse;
import com.rwproto.SpriteAttachProtos.eSpriteAttachRequestType;
import com.rwproto.SpriteAttachProtos.eSpriteAttachResultType;
import com.rwproto.SpriteAttachProtos.spriteAttachMaterial;

public class SpriteAttachHandler {
	protected SpriteAttachHandler() {
	}

	private static SpriteAttachHandler instance = new SpriteAttachHandler();

	public static SpriteAttachHandler getInstance() {
		return instance;
	}

	private SpriteAttachMgr spriteAttachMgr = SpriteAttachMgr.getInstance();

	/**
	 * 附灵
	 * 
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString spriteAttach(Player player, SpriteAttachRequest request) {
		SpriteAttachResponse.Builder res = SpriteAttachResponse.newBuilder();
		eSpriteAttachRequestType requestType = request.getRequestType();
		int heroModelId = request.getHeroModelId();
		int spriteAttachId = request.getSpriteAttachId();
		List<spriteAttachMaterial> materialsList = request.getMaterialsList();

		Hero hero = player.getHeroMgr().getHeroByModerId(player, heroModelId);
		if (hero == null) {
			return sendFailMsg("找不到对应的英雄!", res, requestType);
		}

		SpriteAttachRoleCfg spriteAttachRoleCfg = SpriteAttachRoleCfgDAO.getInstance().getCfgById(String.valueOf(hero.getModeId()));
		if (spriteAttachRoleCfg == null) {
			return sendFailMsg("找不到对应的英雄的灵蕴信息!", res, requestType);
		}
		SpriteAttachCfg spriteAttachCfg = SpriteAttachCfgDAO.getInstance().getCfgById(String.valueOf(spriteAttachId));
		if (spriteAttachCfg == null) {
			return sendFailMsg("找不到对应的英雄的灵蕴信息!", res, requestType);
		}
		SpriteAttachHolder spriteAttachHolder = spriteAttachMgr.getSpriteAttachHolder();
		SpriteAttachSyn synItem = spriteAttachHolder.getSpriteAttachSyn(hero.getUUId());
		Map<Integer, SpriteAttachItem> itemMap = spriteAttachHolder.getSpriteAttachItemMap(hero.getUUId());

		SpriteAttachItem spriteAttachItem = itemMap.get(spriteAttachId);
		// 查找对应的灵蕴点
		if (spriteAttachItem == null) {
			return sendFailMsg("找不到对应的英雄的灵蕴信息!", res, requestType);
		}

		// 判断灵蕴点是否激活
		if (!spriteAttachMgr.checkSpriteAttachActive(player, hero, spriteAttachCfg, itemMap)) {
			return sendFailMsg("英雄的灵蕴点尚未激活,附灵失败!", res, requestType);
		}

		int spriteAttachLevel = spriteAttachItem.getLevel();
		long currentExp = spriteAttachItem.getExp();
		int nextSpriteAttachLevel = spriteAttachLevel + 1;

		// 执行附灵
		int levelCostPlanId = spriteAttachCfg.getLevelCostPlanId();

		SpriteAttachLevelCostCfg spriteAttachLevelCost = SpriteAttachLevelCostCfgDAO.getInstance().getSpriteAttachLevelCost(spriteAttachLevel, levelCostPlanId);

		long levelExp = spriteAttachLevelCost.getExp();

		SpriteAttachLevelCostCfg nextSpriteAttachLevelCost = SpriteAttachLevelCostCfgDAO.getInstance().getSpriteAttachLevelCost(nextSpriteAttachLevel, levelCostPlanId);
		if (nextSpriteAttachLevelCost == null && currentExp >= levelExp) {
			return sendFailMsg("当前附灵已到最高等级!", res, requestType);
		}

		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();

		int costType = spriteAttachLevelCost.getCostType();

		final eSpecialItemId currencyType = eSpecialItemId.getDef(costType);
		if (currencyType == null) {
			return sendFailMsg("附灵配置的货币类型无效,附灵失败!", res, requestType);
		}

		RefLong cost = new RefLong();
		RefInt upgradeLevel = new RefInt(spriteAttachLevel);
		RefLong upgradeExp = new RefLong(currentExp);
		List<IUseItem> useItemList = new ArrayList<IUseItem>(materialsList.size());
		boolean calcResult = calcConsume(player, materialsList, upgradeLevel, upgradeExp, levelCostPlanId, spriteAttachLevelCost, cost, useItemList);
		if (!calcResult) {
			return sendFailMsg("附灵失败，消耗升级材料失败！", res, requestType);
		}

		Map<Integer, Integer> modifyMoneyMap = new HashMap<Integer, Integer>(1);
		modifyMoneyMap.put(costType, -(int) (cost.value));

		long costCount = cost.value;
		// 扣金币和扣材料
		long curValue = player.getReward(currencyType);
		if (costCount > curValue) {
			return sendFailMsg("货币不足,附灵失败!", res, requestType);
		}

		if (!itemBagMgr.useLikeBoxItem(player, useItemList, null, modifyMoneyMap)) {
			return sendFailMsg("附灵失败，消耗升级材料失败！", res, requestType);
		}
		if (upgradeLevel.value != spriteAttachLevel) {
			spriteAttachItem.setLevel(upgradeLevel.value);
			UserEventMgr.getInstance().attachDaily(player, upgradeLevel.value, spriteAttachLevel);
		}
		spriteAttachItem.setExp(upgradeExp.value);
		SpriteAttachMgr.getInstance().getSpriteAttachHolder().updateItem(player, synItem);
		res.setRequestType(requestType);
		res.setReslutType(eSpriteAttachResultType.Success);
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Hero_Strength, 1);
		return res.build().toByteString();
	}

	public ByteString unlockSpriteAttach(Player player, SpriteAttachRequest request) {
		SpriteAttachResponse.Builder res = SpriteAttachResponse.newBuilder();
		eSpriteAttachRequestType requestType = request.getRequestType();
		int heroModelId = request.getHeroModelId();
		int spriteAttachId = request.getSpriteAttachId();

		Hero hero = player.getHeroMgr().getHeroByModerId(player, heroModelId);
		if (hero == null) {
			return sendFailMsg("找不到对应的英雄!", res, requestType);
		}
		SpriteAttachCfg spriteAttachCfg = SpriteAttachCfgDAO.getInstance().getCfgById(String.valueOf(spriteAttachId));
		if (spriteAttachCfg == null) {
			return sendFailMsg("找不到对应的英雄的灵蕴信息!", res, requestType);
		}
		SpriteAttachHolder spriteAttachHolder = spriteAttachMgr.getSpriteAttachHolder();
		SpriteAttachSyn spriteAttachSyn = spriteAttachHolder.getSpriteAttachSyn(hero.getId());
		Map<Integer, SpriteAttachItem> items = spriteAttachHolder.getSpriteAttachItemMap(hero.getId());
		boolean blnActive = spriteAttachMgr.checkSpriteAttachActive(player, hero, spriteAttachCfg, items);
		if (!blnActive) {
			return sendFailMsg("该英雄的灵蕴尚未到达解锁条件，解锁失败!", res, requestType);
		}
		SpriteAttachRoleCfg spriteAttachRoleCfg = SpriteAttachRoleCfgDAO.getInstance().getCfgById(String.valueOf(heroModelId));
		int index = spriteAttachRoleCfg.getIndex(spriteAttachId);

		if (spriteAttachMgr.createSpriteAttachItem(spriteAttachSyn, index, spriteAttachCfg.getId())) {
			SpriteAttachMgr.getInstance().getSpriteAttachHolder().updateItem(player, spriteAttachSyn);
			res.setRequestType(requestType);
			res.setReslutType(eSpriteAttachResultType.Success);
		} else {
			res.setRequestType(requestType);
			res.setReslutType(eSpriteAttachResultType.UnlockRepeat);
		}

		return res.build().toByteString();
	}

	private boolean calcConsume(Player player, List<spriteAttachMaterial> materialsList, RefInt upgradeLevel, RefLong upgradeExp, int levelCostPlanId, SpriteAttachLevelCostCfg spriteAttachLevelCost, RefLong Cost, List<IUseItem> useItemList) {
		int totalCost = 0;
		int materialsExp = 0;

		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		for (Iterator<spriteAttachMaterial> iterator = materialsList.iterator(); iterator.hasNext();) {
			spriteAttachMaterial spriteAttachMaterial = (spriteAttachMaterial) iterator.next();
			int itemModelId = spriteAttachMaterial.getItemModelId();
			int count = spriteAttachMaterial.getCount();
			if (count <= 0) {
				continue;
			}
			ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(itemModelId);
			materialsExp += itemBaseCfg.getEnchantExp() * count;

			List<IUseItem> items = itemBagMgr.checkEnoughItem(player, itemModelId, count, null, new CheckCommonItemWare());
			if (items == null) {
				return false;
			} else {
				useItemList.addAll(items);
			}
		}

		SpriteAttachLevelCostCfg levelCostCfg = spriteAttachLevelCost;
		while (materialsExp > 0) {
			long exp = levelCostCfg.getExp();
			long uExp = exp - upgradeExp.value;
			if (materialsExp > uExp) {

				materialsExp -= uExp;
				totalCost += Math.round((exp - upgradeExp.value) * levelCostCfg.getCostCount());
				upgradeLevel.value++;
				upgradeExp.value = 0;
				levelCostCfg = SpriteAttachLevelCostCfgDAO.getInstance().getSpriteAttachLevelCost(upgradeLevel.value, levelCostPlanId);
				if (levelCostCfg == null) {
					break;
				}
			} else {
				upgradeExp.value += materialsExp;
				totalCost += materialsExp * levelCostCfg.getCostCount();
				materialsExp = 0;
			}

		}
		Cost.value = totalCost;
		return true;
	}

	public ByteString sendFailMsg(String failMsg, SpriteAttachResponse.Builder res, eSpriteAttachRequestType requestType) {
		res.setRequestType(requestType);
		res.setReslutType(eSpriteAttachResultType.Fail);
		res.setReslutValue(failMsg);

		return res.build().toByteString();
	}
}
