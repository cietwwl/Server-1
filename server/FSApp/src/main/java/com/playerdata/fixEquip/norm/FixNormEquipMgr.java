package com.playerdata.fixEquip.norm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.common.IHeroAction;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.FixEquipResult;
import com.playerdata.fixEquip.cfg.FixEquipCfg;
import com.playerdata.fixEquip.cfg.FixEquipCfgDAO;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCostCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCostCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfgDAO;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItemHolder;
import com.playerdata.hero.IHero;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.common.attribute.AttributeItem;

public class FixNormEquipMgr {
	
//	private FixNormEquipDataItemHolder fixNormEquipDataItemHolder = new FixNormEquipDataItemHolder();
	private FixNormEquipDataItemHolder fixNormEquipDataItemHolder = FixNormEquipDataItemHolder.getInstance();
	
	private static final FixNormEquipMgr _INSTANCE = new FixNormEquipMgr();
	
	public static final FixNormEquipMgr getInstance() {
		return _INSTANCE;
	}
	
	protected FixNormEquipMgr() {}

	final private Comparator<FixNormEquipDataItem> comparator = new Comparator<FixNormEquipDataItem>() {

		@Override
		public int compare(FixNormEquipDataItem source, FixNormEquipDataItem target) {
			return source.getSlot() - target.getSlot();
		}

	};

	public boolean initIfNeed(Player player, Hero hero) {
		if (!isInited(player, hero)) {
			newHeroInit(player, hero.getUUId(), hero.getModelId());
			//通知神器羁绊系统
			player.getMe_FetterMgr().notifyHeroChange(player, hero);
		}
		return true;
	}
	
	public boolean initIfNeedV2(Player player, IHero hero) {
		if(!isInitedV2(player, hero)){
			newHeroInit(player, hero.getUUId(), hero.getModelId());
		}
		return true;
	}
	
	private boolean isInited(Player player, Hero hero){
		List<FixNormEquipDataItem> itemList = fixNormEquipDataItemHolder.getItemList(hero.getUUId());
		return !itemList.isEmpty();
	}
	
	private boolean isInitedV2(Player player, IHero hero){
		List<FixNormEquipDataItem> itemList = fixNormEquipDataItemHolder.getItemList(hero.getUUId());
		return !itemList.isEmpty();
	}

	public boolean newHeroInit(Player player, String ownerId, int modelId) {
		List<FixNormEquipDataItem> equipItemList = new ArrayList<FixNormEquipDataItem>();

		RoleFixEquipCfg roleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(modelId));
		if (roleFixEquipCfg == null) {
			return false;
		}

		int slot = 0;
		for (String cfgId : roleFixEquipCfg.getNormCfgIdList()) {

			String id = FixEquipHelper.getNormItemId(ownerId, cfgId);

			FixNormEquipDataItem FixNormEquipDataItem = new FixNormEquipDataItem();
			FixNormEquipDataItem.setId(id);
			FixNormEquipDataItem.setCfgId(cfgId);
			FixNormEquipDataItem.setOwnerId(ownerId);
			FixNormEquipDataItem.setQuality(0);
			FixNormEquipDataItem.setLevel(1);
			FixNormEquipDataItem.setStar(0);
			FixNormEquipDataItem.setSlot(slot);

			equipItemList.add(FixNormEquipDataItem);
			slot++;
		}

		Collections.sort(equipItemList, comparator);

