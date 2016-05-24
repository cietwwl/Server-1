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
 * @date 2016年5月13日 下午5:10:27
 * @Description 英雄时装的属性计算，只有主角才会有时装
 */
public class HeroFashionAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		if (!hero.isMainRole()) {
			return null;
		}

		Map<Integer, AttributeItem> attributeMap = player.getFashionMgr().getAttributeMap();
		if (attributeMap.isEmpty()) {
			GameLog.error("计算英雄时装属性", player.getUserId(), "时装模块计算出来的属性是空的");
			return null;
		}

		GameLog.info("计算英雄时装属性", player.getUserId(), AttributeUtils.partAttrMap2Str("时装", attributeMap), null);
		return new AttributeSet.Builder().addAttribute(new ArrayList<AttributeItem>(attributeMap.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fashion;
	}
}