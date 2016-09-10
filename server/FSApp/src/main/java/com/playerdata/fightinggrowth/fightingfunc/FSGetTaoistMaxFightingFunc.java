package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;

public class FSGetTaoistMaxFightingFunc implements IFunction<Player, Integer> {

	private static final FSGetTaoistMaxFightingFunc _INSTANCE = new FSGetTaoistMaxFightingFunc();
	
	public static final FSGetTaoistMaxFightingFunc getInstance() {
		return _INSTANCE;
	}
	
	@Override
	public Integer apply(Player player) {
		//TODO 道术最大战力未知如何计算
		return 0;
	}

}
