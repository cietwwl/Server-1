package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IFunction;

public class FSGetTaoistCurrentFightingFunc implements IFunction<Player, Integer>{
	
	private IFunction<Hero, Integer> _single;
	
	public FSGetTaoistCurrentFightingFunc(FSGetTaoistCurrentFightingOfSingleFunc pSingle) {
		this._single = pSingle;
	}

	@Override
	public Integer apply(Player player) {
		int fighting = _single.apply(player.getMainRoleHero()) * player.getHeroMgr().getHerosSize(player);
		return fighting;
	}

}
