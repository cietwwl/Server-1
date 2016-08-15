package com.playerdata.fixEquip.exp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.Action;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.FixEquipResult;
import com.playerdata.fixEquip.cfg.FixEquipCfg;
import com.playerdata.fixEquip.cfg.FixEquipCfgDAO;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCostCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCostCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItemHolder;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.enu.eConsumeTypeDef;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.FixEquipProto.ExpLevelUpReqParams;
import com.rwproto.FixEquipProto.SelectItem;

public class FixExpEquipMgr {

	private FixExpEquipDataItemHolder fixExpEquipDataItemHolder = new FixExpEquipDataItemHolder();

	final private Comparator<FixExpEquipDataItem> comparator = new Comparator<FixExpEquipDataItem>() {

		@Override
		public int compare(FixExpEquipDataItem source, FixExpEquipDataItem target) {
			return source.getSlot() - target.getSlot();
		}

	};

	public boolean initIfNeed(Player player, Hero hero) {
		if (!isInited(player, hero)) {
			try {
				newHeroInit(player, hero.getUUId(), hero.getModelId());
			} catch (Exception e) {
				GameLog.error(LogModule.FixEquip, "playerId:" + player.getUserId(), "英雄神器初始化失败,heroId:" + hero.getUUId(), e);
			}
		}
		return true;
	}

	private boolean isInited(Player player, Hero hero) {
		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(hero.getUUId());
		return !itemList.isEmpty();
	}

	public boolean newHeroInit(Player player, String ownerId, int modelId) {
		List<FixExpEquipDataItem> equipItemList = new ArrayList<FixExpEquipDataItem>();

		RoleFixEquipCfg roleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(modelId));
		if (roleFixEquipCfg == null) {
			return false;
		}

		int slot = 4;
		for (String cfgId : roleFixEquipCfg.getExpCfgIdList()) {
			String id = FixEquipHelper.getExpItemId(ownerId, cfgId);

			FixExpEquipDataItem FixExpEquipDataItem = new FixExpEquipDataItem();
			FixExpEquipDataItem.setId(id);
			FixExpEquipDataItem.setCfgId(cfgId);
			FixExpEquipDataItem.setOwnerId(ownerId);
			FixExpEquipDataItem.setQuality(0);
			FixExpEquipDataItem.setLevel(1);
			FixExpEquipDataItem.setStar(0);
			FixExpEquipDataItem.setSlot(slot);

			equipItemList.add(FixExpEquipDataItem);
			slot++;

		}

		Collections.sort(equipItemList, comparator);

