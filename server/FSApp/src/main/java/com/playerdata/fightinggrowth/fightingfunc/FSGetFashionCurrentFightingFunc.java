package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fashion.FashionUsedIF;
import com.rwbase.dao.fighting.FashionFightingCfgDAO;

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
			return _fashionFightingCfgDAO.getCfgById(String.valueOf(usedFashion.getSuitId())).getFighting();
		}
		return 0;
	}

}
