package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.log.GameLog;
import com.rwbase.common.attribute.AttrCheckLoger;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeSet.Builder;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.GemParam;
import com.rwbase.dao.item.GemCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.role.InlayCfgDAO;
import com.rwbase.dao.role.pojo.AttrDataInfo;
import com.rwbase.dao.role.pojo.InlayCfg;

/*
 * @author HC
 * @date 2016年5月14日 下午8:03:02
 * @Description 
 */
public class HeroGemAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		GemParam param = (GemParam) obj;
		String userId = param.getUserId();
		List<String> inlayGemList = param.getGemList();
		String heroId = param.getHeroId();
		if (inlayGemList == null || inlayGemList.isEmpty()) {
			GameLog.error("计算英雄宝石属性", userId, String.format("Id为[%s]的英雄身上没有任何宝石", heroId));
			return null;
		}

		// 镶嵌宝石的等级划分
		HashMap<Integer, Integer> gemLevelNumMap = new HashMap<Integer, Integer>();

		GemCfgDAO cfgDAO = GemCfgDAO.getInstance();
		// 属性Map集合
		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();
		for (int i = 0, size = inlayGemList.size(); i < size; i++) {
			String gemModelId = inlayGemList.get(i);
			GemCfg cfg = cfgDAO.getCfgById(gemModelId);
			if (cfg == null) {
				continue;
			}

			AttributeUtils.calcAttribute(cfg.getAttrDataMap(), cfg.getPrecentAttrDataMap(), map);

			int level = cfg.getGemLevel();
			Integer hasValue = gemLevelNumMap.get(level);
			if (hasValue == null) {
				gemLevelNumMap.put(level, 1);
			} else {
				gemLevelNumMap.put(level, hasValue + 1);
			}
		}

		// 计算附加属性
		String heroModelId = param.getHeroModelId();
		if (heroModelId.indexOf("_") != -1) {
			heroModelId = heroModelId.split("_")[0];
		}

		InlayCfg inlayCfg = InlayCfgDAO.getInstance().getCfgById(heroModelId);
		if (inlayCfg != null) {
			addHeroInlayExtraAttrValue(map, gemLevelNumMap, inlayCfg.getExtraLvList1(), inlayCfg.getExtraNum1(), inlayCfg);
			addHeroInlayExtraAttrValue(map, gemLevelNumMap, inlayCfg.getExtraLvList2(), inlayCfg.getExtraNum2(), inlayCfg);
			addHeroInlayExtraAttrValue(map, gemLevelNumMap, inlayCfg.getExtraLvList3(), inlayCfg.getExtraNum3(), inlayCfg);
		}

		// 计算属性
		if (map.isEmpty()) {
			GameLog.error("计算英雄宝石属性", userId, String.format("Id为[%s]的英雄计算出来的宝石属性是空的", heroId));
			return null;
		}

		AttrCheckLoger.logAttr("英雄宝石属性", heroId, map);
		return new Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	/**
	 * 增加额外的属性到总属性中
	 * 
	 * @param map
	 * @param levelNumMap
	 * @param extraLvList
	 * @param extraNum1
	 * @param inlayCfg
	 */
	private static void addHeroInlayExtraAttrValue(HashMap<Integer, AttributeItem> map, HashMap<Integer, Integer> levelNumMap, List<Integer> extraLvList, int extraNum1, InlayCfg inlayCfg) {
		int maxLevel = 0;// 最大的等级
		for (int i = extraLvList.size() - 1; i >= 0; --i) {
			Integer level = extraLvList.get(i);

			int canNum = 0;
			for (Entry<Integer, Integer> entry : levelNumMap.entrySet()) {
				Integer hasLevel = entry.getKey();
				if (hasLevel < level) {
					continue;
				}

				canNum += entry.getValue();
			}

			if (canNum < extraNum1) {
				continue;
			}

			if (level > maxLevel) {
				maxLevel = level;
			}
		}

		if (maxLevel > 0) {
			AttrDataInfo extraAttrDataInfo = inlayCfg.getExtraAttrDataInfo1(maxLevel);
			if (extraAttrDataInfo != null) {
				AttributeUtils.calcAttribute(extraAttrDataInfo.getAttrDataMap(), extraAttrDataInfo.getPrecentAttrDataMap(), map);
			}
		}
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Gem;
	}
}