		return fixNormEquipDataItemHolder.initItems(player, ownerId, equipItemList);

	}

	public boolean onCarrerChange(Player player) {

		Hero mainRoleHero = player.getMainRoleHero();
		int newModelId = mainRoleHero.getModelId();
		String ownerId = player.getUserId();

		List<FixNormEquipDataItem> itemList = fixNormEquipDataItemHolder.getItemList(ownerId);

		RoleFixEquipCfg newRoleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(newModelId));

		int slot = 0;
		for (String cfgId : newRoleFixEquipCfg.getNormCfgIdList()) {
			FixNormEquipDataItem fixNormEquipDataItem = getBySlot(itemList, slot);
			if (fixNormEquipDataItem != null) {
				fixNormEquipDataItem.setCfgId(cfgId);
			}
			slot++;
		}

		fixNormEquipDataItemHolder.updateItemList(player, itemList);
		//通知神器羁绊系统
		player.getMe_FetterMgr().notifyHeroChange(player, mainRoleHero);
		return true;

	}
	public List<FixNormEquipDataItem> getFixNorEquipItemList(String ownerID){
		List<FixNormEquipDataItem> itemList = fixNormEquipDataItemHolder.getItemList(ownerID);
		return Collections.unmodifiableList(itemList);
	}
	

	private FixNormEquipDataItem getBySlot(List<FixNormEquipDataItem> itemList, int slot) {
		FixNormEquipDataItem target = null;
		for (FixNormEquipDataItem fixNormEquipDataItem : itemList) {
			if (fixNormEquipDataItem.getSlot() == slot) {
				target = fixNormEquipDataItem;
				break;
			}
		}
		return target;
	}
	
	public void regDataChangeCallback(IHeroAction callback) {
		fixNormEquipDataItemHolder.regDataChangeCallback(callback);
	}

	public void synAllData(Player player, Hero hero) {
		fixNormEquipDataItemHolder.synAllData(player, hero);
	}
	
	public void synAllDataV2(Player player, IHero hero){
		fixNormEquipDataItemHolder.synAllDataV2(player, hero);
	}
	

	public List<AttributeItem> levelToAttrItems(String ownerId) {
		List<FixNormEquipDataItem> itemList = fixNormEquipDataItemHolder.getItemList(ownerId);
		List<AttributeItem> attrItemList = new ArrayList<AttributeItem>(FixEquipHelper.parseFixNormEquipLevelAttr(ownerId, itemList).values());
		return attrItemList;
	}

	public List<AttributeItem> qualityToAttrItems(String ownerId) {
		List<FixNormEquipDataItem> itemList = fixNormEquipDataItemHolder.getItemList(ownerId);
		List<AttributeItem> attrItemList = new ArrayList<AttributeItem>(FixEquipHelper.parseFixNormEquipQualityAttr(ownerId, itemList).values());
		return attrItemList;
	}

	public List<AttributeItem> starToAttrItems(String ownerId) {
		List<FixNormEquipDataItem> itemList = fixNormEquipDataItemHolder.getItemList(ownerId);
		List<AttributeItem> attrItemList = new ArrayList<AttributeItem>(FixEquipHelper.parseFixNormEquipStarAttr(ownerId, itemList).values());
		return attrItemList;
	}

	public List<String> qualityUpList(Player player, String ownerId) {

		List<String> upIdList = new ArrayList<String>();
		List<FixNormEquipDataItem> itemList = fixNormEquipDataItemHolder.getItemList(ownerId);

		for (FixNormEquipDataItem dataItem : itemList) {
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
		List<FixNormEquipDataItem> itemList = fixNormEquipDataItemHolder.getItemList(ownerId);
		for (FixNormEquipDataItem dataItem : itemList) {
			FixEquipResult result = checkStarUp(player, ownerId, dataItem);
			if (result.isSuccess()) {
				upIdList.add(dataItem.getId());
			}

		}
		return upIdList;

	}

	public FixEquipResult levelUpOneKey(Player player, String ownerId, String itemId) {

		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, itemId);

		int toLevel = getToLevel(player, ownerId, dataItem);
		int curLevel = dataItem.getLevel();

		FixEquipCfg fixEquipCfg = FixEquipCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
		FixNormEquipLevelCostCfg curLevelCostCfg = FixNormEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(fixEquipCfg.getLevelCostPlanId(), curLevel);
		int costNeed = getLevelCostNeed(fixEquipCfg.getLevelCostPlanId(), curLevel, toLevel);

		FixEquipResult result = FixEquipHelper.takeCost(player, curLevelCostCfg.getCostType(), costNeed);
		if (result.isSuccess()) {
			dataItem.setLevel(toLevel);
			fixNormEquipDataItemHolder.updateItem(player, dataItem);
		}

		return result;
	}

	private int getToLevel(Player player, String ownerId, FixNormEquipDataItem dataItem) {

		int quality = dataItem.getQuality();
		FixEquipCfg fixEquipCfg = FixEquipCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
		FixNormEquipQualityCfg curQualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(fixEquipCfg.getQualityPlanId(), quality);

		int toLevel = curQualityCfg.getLevelNeed();
		int playerLevel = player.getLevel();
		if (toLevel > playerLevel) {
			toLevel = playerLevel;
		}
		return toLevel;
	}

	private int getLevelCostNeed(String levelCostPlanId, int curLevel, int toLevel) {
		int levelCost = 0;
		for (int tmpLevel = curLevel; tmpLevel < toLevel; tmpLevel++) {

			FixNormEquipLevelCostCfg tmpLevelCostCfg = FixNormEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(levelCostPlanId, tmpLevel);
			levelCost = levelCost + tmpLevelCostCfg.getCostCount();

		}

		return levelCost;
	}

	public FixEquipResult levelUp(Player player, String ownerId, String itemId) {

		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, itemId);
		FixEquipResult result = checkLevel(player, ownerId, dataItem);
		if (result.isSuccess()) {
			result = doLevelUp(player, dataItem);
		}

		return result;
	}

	private FixEquipResult checkLevel(Player player, String ownerId, FixNormEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		if (dataItem == null) {
			result.setReason("装备不存在");
		} else {
			int nextLevel = dataItem.getLevel() + 1;
			FixNormEquipLevelCostCfg nextLevelCostCfg = FixNormEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), nextLevel);

			if (player.getLevel() < nextLevel) {
				result.setReason("装备已经达到最高级");
			} else if (nextLevelCostCfg == null) {
				result.setReason("装备等级不能超过英雄等级");
			} else {
				result.setSuccess(true);

			}

		}
		return result;
	}

	private FixEquipResult doLevelUp(Player player, FixNormEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		int curLevel = dataItem.getLevel();

		FixNormEquipLevelCostCfg curLevelCostCfg = FixNormEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), curLevel);

		result = FixEquipHelper.takeCost(player, curLevelCostCfg.getCostType(), curLevelCostCfg.getCostCount());
		if (result.isSuccess()) {
			dataItem.setLevel(curLevel + 1);
			fixNormEquipDataItemHolder.updateItem(player, dataItem);
		}

		return result;
	}

	public FixEquipResult qualityUp(Player player, String ownerId, String itemId) {

		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, itemId);

		FixEquipResult result = checkQualityUp(player, ownerId, dataItem);
		if (result.isSuccess()) {
			result = doQualityUp(player, dataItem);
		}

		return result;
	}

	private FixEquipResult checkQualityUp(Player player, String ownerId, FixNormEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);

		if (dataItem == null) {
			result.setReason("装备不存在");
		} else {
			int curlevel = dataItem.getLevel();
			int currentQuality = dataItem.getQuality();
			FixNormEquipQualityCfg nextQualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), currentQuality + 1);
			if (nextQualityCfg == null) {
				result.setReason("装备已经达到最品质");
			} else {

				FixNormEquipQualityCfg curQualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), currentQuality);
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
			FixNormEquipQualityCfg curQualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), curQuality);
			result = FixEquipHelper.checkCost(player, curQualityCfg.getCostType(), curQualityCfg.getCostCount());
		}

		return result;
	}

	private FixEquipResult doQualityUp(Player player, FixNormEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		int curQuality = dataItem.getQuality();
		FixNormEquipQualityCfg curQualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), curQuality);
		result = FixEquipHelper.takeCost(player, curQualityCfg.getCostType(), curQualityCfg.getCostCount());

		if (result.isSuccess()) {
			Map<Integer, Integer> itemsNeed = curQualityCfg.getItemsNeed();
			result = FixEquipHelper.takeItemCost(player, itemsNeed);
		}

		if (result.isSuccess()) {
			dataItem.setQuality(curQuality + 1);
			fixNormEquipDataItemHolder.updateItem(player, dataItem);
		}

		return result;

	}

	public FixEquipResult starUp(Player player, String ownerId, String itemId) {

		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, itemId);

		FixEquipResult result = checkStarUp(player, ownerId, dataItem);
		if (result.isSuccess()) {
			result = doStarUp(player, dataItem);
		}

		return result;
	}

	private FixEquipResult checkStarUp(Player player, String ownerId, FixNormEquipDataItem dataItem) {

		FixEquipResult result = FixEquipResult.newInstance(false);

		if (dataItem == null) {
			result.setReason("装备不存在");
		} else {
			int curStar = dataItem.getStar();
			FixNormEquipStarCfg nextStarCfg = FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar + 1);

			if (nextStarCfg == null) {
				result.setReason("装备已达最高星级");
			} else {
				FixNormEquipStarCfg curStarCfg = FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);

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

			FixNormEquipStarCfg curStarCfg = FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);
			result = FixEquipHelper.checkCost(player, curStarCfg.getUpCostType(), curStarCfg.getUpCount());
		}
		return result;
	}

	private FixEquipResult doStarUp(Player player, FixNormEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);

		int curStar = dataItem.getStar();

		FixNormEquipStarCfg curStarCfg = FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);
		result = FixEquipHelper.takeCost(player, curStarCfg.getUpCostType(), curStarCfg.getUpCount());

		if (result.isSuccess()) {
			Map<Integer, Integer> itemsNeed = curStarCfg.getItemsNeed();
			result = FixEquipHelper.takeItemCost(player, itemsNeed);
		}

		if (result.isSuccess()) {
			dataItem.setStar(curStar + 1);
			fixNormEquipDataItemHolder.updateItem(player, dataItem);
		}
		return result;
	}

	public FixEquipResult starDown(Player player, String ownerId, String itemId) {

		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, itemId);

		FixEquipResult result = checkStarDown(player, ownerId, dataItem);
		if (result.isSuccess()) {
			int curStar = dataItem.getStar();
			int nextStar = curStar - 1;

			// 降星读当前的配置
			FixNormEquipStarCfg curStarCfg = FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);
			FixNormEquipStarCfg nextStarCfg = FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), nextStar);
			result = FixEquipHelper.takeCost(player, curStarCfg.getDownCostType(), curStarCfg.getDownCount());

			if (result.isSuccess()) {
				dataItem.setStar(nextStar);
				fixNormEquipDataItemHolder.updateItem(player, dataItem);

				Map<Integer, Integer> itemsNeed = nextStarCfg.getItemsNeed();
				result = FixEquipHelper.turnBackItemCost(player, itemsNeed);
			}
		}

		return result;
	}

	private FixEquipResult checkStarDown(Player player, String ownerId, FixNormEquipDataItem dataItem) {

		FixEquipResult result = FixEquipResult.newInstance(false);

		if (dataItem == null) {
			result.setReason("装备不存在");
		} else {
			int nextStar = dataItem.getStar() - 1;
			if (nextStar < 0) {
				result.setReason("装备已是最低等级");
			} else {
				result.setSuccess(true);
			}
		}
		return result;
	}

	public List<HeroFixEquipInfo> getHeroFixSimpleInfo(String heroId) {
		return FixEquipHelper.parseFixNormEquip2SimpleList(fixNormEquipDataItemHolder.getItemList(heroId));
	}
}
