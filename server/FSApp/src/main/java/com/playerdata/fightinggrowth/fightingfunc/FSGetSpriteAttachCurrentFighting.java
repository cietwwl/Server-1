package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IFunction;

public class FSGetSpriteAttachCurrentFighting implements IFunction<Player, Integer> {

	private static final FSGetSpriteAttachCurrentFighting _instance = new FSGetSpriteAttachCurrentFighting();
	
	public static FSGetSpriteAttachCurrentFighting getInstance() {
		return _instance;
	}
	
	private FSGetSpriteAttachCurrentFightingOfSingleFunc _single;
	protected FSGetSpriteAttachCurrentFighting() {
		_single = FSGetSpriteAttachCurrentFightingOfSingleFunc.getInstance();
	}
	
	@Override
	public Integer apply(Player player) {
		List<Hero> allHeros = player.getHeroMgr().getAllHeros(player, null);
		int fighting = 0;
		for (Hero h : allHeros) {
			fighting += _single.apply(h);
		}
		return fighting;
	}

}
