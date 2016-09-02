package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.common.IFunction;

public class FSGetGroupSkillCurrentFightingFunc implements IFunction<Player, Integer> {

	private IFunction<Hero, Integer> _single;
	
	public FSGetGroupSkillCurrentFightingFunc() {
		_single = new FSGetGroupSkillFightingOfSingleFunc();
	}
	
	@Override
	public Integer apply(Player player) {
		int heroSize = FSHeroMgr.getInstance().getHerosSize(player);
		return _single.apply(player.getMainRoleHero()) * heroSize;
	}

}
