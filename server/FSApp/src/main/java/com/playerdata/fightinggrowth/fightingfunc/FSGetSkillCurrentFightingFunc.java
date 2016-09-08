package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IFunction;

public class FSGetSkillCurrentFightingFunc implements IFunction<Player, Integer>{
	
	private static final FSGetSkillCurrentFightingFunc _instance = new FSGetSkillCurrentFightingFunc();


	private IFunction<Hero, Integer> _single;
	
	protected FSGetSkillCurrentFightingFunc() {
		_single = FSGetSkillCurrentFightingOfSingleFunc.getInstance();
	}
	
	public static final FSGetSkillCurrentFightingFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Player player) {
		List<Hero> allHeros = player.getHeroMgr().getAllHeros(player, null);
		int fighting = 0;
		Hero h;
		for(int i = 0; i < allHeros.size(); i++) {
			h = allHeros.get(i);
			fighting += _single.apply(h);
		}
		return fighting;
	}

}
