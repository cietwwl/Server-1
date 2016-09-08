package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.rwbase.common.IFunction;
import com.rwbase.common.attrdata.AttrData;

public class FSGetBasicCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {
	
	private static final FSGetBasicCurrentFightingOfSingleFunc _instance = new FSGetBasicCurrentFightingOfSingleFunc();
	
	public static final FSGetBasicCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}
	
	protected FSGetBasicCurrentFightingOfSingleFunc() {
		
	}

	@Override
	public Integer apply(Hero hero) {
		int fighting = FightingCalculator.calOnlyAttributeFighting(hero.getTemplateId(), (AttrData)hero.getAttrMgr().getRoleAttrData().getRoleBaseTotalData());
		return fighting;
	}

}
