package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fashion.FashionUsedIF;
import com.rwbase.dao.fighting.FashionFightingCfgDAO;

public class FSGetFashionMaxFightingFunc implements IFunction<Player, Integer> {
	
	private static final FSGetFashionMaxFightingFunc _INSTANCE = new FSGetFashionMaxFightingFunc();
	
	private FashionFightingCfgDAO _fashionFightingCfgDAO;
	
	protected FSGetFashionMaxFightingFunc() {
		_fashionFightingCfgDAO = FashionFightingCfgDAO.getInstance();
	}
	
	public static final FSGetFashionMaxFightingFunc getInstance() {
		return _INSTANCE;
	}

	@Override
	public Integer apply(Player player) {
		FashionUsedIF usedFashion = player.getFashionMgr().getFashionUsed();
		if (usedFashion != null && usedFashion.getSuitId() > 0) {
			return _fashionFightingCfgDAO.getCfgById(String.valueOf(usedFashion.getSuitId())).getFighting();
		} else {
			// TODO 时装最大战斗力未知如何计算
			return Integer.valueOf(Short.MAX_VALUE);
		}
	}

}
