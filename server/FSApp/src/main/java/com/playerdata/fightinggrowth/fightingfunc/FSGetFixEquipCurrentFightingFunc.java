package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;


/**
 * 
 * 获取神器的战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetFixEquipCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {
	
	private static final FSGetFixEquipCurrentFightingFunc _instance = new FSGetFixEquipCurrentFightingFunc();
	
	private IFunction<Hero, Integer> _single;
	
	protected FSGetFixEquipCurrentFightingFunc() {
		_single = FSGetFixEquipCurrentFightingOfSingleFunc.getInstance();
	}
	
	public static final FSGetFixEquipCurrentFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		Hero hero;
		int fighting = 0;
		for(int i = 0; i < teamHeros.size(); i++) {
			hero = teamHeros.get(i);
			fighting += _single.apply(hero);
		}
		return fighting;
 	}

}
