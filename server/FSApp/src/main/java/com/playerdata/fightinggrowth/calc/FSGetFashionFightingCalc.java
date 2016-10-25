package com.playerdata.fightinggrowth.calc;

import com.playerdata.fightinggrowth.calc.param.FashionFightingParam;
import com.rwbase.dao.fighting.FashionFightingCfgDAO;

/**
 * @Author HC
 * @date 2016年10月25日 上午10:54:42
 * @desc 计算时装战斗力
 **/

public class FSGetFashionFightingCalc implements IFightingCalc {
	private FashionFightingCfgDAO _fashionFightingCfgDAO;

	protected FSGetFashionFightingCalc() {
		_fashionFightingCfgDAO = FashionFightingCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		FashionFightingParam fashionParam = (FashionFightingParam) param;
		int fighting = 0;

		if (fashionParam.getSuitCount() > 0) {
			fighting += _fashionFightingCfgDAO.getCfgById(String.valueOf(fashionParam.getSuitCount())).getFightingOfSuit();
		}

		if (fashionParam.getWingCount() > 0) {
			fighting += _fashionFightingCfgDAO.getCfgById(String.valueOf(fashionParam.getWingCount())).getFightingOfWing();
		}

		if (fashionParam.getPetCount() > 0) {
			fighting += _fashionFightingCfgDAO.getCfgById(String.valueOf(fashionParam.getPetCount())).getFightingOfPet();
		}

		return fighting;
	}
}