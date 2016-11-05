package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;

public class FSGetFetterCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {
	
	private static final FSGetFetterCurrentFightingFunc _instance = new FSGetFetterCurrentFightingFunc();
	
	private IFunction<Hero, Integer> _single;
	
	protected FSGetFetterCurrentFightingFunc() {
		this._single = FSGetFetterCurrentFightingOfSingleFunc.getInstnce();
	}
	
	public static final FSGetFetterCurrentFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		int fighting = 0;
		for (int i = 0; i < teamHeros.size(); i++) {
			fighting += _single.apply(teamHeros.get(i));
		}
		return fighting;
	}

}
