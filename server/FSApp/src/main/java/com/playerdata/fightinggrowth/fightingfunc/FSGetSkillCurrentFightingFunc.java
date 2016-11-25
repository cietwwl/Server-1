package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;

public class FSGetSkillCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {
	
	private static FSGetSkillCurrentFightingFunc _instance = new FSGetSkillCurrentFightingFunc();


	private IFunction<Hero, Integer> _single;
	
	protected FSGetSkillCurrentFightingFunc() {
		_single = FSGetSkillCurrentFightingOfSingleFunc.getInstance();
	}
	
	public static FSGetSkillCurrentFightingFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		int fighting = 0;
		Hero h;
		for (int i = 0; i < teamHeros.size(); i++) {
			h = teamHeros.get(i);
			fighting += _single.apply(h);
		}
		return fighting;
	}

}
