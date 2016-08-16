package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
import com.rwbase.dao.fighting.FashionFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.ExpectedHeroStatusCfg;
import com.rwbase.dao.fighting.pojo.FashionFightingCfg;

public class FSGetFashionMaxFightingFunc implements IFunction<Player, Integer> {
	
	private static final FSGetFashionMaxFightingFunc _INSTANCE = new FSGetFashionMaxFightingFunc();
	
	private FashionFightingCfgDAO _fashionFightingCfgDAO;
	private ExpectedHeroStatusCfgDAO _expectedHeroStatusCfgDAO;
	
	protected FSGetFashionMaxFightingFunc() {
		_fashionFightingCfgDAO = FashionFightingCfgDAO.getInstance();
		_expectedHeroStatusCfgDAO = ExpectedHeroStatusCfgDAO.getInstance();
	}
	
	public static final FSGetFashionMaxFightingFunc getInstance() {
		return _INSTANCE;
	}

	@Override
	public Integer apply(Player player) {
		ExpectedHeroStatusCfg cfg = _expectedHeroStatusCfgDAO.getCfgById(String.valueOf(player.getLevel()));
		FashionFightingCfg fashionFightingCfg = _fashionFightingCfgDAO.getCfgById(String.valueOf(player.getLevel()));
		return (fashionFightingCfg.getFightingOfSuit() + fashionFightingCfg.getFightingOfWing() + fashionFightingCfg.getFightingOfPet()) * cfg.getFashionCount() / 3;
	}

}
