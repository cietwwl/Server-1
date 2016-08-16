package com.playerdata.fightinggrowth;

import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;

public class FSUserFightingGrowthTitleAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		return null;
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fight_Growth_Title;
	}

}
