package com.playerdata.fightinggrowth.calc;

import com.playerdata.FightingCalculator;
import com.playerdata.fightinggrowth.calc.param.HeroBaseFightingParam;

/**
 * @Author HC
 * @date 2016年10月25日 上午10:43:01
 * @desc
 **/

public class FSGetBaseFightingCalc implements IFightingCalc {

	@Override
	public int calc(Object param) {
		HeroBaseFightingParam calcParam = (HeroBaseFightingParam) param;
		return FightingCalculator.calOnlyAttributeFighting(calcParam.getHeroTmpId(), calcParam.getBaseData());
	}
}