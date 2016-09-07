package com.playerdata.fixEquip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfgDAO;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.playerdata.team.HeroFixEquipInfo;
import com.rw.service.Email.EmailUtils;
import com.rwbase.common.attribute.AttrCheckLoger;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.enu.eConsumeTypeDef;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class FixEquipHelper {

	public static String getExpItemId(String heroId, String cfgId) {

		return heroId + "_" + cfgId;
	}

	public static String getNormItemId(String heroId, String cfgId) {

		return heroId + "_" + cfgId;
	}

	public static Map<Integer, Integer> parseNeedItems(String itemsNeedStr) {
		Map<Integer, Integer> itemsNeed = new HashMap<Integer, Integer>();
		if (StringUtils.isNotBlank(itemsNeedStr)) {
			// modelAId:count;modelBId:count
			String[] itemArray = itemsNeedStr.split(";");
			for (String itemTmp : itemArray) {
				String[] split = itemTmp.split(":");
				int modelId = Integer.valueOf(split[0]);
				int count = Integer.valueOf(split[1]);
				itemsNeed.put(modelId, count);
			}
		}
		return itemsNeed;
	}

	public static FixEquipResult checkCost(Player player, FixEquipCostType costType, int count) {

		FixEquipResult result = FixEquipResult.newInstance(false);

		switch (costType) {
		case COIN:
			if (checkCoin(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("金币不足");
			}
			break;
		case GOLD:
			if (checkGold(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("钻石不足");
			}
			break;

		default:
			break;
		}
		return result;
	}

	public static FixEquipResult takeCost(Player player, FixEquipCostType costType, int count) {

		FixEquipResult result = FixEquipResult.newInstance(false);

		switch (costType) {
		case COIN:
			if (costCoin(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("金币不足");
			}
			break;
		case GOLD:
			if (costGold(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("钻石不足");
			}
			break;

		default:
			break;
		}
		return result;
	}

	private static boolean costGold(Player player, int count) {
		int resultCode = player.getUserGameDataMgr().addGold(-count);
		return resultCode == 0;
	}

	private static boolean costCoin(Player player, int count) {
		int resultCode = player.getUserGameDataMgr().addCoin(-count);
		return resultCode == 0;
	}

	private static boolean checkGold(Player player, int count) {

		return player.getUserGameDataMgr().isGoldEngough(-count);

	}

	private static boolean checkCoin(Player player, int count) {
		return player.getUserGameDataMgr().isCoinEnough(-count);
	}

	final private static String emailCfgId = "10063";

	public static FixEquipResult turnBackItemCost(Player player, Map<Integer, Integer> itemCostMap) {

		FixEquipResult result = FixEquipResult.newInstance(false);

		String userId = player.getUserId();
		boolean sendEmail = EmailUtils.sendEmail(userId, emailCfgId, itemCostMap);
		if (sendEmail) {
			result.setSuccess(true);

		} else {
			String errorReason = "物品返回邮件发送失败";
			result.setReason(errorReason);
			GameLog.error(LogModule.FixEquip, userId, errorReason, null);
			result.setSuccess(sendEmail);
		}

		return result;

	}

	public static FixEquipResult takeItemCost(Player player, Map<Integer, Integer> itemCostMap) {
		FixEquipResult result = FixEquipResult.newInstance(false);

		if (costItemBag(player, itemCostMap)) {
			result.setSuccess(true);
		} else {
			result.setReason("物品不足。");
		}
		return result;

	}
	
	/**
	 * 返回背包中可以用于神器升级的物品
	 * @param player
	 * @return
	 */
	public static HashMap<eConsumeTypeDef,List<ItemData>> getFixConsumeItemMap(Player player) {
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		List<ItemData> lst = itemBagMgr.getItemListByType(EItemTypeDef.Consume);
		HashMap<eConsumeTypeDef, List<ItemData>> result = new HashMap<eConsumeTypeDef, List<ItemData>>(eConsumeTypeDef.values().length);
		for (ItemData itemData : lst) {
			ConsumeCfg cfg = ItemCfgHelper.getConsumeCfg(itemData.getModelId());
			if (cfg != null) {
				eConsumeTypeDef cty = eConsumeTypeDef.getDef(cfg.getConsumeType());
				if (cty != null && (cty == eConsumeTypeDef.Exp4FixEquip_4 || cty == eConsumeTypeDef.Exp4FixEquip_5)) {
					List<ItemData> old = result.get(cty);
					if (old == null) {
						old = new ArrayList<ItemData>();
					}
					old.add(itemData);
				}
			}
		}
		return result;
	}

	public static boolean isItemEnough(Player player, Map<Integer, Integer> itemCostMap) {
		ItemBagMgr itemBagMgr = player.getItemBagMgr();

		boolean isItemEnough = true;
		for (int modelId : itemCostMap.keySet()) {
			int countInBag = itemBagMgr.getItemCountByModelId(modelId);
			if (itemCostMap.get(modelId) > countInBag) {
				isItemEnough = false;
				break;
			}

		}
		return isItemEnough;
	}

	private static boolean costItemBag(Player player, Map<Integer, Integer> itemCostMap) {
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		boolean success = isItemEnough(player, itemCostMap);
		if (success) {
			for (int modelId : itemCostMap.keySet()) {
				Integer need = itemCostMap.get(modelId);
				if (!itemBagMgr.useItemByCfgId(modelId, need)) {
					success = false;
					break;
				}

			}

		}
		return success;
	}

	public static boolean turnBackItems(Player player, Map<Integer, Integer> itemCostMap) {

		boolean success = true;
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		List<ItemInfo> list = new ArrayList<ItemInfo>(itemCostMap.size());
		for (Integer modelId : itemCostMap.keySet()) {
			Integer count = itemCostMap.get(modelId);
//			itemBagMgr.addItem(modelId, count);
			list.add(new ItemInfo(modelId, count));
		}
		itemBagMgr.addItem(list);

		return success;
	}

	// ==============================================================经验类类神器计算属性
	public static Map<Integer, AttributeItem> parseFixExpEquipLevelAttr(String ownerId, List<FixExpEquipDataItem> itemList) {
		HashMap<Integer, AttributeItem> attrMap = new HashMap<Integer, AttributeItem>();

		for (FixExpEquipDataItem itemTmp : itemList) {
			FixExpEquipLevelCfg curLevelCfg = FixExpEquipLevelCfgDAO.getInstance().getByPlanIdAndLevel(itemTmp.getLevelPlanId(), itemTmp.getLevel());
			AttributeUtils.calcAttribute(curLevelCfg.getAttrDataMap(), curLevelCfg.getPrecentAttrDataMap(), attrMap);
			AttrCheckLoger.logAttr("经验神装_等级", ownerId, attrMap);
		}

		return attrMap;
	}

	public static Map<Integer, AttributeItem> parseFixExpEquipQualityAttr(String ownerId, List<FixExpEquipDataItem> itemList) {
		HashMap<Integer, AttributeItem> attrMap = new HashMap<Integer, AttributeItem>();

		for (FixExpEquipDataItem itemTmp : itemList) {
			FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(itemTmp.getQualityPlanId(), itemTmp.getQuality());
			AttributeUtils.calcAttribute(curQualityCfg.getAttrDataMap(), curQualityCfg.getPrecentAttrDataMap(), attrMap);
			AttrCheckLoger.logAttr("经验神装_品阶", ownerId, attrMap);
		}

		return attrMap;
	}

	public static Map<Integer, AttributeItem> parseFixExpEquipStarAttr(String ownerId, List<FixExpEquipDataItem> itemList) {
		HashMap<Integer, AttributeItem> attrMap = new HashMap<Integer, AttributeItem>();

		for (FixExpEquipDataItem itemTmp : itemList) {
			FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(itemTmp.getStarPlanId(), itemTmp.getStar());
			AttributeUtils.calcAttribute(curStarCfg.getAttrDataMap(), curStarCfg.getPrecentAttrDataMap(), attrMap);
			AttrCheckLoger.logAttr("经验神装_觉醒", ownerId, attrMap);
		}

		return attrMap;
	}

	// ==============================================================普通类神器计算属性
	public static Map<Integer, AttributeItem> parseFixNormEquipLevelAttr(String ownerId, List<FixNormEquipDataItem> itemList) {
		HashMap<Integer, AttributeItem> attrMap = new HashMap<Integer, AttributeItem>();

		for (FixNormEquipDataItem itemTmp : itemList) {
			FixNormEquipLevelCfg curLevelCfg = FixNormEquipLevelCfgDAO.getInstance().getByPlanIdAndLevel(itemTmp.getLevelPlanId(), itemTmp.getLevel());
			AttributeUtils.calcAttribute(curLevelCfg.getAttrDataMap(), curLevelCfg.getPrecentAttrDataMap(), attrMap);
			AttrCheckLoger.logAttr("普通神装_等级", ownerId, attrMap);
		}

		return attrMap;
	}

	public static Map<Integer, AttributeItem> parseFixNormEquipQualityAttr(String ownerId, List<FixNormEquipDataItem> itemList) {
		HashMap<Integer, AttributeItem> attrMap = new HashMap<Integer, AttributeItem>();

		for (FixNormEquipDataItem itemTmp : itemList) {
			FixNormEquipQualityCfg curQualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(itemTmp.getQualityPlanId(), itemTmp.getQuality());
			AttributeUtils.calcAttribute(curQualityCfg.getAttrDataMap(), curQualityCfg.getPrecentAttrDataMap(), attrMap);
			AttrCheckLoger.logAttr("普通神装_品阶", ownerId, attrMap);
		}

		return attrMap;
	}

	public static Map<Integer, AttributeItem> parseFixNormEquipStarAttr(String ownerId, List<FixNormEquipDataItem> itemList) {
		HashMap<Integer, AttributeItem> attrMap = new HashMap<Integer, AttributeItem>();

		for (FixNormEquipDataItem itemTmp : itemList) {
			FixNormEquipStarCfg curStarCfg = FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(itemTmp.getStarPlanId(), itemTmp.getStar());
			AttributeUtils.calcAttribute(curStarCfg.getAttrDataMap(), curStarCfg.getPrecentAttrDataMap(), attrMap);
			AttrCheckLoger.logAttr("普通神装_觉醒", ownerId, attrMap);
		}

		return attrMap;
	}

	/**
	 * 转换经验类神器到简单的存储结构列表
	 * 
	 * @param fixExpEquipList
	 * @return
	 */
	public static List<HeroFixEquipInfo> parseFixExpEquip2SimpleList(List<FixExpEquipDataItem> fixExpEquipList) {
		if (fixExpEquipList == null || fixExpEquipList.isEmpty()) {
			return Collections.emptyList();
		}

		int size = fixExpEquipList.size();
		List<HeroFixEquipInfo> fixInfoList = new ArrayList<HeroFixEquipInfo>();
		for (int i = 0; i < size; i++) {
			FixExpEquipDataItem fixExp = fixExpEquipList.get(i);
			if (fixExp == null || (fixExp.getLevel() | fixExp.getQuality() | fixExp.getStar()) == 0) {
				continue;
			}

			HeroFixEquipInfo fixInfo = new HeroFixEquipInfo();
			fixInfo.setId(fixExp.getCfgId());
			fixInfo.setLevel(fixExp.getLevel());
			fixInfo.setQuality(fixExp.getQuality());
			fixInfo.setStar(fixExp.getStar());

			fixInfoList.add(fixInfo);
		}

		return fixInfoList;
	}

	/**
	 * 转换普通类神器到简单的存储结构列表
	 * 
	 * @param fixNormEquipList
	 * @return
	 */
	public static List<HeroFixEquipInfo> parseFixNormEquip2SimpleList(List<FixNormEquipDataItem> fixNormEquipList) {
		if (fixNormEquipList == null || fixNormEquipList.isEmpty()) {
			return Collections.emptyList();
		}

		int size = fixNormEquipList.size();
		List<HeroFixEquipInfo> fixInfoList = new ArrayList<HeroFixEquipInfo>();
		for (int i = 0; i < size; i++) {
			FixNormEquipDataItem fixNorm = fixNormEquipList.get(i);
			if (fixNorm == null || (fixNorm.getLevel() | fixNorm.getQuality() | fixNorm.getStar()) == 0) {
				continue;
			}

			HeroFixEquipInfo fixInfo = new HeroFixEquipInfo();
			fixInfo.setId(fixNorm.getCfgId());
			fixInfo.setLevel(fixNorm.getLevel());
			fixInfo.setQuality(fixNorm.getQuality());
			fixInfo.setStar(fixNorm.getStar());

			fixInfoList.add(fixInfo);
		}

		return fixInfoList;
	}
}