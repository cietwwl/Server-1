package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;

public class FSGetSpriteAttachCurrentFighting implements IBIFunction<Player, List<Hero>, Integer> {

	private static FSGetSpriteAttachCurrentFighting _instance = new FSGetSpriteAttachCurrentFighting();
	
	public static FSGetSpriteAttachCurrentFighting getInstance() {
		return _instance;
	}
	
	private FSGetSpriteAttachCurrentFightingOfSingleFunc _single;
	protected FSGetSpriteAttachCurrentFighting() {
		_single = FSGetSpriteAttachCurrentFightingOfSingleFunc.getInstance();
	}
	
	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		int fighting = 0;
		for (Hero h : teamHeros) {
			fighting += _single.apply(h);
		}
		return fighting;
	}

}
