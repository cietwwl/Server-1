package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.common.IFunction;

/**
 * 
 * 获取装备所提供的战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetNormEquipCurrentFightingFunc implements IFunction<Player, Integer> {
	
	private IFunction<Hero, Integer> _single;

	public FSGetNormEquipCurrentFightingFunc() {
		_single = new FSGetNormEquipCurrentFightingOfSingleFunc();
	}

	@Override
	public Integer apply(Player player) {
		List<Hero> allHeros = FSHeroMgr.getInstance().getAllHeros(player, null);
		Hero h;
		int fighting = 0;
		for (int i = 0; i < allHeros.size(); i++) {
			h = allHeros.get(i);
			fighting += _single.apply(h);
		}
		return fighting;
	}

}
