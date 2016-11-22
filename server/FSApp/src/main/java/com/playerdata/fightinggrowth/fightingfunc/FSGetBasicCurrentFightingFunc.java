package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;

/**
 *
 * 玩家基础战斗力的获取
 * 
 * @author CHEN.P
 *
 */
public class FSGetBasicCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {
	
	private static final FSGetBasicCurrentFightingFunc _instance = new FSGetBasicCurrentFightingFunc();

	private IFunction<Hero, Integer> _single;
	
	protected FSGetBasicCurrentFightingFunc() {
		_single = FSGetBasicCurrentFightingOfSingleFunc.getInstance();
	}
	
	public static final FSGetBasicCurrentFightingFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
//		List<Hero> allHeros = player.getHeroMgr().getAllHeros(player, null);
//		int fighting = 0;
//		for (int i = 0; i < allHeros.size(); i++) {
//			fighting += _single.apply(allHeros.get(i));
//		}
		int fighting = 0;
		for (int i = 0; i < teamHeros.size(); i++) {
			fighting += _single.apply(teamHeros.get(i));
		}
		return fighting;
	}
	

}
