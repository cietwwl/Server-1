package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.rwbase.common.IFunction;
import com.rwbase.common.attrdata.AttrData;

public class FSGetBasicCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	@Override
	public Integer apply(Hero hero) {
		return FightingCalculator.calFighting(hero.getTemplateId(), 0, 0, "", (AttrData)hero.getAttrMgr().getRoleAttrData().getRoleBaseTotalData());
	}

}
