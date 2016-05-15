package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.log.GameLog;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.HeroBaseParam;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;

/*
 * @author HC
 * @date 2016年5月14日 下午7:54:06
 * @Description 
 */
public class HeroBaseAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		HeroBaseParam param = (HeroBaseParam) obj;
		String userId = param.getUserId();
		RoleCfg roleCfg = RoleCfgDAO.getInstance().getCfgById(param.getHeroTmpId());
		if (roleCfg == null) {
			GameLog.error("计算英雄基础属性", userId, String.format("[%s]的英雄获取不到对应的RoleCfg配置表", param.getHeroTmpId()));
			return null;
		}

		int level = param.getLevel();

		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();
		AttributeUtils.calcAttribute(roleCfg.getAttrDataMap(), roleCfg.getPrecentAttrDataMap(), map);

		// 计算成长率
		Map<Integer, Integer> growUpMap = roleCfg.getGrowUpMap();
		for (Entry<Integer, Integer> e : growUpMap.entrySet()) {
			Integer type = e.getKey();

			AttributeType attrType = AttributeType.getAttributeType(type);
			if (attrType == null) {
				continue;
			}

			if (attrType.impactAttrType > 0) {
				type = attrType.impactAttrType;
			}

			Integer growUp = e.getValue();

			AttributeItem attributeItem = map.get(type);
			if (attributeItem == null) {
				continue;
			}

			int increaseValue = attributeItem.getIncreaseValue();
			int incPerTenthousand = attributeItem.getIncPerTenthousand();

			increaseValue += (increaseValue * growUp / AttributeConst.GROW_UP_RATE) * level;

			attributeItem = new AttributeItem(AttributeType.getAttributeType(type), increaseValue, incPerTenthousand);
			map.put(type, attributeItem);
		}

		// 计算品质属性增加
		RoleQualityCfg qualityCfg = RoleQualityCfgDAO.getInstance().getConfig(param.getQualityId());
		if (qualityCfg != null) {
			AttributeUtils.calcAttribute(qualityCfg.getAttrDataMap(), qualityCfg.getPrecentAttrDataMap(), map);
		}

		if (map.isEmpty()) {
			GameLog.error("计算英雄基础属性", userId, "英雄基础计算出来的属性是空的");
			return null;
		}

		GameLog.info("计算英雄基础属性", userId, AttributeUtils.partAttrMap2Str("英雄基础", map), null);
		return new AttributeSet.Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Base;
	}
}