package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.common.IFunction;

public class FSGetGroupSkillCurrentFightingFunc implements IFunction<Player, Integer> {
	
	private static final FSGetGroupSkillCurrentFightingFunc _instance = new FSGetGroupSkillCurrentFightingFunc();

	private IFunction<Hero, Integer> _single;
	
	protected FSGetGroupSkillCurrentFightingFunc() {
		_single = FSGetGroupSkillFightingOfSingleFunc.getInstance();
	}
	
	public static final FSGetGroupSkillCurrentFightingFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Player player) {
		int heroSize = FSHeroMgr.getInstance().getHerosSize(player);
		return _single.apply(player.getMainRoleHero()) * heroSize;
	}

}
