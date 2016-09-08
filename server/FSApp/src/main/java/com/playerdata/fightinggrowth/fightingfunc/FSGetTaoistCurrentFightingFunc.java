package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IFunction;

public class FSGetTaoistCurrentFightingFunc implements IFunction<Player, Integer>{
	
	private static final FSGetTaoistCurrentFightingFunc _instance = new FSGetTaoistCurrentFightingFunc();

	private IFunction<Hero, Integer> _single;
	
	protected FSGetTaoistCurrentFightingFunc() {
		this._single = FSGetTaoistCurrentFightingOfSingleFunc.getInstance();
	}
	
	public static final FSGetTaoistCurrentFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		int fighting = _single.apply(player.getMainRoleHero()) * player.getHeroMgr().getHerosSize(player);
		return fighting;
	}

}
