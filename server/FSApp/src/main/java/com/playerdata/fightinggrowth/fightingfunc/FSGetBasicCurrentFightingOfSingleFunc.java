package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Hero;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.fightinggrowth.calc.param.HeroBaseFightingParam.Builder;
import com.rwbase.common.IFunction;
import com.rwbase.common.attrdata.AttrData;

public class FSGetBasicCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static FSGetBasicCurrentFightingOfSingleFunc _instance = new FSGetBasicCurrentFightingOfSingleFunc();

	public static FSGetBasicCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}

	protected FSGetBasicCurrentFightingOfSingleFunc() {

	}

	@Override
	public Integer apply(Hero hero) {
		Builder b = new Builder();
		b.setHeroTmpId(hero.getTemplateId());
		b.setBaseData((AttrData) hero.getAttrMgr().getRoleAttrData().getRoleBaseTotalData());
		return FightingCalcComponentType.BASE.calc.calc(b.build());
	}
}