package com.rwbase.common.attribute.component;

import java.util.ArrayList;
import java.util.Map;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;

/*
 * @author HC
 * @date 2016年5月13日 下午2:36:14
 * @Description 帮派技能属性加成
 */
public class HeroGroupSkillAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		Map<Integer, AttributeItem> groupSkillAttrDataMap = player.getUserGroupAttributeDataMgr().getGroupSkillAttrDataMap();
		if (groupSkillAttrDataMap == null || groupSkillAttrDataMap.isEmpty()) {
			GameLog.error("计算英雄帮派属性", player.getUserId(), "帮派技能计算出来的属性是空的");
			return null;
		}

		GameLog.info("计算英雄帮派属性", player.getUserId(), AttributeUtils.partAttrMap2Str("帮派技能", groupSkillAttrDataMap), null);
		return new AttributeSet.Builder().addAttribute(new ArrayList<AttributeItem>(groupSkillAttrDataMap.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Group_Skill;
	}
}