package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.common.IFunction;

/**
 * 
 * 玩家宝石提供的战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetGemCurrentFightingFunc implements IFunction<Player, Integer> {

	private IFunction<Hero, Integer> _single;
	
	public FSGetGemCurrentFightingFunc() {
		_single = new FSGetGemCurrentFightingOfSingleFunc();
	}
	
	@Override
	public Integer apply(Player player) {
		List<Hero> heros = FSHeroMgr.getInstance().getAllHeros(player, null);
		Hero temp;
		int fighting = 0;
		for (int i = 0; i < heros.size(); i++) {
			temp = heros.get(i);
			fighting += _single.apply(temp);
		}
		return fighting;
	}

}
