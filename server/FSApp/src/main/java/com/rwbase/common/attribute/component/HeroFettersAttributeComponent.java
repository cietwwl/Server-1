package com.rwbase.common.attribute.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeSet.Builder;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.SynFettersData;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersConditionCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersConditionTemplate;

/*
 * @author HC
 * @date 2016年5月12日 下午3:18:59
 * @Description 
 */
public class HeroFettersAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		String userId = player.getUserId();
		SynFettersData heroFetters = player.getHeroFettersByModelId(hero.getModelId());
		if (heroFetters == null) {
			GameLog.error("计算英雄羁绊属性", userId, String.format("模版为[%s]的英雄没有激活的羁绊数据", hero.getModelId()));
			return null;
		}

		Map<Integer, SynConditionData> openList = heroFetters.getOpenList();
		if (openList == null || openList.isEmpty()) {
			GameLog.error("计算英雄羁绊属性", userId, String.format("模版为[%s]的英雄所有的羁绊都没有被激活过", hero.getModelId()));
			return null;
		}

		FettersConditionCfgDAO cfgDAO = FettersConditionCfgDAO.getCfgDAO();
		// 属性Map集合
		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();

		for (Entry<Integer, SynConditionData> e : openList.entrySet()) {
			SynConditionData conditionData = e.getValue();
			if (conditionData == null) {
				continue;
			}

			List<Integer> conditionList = conditionData.getConditionList();
			if (conditionList == null || conditionList.isEmpty()) {
				continue;
			}

			// 条件列表
			for (int i = conditionList.size() - 1; i >= 0; --i) {
				int uniqueId = conditionList.get(i);
				FettersConditionTemplate fettersConditionTmp = cfgDAO.getFettersConditionTemplateByUniqueId(uniqueId);
				if (fettersConditionTmp == null) {
					continue;
				}

				// 计算固定值
				Map<Integer, Integer> attrDataMap = fettersConditionTmp.getHeroFettersAttrDataMap();
				for (Entry<Integer, Integer> entry : attrDataMap.entrySet()) {
					Integer key = entry.getKey();
					AttributeItem attributeItem = map.get(key);
					int value = 0;
					int precentValue = 0;
					if (attributeItem != null) {
						value = attributeItem.getIncreaseValue();
						precentValue = attributeItem.getIncPerTenthousand();
					}

					attributeItem = new AttributeItem(AttributeType.getAttributeType(key), entry.getValue() + value, precentValue);
					map.put(key, attributeItem);
				}

				// 计算百分比值
				Map<Integer, Integer> precentAttrDataMap = fettersConditionTmp.getHeroFettersPrecentAttrDataMap();
				for (Entry<Integer, Integer> entry : precentAttrDataMap.entrySet()) {
					Integer key = entry.getKey();
					AttributeItem attributeItem = map.get(key);
					int value = 0;
					int precentValue = 0;
					if (attributeItem != null) {
						value = attributeItem.getIncreaseValue();
						precentValue = attributeItem.getIncPerTenthousand();
					}

					attributeItem = new AttributeItem(AttributeType.getAttributeType(key), value, entry.getValue() + precentValue);
					map.put(key, attributeItem);
				}
			}
		}

		// 计算属性
		if (map.isEmpty()) {
			GameLog.error("计算英雄羁绊属性", userId, String.format("模版为[%s]的英雄羁绊计算出来的属性是空的", hero.getModelId()));
			return null;
		}

		GameLog.info("计算英雄羁绊属性", userId, AttributeUtils.partAttrMap2Str("羁绊", map), null);
		return new Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fetters;
	}
}