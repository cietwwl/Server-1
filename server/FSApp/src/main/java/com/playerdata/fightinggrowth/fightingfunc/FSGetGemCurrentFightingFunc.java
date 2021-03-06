package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;

/**
 * 
 * 玩家宝石提供的战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetGemCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {
	
	private static FSGetGemCurrentFightingFunc _instance = new FSGetGemCurrentFightingFunc();

	private IFunction<Hero, Integer> _single;
	
	protected FSGetGemCurrentFightingFunc() {
		_single = FSGetGemCurrentFightingOfSingleFunc.getInstance();
	}
	
	public static FSGetGemCurrentFightingFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		Hero temp;
		int fighting = 0;
		for (int i = 0; i < teamHeros.size(); i++) {
			temp = teamHeros.get(i);
			fighting += _single.apply(temp);
		}
		return fighting;
	}

}
