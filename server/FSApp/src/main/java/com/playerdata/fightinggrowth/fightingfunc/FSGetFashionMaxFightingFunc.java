package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
import com.rwbase.dao.fighting.FashionFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.ExpectedHeroStatusCfg;

public class FSGetFashionMaxFightingFunc implements IFunction<Player, Integer> {

	private static FSGetFashionMaxFightingFunc _instance = new FSGetFashionMaxFightingFunc();

	private FashionFightingCfgDAO _fashionFightingCfgDAO;
	private ExpectedHeroStatusCfgDAO _expectedHeroStatusCfgDAO;

	protected FSGetFashionMaxFightingFunc() {
		_fashionFightingCfgDAO = FashionFightingCfgDAO.getInstance();
		_expectedHeroStatusCfgDAO = ExpectedHeroStatusCfgDAO.getInstance();
	}

	public static FSGetFashionMaxFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		ExpectedHeroStatusCfg cfg = _expectedHeroStatusCfgDAO.getCfgById(String.valueOf(player.getLevel()));
		int fighting = _fashionFightingCfgDAO.getCfgById(String.valueOf(cfg.getFashionWingCount())).getFightingOfSuit() * cfg.getFashionSuitCount();
		fighting += _fashionFightingCfgDAO.getCfgById(String.valueOf(cfg.getFashionWingCount())).getFightingOfWing() * cfg.getFashionWingCount();
		fighting += _fashionFightingCfgDAO.getCfgById(String.valueOf(cfg.getFashionPetCount())).getFightingOfPet() * cfg.getFashionPetCount();
		return fighting;
	}

}
