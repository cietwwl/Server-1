package com.playerdata.fightgrowth;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;

public class FSUserFightingGrowthTitleAttributeComponent extends AbstractAttributeCalc {

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fight_Growth_Title;
	}

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		return null;
	}

}
