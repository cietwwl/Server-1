package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IFunction;

public class FSGetFetterCurrentFightingFunc implements IFunction<Player, Integer> {
	
	private static final FSGetFetterCurrentFightingFunc _instance = new FSGetFetterCurrentFightingFunc();
	
	private IFunction<Hero, Integer> _single;
	
	protected FSGetFetterCurrentFightingFunc() {
		this._single = FSGetFetterCurrentFightingOfSingleFunc.getInstnce();
	}
	
	public static final FSGetFetterCurrentFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		int fighting = 0;
		List<Hero> allHeros = player.getHeroMgr().getAllHeros(player, null);
		for (int i = 0; i < allHeros.size(); i++) {
			fighting += _single.apply(allHeros.get(i));
		}
		return fighting;
	}

}
