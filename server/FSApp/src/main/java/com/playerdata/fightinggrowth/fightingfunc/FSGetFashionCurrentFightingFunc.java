package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;

public class FSGetFashionCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {

	private static final FSGetFashionCurrentFightingFunc _INSTANCE = new FSGetFashionCurrentFightingFunc();


	private FSGetFashionCurrentFightingOfSingleFunc _single;
	
	protected FSGetFashionCurrentFightingFunc() {
		_single = FSGetFashionCurrentFightingOfSingleFunc.getInstance();
	}

	public static final FSGetFashionCurrentFightingFunc getInstance() {
		return _INSTANCE;
	}

	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		return _single.apply(player);
	}
}