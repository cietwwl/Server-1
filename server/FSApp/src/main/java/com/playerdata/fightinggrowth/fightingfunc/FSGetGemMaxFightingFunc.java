package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroCountCfgDAO;
import com.rwbase.dao.fighting.GemFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;
import com.rwbase.dao.inlay.InlayItemHelper;

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
		int gemCount = InlayItemHelper.getOpenCount(player.getLevel());
		OneToOneTypeFightingCfg cfg = GemFightingCfgDAO.getInstance().getByRequiredLv(player.getLevel());
		return expectedHeroCount * gemCount * cfg.getFighting(); // 当前等级的最高战斗力就是英雄数量期望*宝石数量*宝石品质期望
	}

}
