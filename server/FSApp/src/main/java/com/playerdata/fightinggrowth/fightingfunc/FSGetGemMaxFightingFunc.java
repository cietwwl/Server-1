package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroCountCfgDAO;

/**
 * 
 * 获取当前等级下，玩家宝石所能获得的最大战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetGemMaxFightingFunc implements IFunction<Player, Integer> {

	@Override
	public Integer apply(Player player) {
		int expectedHeroCount = ExpectedHeroCountCfgDAO.getInstance().getExpectedHeroCount(player.getLevel());
		return 0;
	}

}
