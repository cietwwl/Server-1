package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IFunction;

/**
 *
 * 玩家基础战斗力的获取
 * 
 * @author CHEN.P
 *
 */
public class FSGetBasicCurrentFightingFunc implements IFunction<Player, Integer> {

	private IFunction<Hero, Integer> _single;
	
	public FSGetBasicCurrentFightingFunc() {
		_single = new FSGetBasicCurrentFightingOfSingleFunc();
	}
	
	@Override
	public Integer apply(Player player) {
		List<Hero> allHeros = player.getHeroMgr().getAllHeros(player, null);
		int fighting = 0;
		for (int i = 0; i < allHeros.size(); i++) {
			fighting += _single.apply(allHeros.get(i));
		}
		return fighting;
	}
	

}
