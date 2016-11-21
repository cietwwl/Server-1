package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
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
	
	private static FSGetGemMaxFightingFunc _instance = new FSGetGemMaxFightingFunc();

	private ExpectedHeroStatusCfgDAO expectedHeroStatusCfgDAO;
	private GemFightingCfgDAO gemFightingCfgDAO;
	
	protected FSGetGemMaxFightingFunc() {
		expectedHeroStatusCfgDAO = ExpectedHeroStatusCfgDAO.getInstance();
		gemFightingCfgDAO = GemFightingCfgDAO.getInstance();
	}
	
	public static FSGetGemMaxFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		int expectedHeroCount = expectedHeroStatusCfgDAO.getExpectedHeroCount(player.getLevel());
		int gemCount = InlayItemHelper.getOpenCount(player.getLevel());
		OneToOneTypeFightingCfg cfg = gemFightingCfgDAO.getByRequiredLv(player.getLevel());
		return expectedHeroCount * gemCount * cfg.getFighting(); // 当前等级的最高战斗力就是英雄数量期望*宝石数量*宝石品质期望
	}

}
