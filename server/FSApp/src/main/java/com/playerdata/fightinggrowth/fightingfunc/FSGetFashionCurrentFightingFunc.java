package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fashion.FashionUsedIF;
import com.rwbase.dao.fighting.FashionFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.FashionFightingCfg;

public class FSGetFashionCurrentFightingFunc implements IFunction<Player, Integer> {
	
	private static final FSGetFashionCurrentFightingFunc _INSTANCE = new FSGetFashionCurrentFightingFunc();
	
	private FashionFightingCfgDAO _fashionFightingCfgDAO;
	
	protected FSGetFashionCurrentFightingFunc() {
		_fashionFightingCfgDAO = FashionFightingCfgDAO.getInstance();
	}
	
	public static final FSGetFashionCurrentFightingFunc getInstance() {
		return _INSTANCE;
	}

	@Override
	public Integer apply(Player player) {
		FashionUsedIF usedFashion = player.getFashionMgr().getFashionUsed();
		if (usedFashion != null && usedFashion.getSuitId() > 0) {
			FashionFightingCfg fashionFightingCfg = _fashionFightingCfgDAO.getCfgById(String.valueOf(player.getLevel()));
			return fashionFightingCfg.getFightingOfSuit() + fashionFightingCfg.getFightingOfWing() + fashionFightingCfg.getFightingOfPet();
		}
		return 0;
	}

}
