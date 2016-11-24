package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;

/**
 * 
 * 获取装备所提供的战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetNormEquipCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {
	
	private static FSGetNormEquipCurrentFightingFunc _instance = new FSGetNormEquipCurrentFightingFunc();

	private IFunction<Hero, Integer> _single;

	protected FSGetNormEquipCurrentFightingFunc() {
		_single = FSGetNormEquipCurrentFightingOfSingleFunc.getInstance();
	}
	
	public static FSGetNormEquipCurrentFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		Hero h;
		int fighting = 0;
		for (int i = 0; i < teamHeros.size(); i++) {
			h = teamHeros.get(i);
			fighting += _single.apply(h);
		}
		return fighting;
	}

}