		return fixExpEquipDataItemHolder.initItems(player, ownerId, equipItemList);
	}

	public boolean onCarrerChange(Player player) {

		Hero mainRoleHero = player.getMainRoleHero();
		int newModelId = mainRoleHero.getModelId();
		String ownerId = player.getUserId();

		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(ownerId);

		RoleFixEquipCfg newRoleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(newModelId));

		int slot = 0;
		for (String cfgId : newRoleFixEquipCfg.getExpCfgIdList()) {
			FixExpEquipDataItem item = getBySlot(itemList, slot);
			if (item != null) {
				item.setCfgId(cfgId);
			}
			slot++;
		}

		fixExpEquipDataItemHolder.updateItemList(player, itemList);
		return true;

	}

	private FixExpEquipDataItem getBySlot(List<FixExpEquipDataItem> itemList, int slot) {
		FixExpEquipDataItem target = null;
		for (FixExpEquipDataItem fixNormEquipDataItem : itemList) {
			if (fixNormEquipDataItem.getSlot() == slot) {
				target = fixNormEquipDataItem;
				break;
			}
		}
		return target;
	}

	public void regChangeCallBack(Action callBack) {
		fixExpEquipDataItemHolder.regChangeCallBack(callBack);
	}

	public void synAllData(Player player, Hero hero) {
		fixExpEquipDataItemHolder.synAllData(player, hero);
	}

	public List<AttributeItem> levelToAttrItems(String ownerId) {
		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(ownerId);
		List<AttributeItem> attrItemList = new ArrayList<AttributeItem>(FixEquipHelper.parseFixExpEquipLevelAttr(ownerId, itemList).values());
		return attrItemList;
	}

	public List<AttributeItem> qualityToAttrItems(String ownerId) {
		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(ownerId);
		List<AttributeItem> attrItemList = new ArrayList<AttributeItem>(FixEquipHelper.parseFixExpEquipQualityAttr(ownerId, itemList).values());
		return attrItemList;
	}

	public List<AttributeItem> starToAttrItems(String ownerId) {
		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(ownerId);
		List<AttributeItem> attrItemList = new ArrayList<AttributeItem>(FixEquipHelper.parseFixExpEquipStarAttr(ownerId, itemList).values());
		return attrItemList;
	}

	public List<String> qualityUpList(Player player, String ownerId) {

		List<String> upIdList = new ArrayList<String>();

		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(ownerId);
		for (FixExpEquipDataItem dataItem : itemList) {
			int level = dataItem.getLevel();
			FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), dataItem.getQuality());
			int nextQualityLevel = curQualityCfg.getLevelNeed();
			if (level == nextQualityLevel) {
				FixEquipResult result = checkQualityUp(player, ownerId, dataItem);
				if (result.isSuccess()) {
					upIdList.add(dataItem.getId());
				}
			}
		}
		return upIdList;

	}

	public List<String> starUpList(Player player, String ownerId) {
		List<String> upIdList = new ArrayList<String>();

		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(ownerId);
		for (FixExpEquipDataItem dataItem : itemList) {
			FixEquipResult result = checkStarUp(player, ownerId, dataItem);
			if (result.isSuccess()) {
				upIdList.add(dataItem.getId());
			}

		}
		return upIdList;

	}
	
	public List<String> levelUpList(Player player, String ownerId) {
		
		List<String> canUpList = new ArrayList<String>();
		
		HashMap<eConsumeTypeDef, List<ItemData>> consumeItemMap = FixEquipHelper.getFixConsumeItemMap(player);
		
		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(ownerId);
		for (FixExpEquipDataItem dataItem : itemList) {
			FixEquipResult result = null;
			FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), dataItem.getQuality());
			int nextQualityLevel = curQualityCfg.getLevelNeed();
			if(dataItem.getLevel() < nextQualityLevel){
				
				eConsumeTypeDef consumeType = getConsumeType(dataItem);
				
				List<ItemData> itemlist = consumeItemMap.get(consumeType);
				if (itemlist == null) continue;
				int totalExp = 0;
				for (ItemData itemData : itemlist) {
					int modelId = itemData.getModelId();
					int count = itemData.getCount();
					ConsumeCfg consumeCfg = ItemCfgHelper.getConsumeCfg(modelId);
					if (consumeType.getOrder() == consumeCfg.getConsumeType()) {
						totalExp = totalExp + consumeCfg.getValue() * count;
					}
					
				}			
				
				result = checkLevelUpCost(player, dataItem, totalExp);
			}
			if(result!=null && result.isSuccess()){
				canUpList.add(dataItem.getId());
			}
		}
		return canUpList;
	}
	
	private FixEquipResult checkLevelUpCost(Player player, FixExpEquipDataItem dataItem, int totalExp) {		
		
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		FixExpEquipLevelCostCfg curLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), dataItem.getLevel());
		int nextLevel = dataItem.getLevel()+1;
		FixExpEquipLevelCostCfg nextLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), nextLevel);
		if(nextLevelCfg == null && dataItem.getExp() >= curLevelCfg.getExpNeed()){
			result.setReason("装备已达最高等级");
		}else{
			int expNeed = curLevelCfg.getExpNeed();
			
			if(totalExp > expNeed){
				FixEquipCfg equipCfg = FixEquipCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
				int needCost = expNeed * equipCfg.getCostPerExp();
				result = FixEquipHelper.checkCost(player, equipCfg.getExpCostType(), needCost);			
			}
		}
		
			
		return result;
	}

	public FixEquipResult levelUp(Player player, String ownerId, String itemId, ExpLevelUpReqParams reqParams) {

		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		FixEquipResult result = checkLevel(player, ownerId, dataItem);
		if (result.isSuccess()) {
			result = doLevelUp(player, dataItem, reqParams);
		}

		return result;
	}

	private FixEquipResult checkLevel(Player player, String ownerId, FixExpEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP,player.getLevel())) {
			result.setReason("未到功能开放等级");
		} else if (dataItem == null) {
			result.setReason("装备不存在");
		} else {
			int curLevel = dataItem.getLevel();
			int nextLevel = curLevel + 1;
			if (player.getLevel() < nextLevel) {
				result.setReason("装备等级不能超过英雄等级");
			} else {
				FixExpEquipLevelCostCfg curLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), curLevel);
				FixExpEquipLevelCostCfg nextLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), nextLevel);
				if(nextLevelCfg == null && dataItem.getExp() >= curLevelCfg.getExpNeed()){
					result.setReason("装备已达最高等级");
				}else{
					result.setSuccess(true);
				}
			}

		}
		return result;
	}
	
	private eConsumeTypeDef getConsumeType(FixExpEquipDataItem dataItem){
		eConsumeTypeDef consumeType = null;
		if (dataItem.getSlot() == 4) {
			consumeType = eConsumeTypeDef.Exp4FixEquip_4;
		} else if (dataItem.getSlot() == 5) {
			consumeType = eConsumeTypeDef.Exp4FixEquip_5;
		}
		return consumeType;
	}

	private FixEquipResult doLevelUp(Player player, FixExpEquipDataItem dataItem, ExpLevelUpReqParams reqParams) {

		List<SelectItem> selectItemList = reqParams.getSelectItemList();

		eConsumeTypeDef consumeType = getConsumeType(dataItem);


		FixEquipResult result = FixEquipResult.newInstance(false);
		if (consumeType == null) {
			result.setReason("所选经验道具和升级装备不匹配");
		} else {

			int totalExp = selectItems2Exp(consumeType, selectItemList);
			int nextQualityNeedExp = getNextQualityNeedExp(dataItem);
			int curExp = dataItem.getExp();
			totalExp = totalExp+curExp < nextQualityNeedExp?totalExp:(nextQualityNeedExp-curExp);

			FixEquipCfg equipCfg = FixEquipCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
			int totalCost = totalExp * equipCfg.getCostPerExp();

			result = FixEquipHelper.takeCost(player, equipCfg.getExpCostType(), totalCost);
			if (result.isSuccess()) {
				Map<Integer, Integer> itemsSelected = new HashMap<Integer, Integer>();
				for (SelectItem selectItem : selectItemList) {
					int modelId = selectItem.getModelId();
					int count = selectItem.getCount();
					if (itemsSelected.containsKey(modelId)) {
						count = count + itemsSelected.get(modelId);
						itemsSelected.put(modelId, count);
					} else {
						itemsSelected.put(modelId, count);
					}
				}
				result = FixEquipHelper.takeItemCost(player, itemsSelected);
			}

			if (result.isSuccess()) {
				iterateLevelUp(dataItem, totalExp);
				fixExpEquipDataItemHolder.updateItem(player, dataItem);
			}
		}

		return result;
	}


	private int getNextQualityNeedExp(FixExpEquipDataItem dataItem) {
		FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), dataItem.getQuality());
		int curLevel = dataItem.getLevel();
		int nextQualityLevel = curQualityCfg.getLevelNeed();
		int expNeed = 0;
		for (int level = curLevel; level < nextQualityLevel; level++) {
			FixExpEquipLevelCostCfg levelCostCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), level);

			expNeed = expNeed + levelCostCfg.getExpNeed();

		}
		return expNeed;
	}

	private void iterateLevelUp(FixExpEquipDataItem dataItem, int totalExp) {

		int curLevel = dataItem.getLevel();
		while (totalExp > 0) {
			int nextLevel = curLevel + 1;

			FixExpEquipLevelCostCfg curLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), curLevel);
			FixExpEquipLevelCostCfg nextLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), nextLevel);
			if (nextLevelCfg == null) {// 已经是最满级
				int exp = dataItem.getExp() + totalExp < curLevelCfg.getExpNeed() ? dataItem.getExp() + totalExp : curLevelCfg.getExpNeed();
				dataItem.setExp(exp);
				break;
			} else {

				if (totalExp + dataItem.getExp() >= curLevelCfg.getExpNeed()) {
					dataItem.setLevel(nextLevel);
					int expCost = curLevelCfg.getExpNeed() - dataItem.getExp();
					totalExp = totalExp - expCost;
					dataItem.setExp(0);
				} else {
					dataItem.setExp(totalExp + dataItem.getExp());
					totalExp = 0;
				}
			}

			curLevel++;
		}
	}

	private int selectItems2Exp(eConsumeTypeDef consumeType, List<SelectItem> selectItemList) {
		int totalExp = 0;
		for (SelectItem selectItem : selectItemList) {
			int modelId = selectItem.getModelId();
			int count = selectItem.getCount();
			ConsumeCfg consumeCfg = ItemCfgHelper.getConsumeCfg(modelId);
			if (consumeType.getOrder() == consumeCfg.getConsumeType()) {
				totalExp = totalExp + consumeCfg.getValue() * count;
			}
		}
		return totalExp;
	}

	public FixEquipResult qualityUp(Player player, String ownerId, String itemId) {

		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);

		FixEquipResult result = checkQualityUp(player, ownerId, dataItem);
		if (result.isSuccess()) {
			result = doQualityUp(player, dataItem);
		}

		return result;
	}

	private FixEquipResult checkQualityUp(Player player, String ownerId, FixExpEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);

		if (dataItem == null) {
			result.setReason("装备不存在");
		} else {
			int curlevel = dataItem.getLevel();
			int currentQuality = dataItem.getQuality();

			FixExpEquipQualityCfg nextQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), currentQuality + 1);
			if (nextQualityCfg == null) {
				result.setReason("装备已经达到最品质");
			} else {

				FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), currentQuality);
				Map<Integer, Integer> itemsNeed = curQualityCfg.getItemsNeed();
				if (curlevel < curQualityCfg.getLevelNeed()) {
					result.setReason("装备等级不够");
				} else if (!FixEquipHelper.isItemEnough(player, itemsNeed)) {
					result.setReason("进化材料不足");
				} else {
					result.setSuccess(true);

				}
			}

		}
		if (result.isSuccess()) {
			int curQuality = dataItem.getQuality();
			FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), curQuality);

			result = FixEquipHelper.checkCost(player, curQualityCfg.getCostType(), curQualityCfg.getCostCount());
		}
		return result;
	}

	private FixEquipResult doQualityUp(Player player, FixExpEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);

		int curQuality = dataItem.getQuality();
		FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), curQuality);

		result = FixEquipHelper.takeCost(player, curQualityCfg.getCostType(), curQualityCfg.getCostCount());
		if (result.isSuccess()) {
			Map<Integer, Integer> itemsNeed = curQualityCfg.getItemsNeed();
			result = FixEquipHelper.takeItemCost(player, itemsNeed);
		}

		if (result.isSuccess()) {
			dataItem.setQuality(curQuality + 1);
			fixExpEquipDataItemHolder.updateItem(player, dataItem);
		}

		return result;

	}

	public FixEquipResult starUp(Player player, String ownerId, String itemId) {

		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);

		FixEquipResult result = checkStarUp(player, ownerId, dataItem);
		if (result.isSuccess()) {
			result = doStarUp(player, dataItem);
		}

		return result;
	}

	private FixEquipResult checkStarUp(Player player, String ownerId, FixExpEquipDataItem dataItem) {

		FixEquipResult result = FixEquipResult.newInstance(false);

		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP_STAR,player.getLevel())) {
			result.setReason("未到功能开放等级");
		} else if (dataItem == null) {
			result.setReason("装备不存在。");
		} else {
			int curStar = dataItem.getStar();
			FixExpEquipStarCfg nextStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar + 1);

			if (nextStarCfg == null) {
				result.setReason("装备已达最高星级。");
			} else {
				FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);

				Map<Integer, Integer> itemsNeed = curStarCfg.getItemsNeed();
				if (!FixEquipHelper.isItemEnough(player, itemsNeed)) {
					result.setReason("觉醒材料不足");
				} else {
					result.setSuccess(true);
				}
			}
		}
		if (result.isSuccess()) {
			int curStar = dataItem.getStar();
			FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);

			result = FixEquipHelper.checkCost(player, curStarCfg.getUpCostType(), curStarCfg.getUpCount());
		}
		return result;
	}

	private FixEquipResult doStarUp(Player player, FixExpEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);

		int curStar = dataItem.getStar();
		FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);

		result = FixEquipHelper.takeCost(player, curStarCfg.getUpCostType(), curStarCfg.getUpCount());

		if (result.isSuccess()) {
			Map<Integer, Integer> itemsNeed = curStarCfg.getItemsNeed();
			result = FixEquipHelper.takeItemCost(player, itemsNeed);
		}

		if (result.isSuccess()) {
			dataItem.setStar(curStar + 1);
			fixExpEquipDataItemHolder.updateItem(player, dataItem);
		}
		return result;
	}

	public FixEquipResult starDown(Player player, String ownerId, String itemId) {

		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);

		FixEquipResult result = checkStarDown(player, ownerId, dataItem);
		if (result.isSuccess()) {
			int curStar = dataItem.getStar();
			int nextStar = curStar - 1;

			FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);
			FixExpEquipStarCfg nextStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), nextStar);
			result = FixEquipHelper.takeCost(player, curStarCfg.getDownCostType(), curStarCfg.getDownCount());

			if (result.isSuccess()) {
				dataItem.setStar(nextStar);
				fixExpEquipDataItemHolder.updateItem(player, dataItem);

				Map<Integer, Integer> itemsNeed = nextStarCfg.getItemsNeed();
				result = FixEquipHelper.turnBackItemCost(player, itemsNeed);
			}
		}

		return result;
	}

	private FixEquipResult checkStarDown(Player player, String ownerId, FixExpEquipDataItem dataItem) {

		FixEquipResult result = FixEquipResult.newInstance(false);

		if (dataItem == null) {
			result.setReason("装备不存在。");
		} else {
			int nextStar = dataItem.getStar() - 1;
			if (nextStar < 0) {
				result.setReason("装备已是最低等级。");
			} else {
				result.setSuccess(true);
			}
		}
		return result;
	}

	public List<HeroFixEquipInfo> getHeroFixSimpleInfo(String heroId) {
		return FixEquipHelper.parseFixExpEquip2SimpleList(fixExpEquipDataItemHolder.getItemList(heroId));
	}
}
